package com.karry.ohmychat.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class FirebaseDatabaseInstance {
    private val instance = FirebaseDatabase.getInstance()
    private val firebaseUser = FirebaseAuth.getInstance().currentUser
    private val storageReference = FirebaseStorage.getInstance().getReference("uploads")
}