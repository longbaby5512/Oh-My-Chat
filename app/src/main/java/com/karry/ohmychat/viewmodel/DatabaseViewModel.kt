package com.karry.ohmychat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.karry.ohmychat.model.Message
import com.karry.ohmychat.model.User
import com.karry.ohmychat.repository.FirebaseDatabaseInstance

class DatabaseViewModel : ViewModel() {
    private val instance = FirebaseDatabaseInstance()
    lateinit var documentReference: LiveData<DocumentReference>
    lateinit var user: LiveData<User>
    lateinit var isUpdated: LiveData<Boolean>
    lateinit var userArrayList: LiveData<ArrayList<User>>
    lateinit var messageList: LiveData<ArrayList<Message>>
    lateinit var documentSnapshot: LiveData<DocumentSnapshot>
    lateinit var documentChangeList: LiveData<List<DocumentChange>>

    fun addUserInDatabase(user: User) {
        isUpdated = instance.addUserInDatabase(user)
    }

    fun fetchUser(userId: String) {
        user = instance.fetchUser(userId)
    }

    fun fetchAllUsers(userId: String) {
        userArrayList = instance.fetchAllUsers(userId)
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

    fun sendMessage(message: HashMap<String, Any>) {
        isUpdated = instance.sendMessage(message)
    }

    fun eventListenerMessageSend(senderId: String, receiverId: String) {
        messageList = instance.eventListenerMessageSend(senderId, receiverId)
    }

    fun checkForConversionRemotely(senderId: String, receiverId: String) {
        this.documentSnapshot = instance.checkForConversionRemotely(senderId, receiverId)
    }

    fun addConversion(conversion: HashMap<String, Any>) {
        this.documentReference = instance.addConversion(conversion)
    }

    fun updateConversion(conversionId: String, message: String) {
        isUpdated = instance.updateConversion(conversionId, message)
    }

    fun fetchConversions(key: String, userId: String) {
        documentChangeList = instance.fetchConversions(key, userId)
    }

    fun listenerReceiver(userId: String) {
        user = instance.listenerReceiver(userId)
    }
}