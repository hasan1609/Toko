package com.go4sumbergedang.toko.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.go4sumbergedang.toko.R
import com.go4sumbergedang.toko.adapter.ProdukAdapter
import com.go4sumbergedang.toko.databinding.ActivityDataProdukBinding
import com.go4sumbergedang.toko.model.KategoriModel
import com.go4sumbergedang.toko.model.ProdukModel
import com.go4sumbergedang.toko.model.ResponseProduk
import com.go4sumbergedang.toko.webservice.ApiClient
import com.google.gson.Gson
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DataProdukActivity : AppCompatActivity(), AnkoLogger{
    lateinit var binding: ActivityDataProdukBinding
    lateinit var kategori: KategoriModel
    lateinit var mAdapter: ProdukAdapter
    var api = ApiClient.instance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_data_produk)
        binding.lifecycleOwner = this
        val gson = Gson()
        kategori =
            gson.fromJson(intent.getStringExtra("kategori"), KategoriModel::class.java)

        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setTitle("Data Produk")
        binding.toolbar.setNavigationIcon(R.drawable.back)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun getProduk(id: String, kategori : String) {
        binding.rvProduk.layoutManager = LinearLayoutManager(this)
        binding.rvProduk.setHasFixedSize(true)
        (binding.rvProduk.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL

        api.getProduk(id, kategori).enqueue(object :Callback<ResponseProduk> {
            override fun onResponse(
                call: Call<ResponseProduk>,
                response: Response<ResponseProduk>
            ) {
                try {
                    if (response.isSuccessful) {
                        val notesList = mutableListOf<ProdukModel>()
                        val data = response.body()
                        if (data!!.status == true) {
                            for (hasil in data.data!!) {
                                notesList.add(hasil!!)
                                mAdapter = ProdukAdapter(notesList, this@DataProdukActivity)
                                binding.rvProduk.adapter = mAdapter
                                mAdapter.setDialog(object : ProdukAdapter.Dialog {
                                    override fun onClick(position: Int, note: ProdukModel) {
                                        val gson = Gson()
                                        val noteJson = gson.toJson(note)
                                        startActivity<EditProdukActivity>("detail" to noteJson)
                                    }
                                })
                                mAdapter.notifyDataSetChanged()
                            }
                        }
                    } else {
                        toast("gagal mendapatkan response")
                    }
                } catch (e: Exception) {
                    info { "hasan ${e.message}" }
                    toast(e.message.toString())
                }
            }
            override fun onFailure(call: Call<ResponseProduk>, t: Throwable) {
                info { "hasan ${t.message}" }
                toast(t.message.toString())
            }
        })

    }

    override fun onStart() {
        super.onStart()
        getProduk("c9927b1a-a334-4bd8-9633-62fb9b843b85", kategori.kategori.toString())
    }


}