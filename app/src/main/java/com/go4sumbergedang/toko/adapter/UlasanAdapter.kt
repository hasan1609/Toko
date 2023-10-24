package com.go4sumbergedang.toko.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.go4sumbergedang.toko.R
import com.go4sumbergedang.toko.model.UlasanModel
import com.squareup.picasso.Picasso

class UlasanAdapter (
    private val listData :MutableList<UlasanModel>,
    private val context: Context
) : RecyclerView.Adapter<UlasanAdapter.ViewHolder>(){

    override fun getItemCount(): Int {
        return listData.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var foto: ImageView
        var nama: TextView
        var txtStar: TextView
        var txtUlasan: TextView
        var star: RatingBar

        init {
            foto = view.findViewById(R.id.foto)
            nama = view.findViewById(R.id.txt_nama)
            txtStar = view.findViewById(R.id.txt_star)
            txtUlasan = view.findViewById(R.id.txt_ulasan)
            star = view.findViewById(R.id.rb_star)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_ulasan, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = listData[position]
        val urlImage = context.getString(R.string.urlImage)
        val fotoCustomer = list.customer!!.foto.toString()
        var def = "/public/images/no_image.png"
        if(list.customer.foto != null){
            Picasso.get()
                .load(urlImage+fotoCustomer)
                .into(holder.foto)
        }else{
            Picasso.get()
                .load(urlImage+def)
                .into(holder.foto)
        }
        holder.nama.text = list.userCust!!.nama
        holder.txtStar.text = list.ratingResto.toString()
        if (list.ulasanResto != null) {
            holder.txtUlasan.text = list.ulasanResto.toString()
        }else{
            holder.txtUlasan.visibility = View.GONE
        }
        holder.star.rating = list.ratingResto!!.toString().toFloat()
    }

}