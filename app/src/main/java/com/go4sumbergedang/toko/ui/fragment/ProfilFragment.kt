package com.go4sumbergedang.toko.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.toko.R
import com.go4sumbergedang.toko.databinding.FragmentProfilBinding
import com.go4sumbergedang.toko.model.ResponseToko
import com.go4sumbergedang.toko.session.SessionManager
import com.go4sumbergedang.toko.ui.LoginActivity
import com.go4sumbergedang.toko.ui.activity.DetailProfilActivity
import com.go4sumbergedang.toko.ui.activity.UlasanActivity
import com.go4sumbergedang.toko.webservice.ApiClient
import com.squareup.picasso.Picasso
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfilFragment : Fragment(), AnkoLogger {
    lateinit var binding: FragmentProfilBinding
    lateinit var sessionManager: SessionManager
    var api = ApiClient.instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater ,R.layout.fragment_profil, container, false)
        binding.lifecycleOwner = this
        sessionManager = SessionManager(requireActivity())
        binding.detailToko.setOnClickListener{
            startActivity<DetailProfilActivity>()
        }
        binding.ulasan.setOnClickListener {
            startActivity<UlasanActivity>()
        }

        binding.logout.setOnClickListener {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setMessage("Logout ? ")
            builder.setPositiveButton("Ok") { dialog, which ->
                sessionManager.clearSession()
                startActivity<LoginActivity>()
                toast("Berhasil Logout")
                requireActivity().finish()
            }

            builder.setNegativeButton("Cancel ?") { dialog, which ->
                dialog.dismiss()
            }

            builder.show()
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        getData()
    }

    private fun getData() {
        api.getToko(sessionManager.getId().toString()).enqueue(object : Callback<ResponseToko> {
            override fun onResponse(
                call: Call<ResponseToko>,
                response: Response<ResponseToko>
            ) {
                try {
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data!!.status == true) {
                            binding.txtToko.text = data.data!!.detailUser!!.namaResto.toString()
                            val urlImage = getString(R.string.urlImage)
                            Picasso.get()
                                .load(urlImage+data.data.detailUser!!.foto.toString())
                                .into(binding.foto)
                        }
                    } else {
                        toast("gagal mendapatkan response")
                    }
                } catch (e: Exception) {
                    info { "hasan ${e.message}" }
                    toast(e.message.toString())
                }
            }
            override fun onFailure(call: Call<ResponseToko>, t: Throwable) {
                info { "hasan ${t.message}" }
                toast(t.message.toString())
            }
        })

    }

}