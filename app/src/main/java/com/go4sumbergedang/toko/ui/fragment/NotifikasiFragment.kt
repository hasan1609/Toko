package com.go4sumbergedang.toko.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.toko.R
import com.go4sumbergedang.toko.adapter.NotifikasiAdapter
import com.go4sumbergedang.toko.databinding.FragmentNotifikasiBinding
import com.go4sumbergedang.toko.session.SessionManager
import com.go4sumbergedang.toko.webservice.ApiClient
import org.jetbrains.anko.AnkoLogger

class NotifikasiFragment : Fragment(), AnkoLogger{
    lateinit var binding: FragmentNotifikasiBinding
    var api = ApiClient.instance()
    private lateinit var mAdapter: NotifikasiAdapter
    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater ,R.layout.fragment_notifikasi, container, false)
        binding.lifecycleOwner = this
        sessionManager = SessionManager(requireActivity())

        return binding.root
    }

    private fun getData()
    {

    }

}