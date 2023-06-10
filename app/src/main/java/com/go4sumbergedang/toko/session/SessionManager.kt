package com.go4sumbergedang.toko.session

import android.content.Context
import android.content.SharedPreferences


class SessionManager(private val context: Context) {
    val privateMode = 0
    val privateName ="login"
    var Pref : SharedPreferences?= context.getSharedPreferences(privateName,privateMode)
    var editor : SharedPreferences.Editor?=Pref?.edit()

    private val islogin = "login"
    fun setLogin(check: Boolean){
        editor?.putBoolean(islogin,check)
        editor?.commit()
    }

    fun getLogin():Boolean?
    {
        return Pref?.getBoolean(islogin,false)
    }


    private val isToken = "isToken"
    fun setToken(check: String){
        editor?.putString(isToken,check)
        editor?.commit()
    }

    fun getToken():String?
    {
        return Pref?.getString(isToken,"")
    }

    private val isId = "isId"
    fun setId(check: String){
        editor?.putString(isId,check)
        editor?.commit()
    }

    fun getId():String?
    {
        return Pref?.getString(isId,"")
    }

    private val isNamaToko = "isNamaToko"
    fun setNamaToko(check: String){
        editor?.putString(isNamaToko,check)
        editor?.commit()
    }

    fun getNamaToko():String?
    {
        return Pref?.getString(isNamaToko,"")
    }

    fun clearSession() {
        val editor = Pref?.edit()
        editor?.clear()
        editor?.apply()
    }

}