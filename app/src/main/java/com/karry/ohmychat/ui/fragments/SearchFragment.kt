package com.karry.ohmychat.ui.fragments

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.karry.ohmychat.R
import com.karry.ohmychat.adapter.OnItemClicked
import com.karry.ohmychat.adapter.UserAdapter
import com.karry.ohmychat.databinding.FragmentSearchBinding
import com.karry.ohmychat.model.User
import com.karry.ohmychat.utils.Constants.KEY_USER_ID
import com.karry.ohmychat.utils.PreferenceManager
import com.karry.ohmychat.utils.getColorResource
import com.karry.ohmychat.utils.showToast
import com.karry.ohmychat.viewmodel.DatabaseViewModel


class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseViewModel: DatabaseViewModel
    private lateinit var adapter: UserAdapter
    private lateinit var userList: ArrayList<User>
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        init()
        listener()
        return binding.root
    }

    private fun init() {
        databaseViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(DatabaseViewModel::class.java)
        preferenceManager = PreferenceManager(requireContext())

        userList = ArrayList()
        adapter = UserAdapter(userList)
        binding.userList.adapter = adapter
        databaseViewModel.fetchAllUsers(preferenceManager.getString(KEY_USER_ID)!!)
        databaseViewModel.userArrayList.observe(viewLifecycleOwner) {
            userList.addAll(it)
            binding.progressBarLoadUser.visibility = View.GONE
            if (userList.size == 0) {
                binding.emptyUserTextView.visibility = View.VISIBLE
            } else {
                binding.searchLayout.visibility = View.VISIBLE
                ViewCompat.setNestedScrollingEnabled(binding.userList, false)
            }
        }
        editSearchView()
    }

    private fun listener() {
        adapter.setOnItemClickListener(object : OnItemClicked {
            val animation = AlphaAnimation(1f, 0.8f)
            override fun onItemClicked(v: View, position: Int) {
                super.onItemClicked(v, position)
                v.startAnimation(animation)
                val action = ViewPaperFragmentDirections.actionViewPaperFragmentToMessageFragment(this@SearchFragment.userList[position], 2)
                findNavController().navigate(action)
            }

            override fun onItemLongClicked(v: View, position: Int): Boolean {
                super.onItemLongClicked(v, position)
                v.startAnimation(animation)
                showToast(requireContext(), "On Long Click")
                return true
            }
        })
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
        DrawableCompat.setTint(drawable, getColorResource(requireActivity(), R.color.search_900))
        exitIcon.setImageDrawable(drawable)

        val searchEditText: EditText =
            binding.searchUser.findViewById(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(getColorResource(requireActivity(), R.color.search_900))
        searchEditText.setHintTextColor(getColorResource(requireActivity(), R.color.search_300))
        searchEditText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(R.dimen.query_seach)
        )
    }

    companion object
}