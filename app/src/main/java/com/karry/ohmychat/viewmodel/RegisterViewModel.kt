package com.karry.ohmychat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.karry.ohmychat.repository.FirebaseRegisterInstance

class RegisterViewModel: ViewModel() {
    private var registerInstance = FirebaseRegisterInstance()
    lateinit var registerUser: LiveData<Task<*>>
    val userFirebaseSession get(): LiveData<FirebaseUser> = registerInstance.firebaseUsers

    fun registerUser(email: String, password: String) {
        registerUser = registerInstance.registerUser(email, password)
    }
}