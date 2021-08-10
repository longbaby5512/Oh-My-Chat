package com.karry.ohmychat.ui.fragments

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.karry.ohmychat.R
import com.karry.ohmychat.databinding.FragmentSearchBinding


class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        editSearchView()

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        binding.searchUser.setQuery("", true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun editSearchView() {
        val exitIcon =
            binding.searchUser.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        var drawable =
            ContextCompat.getDrawable(requireActivity().applicationContext, R.drawable.ic_exit)
        drawable = DrawableCompat.wrap(drawable!!)
        DrawableCompat.setTint(drawable, getColorResource(R.color.search_900))
        exitIcon.setImageDrawable(drawable)

        val searchEditText: EditText =
            binding.searchUser.findViewById(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(getColorResource(R.color.search_900))
        searchEditText.setHintTextColor(getColorResource(R.color.search_500))
        searchEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.query_seach))
    }

    private fun getColorResource(@ColorRes color: Int) =
        ContextCompat.getColor(requireActivity().applicationContext, color)

    companion object {
        private const val TAG = "SearchFragment"
    }
}