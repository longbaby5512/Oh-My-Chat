package com.karry.ohmychat.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream


object ImageProcessing {
    fun convert(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    fun convert(base64: String): Bitmap {
        val decodedBytes: ByteArray = Base64.decode(
            base64.substring(base64.indexOf(",") + 1),
            Base64.DEFAULT
        )
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
}