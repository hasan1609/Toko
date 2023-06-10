package com.go4sumbergedang.toko.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.go4sumbergedang.toko.R
import com.go4sumbergedang.toko.session.SessionManager
import com.go4sumbergedang.toko.ui.fragment.HomeFragment
import com.go4sumbergedang.toko.ui.fragment.NotifikasiFragment
import com.go4sumbergedang.toko.ui.fragment.ProfilFragment
import com.go4sumbergedang.toko.ui.fragment.TransaksiFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    lateinit var sessionManager: SessionManager
    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameHome,
                        HomeFragment()
                    ).commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_notifications -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameHome,
                        NotifikasiFragment()
                    ).commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_transaksi -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameHome,
                        TransaksiFragment()
                    ).commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameHome,
                        ProfilFragment()
                    ).commit()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val navView: BottomNavigationView = findViewById(R.id.nav_viewhome)
        sessionManager = SessionManager(this)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        moveToFragment(HomeFragment())
    }

    private fun moveToFragment(fragment: Fragment) {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.frameHome, fragment)
        fragmentTrans.commit()
    }
}