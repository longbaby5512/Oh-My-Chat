package com.karry.ohmychat.ui.fragments

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.karry.ohmychat.R
import com.karry.ohmychat.databinding.FragmentPhotoViewBinding


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
            binding.photoView.setImageBitmap(args.bitmap)
        }
        setStatusBarColor(getColorResource(R.color.status_bar_color))

        with(binding) {
            setIconColor(R.drawable.ic_exit, buttonExit, R.color.camera_icon_register)
            setBackgroundColor(buttonExit.background!!, getColorResource(R.color.camera_icon_register_background))

            buttonExit.setOnClickListener {
                val buttonClick = AlphaAnimation(1f, 0.8f)
                it.startAnimation(buttonClick)
                activity?.onBackPressed()
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

    private fun setStatusBarColor(@ColorInt color: Int) {
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        requireActivity().window.statusBarColor = color
    }
}