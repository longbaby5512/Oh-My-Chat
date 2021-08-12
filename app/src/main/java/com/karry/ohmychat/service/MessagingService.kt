package com.karry.ohmychat.service

import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.karry.ohmychat.viewmodel.LoginViewModel

class MessagingService : FirebaseMessagingService() {

    private val loginViewModel =
        ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(
            LoginViewModel::class.java
        )

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        val auth = FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser

        if (null != firebaseUser) {
            Log.d(TAG, "onNewToken: Token is $newToken")
        }

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "onMessageReceived: Title is \"${remoteMessage.notification!!.title}\"")
        Log.d(TAG, "onMessageReceived: Message is \"${remoteMessage.notification!!.body}\"")
    }

    companion object {
        private const val TAG = "FCM"
    }
}