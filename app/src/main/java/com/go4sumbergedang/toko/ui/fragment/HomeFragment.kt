package com.go4sumbergedang.toko.ui.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.go4sumbergedang.toko.R
import com.go4sumbergedang.toko.adapter.KategoriAdapter
import com.go4sumbergedang.toko.databinding.FragmentHomeBinding
import com.go4sumbergedang.toko.model.KategoriModel
import com.go4sumbergedang.toko.model.ResponseKategori
import com.go4sumbergedang.toko.model.ResponseStatus
import com.go4sumbergedang.toko.ui.activity.AddProdukActivity
import com.go4sumbergedang.toko.ui.activity.DataProdukActivity
import com.go4sumbergedang.toko.webservice.ApiClient
import com.google.gson.Gson
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(), AnkoLogger {
    lateinit var binding: FragmentHomeBinding
    var api = ApiClient.instance()
    var param: String? = null
    private lateinit var mAdapter: KategoriAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater ,R.layout.fragment_home, container, false)
        binding.lifecycleOwner = this
        binding.statusToko.setOnClickListener{
            if(binding.statusToko.isChecked){
                param = "buka"
                updateStatus(param!!)
            }else{
                param = "tutup"
                updateStatus(param!!)
            }
        }

        binding.addProduk.setOnClickListener{
            startActivity<AddProdukActivity>()
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        getStatusToko()
        getKategoriProduk()
    }

    private fun getKategoriProduk() {
        if (!isAdded) {
            // Fragment tidak terhubung ke aktivitas, hentikan eksekusi lebih lanjut
            return
        }
        // Memeriksa koneksi internet sebelum memulai panggilan API
        if (!isNetworkAvailable()) {
            toast("Tidak ada koneksi internet. Silakan cek koneksi Anda dan coba lagi.")
            return
        }
        binding.rvKategori.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvKategori.setHasFixedSize(true)
        (binding.rvKategori.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL

        api.getKategori("c9927b1a-a334-4bd8-9633-62fb9b843b85").enqueue(object : Callback<ResponseKategori> {
            override fun onResponse(call: Call<ResponseKategori>, response: Response<ResponseKategori>) {
                if (!isAdded) {
                    // Fragment tidak terhubung ke aktivitas lagi, hentikan eksekusi lebih lanjut
                    return
                }
                try {
                    if (response.isSuccessful) {
                        val notesList = mutableListOf<KategoriModel>()
                        val data = response.body()
                        if (data!!.status == true) {
                            for (hasil in data.data!!) {
                                notesList.add(hasil!!)
                            }

                            mAdapter = KategoriAdapter(notesList, requireActivity())
                            binding.rvKategori.adapter = mAdapter

                            mAdapter.setDialog(object : KategoriAdapter.Dialog {
                                override fun onClick(position: Int, note: KategoriModel) {
                                    val gson = Gson()
                                    val noteJson = gson.toJson(note)
                                    startActivity<DataProdukActivity>("kategori" to noteJson)
                                }
                            })

                            mAdapter.notifyDataSetChanged()
                        }
                    } else {
                        toast("gagal mendapatkan response")
                    }
                } catch (e: Exception) {
                    if (isAdded) {
                        info { "hasan ${e.message}" }
                        toast(e.message.toString())
                    }
                }
            }

            override fun onFailure(call: Call<ResponseKategori>, t: Throwable) {
                if (isAdded) {
                    info { "hasan ${t.message}" }
                    toast(t.message.toString())
                }
            }
        })
    }


    private fun getStatusToko() {
        if (!isAdded) {
            // Fragment tidak terhubung ke aktivitas lagi, hentikan eksekusi lebih lanjut
            return
        }
        if (!isNetworkAvailable()) {
            toast("Tidak ada koneksi internet. Silakan cek koneksi Anda dan coba lagi.")
            return
        }
        api.getStatusToko("c9927b1a-a334-4bd8-9633-62fb9b843b85").enqueue(object : Callback<ResponseStatus> {
            override fun onResponse(
                call: Call<ResponseStatus>,
                response: Response<ResponseStatus>
            ) {
                if (!isAdded) {
                    // Fragment tidak terhubung ke aktivitas lagi, hentikan eksekusi lebih lanjut
                    return
                }
                try {
                    if (response.isSuccessful) {
                        val data = response.body()
                        binding.statusToko.isChecked = data!!.data!!.status == "buka"
                    } else {
                        toast("gagal mendapatkan response")
                    }
                } catch (e: Exception) {
                    info { "dinda ${e.message}" }
                    toast(e.toString())
                }
            }
            override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                if (isAdded) {
                    info { "hasan ${t.message}" }
                    toast(t.message.toString())
                }
            }
        })
    }

    private fun updateStatus(param: String){
        api.updateStatusToko("c9927b1a-a334-4bd8-9633-62fb9b843b85", param).enqueue(object :Callback<ResponseStatus>{
            override fun onResponse(
                call: Call<ResponseStatus>,
                response: Response<ResponseStatus>
            ) {
                try {
                    if (response.body()!!.status == true) {
                        toast("Status toko diubah")
                    } else {
                        toast("Status toko gagal diubah")
                    }


                } catch (e: Exception) {
                    info { "dinda ${e.message}${response.code()} " }
                }

            }

            override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                toast("Kesalahab Jarngan")
            }

        })
    }

    // Fungsi untuk memeriksa ketersediaan koneksi internet
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}