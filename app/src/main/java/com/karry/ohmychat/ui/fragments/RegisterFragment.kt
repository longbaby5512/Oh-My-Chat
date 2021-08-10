package com.karry.ohmychat.ui.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.karry.ohmychat.R
import com.karry.ohmychat.databinding.FragmentRegisterBinding
import com.karry.ohmychat.model.User
import com.karry.ohmychat.ui.activities.MainActivity
import com.karry.ohmychat.utils.*
import java.io.FileNotFoundException


class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private var bitmap: Bitmap? = null
    private var uri: Uri? = null

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        bitmap = null
        uri = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        if (bitmap != null) {
            binding.profileImageRegister.setImageBitmap(bitmap)
        }

        with(binding) {
            val buttonClick = AlphaAnimation(1f, 0.8f)

            setIconColor(R.drawable.ic_camera, buttonUpImage, R.color.camera_icon_register)
            setBackgroundColor(buttonUpImage.background!!, getColorResource(R.color.camera_icon_register_background))


            buttonToLogin.setOnClickListener {
                activity?.onBackPressed()
            }

            profileImageRegister.setOnClickListener {
                val action = RegisterFragmentDirections.actionRegisterFragmentToPhotoViewFragment(bitmap)
                findNavController().navigate(action)
            }

            buttonRegister.setOnClickListener {
                usernameSignupInputLayout.clearFocus()
                emailSignupInputLayout.clearFocus()
                passwordSignupInputLayout.clearFocus()
                it.startAnimation(buttonClick)

                val email = emailSignupEditText.text.toString()
                val name = usernameSignupEditText.text.toString()
                val password = passwordSignupEditText.text.toString()
                val passwordConfirm = confirmSignupEditText.text.toString()

                usernameSignupInputLayout.error = ""
                emailSignupInputLayout.error = ""
                passwordSignupInputLayout.error = ""
                confirmSignupInputLayout.error = ""

                if (email.isEmpty() && name.isEmpty() && password.isEmpty() && passwordConfirm.isEmpty()) {
                    showToast(requireContext(), "Fields are empty!")
                    usernameSignupInputLayout.requestFocus()
                } else if (name.isEmpty()) {
                    usernameSignupInputLayout.error = "Please enter a username."
                    usernameSignupInputLayout.requestFocus()
                } else if (email.isEmpty()) {
                    emailSignupInputLayout.error = "Please enter your email."
                    emailSignupInputLayout.requestFocus()
                } else if(!EMAIL_ADDRESS.matcher(email).matches()) {
                    emailSignupInputLayout.error = "Your text is not email."
                    emailSignupInputLayout.requestFocus()
                }else if (password.isEmpty()) {
                    passwordSignupInputLayout.error = "Please enter your password"
                    passwordSignupInputLayout.requestFocus()
                } else if (passwordConfirm.isEmpty()) {
                    confirmSignupInputLayout.error = "Please enter your password"
                    confirmSignupInputLayout.requestFocus()
                } else if(password != passwordConfirm) {
                    confirmSignupInputLayout.error = "Password confirm and password are not similar"
                    confirmSignupInputLayout.requestFocus()
                } else {
                    progressBarSignup.visibility = View.VISIBLE
                    usernameSignupInputLayout.isClickable = false
                    emailSignupInputLayout.isClickable = false
                    passwordSignupInputLayout.isClickable = false
                    buttonRegister.isClickable = false
                    dismissKeyboard(requireActivity())
                    val imageBitmap = (profileImageRegister.drawable as BitmapDrawable).bitmap
                    val imageBase64 = ImageProcessing.convert(imageBitmap)

                    val user = User(0.toString(), name, email, password, imageBase64)
                    register(user)
                }
            }



            buttonRegister.setOnClickListener {
                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }

            buttonUpImage.setOnClickListener {
                it.startAnimation(buttonClick)
                showPopupMenu(it, R.menu.photo_menu)
            }
        }

        return binding.root
    }


    private fun register(user: User) {
        showToast(requireContext(), "Register")
    }

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
            uri = it.data!!.data!!
            try {
                val inputStream = requireActivity().contentResolver.openInputStream(uri!!)
                bitmap = BitmapFactory.decodeStream(inputStream)
                binding.profileImageRegister.setImageBitmap(bitmap)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
            bitmap = it!!.data!!.extras!!.get("data") as Bitmap
            binding.profileImageRegister.setImageBitmap(bitmap)
        }
    }

    private fun showPopupMenu(v: View, @MenuRes menuRes: Int) {
        val popupMenu = PopupMenu(requireContext(), v)
        popupMenu.menuInflater.inflate(menuRes, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener {
            return@setOnMenuItemClickListener when(it.itemId) {
                R.id.choose_gallery -> {
                    val intent = Intent(
                        Intent.ACTION_PICK, MediaStore.Images.Media
                            .EXTERNAL_CONTENT_URI).apply {
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    pickImage.launch(intent)
                    true
                }
                R.id.take_photo -> {
                    if (checkAndRequestPermissions(requireActivity())) {
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        takePhoto.launch(intent)
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

    private fun getColorResource(@ColorRes color: Int) =
        ContextCompat.getColor(requireActivity().applicationContext, color)

    private fun setIconColor(@DrawableRes iconRes: Int, button: ImageView, @ColorRes colorRes: Int) {
        var drawable = ContextCompat.getDrawable(requireActivity().applicationContext, iconRes)
        drawable = DrawableCompat.wrap(drawable!!)
        DrawableCompat.setTint(drawable, getColorResource(colorRes))
        button.setImageDrawable(drawable)
    }

    private fun setBackgroundColor(background: Drawable, @ColorInt color: Int) {
        if (background is ShapeDrawable) {
            background.paint.color = color
        } else if (background is GradientDrawable) {
            background.setColor(color)
        } else if (background is ColorDrawable) {
            background.color = color
        }
    }

}