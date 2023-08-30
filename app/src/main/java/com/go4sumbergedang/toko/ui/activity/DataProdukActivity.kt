package com.go4sumbergedang.toko.ui.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.go4sumbergedang.toko.R
import com.go4sumbergedang.toko.adapter.ProdukAdapter
import com.go4sumbergedang.toko.databinding.ActivityDataProdukBinding
import com.go4sumbergedang.toko.model.*
import com.go4sumbergedang.toko.session.SessionManager
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
    lateinit var kategori: KategoriProdukModel
    lateinit var mAdapter: ProdukAdapter
    var api = ApiClient.instance()
    lateinit var sessionManager: SessionManager
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_data_produk)
        binding.lifecycleOwner = this
        sessionManager = SessionManager(this)
        progressDialog = ProgressDialog(this)
        val gson = Gson()
        kategori =
            gson.fromJson(intent.getStringExtra("kategori"), KategoriProdukModel::class.java)
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mAdapter.getFilter().filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mAdapter.getFilter().filter(newText)
                return true
            }

        })

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
        loading(true)
        api.getProduk(id, kategori).enqueue(object :Callback<ResponseProduk> {
            override fun onResponse(
                call: Call<ResponseProduk>,
                response: Response<ResponseProduk>
            ) {
                try {
                    if (response.isSuccessful) {
                        loading(false)
                        val notesList = mutableListOf<ProdukModel>()
                        val data = response.body()
                        if (data!!.status == true) {
                            for (hasil in data.data!!) {
                                notesList.add(hasil!!)
                                mAdapter = ProdukAdapter(notesList, this@DataProdukActivity)
                                binding.rvProduk.adapter = mAdapter
                                mAdapter.setOnEditClickListener(object : ProdukAdapter.OnEditClickListener{
                                    override fun onEditClick(position: Int, note: ProdukModel) {
                                        val gson = Gson()
                                        val noteJson = gson.toJson(note)
                                        startActivity<EditProdukActivity>("detail" to noteJson)
                                    }
                                })
                                mAdapter.setOnDeleteClickListener(object : ProdukAdapter.OnDeleteClickListener{
                                    override fun onDeleteClick(position: Int, note: ProdukModel) {
                                        val builder =
                                            AlertDialog.Builder(this@DataProdukActivity)
                                        builder.setTitle("Hapus Data")
                                        builder.setPositiveButton("Ya") { dialog, which ->
                                            ApiClient.instance().hapusProduk(note.idProduk.toString()).enqueue(object :
                                                Callback<ResponsePostData> {
                                                override fun onResponse(
                                                    call: Call<ResponsePostData>,
                                                    response: Response<ResponsePostData>
                                                ) {
                                                    try {
                                                        if (response.body()!!.status == true) {
                                                            toast("Berhasil mengapus")
                                                        } else {
                                                            toast("Gagal mengapus")
                                                        }
                                                    } catch (e: Exception) {
                                                        toast("Kesalahan jaringan")
                                                        info { "hasan ${e.message}${response.code()} " }
                                                    }
                                                }
                                                override fun onFailure(
                                                    call: Call<ResponsePostData>,
                                                    t: Throwable
                                                ) {
                                                    toast("Respon server gagal")
                                                }
                                            })
                                        }
                                        builder.setNegativeButton("Batal") { dialog, which ->
                                        }
                                        builder.show()
                                    }
                                })
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
            override fun onFailure(call: Call<ResponseProduk>, t: Throwable) {
                loading(false)
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

    override fun onStart() {
        super.onStart()
        getProduk(sessionManager.getId().toString(), kategori.kategori.toString())
    }
}