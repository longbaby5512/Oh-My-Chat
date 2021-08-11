package com.karry.ohmychat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.StorageReference
import com.karry.ohmychat.model.User
import com.karry.ohmychat.repository.FirebaseDatabaseInstance

class DatabaseViewModel : ViewModel() {
    private val instance = FirebaseDatabaseInstance()
    lateinit var successAddUserToDatabase: LiveData<DocumentReference?>
    lateinit var userSnapshot: LiveData<DocumentSnapshot?>
    lateinit var imageReference: LiveData<StorageReference>

    fun addUserInDatabase(user: User) {
        successAddUserToDatabase = instance.addUserInDatabase(user)
    }

    fun checkLogin(userId: String) {
        userSnapshot = instance.checkLogin(userId)
    }
}