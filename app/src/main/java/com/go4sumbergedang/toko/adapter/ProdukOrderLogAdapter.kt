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
import com.go4sumbergedang.toko.model.ProdukOrderLogModel
import com.squareup.picasso.Picasso
import java.text.DecimalFormat

class ProdukOrderLogAdapter(
    private val listData :MutableList<ProdukOrderLogModel>,
    private val context: Context
) : RecyclerView.Adapter<ProdukOrderLogAdapter.ViewHolder>() {


    override fun getItemCount(): Int {
        return listData.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var foto: ImageView
        var namaProduk: TextView
        var jumlahHarga: TextView
        var keterangan: TextView
        var total: TextView

        init {
            foto = view.findViewById(R.id.foto)
            namaProduk = view.findViewById(R.id.txt_nama)
            jumlahHarga = view.findViewById(R.id.txt_harga)
            keterangan = view.findViewById(R.id.txt_ket)
            total = view.findViewById(R.id.txt_total)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_produk_order_log, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = listData[position]
        val urlImage = context.getString(R.string.urlImage)
        val foto = list.produk!!.fotoProduk.toString()
        val def = "/public/images/no_image.png"
        if (list.produk.fotoProduk != null) {
            Picasso.get()
                .load(urlImage + foto)
                .into(holder.foto)
        } else {
            Picasso.get()
                .load(urlImage + def)
                .into(holder.foto)
        }
        holder.namaProduk.text = list.produk.namaProduk!!.toString().toUpperCase()

        if (list.catatan != null){
            holder.keterangan.visibility = View.VISIBLE
            holder.total.text = list.total
        }else{
            holder.keterangan.visibility = View.GONE

        }

        val hargax = list.produk!!.harga!!.toDoubleOrNull() ?: 0.0
        val totalx = list.total!!.toDoubleOrNull() ?: 0.0
        val formatter = DecimalFormat.getCurrencyInstance() as DecimalFormat
        val symbols = formatter.decimalFormatSymbols
        symbols.currencySymbol = "Rp. "
        formatter.decimalFormatSymbols = symbols

        val totals = formatter.format(totalx)
        val hargas = formatter.format(hargax)

        holder.jumlahHarga.text = "$hargas x " + list.jumlah.toString()
        holder.total.text = totals
    }
}