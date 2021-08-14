package com.karry.ohmychat.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class User(
    var id: String,
    var name: String,
    var email: String,
    var timestamp: Long,
    var imageBase64: String,
    var bio: String,
    var status: Boolean,
    var search: String = name.lowercase(Locale.getDefault()),
    var fcmToken: String? = null
) : Parcelable