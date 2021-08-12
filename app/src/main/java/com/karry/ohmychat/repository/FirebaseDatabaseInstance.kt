package com.karry.ohmychat.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.karry.ohmychat.model.User
import com.karry.ohmychat.utils.Constants.KEY_BIO
import com.karry.ohmychat.utils.Constants.KEY_COLLECTION_USERS
import com.karry.ohmychat.utils.Constants.KEY_EMAIL
import com.karry.ohmychat.utils.Constants.KEY_FCM_TOKEN
import com.karry.ohmychat.utils.Constants.KEY_IMAGE
import com.karry.ohmychat.utils.Constants.KEY_NAME
import com.karry.ohmychat.utils.Constants.KEY_STATUS
import com.karry.ohmychat.utils.Constants.KEY_TIMESTAMP
import com.karry.ohmychat.utils.Constants.KEY_USER_ID

class FirebaseDatabaseInstance {
    private val database = FirebaseFirestore.getInstance()
    private val message = FirebaseMessaging.getInstance()

    fun addUserInDatabase(user: User): MutableLiveData<DocumentReference?> {
        val successAddUserToDatabase = MutableLiveData<DocumentReference?>()
        database.collection(KEY_COLLECTION_USERS).add(user)
            .addOnSuccessListener {
                successAddUserToDatabase.value = it
            }.addOnFailureListener { successAddUserToDatabase.value = null }
        return successAddUserToDatabase
    }

    fun fetchUser(userId: String): MutableLiveData<User> {
        val currentUser = MutableLiveData<User>()
        database.collection(KEY_COLLECTION_USERS).whereEqualTo(KEY_USER_ID, userId).get()
            .addOnCompleteListener {
                if (it.isSuccessful && it.result != null && it.result.documents.size > 0) {
                    Log.d("QuerySnapshot", "${it.result.documents.size}")
                    val snapshot = it.result.documents[0]
                    val id = snapshot.getString(KEY_USER_ID)
                    val name = snapshot.getString(KEY_NAME)
                    val email = snapshot.getString(KEY_EMAIL)
                    val timestamp = snapshot.getLong(KEY_TIMESTAMP)!!
                    val imageBase64 = snapshot.getString(KEY_IMAGE)!!
                    val bio = snapshot.getString(KEY_BIO)!!
                    val status = snapshot.getBoolean(KEY_STATUS)!!
                    val user = User(id!!, name!!, email!!, timestamp, imageBase64, bio, status)
                    currentUser.value = user
                } else {
                    currentUser.value = null
                }
            }
        return currentUser
    }

    fun updateToken(userId: String, newToken: String): MutableLiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        database.collection(KEY_COLLECTION_USERS).whereEqualTo(KEY_USER_ID, userId).get()
            .addOnCompleteListener {
                if (it.isSuccessful && it.result != null && it.result.documents.size == 1) {
                    val dataId = it.result.documents[0].id
                    database.collection(KEY_COLLECTION_USERS).document(dataId)
                        .update(KEY_FCM_TOKEN, newToken)
                        .addOnSuccessListener { result.value = true }
                        .addOnFailureListener { result.value = false }
                } else {
                    result.value = false
                }
            }.addOnFailureListener { result.value = false }
        return result
    }

    fun updateImage(userId: String, imageBase64: String): MutableLiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        database.collection(KEY_COLLECTION_USERS).whereEqualTo(KEY_USER_ID, userId).get()
            .addOnCompleteListener {
                if (it.isSuccessful && it.result != null && it.result.documents.size == 1) {
                    val dataId = it.result.documents[0].id
                    database.collection(KEY_COLLECTION_USERS).document(dataId)
                        .update(KEY_IMAGE, imageBase64)
                        .addOnSuccessListener { result.value = true }
                        .addOnFailureListener { result.value = false }
                } else {
                    result.value = false
                }
            }.addOnFailureListener { result.value = false }
        return result
    }

    fun updateStatus(userId: String, status: Boolean): MutableLiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        database.collection(KEY_COLLECTION_USERS).whereEqualTo(KEY_USER_ID, userId).get()
            .addOnCompleteListener {
                if (it.isSuccessful && it.result != null && it.result.documents.size == 1) {
                    val dataId = it.result.documents[0].id
                    database.collection(KEY_COLLECTION_USERS).document(dataId)
                        .update(KEY_STATUS, status)
                        .addOnSuccessListener { result.value = true }
                        .addOnFailureListener { result.value = false }
                } else {
                    result.value = false
                }

            }.addOnFailureListener { result.value = false }
        return result
    }

    fun logout(userId: String): MutableLiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        database.collection(KEY_COLLECTION_USERS).whereEqualTo(KEY_USER_ID, userId).get()
            .addOnCompleteListener {
                if (it.isSuccessful && it.result != null && it.result.documents.size == 1) {
                    val dataId = it.result.documents[0].id
                    database.collection(KEY_COLLECTION_USERS).document(dataId)
                        .update(KEY_FCM_TOKEN, FieldValue.delete())
                        .addOnSuccessListener { result.value = true }
                        .addOnFailureListener { result.value = false }
                } else {
                    result.value = false
                }
            }.addOnFailureListener { result.value = false }
        return result
    }
}