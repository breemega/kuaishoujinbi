package com.example.kuaishouautogold.login

import android.content.Context
import android.content.SharedPreferences

class AccountManager(private val context: Context) {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("account", Context.MODE_PRIVATE)
    }

    data class Account(val phone: String, val password: String)

    fun saveAccount(phone: String, password: String) {
        sharedPreferences.edit()
            .putString("phone", phone)
            .putString("password", password)
            .apply()
    }

    fun getAccount(): Account {
        val phone = sharedPreferences.getString("phone", "") ?: ""
        val password = sharedPreferences.getString("password", "") ?: ""
        return Account(phone, password)
    }

    fun clearAccount() {
        sharedPreferences.edit()
            .remove("phone")
            .remove("password")
            .apply()
    }
}