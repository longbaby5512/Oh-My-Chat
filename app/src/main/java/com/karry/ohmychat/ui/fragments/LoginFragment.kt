package com.karry.ohmychat.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Toast
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        init()
        getUserSession()
        listener()

        return binding.root
    }

    private fun listener() {
        with(binding) {
            val animator = AlphaAnimation(1f, 0.7f)

            buttonToRegister.setOnClickListener {
                it.startAnimation(animator)
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            buttonToForgetPassword.setOnClickListener {
                it.startAnimation(animator)
                findNavController().navigate(R.id.action_loginFragment_to_resetPasswordFragment)
            }

            buttonLogin.setOnClickListener {
                val buttonClick = AlphaAnimation(1f, 0.8f)
                it.startAnimation(buttonClick)
                onLoginClick()
            }
        }
    }

    private fun onLoginClick() {
        with(binding) {
            emailLoginInputLayout.clearFocus()
            passwordLoginInputLayout.clearFocus()


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

    private fun init() {
        loginViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(LoginViewModel::class.java)
        databaseViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(DatabaseViewModel::class.java)
        preferenceManager = PreferenceManager(requireContext())
    }

    private fun loginUser(email: String, password: String) {
        loginViewModel.loginUser(email, password)
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
                emailLoginInputLayout.isEnabled = false

                passwordLoginInputLayout.isClickable = false
                passwordLoginInputLayout.isEnabled = false

                buttonToRegister.isClickable = false
                buttonToRegister.isEnabled = false

                buttonToForgetPassword.isClickable = false
                buttonToForgetPassword.isEnabled = false

                progressBarLogin.visibility = View.VISIBLE

                buttonLogin.animate().alpha(0.5F).duration = 500L
                buttonLogin.isCheckable = false
                buttonLogin.isEnabled = false

                emailLoginInputLayout.error = ""
                passwordLoginInputLayout.error = ""
            } else {
                emailLoginInputLayout.isClickable = true
                emailLoginInputLayout.isEnabled = true

                passwordLoginInputLayout.isClickable = true
                passwordLoginInputLayout.isEnabled = true

                buttonToRegister.isClickable = true
                buttonToRegister.isEnabled = true

                buttonToForgetPassword.isClickable = true
                buttonToForgetPassword.isEnabled = true

                progressBarLogin.visibility = View.GONE

                buttonLogin.animate().alpha(1F).duration = 500L
                buttonLogin.isCheckable = true
                buttonLogin.isEnabled = true

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