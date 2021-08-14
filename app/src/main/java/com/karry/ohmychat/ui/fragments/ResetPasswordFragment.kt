package com.karry.ohmychat.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.PatternsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.karry.ohmychat.databinding.FragmentResetPasswordBinding
import com.karry.ohmychat.utils.dismissKeyboard
import com.karry.ohmychat.utils.showToast
import com.karry.ohmychat.viewmodel.LoginViewModel

class ResetPasswordFragment : Fragment() {
    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)

        init()
        listener()

        return binding.root
    }

    private fun listener() {
        with(binding) {
            buttonBackToLogin.setOnClickListener {
                activity?.onBackPressed()
            }

            buttonReset.setOnClickListener {
                val email = emailResetEditText.text.toString()
                dismissKeyboard(requireActivity())

                emailResetInputLayout.error = ""

                if (email.isEmpty()) {
                    emailResetInputLayout.error = "Please enter your email."
                    emailResetInputLayout.requestFocus()
                } else if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailResetInputLayout.error = "Your text is not email."
                    emailResetInputLayout.requestFocus()
                } else {
                    emailResetInputLayout.isClickable = false
                    buttonReset.animate().alpha(0.5F).duration = 500L
                    progressBarReset.visibility = View.VISIBLE

                    resetPassword(email)
                }
            }
        }
    }

    private fun init() {
        loginViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(LoginViewModel::class.java)
    }

    private fun resetPassword(email: String) {
        loginViewModel.successPasswordReset(email)
        loginViewModel.successPasswordReset.observe(viewLifecycleOwner) {
            if (!it.isSuccessful) {
                with(binding) {
                    emailResetInputLayout.isClickable = true
                    buttonReset.animate().alpha(1F).duration = 500L
                    progressBarReset.visibility = View.GONE
                    emailResetEditText.error = ""
                    emailResetInputLayout.requestFocus()

                    val error = it.exception!!.toString()
                    showToast(requireActivity(), error)
                }
            } else {
                showToast(requireActivity(), "Please check your email.")
                activity?.onBackPressed()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}