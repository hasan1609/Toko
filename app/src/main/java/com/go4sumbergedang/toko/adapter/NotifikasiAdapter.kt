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
import com.go4sumbergedang.toko.model.NotifikasiLogModel
import com.go4sumbergedang.toko.model.UlasanModel
import com.squareup.picasso.Picasso

class NotifikasiAdapter (
    private val listData :MutableList<NotifikasiLogModel>,
    private val context: Context
) : RecyclerView.Adapter<NotifikasiAdapter.ViewHolder>(){

    override fun getItemCount(): Int {
        return listData.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var judul: TextView
        var tgl: TextView
        var body: TextView


        init {
            judul = view.findViewById(R.id.txt_judul)
            tgl = view.findViewById(R.id.txt_tgl)
            body = view.findViewById(R.id.txt_body)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_notifikasi, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = listData[position]
        holder.judul.text = list.judul
        holder.tgl.text = list.createdAt
        holder.body.text = list.body
    }

}