package com.karry.ohmychat.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.karry.ohmychat.R
import com.karry.ohmychat.databinding.ActivityMainBinding
import com.karry.ohmychat.ui.fragments.ViewPaperFragment
import com.karry.ohmychat.utils.showToast
import com.karry.ohmychat.viewmodel.DatabaseViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var controller: NavController
    private lateinit var databaseViewModel: DatabaseViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment

        controller = fragment.navController

        databaseViewModel = ViewModelProvider(
            this, ViewModelProvider
                .AndroidViewModelFactory.getInstance(application)
        ).get(DatabaseViewModel::class.java)

        getToken()

        setupActionBarWithNavController(controller)
    }

    override fun onSupportNavigateUp(): Boolean {
        return controller.navigateUp() || super.onSupportNavigateUp()
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.d("TOKEN", it)
            databaseViewModel.updateToken(ViewPaperFragment.currentUser.id, it)
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
            databaseViewModel.updateStatus(
                it.uid,
                status
            )
        }
    }

    override fun onResume() {
        super.onResume()
        status(true)
    }

    override fun onPause() {
        super.onPause()
        status(false)
    }
}