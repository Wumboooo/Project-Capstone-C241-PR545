package com.example.badworddetector.data.preference

import android.content.Context
import android.content.SharedPreferences

class UserPreference(context: Context) {

    private val preferences = context.getSharedPreferences("user_pref", Context.MODE_PRIVATE)

    fun saveUserToken(token: String) {
        preferences.edit().putString("token", token).apply()
    }

    fun getUserToken(): String? {
        return preferences.getString("token", null)
    }

    fun saveUserId(userId: String) {
        preferences.edit().putString("userId", userId).apply()
    }

    fun getUserId(): String? {
        return preferences.getString("userId", null)
    }

    fun clearUserPreference() {
        preferences.edit().remove("token").apply()
        preferences.edit().remove("userId").apply()
    }
}