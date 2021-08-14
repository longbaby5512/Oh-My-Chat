package com.karry.ohmychat.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.karry.ohmychat.databinding.ActivityMainBinding
import com.karry.ohmychat.utils.Constants.KEY_STATUS
import com.karry.ohmychat.utils.Constants.KEY_USER_ID
import com.karry.ohmychat.utils.PreferenceManager
import com.karry.ohmychat.utils.showToast
import com.karry.ohmychat.viewmodel.DatabaseViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var databaseViewModel: DatabaseViewModel
    private lateinit var preferenceManager: PreferenceManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        getToken()
    }

    private fun init() {
        databaseViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(DatabaseViewModel::class.java)
        preferenceManager = PreferenceManager(this)
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.d("TOKEN", it)
            Log.d(KEY_USER_ID, preferenceManager.getString(KEY_USER_ID)!!)
            databaseViewModel.updateToken(preferenceManager.getString(KEY_USER_ID)!!, it)
            databaseViewModel.isUpdated.observe(this) { result ->
                if (!result) {
                    showToast(this, "Can't update token")
                }
            }
        }.addOnFailureListener {
            showToast(this, "Can't update token")
        }
    }

    private fun status(status: Boolean) {
        FirebaseAuth.getInstance().currentUser?.let {
            databaseViewModel.updateStatus(it.uid, status)
        }
        preferenceManager.putBoolean(KEY_STATUS, status)
    }

    override fun onResume() {
        super.onResume()
        status(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        status(false)
    }
}