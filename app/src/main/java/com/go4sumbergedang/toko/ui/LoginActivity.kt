package com.go4sumbergedang.toko.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.toko.R
import com.go4sumbergedang.toko.databinding.ActivityLoginBinding
import com.go4sumbergedang.toko.ui.activity.HomeActivity
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.startActivity

class LoginActivity : AppCompatActivity(), AnkoLogger {
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this
        binding.btnlogin.setOnClickListener{
            startActivity<HomeActivity>()
        }
    }
}