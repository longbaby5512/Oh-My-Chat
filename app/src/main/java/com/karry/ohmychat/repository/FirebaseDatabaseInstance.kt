package com.karry.ohmychat.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.karry.ohmychat.model.User
import com.karry.ohmychat.utils.Constants

class FirebaseDatabaseInstance {
    private val instance = FirebaseFirestore.getInstance()

    fun addUserInDatabase(user: User): MutableLiveData<DocumentReference?> {
        val successAddUserToDatabase = MutableLiveData<DocumentReference?>()
        instance.collection(Constants.KEY_COLLECTION_USERS).add(user)
            .addOnSuccessListener {
                successAddUserToDatabase.value = it
            }.addOnFailureListener { successAddUserToDatabase.value = null }
        return successAddUserToDatabase
    }

    fun checkLogin(userId: String): MutableLiveData<DocumentSnapshot?> {
        val checkLogin = MutableLiveData<DocumentSnapshot?>()
        instance.collection(Constants.KEY_COLLECTION_USERS)
            .whereEqualTo(Constants.KEY_USER_ID, userId)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful && it.result != null && it.result.documents.size > 0) {
                    Log.d("QuerySnapshot", "${it.result.documents.size}")
                    checkLogin.value = it.result.documents[0]
                } else {
                    checkLogin.value = null
                }
            }
        return checkLogin
    }


}