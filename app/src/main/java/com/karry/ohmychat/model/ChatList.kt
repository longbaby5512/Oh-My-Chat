package com.karry.ohmychat.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatList(
    val id: String,
    val timestamp: String,
    val latestMessage: String
) : Parcelable
