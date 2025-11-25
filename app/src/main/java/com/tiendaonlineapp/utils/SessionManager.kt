package com.tiendaonlineapp.utils

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    fun saveUser(id: Int, email: String) {
        prefs.edit()
            .putInt("user_id", id)
            .putString("user_email", email)
            .apply()
    }

    fun getUserId(): Int = prefs.getInt("user_id", -1)

    fun logout() {
        prefs.edit().clear().apply()
    }
}
