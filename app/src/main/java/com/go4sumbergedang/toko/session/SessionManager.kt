package com.go4sumbergedang.toko.session

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    companion object {
        private const val KEY_ID = "id"
        private const val KEY_LOGGED_IN = "logged_in"
    }

    var username: String?
        get() = sharedPreferences.getString(KEY_ID, null)
        set(value) = editor.putString(KEY_ID, value).apply()

    var isLoggedIn: Boolean
        get() = sharedPreferences.getBoolean(KEY_LOGGED_IN, false)
        set(value) = editor.putBoolean(KEY_LOGGED_IN, value).apply()

    fun login(username: String) {
        isLoggedIn = true
        this.username = username
    }

    fun logout() {
        isLoggedIn = false
        username = null
    }

    fun clearSession() {
        editor.clear().apply()
    }
}