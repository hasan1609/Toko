package com.go4sumbergedang.toko.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.toko.R
import com.go4sumbergedang.toko.databinding.FragmentProfilBinding
import com.go4sumbergedang.toko.ui.activity.DetailProfilActivity
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.startActivity

class ProfilFragment : Fragment(), AnkoLogger {
    lateinit var binding: FragmentProfilBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater ,R.layout.fragment_profil, container, false)
        binding.lifecycleOwner = this
        binding.detailToko.setOnClickListener{
            startActivity<DetailProfilActivity>()
        }
        return binding.root
    }
}