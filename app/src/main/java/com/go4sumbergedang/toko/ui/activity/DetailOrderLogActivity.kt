package com.go4sumbergedang.toko.ui.activity

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.go4sumbergedang.toko.R
import com.go4sumbergedang.toko.adapter.ProdukOrderLogAdapter
import com.go4sumbergedang.toko.databinding.ActivityDetailOrderLogBinding
import com.go4sumbergedang.toko.model.DataItemOrder
import com.go4sumbergedang.toko.model.ProdukOrderLogModel
import com.go4sumbergedang.toko.model.ResponseProdukOrderLog
import com.go4sumbergedang.toko.session.SessionManager
import com.go4sumbergedang.toko.webservice.ApiClient
import com.google.gson.Gson
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class DetailOrderLogActivity : AppCompatActivity(), AnkoLogger {
    lateinit var binding: ActivityDetailOrderLogBinding
    lateinit var detailOrder: DataItemOrder
    lateinit var mAdapter: ProdukOrderLogAdapter
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

        val formatter = DecimalFormat.getCurrencyInstance() as DecimalFormat
        val symbols = formatter.decimalFormatSymbols
        symbols.currencySymbol = "Rp. "
        formatter.decimalFormatSymbols = symbols
        val totalx = detailOrder.order!!.total!!.toDoubleOrNull() ?: 0.0
        val totals = formatter.format(totalx)
        binding.txtTotal.text = totals
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

        setupToolbar()
        getProduk(detailOrder.order!!.produkOrder.toString())
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

    private fun getProduk(produkId: String) {
        binding.rvProduk.layoutManager = LinearLayoutManager(this)
        binding.rvProduk.setHasFixedSize(true)
        (binding.rvProduk.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        loading(true)
        api.getProdukOrderLog(produkId).enqueue(object :
            Callback<ResponseProdukOrderLog> {
            override fun onResponse(call: Call<ResponseProdukOrderLog>, response: Response<ResponseProdukOrderLog>) {
                try {
                    if (response.isSuccessful) {
                        loading(false)
                        val notesList = mutableListOf<ProdukOrderLogModel>()
                        val data = response.body()
                        if (data!!.status == true) {
                            for (hasil in data.data!!) {
                                notesList.add(hasil!!)
                            }
                            mAdapter = ProdukOrderLogAdapter(notesList, this@DetailOrderLogActivity)
                            binding.rvProduk.adapter = mAdapter
                            mAdapter.notifyDataSetChanged()
                        }
                    } else {
                        loading(false)
                        toast("gagal mendapatkan response")
                    }
                } catch (e: Exception) {
                    loading(false)
                    info { "hasan ${e.message}" }
                    toast(e.message.toString())
                }
            }

            override fun onFailure(call: Call<ResponseProdukOrderLog>, t: Throwable) {
                info { "hasan ${t.message}" }
                toast(t.message.toString())
            }
        })
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            progressDialog.setMessage("Tunggu sebentar...")
            progressDialog.setCancelable(false)
            progressDialog.show()
        } else {
            progressDialog.dismiss()
        }
    }
}