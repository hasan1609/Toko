package com.go4sumbergedang.toko.ui.activity

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.toko.R
import com.go4sumbergedang.toko.adapter.ProdukAdapter
import com.go4sumbergedang.toko.databinding.ActivityDataProdukBinding
import com.go4sumbergedang.toko.databinding.ActivityDetailOrderLogBinding
import com.go4sumbergedang.toko.model.DataItemOrder
import com.go4sumbergedang.toko.session.SessionManager
import com.go4sumbergedang.toko.webservice.ApiClient
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class DetailOrderLogActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailOrderLogBinding
    lateinit var detailOrder: DataItemOrder
    lateinit var mAdapter: ProdukAdapter
    var api = ApiClient.instance()
    lateinit var sessionManager: SessionManager
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_detail_order_log)
        binding.lifecycleOwner = this
        sessionManager = SessionManager(this)
        progressDialog = ProgressDialog(this)
        val gson = Gson()
        detailOrder =
            gson.fromJson(intent.getStringExtra("detailOrder"), DataItemOrder::class.java)

        binding.namaDriver.text = detailOrder.order!!.driver!!.nama.toString()
        binding.txtNamaCustomer.text = detailOrder.order!!.customer!!.nama.toString()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = dateFormat.parse(detailOrder.order!!.createdAt.toString())
        val formattedDate = SimpleDateFormat("dd MMM yyyy, HH:mm:ss").format(date!!)
        binding.txtTgl.text = formattedDate
        binding.txtTujuan.text = detailOrder.order!!.alamatTujuan
        binding.txtTotal.text = detailOrder.order!!.total
        when (detailOrder.order!!.status) {
            "0" -> {
                binding.txtStatus.text = "Pesanan Aktif"
                binding.txtStatus.setTextColor(this.getColor(R.color.primary_color))
            }
            "4" -> {
                binding.txtStatus.text = "Selesai"
            }
            "5" -> {
                binding.txtStatus.text = "Batal"
                binding.txtStatus.setTextColor(this.getColor(R.color.red))
            }
            else -> {
                binding.txtStatus.text = "Sedang diproses Driver"
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setTitle("Detail Pesanan")
        binding.toolbar.setNavigationIcon(R.drawable.back)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun getProduk(){

    }
}