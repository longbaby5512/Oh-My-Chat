package com.karry.ohmychat.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.PatternsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.karry.ohmychat.R
import com.karry.ohmychat.databinding.FragmentLoginBinding
import com.karry.ohmychat.model.User
import com.karry.ohmychat.ui.activities.MainActivity
import com.karry.ohmychat.utils.PreferenceManager
import com.karry.ohmychat.utils.dismissKeyboard
import com.karry.ohmychat.utils.showToast
import com.karry.ohmychat.viewmodel.DatabaseViewModel
import com.karry.ohmychat.viewmodel.LoginViewModel


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var currentUser: FirebaseUser
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var databaseViewModel: DatabaseViewModel


    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        loginViewModel = ViewModelProvider(
            requireActivity(), ViewModelProvider
                .AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(LoginViewModel::class.java)

        databaseViewModel = ViewModelProvider(
            requireActivity(), ViewModelProvider
                .AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(DatabaseViewModel::class.java)

        preferenceManager = PreferenceManager(requireContext())

        getUserSession()

        with(binding) {
            buttonToRegister.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            buttonToForgetPassword.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_resetPasswordFragment)
            }

            buttonLogin.setOnClickListener {
                emailLoginInputLayout.clearFocus()
                passwordLoginInputLayout.clearFocus()
                val buttonClick = AlphaAnimation(1f, 0.8f).apply {
                    duration = 500L
                }
                it.startAnimation(buttonClick)
                dismissKeyboard(requireActivity())

                val email = emailLoginEditText.text.toString()
                val password = passwordLoginEditText.text.toString()

                if (password.isEmpty() && email.isEmpty()) {
                    Toast.makeText(requireContext(), "Fields are empty!", Toast.LENGTH_SHORT).show()
                    emailLoginInputLayout.requestFocus()
                } else if (email.isEmpty()) {
                    emailLoginInputLayout.error = "Please enter your Email Id."
                    emailLoginInputLayout.requestFocus()
                } else if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailLoginInputLayout.error = "Your text is not email."
                    emailLoginInputLayout.requestFocus()
                } else if (password.isEmpty()) {
                    passwordLoginEditText.error = "Please enter your password."
                    passwordLoginEditText.requestFocus()
                } else {
                    loading(true)
                    loginUser(email, password)
                }
            }


        }

        return binding.root
    }

    private fun loginUser(email: String, password: String) {
        loginViewModel.loginUser(email, password)
        var user: User? = null
        loginViewModel.loginUser.observe(viewLifecycleOwner) { task ->
            if (!task.isSuccessful) {
                loading(false)
                binding.emailLoginInputLayout.requestFocus()
                try {
                    throw task.exception!!
                } catch (invalidEmail: FirebaseAuthInvalidUserException) {
                    Toast.makeText(
                        context,
                        "Invalid credentials, please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (wrongPassword: FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(
                        context,
                        "Wrong password or username , please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Check Internet Connection.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val userId = FirebaseAuth.getInstance().currentUser!!.uid
                Log.d("Login", "Current uid")

                databaseViewModel.fetchUser(userId)
                databaseViewModel.user.observe(viewLifecycleOwner) {
                    if (it != null) {
                        preferenceManager.putUser(it)

                        val intent = Intent(requireActivity(), MainActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        }
                        showToast(requireActivity(), "Login successfully!")
                        startActivity(intent)
                        requireActivity().finish()
                    } else {
                        loading(false)
                        binding.emailLoginInputLayout.requestFocus()
                        FirebaseAuth.getInstance().signOut()
                        showToast(requireActivity(), "Can't fetch data from database")
                    }
                }
            }
        }
    }

    private fun getUserSession() {
        loginViewModel.firebaseUserLoginStatus.observe(viewLifecycleOwner) {
            if(it != null) {
                currentUser = it
                val intent = Intent(requireActivity(), MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    private fun loading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                emailLoginInputLayout.isClickable = false
                passwordLoginInputLayout.isClickable = false
                buttonToRegister.isClickable = false
                buttonToForgetPassword.isClickable = false
                progressBarLogin.visibility = View.VISIBLE
                buttonLogin.animate().alpha(0.5F).duration = 500L
                buttonLogin.isCheckable = false

                emailLoginInputLayout.error = ""
                passwordLoginInputLayout.error = ""
            } else {
                emailLoginInputLayout.isClickable = true
                passwordLoginInputLayout.isClickable = true
                buttonToRegister.isClickable = true
                buttonToForgetPassword.isClickable = true
                progressBarLogin.visibility = View.GONE
                buttonLogin.animate().alpha(1F).duration = 500L
                buttonLogin.isCheckable = true

                emailLoginEditText.setText("")
                passwordLoginEditText.setText("")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}