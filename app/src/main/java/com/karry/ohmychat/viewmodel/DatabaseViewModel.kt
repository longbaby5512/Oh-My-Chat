package com.karry.ohmychat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.karry.ohmychat.model.User
import com.karry.ohmychat.repository.FirebaseDatabaseInstance

class DatabaseViewModel : ViewModel() {
    private val instance = FirebaseDatabaseInstance()
    lateinit var successAddUserToDatabase: LiveData<DocumentReference?>
    lateinit var user: LiveData<User>
    lateinit var isUpdated: LiveData<Boolean>

    fun addUserInDatabase(user: User) {
        successAddUserToDatabase = instance.addUserInDatabase(user)
    }

    fun fetchUser(userId: String) {
        user = instance.fetchUser(userId)
    }

    fun updateToken(userId: String, newToken: String) {
        isUpdated = instance.updateToken(userId, newToken)
    }

    fun updateImage(userId: String, imageBase64: String) {
        isUpdated = instance.updateImage(userId, imageBase64)
    }

    fun updateStatus(userId: String, status: Boolean) {
        isUpdated = instance.updateStatus(userId, status)
    }

    fun logout(userId: String) {
        isUpdated = instance.logout(userId)
    }
}