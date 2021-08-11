package com.karry.ohmychat.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.karry.ohmychat.R
import com.karry.ohmychat.databinding.FragmentPhotoViewBinding
import com.karry.ohmychat.utils.getColorResource
import com.karry.ohmychat.utils.setBackgroundColor
import com.karry.ohmychat.utils.setIconColor
import com.karry.ohmychat.utils.setStatusBarColor


class PhotoViewFragment : Fragment() {
    private val args: PhotoViewFragmentArgs by navArgs()

    private var _binding: FragmentPhotoViewBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentPhotoViewBinding.inflate(inflater, container, false)

        if (args.bitmap != null) {
            binding.photoView.setImageBitmap(args.bitmap!!)
        }
        setStatusBarColor(
            requireActivity(),
            getColorResource(requireActivity(), R.color.status_bar_color)
        )

        with(binding) {
            setIconColor(
                requireActivity(),
                R.drawable.ic_exit,
                buttonExit,
                R.color.camera_icon_register
            )
            setBackgroundColor(
                buttonExit.background!!,
                getColorResource(requireActivity(), R.color.camera_icon_register_background)
            )

            buttonExit.setOnClickListener {
                val buttonClick = AlphaAnimation(1f, 0.8f)
                it.startAnimation(buttonClick)
                requireActivity().onBackPressed()
            }

            photoView.setOnClickListener {
                if (buttonExit.isClickable) {
                    buttonExit.animate().alpha(0f).duration = 500L
                    buttonExit.isClickable = false
                } else {
                    buttonExit.animate().alpha(1f).duration = 500L
                    buttonExit.isClickable = true
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

