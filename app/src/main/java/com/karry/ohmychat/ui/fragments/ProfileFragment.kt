package com.karry.ohmychat.ui.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.karry.ohmychat.R
import com.karry.ohmychat.databinding.FragmentProfileBinding
import com.karry.ohmychat.utils.*
import com.karry.ohmychat.utils.Constants.KEY_BIO
import com.karry.ohmychat.utils.Constants.KEY_EMAIL
import com.karry.ohmychat.utils.Constants.KEY_IMAGE
import com.karry.ohmychat.utils.Constants.KEY_NAME
import com.karry.ohmychat.utils.Constants.KEY_USER_ID
import com.karry.ohmychat.viewmodel.DatabaseViewModel
import java.io.FileNotFoundException

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var bitmap: Bitmap
    private lateinit var uri: Uri
    private lateinit var profileImageToolbar: ImageView
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var databaseViewModel: DatabaseViewModel
    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
            uri = it.data!!.data!!
            try {
                val inputStream = requireActivity().contentResolver.openInputStream(uri)
                val newBitmap = BitmapFactory.decodeStream(inputStream)
                setProfileImage(newBitmap)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }
    private val takePhoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
            val newBitmap = it!!.data!!.extras!!.get("data") as Bitmap
            setProfileImage(newBitmap)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        init()
        listener()
        return binding.root
    }

    private fun listener() {
        with(binding) {
            val buttonClick = AlphaAnimation(1f, 0.6f).apply {
                duration = 500L
            }
            buttonSettingProfile.setOnClickListener {
                it.startAnimation(buttonClick)
                buttonSettingProfile.animate().rotationBy(90f).duration = 500L
            }

            buttonEdit.setOnClickListener {
                it.startAnimation(buttonClick)
            }

            myBioProfile.setOnLongClickListener {
                it.startAnimation(buttonClick)
                true
            }
            myProfileImage.setOnClickListener {
                val action = ViewPaperFragmentDirections.actionViewPaperFragmentToPhotoViewFragment(bitmap)
                findNavController().navigate(action)
            }
            myButtonCamera.setOnClickListener {
                it.startAnimation(buttonClick)
                showPopupMenu(it, R.menu.photo_menu)
            }
        }
    }

    private fun init() {
        profileImageToolbar = requireActivity().findViewById(R.id.profile_image_home)
        with(binding) {
            databaseViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(DatabaseViewModel::class.java)
            preferenceManager = PreferenceManager(requireContext())

            myUsername.text = preferenceManager.getString(KEY_NAME)
            bitmap = convert(preferenceManager.getString(KEY_IMAGE)!!)
            myProfileImage.setImageBitmap(bitmap)
            myEmailProfile.text = preferenceManager.getString(KEY_EMAIL)
            myBioProfile.text = preferenceManager.getString(KEY_BIO)


            setIconColor(
                requireActivity(), R.drawable.ic_camera, myButtonCamera, R.color.profiles_900
            )
            setIconColor(requireActivity(), R.drawable.ic_edit, buttonEdit, R.color.profiles_900)
            setIconColor(requireActivity(), R.drawable.ic_setting, buttonSettingProfile, R.color.profiles_900)
            setBackgroundColor(buttonSettingProfile.background!!, getColorResource(requireActivity(), R.color.profiles_200_tran_5A))
            setBackgroundColor(myBioProfile.background!!, getColorResource(requireActivity(), R.color.profiles_200_tran_BA))
            setBackgroundColor(myButtonCamera.background!!, getColorResource(requireActivity(), R.color.profiles_200_tran_5A))
            setBackgroundColor(myEmailProfile.background!!, getColorResource(requireActivity(), R.color.profiles_200_tran_BA))

            if (this@ProfileFragment::bitmap.isInitialized) {
                binding.myProfileImage.setImageBitmap(bitmap)
                profileImageToolbar.setImageBitmap(bitmap)
            }
        }
    }

    private fun setProfileImage(bitmap: Bitmap) {
        databaseViewModel.isUpdated.observe(viewLifecycleOwner) {
            if (it) {
                binding.myProfileImage.setImageBitmap(bitmap)
                profileImageToolbar.setImageBitmap(bitmap)
                preferenceManager.putString(KEY_IMAGE, convert(bitmap, width = 150))
                databaseViewModel.updateImage(preferenceManager.getString(KEY_USER_ID)!!, convert(bitmap, width = 150))
                this.bitmap = bitmap
                showToast(requireContext(), "Upload image done!")
            } else {
                showToast(requireContext(), "Upload image fail!")
            }
        }

    }

    private fun showPopupMenu(v: View, @MenuRes menuRes: Int) {
        val popupMenu = PopupMenu(requireContext(), v)
        popupMenu.menuInflater.inflate(menuRes, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener {
            return@setOnMenuItemClickListener when (it.itemId) {
                R.id.choose_gallery -> {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
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
            menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java).invoke(menu, true)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            popupMenu.show()
        }
    }
}