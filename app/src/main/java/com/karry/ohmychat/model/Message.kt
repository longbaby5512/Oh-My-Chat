package com.karry.ohmychat.model

data class Message(
    val receiverId: String,
    val senderId: String,
    val message: String,
    val timestamp: String,
    val seen: String
)
