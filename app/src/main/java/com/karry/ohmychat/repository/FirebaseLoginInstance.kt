package com.karry.ohmychat.repository

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.karry.ohmychat.utils.Token

class FirebaseLoginInstance {
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    val firebaseLoginStatus get(): MutableLiveData<FirebaseUser?> {
        val firebaseUserLoginStatus = MutableLiveData<FirebaseUser?>()
        firebaseUserLoginStatus.value = currentUser
        return firebaseUserLoginStatus
    }

    val firebaseAuth get(): MutableLiveData<FirebaseAuth> {
        val auth = MutableLiveData<FirebaseAuth>()
        auth.value = this.auth
        return auth
    }

    fun successTokenUpdate(newToken: String): MutableLiveData<Boolean> {
        val successTokenUpdate = MutableLiveData<Boolean>()
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Tokens")
        val token = Token(newToken)
        reference.child(firebaseUser!!.uid).setValue(token)
        return successTokenUpdate
    }

    fun loginUser(email: String, password: String): MutableLiveData<Task<*>> {
        val taskLogin = MutableLiveData<Task<*>>()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { value: Task<AuthResult?> -> taskLogin.setValue(value) }
        return taskLogin
    }

    fun resetPassword(email: String): MutableLiveData<Task<*>> {
        val successResetPassword = MutableLiveData<Task<*>>()
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { value: Task<Void?> -> successResetPassword.setValue(value) }
        return successResetPassword
    }
}