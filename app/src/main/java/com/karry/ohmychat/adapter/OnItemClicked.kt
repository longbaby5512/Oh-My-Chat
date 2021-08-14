package com.karry.ohmychat.adapter

import android.view.View

interface OnItemClicked {
    fun onItemClicked(v: View, position: Int) {
    }

    fun onItemLongClicked(v: View, position: Int): Boolean {
        return false
    }
}