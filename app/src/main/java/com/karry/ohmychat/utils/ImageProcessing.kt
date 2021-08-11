package com.karry.ohmychat.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.*
import android.net.Uri
import android.util.Base64
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import java.io.ByteArrayOutputStream
import java.util.*

fun setStatusBarColor(activity: Activity, @ColorInt color: Int) {
    activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    activity.window.statusBarColor = color
}

fun convert(bitmap: Bitmap, compress: Int = 100): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, compress, outputStream)
    return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
}


fun convertToByteArray(bitmap: Bitmap, compress: Int = 100): ByteArray {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, compress, outputStream)
    return outputStream.toByteArray()
}

fun convert(base64: String): Bitmap {
    val decodedBytes: ByteArray = Base64.decode(
        base64.substring(base64.indexOf(",") + 1),
        Base64.DEFAULT
    )
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
}

fun getBitmap(imageView: ImageView): Bitmap {
    return (imageView.drawable as BitmapDrawable).bitmap
}

fun getFileExtension(imageUri: Uri, context: Context): String {
    val contentResolver = Objects.requireNonNull(context).contentResolver
    val mimeTypeMap = MimeTypeMap.getSingleton()
    return mimeTypeMap.getMimeTypeFromExtension(contentResolver.getType(imageUri))!!
}

fun getColorResource(context: Context, @ColorRes color: Int) =
    ContextCompat.getColor(context.applicationContext, color)

fun setIconColor(
    context: Context,
    @DrawableRes iconRes: Int,
    button: ImageView,
    @ColorRes colorRes: Int
) {
    var drawable = ContextCompat.getDrawable(context.applicationContext, iconRes)
    drawable = DrawableCompat.wrap(drawable!!)
    DrawableCompat.setTint(drawable, getColorResource(context, colorRes))
    button.setImageDrawable(drawable)
}

fun setBackgroundColor(background: Drawable, @ColorInt color: Int) {
    when (background) {
        is ShapeDrawable -> background.paint.color = color
        is GradientDrawable -> background.setColor(color)
        is ColorDrawable -> background.color = color
        else -> return
    }
}
