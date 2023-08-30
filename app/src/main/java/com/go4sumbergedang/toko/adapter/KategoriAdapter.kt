package com.go4sumbergedang.toko.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.go4sumbergedang.toko.R
import com.go4sumbergedang.toko.model.KategoriProdukModel

class KategoriAdapter (
    private val listData :MutableList<KategoriProdukModel>,
    private val context: Context
) : RecyclerView.Adapter<KategoriAdapter.ViewHolder>(){

    private var dialog: Dialog? = null
    interface Dialog {
        fun onClick(position: Int, list : KategoriProdukModel)
    }

    fun setDialog(dialog: Dialog) {
        this.dialog = dialog
    }
    override fun getItemCount(): Int {
        return listData.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var icon: ImageView
        var kategori: TextView
        var hasil: TextView

        init {
            icon = view.findViewById(R.id.ic_kategori)
            kategori = view.findViewById(R.id.nama_kategori)
            hasil = view.findViewById(R.id.jumlah_kategori)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_kategori, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val list = listData[position]
        holder.kategori.text = list.kategori.toString().toUpperCase()
        holder.hasil.text = list.hasil.toString()
        if (list.kategori == "makanan"){
            holder.icon.setImageResource(R.drawable.makanan)
        }else if(list.kategori == "minuman"){
            holder.icon.setImageResource(R.drawable.minuman)
        }else{
            holder.icon.setImageResource(R.drawable.snack)
        }
        holder.itemView.setOnClickListener {
            if (dialog!=null){
                dialog!!.onClick(position,list)
            }
        }
    }

}