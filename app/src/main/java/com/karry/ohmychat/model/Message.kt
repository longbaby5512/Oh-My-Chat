package com.karry.ohmychat.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    val receiverId: String,
    val senderId: String,
    val message: String,
    val timestamp: String,
    val seen: String
) : Parcelable
