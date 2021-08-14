package com.karry.ohmychat.model

data class Message(
    var senderId: String,
    var receiverId: String,
    var message: String,
    var isSeen: Boolean,
    var type: String,
    var timestamp: Long
)
