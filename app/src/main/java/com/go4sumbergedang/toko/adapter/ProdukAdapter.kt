package com.go4sumbergedang.toko.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.go4sumbergedang.toko.R
import com.go4sumbergedang.toko.model.ProdukModel
import com.go4sumbergedang.toko.model.ResponsePostData
import com.go4sumbergedang.toko.webservice.ApiClient
import com.squareup.picasso.Picasso
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback


class ProdukAdapter (
    private val listData :MutableList<ProdukModel>,
    private val context: Context
) : RecyclerView.Adapter<ProdukAdapter.ViewHolder>(), AnkoLogger{

    private var dialog: Dialog? = null
    interface Dialog {
        fun onClick(position: Int, note : ProdukModel)
    }

    fun setDialog(dialog: Dialog) {
        this.dialog = dialog
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var foto: ImageView
        var namaMakanan: TextView
        var harga: TextView
        var status: TextView
        var switchStatus: Switch
        var btnUbah: Button
        var btnHapus: Button

        init {
            foto = view.findViewById(R.id.img_makanan)
            namaMakanan = view.findViewById(R.id.nama_makanan)
            harga = view.findViewById(R.id.harga)
            status = view.findViewById(R.id.txt_status)
            switchStatus = view.findViewById(R.id.status_produk)
            btnUbah = view.findViewById(R.id.btn_ubah)
            btnHapus = view.findViewById(R.id.btn_hapus)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_produk, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = listData[position]
        val urlImage = context.getString(R.string.urlImage)
        val fotoMakanan = list.fotoMakanan.toString()
        holder.namaMakanan.text = list.namaMakanan.toString().toUpperCase()
        holder.harga.text = list.harga.toString()
        holder.switchStatus.isChecked = list.status.toString() == "1"
        if (holder.switchStatus.isChecked){
            holder.status.text ="Tersedia"
        }else{
            holder.status.text ="Habis"
        }
        Picasso.get()
            .load(urlImage+fotoMakanan)
            .into(holder.foto)
        holder.btnUbah.setOnClickListener{
            if (dialog!=null){
                dialog!!.onClick(position,list)
            }
        }
        holder.btnHapus.setOnClickListener{
            val builder =
                AlertDialog.Builder(context)
            builder.setTitle(list.idMakanan.toString())
            builder.setPositiveButton("Ya") { dialog, which ->
                ApiClient.instance().hapusProduk(list.idMakanan.toString()).enqueue(object :
                    Callback<ResponsePostData> {
                    override fun onResponse(
                        call: Call<ResponsePostData>,
                        response: Response<ResponsePostData>
                    ) {
                        try {
                            if (response.body()!!.status == true) {
                                Toast.makeText(context, "Berhasil mengapus", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "respon gagal", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                            info { "hasan ${e.message}${response.code()} " }
                        }
                    }
                    override fun onFailure(
                        call: Call<ResponsePostData>,
                        t: Throwable
                    ) {
                        Toast.makeText(context,"respon Gagal", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            builder.setNegativeButton("Batal") { dialog, which ->
            }
            builder.show()
        }
    }

}