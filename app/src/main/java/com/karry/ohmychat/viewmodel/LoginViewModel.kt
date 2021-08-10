package com.karry.ohmychat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.karry.ohmychat.repository.FirebaseLoginInstance

class LoginViewModel: ViewModel() {
    private var loginInstance = FirebaseLoginInstance()
    lateinit var loginUser: LiveData<Task<*>>
    val firebaseUserLoginStatus get(): LiveData<FirebaseUser?> = loginInstance.firebaseLoginStatus
    val firebaseAuth get(): LiveData<FirebaseAuth> = loginInstance.firebaseAuth
    lateinit var successPasswordReset: LiveData<Task<*>>
    lateinit var updateToken: LiveData<Boolean>



    fun loginUser(email: String, password: String) {
        loginUser = loginInstance.loginUser(email, password)
    }


    fun successPasswordReset(email: String) {
        successPasswordReset = loginInstance.resetPassword(email)
    }

    fun updateToken(newToken: String) {
        updateToken = loginInstance.successUpdateToken(newToken)
    }
}