package com.go4sumbergedang.toko.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.go4sumbergedang.toko.R
import com.go4sumbergedang.toko.model.DataItemOrder
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class RiwayatOrderAdapter (
    private val listData :MutableList<DataItemOrder>,
    private val context: Context
) : RecyclerView.Adapter<RiwayatOrderAdapter.ViewHolder>(){

    private var dialog: Dialog? = null
    interface Dialog {
        fun onClick(position: Int, idOrder : String)
    }

    fun setDialog(dialog: Dialog) {
        this.dialog = dialog
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var fotoDriver: ImageView
        var namaDriver: TextView
        var namaCustomer: TextView
        var jmlProduk: TextView
        var tgl: TextView
        var status: TextView

        init {
            fotoDriver = view.findViewById(R.id.foto_driver)
            namaDriver = view.findViewById(R.id.nama_driver)
            namaCustomer = view.findViewById(R.id.nama_customer)
            jmlProduk = view.findViewById(R.id.jml_produk)
            tgl = view.findViewById(R.id.txt_tgl)
            status = view.findViewById(R.id.txt_status)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_riwayat_order, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = listData[position]
        val urlImage = context.getString(R.string.urlImage)
        val foto= list.order!!.detailDriver!!.foto!!.toString()
        var def = "/public/images/no_image.png"
        if(list.order.detailDriver!!.foto != null){
            Picasso.get()
                .load(urlImage+foto)
                .into(holder.fotoDriver)
        }else{
            Picasso.get()
                .load(urlImage+def)
                .into(holder.fotoDriver)
        }
        holder.namaDriver.text = "Driver: " + list.order.driver!!.nama
        holder.namaCustomer.text = "Customer: " + list.order.customer!!.nama
        holder.jmlProduk.text = list.count.toString() + " Produk"
//list.order.createdAt
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = dateFormat.parse(list.order.createdAt!!)
        val formattedDate = SimpleDateFormat("dd MMM yyyy, HH:mm:ss").format(date!!)
        holder.tgl.text = formattedDate
        // 0 = driver ke toko
        // 1 = driver sampai toko
        // 2 = driver mengantar
        // 3 = driver sampai
        // 4 = selesai
        // 5 = batal
        when (list.order.status) {
            "0" -> {
                holder.status.text = "Pesanan Aktif"
                holder.status.setTextColor(context.getColor(R.color.primary_color))
            }
            "4" -> {
                holder.status.text = "Selesai"
            }
            "5" -> {
                holder.status.text = "Batal"
                holder.status.setTextColor(context.getColor(R.color.red))
            }
            else -> {
                holder.status.text = "Sedang diproses Driver"
            }
        }

        holder.itemView.setOnClickListener {
            if (dialog!=null){
                dialog!!.onClick(position, list.order.idOrder.toString())
            }
        }
    }

}