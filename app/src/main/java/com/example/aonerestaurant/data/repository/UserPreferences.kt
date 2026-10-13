package com.example.aonerestaurant.data.repository

import android.content.Context
import android.content.SharedPreferences

object UserPreferences {
    private const val PREF_NAME = "aone_user_prefs"
    private const val KEY_PROFILE_IMAGE = "key_profile_image"
    private const val KEY_USER_NAME = "key_user_name"
    private const val KEY_USER_PHONE = "key_user_phone"
    private const val KEY_USER_EMAIL = "key_user_email"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserProfileImage(context: Context, imageUri: String) {
        getPrefs(context).edit().putString(KEY_PROFILE_IMAGE, imageUri).apply()
    }

    fun getUserProfileImage(context: Context): String {
        return getPrefs(context).getString(KEY_PROFILE_IMAGE, "") ?: ""
    }

    fun saveUserName(context: Context, name: String) {
        getPrefs(context).edit().putString(KEY_USER_NAME, name).apply()
    }

    fun getUserName(context: Context): String {
        return getPrefs(context).getString(KEY_USER_NAME, "") ?: ""
    }

    fun saveUserPhone(context: Context, phone: String) {
        getPrefs(context).edit().putString(KEY_USER_PHONE, phone).apply()
    }

    fun getUserPhone(context: Context): String {
        return getPrefs(context).getString(KEY_USER_PHONE, "") ?: ""
    }

    fun saveUserEmail(context: Context, email: String) {
        getPrefs(context).edit().putString(KEY_USER_EMAIL, email).apply()
    }

    fun getUserEmail(context: Context): String {
        return getPrefs(context).getString(KEY_USER_EMAIL, "") ?: ""
    }
}
