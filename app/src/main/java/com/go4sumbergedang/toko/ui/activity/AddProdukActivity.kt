package com.go4sumbergedang.toko.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.toko.R
import com.go4sumbergedang.toko.databinding.ActivityAddProdukBinding
import com.go4sumbergedang.toko.ui.fragment.BottomSheetFilePickerFragment

class AddProdukActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddProdukBinding
    val sheet = BottomSheetFilePickerFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_produk)
        binding.lifecycleOwner = this
        setupToolbar()
        binding.addFoto.setOnClickListener{
            sheet.show(this.supportFragmentManager, "BottomSheetFilePickerFragment")
        }
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
