package com.karry.ohmychat.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val image: String,
    val bio: String,
    val status: String
)
