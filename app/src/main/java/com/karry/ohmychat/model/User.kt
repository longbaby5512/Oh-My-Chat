package com.karry.ohmychat.model

import java.util.*

data class User(
    var id: String,
    var name: String,
    var email: String,
    var timestamp: Long,
    var imageBase64: String,
    var bio: String,
    var status: Boolean,
    var search: String = name.lowercase(Locale.getDefault())
)