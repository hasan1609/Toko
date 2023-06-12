package com.go4sumbergedang.toko.ui.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.go4sumbergedang.toko.R
import com.go4sumbergedang.toko.adapter.UlasanAdapter
import com.go4sumbergedang.toko.databinding.ActivityUlasanBinding
import com.go4sumbergedang.toko.model.ResponseUlasan
import com.go4sumbergedang.toko.model.UlasanModel
import com.go4sumbergedang.toko.session.SessionManager
import com.go4sumbergedang.toko.webservice.ApiClient
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UlasanActivity : AppCompatActivity(), AnkoLogger {
    lateinit var binding: ActivityUlasanBinding
    private lateinit var progressDialog: ProgressDialog
    lateinit var sessionManager: SessionManager
    lateinit var mAdapter: UlasanAdapter
    var api = ApiClient.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ulasan)
        binding.lifecycleOwner = this
        sessionManager = SessionManager(this)
        progressDialog = ProgressDialog(this)
        setupToolbar()
    }

    private fun getUlasan(id: String) {
        binding.rvUlasan.layoutManager = LinearLayoutManager(this)
        binding.rvUlasan.setHasFixedSize(true)
        (binding.rvUlasan.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        loading(true)
        api.getReview(id).enqueue(object : Callback<ResponseUlasan> {
            override fun onResponse(
                call: Call<ResponseUlasan>,
                response: Response<ResponseUlasan>
            ) {
                try {
                    if (response.isSuccessful) {
                        loading(false)
                        val notesList = mutableListOf<UlasanModel>()
                        val data = response.body()
                        if (data!!.status == true) {
                            for (hasil in data.data!!) {
                                notesList.add(hasil!!)
                                mAdapter = UlasanAdapter(notesList, this@UlasanActivity)
                                binding.rvUlasan.adapter = mAdapter
                                mAdapter.notifyDataSetChanged()
                            }
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
            override fun onFailure(call: Call<ResponseUlasan>, t: Throwable) {
                loading(false)
                info { "hasan ${t.message}" }
                toast(t.message.toString())
            }
        })
    }


    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setTitle("Ulasan")
        binding.toolbar.setNavigationIcon(R.drawable.back)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
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

    override fun onStart() {
        super.onStart()
        getUlasan(sessionManager.getId().toString())
    }
}