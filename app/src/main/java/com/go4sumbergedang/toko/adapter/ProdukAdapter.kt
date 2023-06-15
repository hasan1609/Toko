package com.go4sumbergedang.toko.adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.go4sumbergedang.toko.R
import com.go4sumbergedang.toko.model.ProdukModel
import com.go4sumbergedang.toko.model.ResponsePostData
import com.go4sumbergedang.toko.model.ResponseStatus
import com.go4sumbergedang.toko.webservice.ApiClient
import com.squareup.picasso.Picasso
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import java.util.*


class ProdukAdapter (
    private val listData :MutableList<ProdukModel>,
    private val context: Context
) : RecyclerView.Adapter<ProdukAdapter.ViewHolder>(), AnkoLogger{

    private var editDialog: OnEditClickListener? = null
    private var hapusDialog: OnDeleteClickListener? = null

    fun setOnEditClickListener(listener: OnEditClickListener) {
        this.editDialog = listener
    }

    fun setOnDeleteClickListener(listener: OnDeleteClickListener) {
        this.hapusDialog = listener
    }

    interface OnEditClickListener {
        fun onEditClick(position: Int, note: ProdukModel)
    }

    interface OnDeleteClickListener {
        fun onDeleteClick(position: Int, note: ProdukModel)
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
        val fotoMakanan = list.fotoProduk.toString()
        holder.namaMakanan.text = list.namaProduk.toString().toUpperCase()
        holder.harga.text = list.harga.toString()
        holder.switchStatus.isChecked = list.status == "tersedia"
        if (holder.switchStatus.isChecked){
            holder.status.text ="Tersedia"
        }else{
            holder.status.text ="Habis"
        }
        Picasso.get()
            .load(urlImage+fotoMakanan)
            .into(holder.foto)
        holder.switchStatus.setOnClickListener{
            if(holder.switchStatus.isChecked){
                updateStatus(list.idProduk.toString(),"tersedia")
                holder.status.text ="Tersedia"
            }else{
                updateStatus(list.idProduk.toString(),"habis")
                holder.status.text ="Habis"
            }
        }
        holder.btnUbah.setOnClickListener {
            editDialog!!.onEditClick(position, list)
        }
        holder.btnHapus.setOnClickListener {
            hapusDialog!!.onDeleteClick(position, list)
        }
    }

    private fun updateStatus(id: String,param: String){
        ApiClient.instance().updateStatusProduk(id, param).enqueue(object :Callback<ResponseStatus>{
            override fun onResponse(
                call: Call<ResponseStatus>,
                response: Response<ResponseStatus>
            ) {
                try {
                    if (response.body()!!.status == true) {
                        Toast.makeText(context, "Berhasil mengubah", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "respon gagal", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    info { "hasan ${e.message}${response.code()} " }
                }

            }

            override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                Toast.makeText(context,"respon Gagal", Toast.LENGTH_SHORT).show()
            }

        })
    }

    val initialDataList = mutableListOf<ProdukModel>().apply {
        listData.let { addAll(it) }
    }

    fun getFilter(): Filter {
        return dataFilter
    }

    private val dataFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList: MutableList<ProdukModel> = mutableListOf()
            if (constraint == null || constraint.isEmpty()) {
                initialDataList.let { filteredList.addAll(it) }
            } else {
                val query = constraint.toString().trim().toLowerCase()
                initialDataList.forEach {
                    if (it.namaProduk!!.toLowerCase(Locale.ROOT).contains(query)) {
                        filteredList.add(it)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if (results?.values is MutableList<*>) {
                listData.clear()
                listData.addAll(results.values as MutableList<ProdukModel>)
                notifyDataSetChanged()
            }
        }
    }

}