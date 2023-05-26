package com.go4sumbergedang.toko.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.toko.R
import com.go4sumbergedang.toko.databinding.ActivityAddProdukBinding
import com.go4sumbergedang.toko.model.ProdukModel
import com.go4sumbergedang.toko.ui.fragment.BottomSheetFilePickerFragment
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import org.jetbrains.anko.AnkoLogger

class EditProdukActivity : AppCompatActivity(), AnkoLogger {

    lateinit var binding: ActivityAddProdukBinding
    private val sheet = BottomSheetFilePickerFragment()
    lateinit var produk: ProdukModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_produk)
        binding.lifecycleOwner = this
        val gson = Gson()
        produk =
            gson.fromJson(intent.getStringExtra("detail"), ProdukModel::class.java)
        setupToolbar()
        binding.addFoto.setOnClickListener {
            sheet.show(this.supportFragmentManager, "BottomSheetFilePickerFragment")
        }
        binding.edtNama.setText(produk.namaMakanan)
        binding.edtHarga.setText(produk.harga)
        if (produk.keterangan != null){
            binding.edtKeterangan.setText(produk.keterangan.toString())
        }

        val urlImage = getString(R.string.urlImage)
        val fotoMakanan = produk.fotoMakanan.toString()
        Picasso.get()
            .load(urlImage+fotoMakanan)
            .into(binding.foto)

    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setTitle("Tambah Produk")
        binding.toolbar.setNavigationIcon(R.drawable.back)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}