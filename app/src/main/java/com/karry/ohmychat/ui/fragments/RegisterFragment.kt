package com.karry.ohmychat.ui.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.*
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.*
import com.karry.ohmychat.R
import com.karry.ohmychat.databinding.FragmentRegisterBinding
import com.karry.ohmychat.model.User
import com.karry.ohmychat.ui.activities.MainActivity
import com.karry.ohmychat.utils.*
import com.karry.ohmychat.viewmodel.DatabaseViewModel
import com.karry.ohmychat.viewmodel.RegisterViewModel
import java.io.FileNotFoundException
import java.util.*


class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var bitmap: Bitmap
    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var currentUser: FirebaseUser
    private lateinit var databaseViewModel: DatabaseViewModel
    private lateinit var preferenceManager: PreferenceManager
    private val timestamp = System.currentTimeMillis()
    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
            val uri = it.data!!.data!!
            try {
                val inputStream = requireActivity().contentResolver.openInputStream(uri)
                bitmap = BitmapFactory.decodeStream(inputStream)
                binding.profileImageRegister.setImageBitmap(bitmap)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }
    private val takePhoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
            bitmap = it.data!!.extras!!.get("data") as Bitmap
            binding.profileImageRegister.setImageBitmap(bitmap)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        init()
        listener()

        return binding.root
    }

    private fun listener() {
        with(binding) {
            val buttonClick = AlphaAnimation(1f, 0.8f)
            buttonToLogin.setOnClickListener {
                it.startAnimation(buttonClick)
                requireActivity().onBackPressed()
            }

            profileImageRegister.setOnClickListener {
                val action = RegisterFragmentDirections.actionRegisterFragmentToPhotoViewFragment(bitmap)
                findNavController().navigate(action)
            }

            buttonRegister.setOnClickListener {
                onRegisterClick()
            }

            buttonUpImage.setOnClickListener {
                it.startAnimation(buttonClick)
                showPopupMenu(it, R.menu.photo_menu)
            }
        }
    }

    private fun onRegisterClick() {
        with(binding) {
            usernameRegisterInputLayout.clearFocus()
            emailRegisterInputLayout.clearFocus()
            passwordRegisterInputLayout.clearFocus()

            val email = emailRegisterEditText.text.toString()
            val name = usernameRegisterEditText.text.toString()
            val password = passwordRegisterEditText.text.toString()
            val passwordConfirm = confirmRegisterEditText.text.toString()

            if (email.isEmpty() && name.isEmpty() && password.isEmpty() && passwordConfirm.isEmpty()) {
                showToast(requireContext(), "Fields are empty!")
                usernameRegisterInputLayout.requestFocus()
            } else if (name.isEmpty()) {
                usernameRegisterInputLayout.error = "Please enter a username."
                usernameRegisterInputLayout.requestFocus()
            } else if (email.isEmpty()) {
                emailRegisterInputLayout.error = "Please enter your email."
                emailRegisterInputLayout.requestFocus()
            } else if (!EMAIL_ADDRESS.matcher(email).matches()) {
                emailRegisterInputLayout.error = "Your text is not email."
                emailRegisterInputLayout.requestFocus()
            } else if (password.isEmpty()) {
                passwordRegisterInputLayout.error = "Please enter your password"
                passwordRegisterInputLayout.requestFocus()
            } else if (passwordConfirm.isEmpty()) {
                confirmRegisterInputLayout.error = "Please enter your password"
                confirmRegisterInputLayout.requestFocus()
            } else if (password != passwordConfirm) {
                confirmRegisterInputLayout.error = "Password confirm and password are not similar"
                confirmRegisterInputLayout.requestFocus()
            } else {
                dismissKeyboard(requireActivity())
                loading(true)
                register(name, email, password)
            }
        }
    }

    private fun init() {
        if (this@RegisterFragment::bitmap.isInitialized) {
            binding.profileImageRegister.setImageBitmap(bitmap)
        }

        registerViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(RegisterViewModel::class.java)
        databaseViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(DatabaseViewModel::class.java)
        preferenceManager = PreferenceManager(requireContext())

        with(binding) {
            setIconColor(requireActivity(), R.drawable.ic_camera, buttonUpImage, R.color.camera_icon_register)
            setBackgroundColor(buttonUpImage.background!!, getColorResource(requireActivity(), R.color.camera_icon_register_background))
        }
    }

    private fun register(name: String, email: String, password: String) {
        registerViewModel.registerUser(email, password)
        registerViewModel.registerUser.observe(viewLifecycleOwner) {
            if (!it.isSuccessful) {
                loading(false)
                binding.usernameRegisterInputLayout.requestFocus()
                try {
                    throw it.exception!!
                } catch (existEmail: FirebaseAuthUserCollisionException) {
                    showToast(requireActivity(), "Email already exists.")
                } catch (weakPassword: FirebaseAuthWeakPasswordException) {
                    showToast(
                        requireActivity(),
                        "Password length should be more then six characters."
                    )
                } catch (malformedEmail: FirebaseAuthInvalidCredentialsException) {
                    showToast(requireActivity(), "Invalid credentials, please try again.")
                } catch (e: java.lang.Exception) {
                    showToast(requireActivity(), "Register unsuccessful. Try again.")
                }
            } else {
                getUserSession()

                val id = currentUser.uid

                val user = User(
                    id = id,
                    name = name,
                    email = email,
                    timestamp = timestamp,
                    imageBase64 = convert(getBitmap(binding.profileImageRegister), 50),
                    bio = "Hey there!",
                    status = false
                )
                addUserToDatabase(user, email, password)
            }
        }
    }

    private fun addUserToDatabase(user: User, email: String, password: String) {
        databaseViewModel.addUserInDatabase(user)
        databaseViewModel.isUpdated.observe(viewLifecycleOwner) {
            if (!it) {
                loading(false)
                binding.usernameRegisterInputLayout.requestFocus()
                showToast(requireActivity(), "Can't add user to database")
                val credential = EmailAuthProvider.getCredential(email, password)
                currentUser.reauthenticate(credential).addOnCompleteListener {
                    currentUser.delete().addOnSuccessListener {
                        Log.d("Delete account", "Delete account authentication")
                    }
                }
                FirebaseAuth.getInstance().signOut()
            } else {
                preferenceManager.putUser(user)

                showToast(requireActivity(), "Register successful!")

                val intent = Intent(requireActivity(), MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    private fun getUserSession() {
        registerViewModel.userFirebaseSession.observe(viewLifecycleOwner) {
            currentUser = it
        }
    }

    private fun loading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                progressBarRegister.visibility = View.VISIBLE

                usernameRegisterInputLayout.isClickable = false
                usernameRegisterInputLayout.isEnabled = false

                emailRegisterInputLayout.isClickable = false
                emailRegisterInputLayout.isEnabled = false

                passwordRegisterInputLayout.isClickable = false
                passwordRegisterInputLayout.isClickable = false

                buttonRegister.isClickable = false
                buttonRegister.isCheckable = false
                buttonRegister.animate().alpha(0.5F).duration = 500L

                buttonToLogin.isClickable = false
                buttonToLogin.isEnabled = false

                profileImageRegister.isClickable = false
                profileImageRegister.isEnabled = false

                buttonUpImage.isClickable = false
                buttonUpImage.isEnabled = false

                usernameRegisterInputLayout.error = ""
                emailRegisterInputLayout.error = ""
                passwordRegisterInputLayout.error = ""
                confirmRegisterInputLayout.error = ""
            } else {
                progressBarRegister.visibility = View.GONE

                usernameRegisterInputLayout.isClickable = true
                usernameRegisterInputLayout.isEnabled = true

                emailRegisterInputLayout.isClickable = true
                emailRegisterInputLayout.isEnabled = true

                passwordRegisterInputLayout.isClickable = true
                passwordRegisterInputLayout.isClickable = true

                buttonRegister.isClickable = true
                buttonRegister.isCheckable = true
                buttonRegister.animate().alpha(1F).duration = 500L

                buttonToLogin.isClickable = true
                buttonToLogin.isEnabled = true

                profileImageRegister.isClickable = true
                profileImageRegister.isEnabled = true

                buttonUpImage.isClickable = true
                buttonUpImage.isEnabled = true

                usernameRegisterEditText.setText("")
                emailRegisterEditText.setText("")
                passwordRegisterEditText.setText("")
                confirmRegisterEditText.setText("")

                profileImageRegister.setImageResource(R.drawable.profile)

            }
        }
    }

    private fun showPopupMenu(v: View, @MenuRes menuRes: Int) {
        val popupMenu = PopupMenu(requireContext(), v)
        popupMenu.menuInflater.inflate(menuRes, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener {
            return@setOnMenuItemClickListener when (it.itemId) {
                R.id.choose_gallery -> {
                    Intent(
                        Intent.ACTION_PICK, MediaStore.Images.Media
                            .EXTERNAL_CONTENT_URI
                    ).apply {
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        pickImage.launch(this)
                    }

                    true
                }
                R.id.take_photo -> {
                    if (checkAndRequestPermissions(requireActivity())) {
                        Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                            resolveActivity(requireActivity().packageManager).also {
                                takePhoto.launch(this)
                            }
                        }
                    }
                    true
                }
                else -> false
            }
        }

        try {
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenu)
            menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(menu, true)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            popupMenu.show()
        }
    }


}