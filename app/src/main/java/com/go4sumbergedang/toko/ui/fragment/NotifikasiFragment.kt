package com.go4sumbergedang.toko.ui.fragment

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.go4sumbergedang.toko.R
import com.go4sumbergedang.toko.adapter.NotifikasiAdapter
import com.go4sumbergedang.toko.adapter.ProdukOrderLogAdapter
import com.go4sumbergedang.toko.adapter.RiwayatOrderAdapter
import com.go4sumbergedang.toko.databinding.FragmentNotifikasiBinding
import com.go4sumbergedang.toko.model.*
import com.go4sumbergedang.toko.session.SessionManager
import com.go4sumbergedang.toko.ui.activity.DetailOrderLogActivity
import com.go4sumbergedang.toko.webservice.ApiClient
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class NotifikasiFragment : Fragment(), AnkoLogger{
    lateinit var binding: FragmentNotifikasiBinding
    var api = ApiClient.instance()
    private lateinit var mAdapter: NotifikasiAdapter
    lateinit var sessionManager: SessionManager
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater ,R.layout.fragment_notifikasi, container, false)
        binding.lifecycleOwner = this
        sessionManager = SessionManager(requireActivity())
        progressDialog = ProgressDialog(requireActivity())

        return binding.root
    }

    private fun getNotifikasi(reciveId: String) {
        binding.rvNotifikasi.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvNotifikasi.setHasFixedSize(true)
        (binding.rvNotifikasi.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        loading(true)
        api.getNotifikasiLog(reciveId).enqueue(object :
            Callback<ResponseNotifikasiLog> {
            override fun onResponse(call: Call<ResponseNotifikasiLog>, response: Response<ResponseNotifikasiLog>) {
                try {
                    if (response.isSuccessful) {
                        loading(false)
                        if (response.body()!!.data!!.isEmpty()){
                            binding.txtKosong.visibility = View.VISIBLE
                            binding.rvNotifikasi.visibility = View.GONE
                        }else{
                            binding.txtKosong.visibility = View.GONE
                            binding.rvNotifikasi.visibility = View.VISIBLE
                            val noteList = mutableListOf<NotifikasiLogModel>()
                            val data = response.body()
                            if (data!!.status == true) {
                                for (hasil in data.data!!) {
                                    noteList.add(hasil!!)
                                }
                                mAdapter = NotifikasiAdapter(noteList, requireActivity())
                                binding.rvNotifikasi.adapter = mAdapter
                                mAdapter.notifyDataSetChanged()
                                mAdapter.setDialog(object : NotifikasiAdapter.Dialog {
                                    override fun onClick(position: Int, idOrder: String, status: String, idNotifikasi: String) {
                                        val intent = intentFor<DetailOrderLogActivity>()
                                            .putExtra("idOrder", idOrder)

                                        if (status == "0"){
                                            updateStatus(idNotifikasi)
                                        }
                                        startActivity(intent)
                                    }
                                })
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

            override fun onFailure(call: Call<ResponseNotifikasiLog>, t: Throwable) {
                info { "hasan ${t.message}" }
                toast(t.message.toString())
            }
        })
    }

    private fun updateStatus(notifikasiId: String){
        api.updateNotifikasiStatusLog(notifikasiId).enqueue(object :
            Callback<ResponsePostData> {
            override fun onResponse(call: Call<ResponsePostData>, response: Response<ResponsePostData>) {
                try {
                    if (response.isSuccessful) {
                        loading(false)
                    } else {
                        loading(false)
                        toast("Gagal mengupdate")
                    }
                } catch (e: Exception) {
                    loading(false)
                    info { "hasan ${e.message}" }
                    toast(e.message.toString())
                }
            }

            override fun onFailure(call: Call<ResponsePostData>, t: Throwable) {
                info { "hasan ${t.message}" }
                toast(t.message.toString())
            }
        })
    }

    override fun onStart() {
        super.onStart()
        getNotifikasi(sessionManager.getId().toString())
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