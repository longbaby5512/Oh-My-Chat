package com.karry.ohmychat.utils

import android.content.Context
import com.karry.ohmychat.model.User
import com.karry.ohmychat.utils.Constants.KEY_BIO
import com.karry.ohmychat.utils.Constants.KEY_EMAIL
import com.karry.ohmychat.utils.Constants.KEY_IMAGE
import com.karry.ohmychat.utils.Constants.KEY_NAME
import com.karry.ohmychat.utils.Constants.KEY_STATUS
import com.karry.ohmychat.utils.Constants.KEY_TIMESTAMP
import com.karry.ohmychat.utils.Constants.KEY_USER_ID

class PreferenceManager(context: Context) {
    private val sharePreferences = context.getSharedPreferences(
        Constants.KEY_PREFERENCE_NAME,
        Context.MODE_PRIVATE
    )

    fun putUser(value: User) {
        val editor = sharePreferences.edit()
        editor.putString(KEY_USER_ID, value.id)
        editor.putString(KEY_NAME, value.name)
        editor.putString(KEY_EMAIL, value.email)
        editor.putString(KEY_IMAGE, value.imageBase64)
        editor.putString(KEY_BIO, value.bio)
        editor.putLong(KEY_TIMESTAMP, value.timestamp)
        editor.putBoolean(KEY_STATUS, value.status)
        editor.apply()
    }

    fun getUser(): User {
        val id = sharePreferences.getString(KEY_USER_ID, "")
        val name = sharePreferences.getString(KEY_NAME, "")
        val email = sharePreferences.getString(KEY_EMAIL, "")
        val image = sharePreferences.getString(KEY_IMAGE, "")
        val timestamp = sharePreferences.getLong(KEY_TIMESTAMP, 0)
        val bio = sharePreferences.getString(KEY_BIO, "")
        val status = sharePreferences.getBoolean(KEY_STATUS, false)
        return User(id!!, name!!, email!!, timestamp, image!!, bio!!, status)
    }

    fun getString(key: String) = sharePreferences.getString(key, "")

    fun putString(key: String, value: String) {
        val editor = sharePreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getLong(key: String) = sharePreferences.getLong(key, 0)

    fun putLong(key: String, value: Long) {
        val editor = sharePreferences.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun getBoolean(key: String) = sharePreferences.getBoolean(key, false)

    fun putBoolean(key: String, value: Boolean) {
        val editor = sharePreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun clear() {
        val editor = sharePreferences.edit()
        editor.clear()
        editor.apply()
    }
}