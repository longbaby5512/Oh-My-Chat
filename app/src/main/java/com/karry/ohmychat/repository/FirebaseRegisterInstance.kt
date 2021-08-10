package com.karry.ohmychat.repository

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseRegisterInstance {
    private val auth = FirebaseAuth.getInstance()
    private var firebaseUser: FirebaseUser? = null
    var firebaseUsers = MutableLiveData<FirebaseUser>()

    fun registerUser(email: String, password: String): MutableLiveData<Task<*>> {
        val taskRegister = MutableLiveData<Task<*>>()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { value: Task<AuthResult?> ->
                firebaseUser = auth.currentUser
                firebaseUsers.value = firebaseUser
                taskRegister.value = value
            }
        return taskRegister
    }
}