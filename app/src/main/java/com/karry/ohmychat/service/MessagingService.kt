package com.karry.ohmychat.service

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.karry.ohmychat.viewmodel.LoginViewModel

class MessagingService : FirebaseMessagingService(), LifecycleOwner {

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
            updateToken(newToken)
        }

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "onMessageReceived: Title is \"${remoteMessage.notification!!.title}\"")
        Log.d(TAG, "onMessageReceived: Message is \"${remoteMessage.notification!!.body}\"")
    }

    private fun updateToken(newToken: String) {
        loginViewModel.updateToken(newToken)
        loginViewModel.updateToken.observe(this) {
            if (it) {
                Log.d(TAG, "updateToken: Update token successful")
            } else {
                Log.e(TAG, "updateToken: Update token fail")
            }
        }
    }

    override fun getLifecycle(): Lifecycle {
        return this.lifecycle
    }

    companion object {
        private const val TAG = "FCM"
    }
}