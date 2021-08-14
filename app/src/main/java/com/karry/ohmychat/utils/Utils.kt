package com.karry.ohmychat.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.pm.PackageManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun dismissKeyboard(activity: Activity) {
    val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = activity.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun checkAndRequestPermissions(activity: Activity): Boolean {
    val wExternalPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val cameraPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)

    val listPermissionsNeeded: MutableList<String> = ArrayList()
    if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
        listPermissionsNeeded.add(Manifest.permission.CAMERA)
    }
    if (wExternalPermission != PackageManager.PERMISSION_GRANTED) {
        listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
    if (listPermissionsNeeded.isNotEmpty()) {
        ActivityCompat.requestPermissions(
            activity, listPermissionsNeeded
                .toTypedArray(),
            Constants.REQUEST_ID_MULTIPLE_PERMISSIONS
        )
        return false
    }
    return true
}

