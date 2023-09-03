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
import com.go4sumbergedang.toko.model.DetailOrderModel
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
    lateinit var mAdapter: ProdukOrderLogAdapter
    var api = ApiClient.instance()
    lateinit var sessionManager: SessionManager
    private lateinit var progressDialog: ProgressDialog

    companion object {
        const val idOrder = "idOrder"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_detail_order_log)
        binding.lifecycleOwner = this
        sessionManager = SessionManager(this)
        progressDialog = ProgressDialog(this)

        val data = intent.getStringExtra(idOrder)
        setupToolbar()
        getProduk(data.toString())
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

    private fun getProduk(orderId: String) {
        binding.rvProduk.layoutManager = LinearLayoutManager(this)
        binding.rvProduk.setHasFixedSize(true)
        (binding.rvProduk.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        loading(true)
        api.getProdukOrderLog(orderId).enqueue(object :
            Callback<ResponseProdukOrderLog> {
            override fun onResponse(call: Call<ResponseProdukOrderLog>, response: Response<ResponseProdukOrderLog>) {
                try {
                    if (response.isSuccessful) {
                        loading(false)
                        val produkList = mutableListOf<ProdukOrderLogModel>()
                        val data = response.body()
                        val orderList = data!!.order
                        if (data.status == true) {
                            binding.namaDriver.text = orderList!!.driver!!.nama.toString()
                            binding.txtNamaCustomer.text = orderList.customer!!.nama.toString()
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
                            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                            val date = dateFormat.parse(orderList.createdAt.toString())
                            val formattedDate = SimpleDateFormat("dd MMM yyyy, HH:mm:ss").format(date!!)
                            binding.txtTgl.text = formattedDate
                            binding.txtTujuan.text = orderList.alamatTujuan

                            val formatter = DecimalFormat.getCurrencyInstance() as DecimalFormat
                            val symbols = formatter.decimalFormatSymbols
                            symbols.currencySymbol = "Rp. "
                            formatter.decimalFormatSymbols = symbols
                            val totalx = orderList.total!!.toDoubleOrNull() ?: 0.0
                            val totals = formatter.format(totalx)
                            binding.txtTotal.text = totals
                            when (orderList.status) {
                                "0" -> {
                                    binding.txtStatus.text = "Pesanan Aktif"
                                    binding.txtStatus.setTextColor(getColor(R.color.primary_color))
                                }
                                "4" -> {
                                    binding.txtStatus.text = "Selesai"
                                    binding.txtStatus.setTextColor(getColor(R.color.teal_200))
                                }
                                "5" -> {
                                    binding.txtStatus.text = "Batal"
                                    binding.txtStatus.setTextColor(getColor(R.color.red))
                                }
                                else -> {
                                    binding.txtStatus.text = "Sedang diproses Driver"
                                    binding.txtStatus.setTextColor(getColor(R.color.black))
                                }
                            }
                            for (hasil in data.produk!!) {
                                produkList.add(hasil!!)
                            }
                            mAdapter = ProdukOrderLogAdapter(produkList, this@DetailOrderLogActivity)
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