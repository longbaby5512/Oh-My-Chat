package com.karry.ohmychat.ui.fragments

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.karry.ohmychat.R
import com.karry.ohmychat.adapter.ViewPager2Adapter
import com.karry.ohmychat.databinding.FragmentViewPaperBinding
import com.karry.ohmychat.ui.activities.AccountActivity
import com.karry.ohmychat.utils.getColorResource
import com.karry.ohmychat.utils.setStatusBarColor
import com.karry.ohmychat.viewmodel.LoginViewModel

class ViewPaperFragment : Fragment() {
    private var _binding: FragmentViewPaperBinding? = null
    private val binding get() = _binding!!
    private lateinit var rootView: View
    private lateinit var loginViewModel: LoginViewModel

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
        _binding = FragmentViewPaperBinding.inflate(inflater, container, false)

        init()
        listener()

        return binding.root
    }

    private fun init() {
        val listFragment = arrayListOf(
            ChatsFragment(),
            SearchFragment(),
            GroupsFragment(),
            ProfileFragment()
        )
        loginViewModel = ViewModelProvider(
            requireActivity(), ViewModelProvider
                .AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(LoginViewModel::class.java)

        val adapter = ViewPager2Adapter(listFragment, childFragmentManager,
            lifecycle)
        with(binding){
            viewPager.adapter = adapter
            bottomNavigation.setItemSelected(R.id.action_chats)

            rootView = mainLayout.rootView

            onTabChats()
            viewPager.currentItem = 0
        }
    }


    private fun listener() {
        with(binding) {
            buttonLogout.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Do you want logout?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Accept") { _, _ -> getUserAuthToSignOut() }
                    .setCancelable(false)
                    .show()

            }

            bottomNavigation.setOnItemSelectedListener {
                when (it) {
                    R.id.action_chats -> {
                        onTabChats()
                        viewPager.currentItem = 0
                    }
                    R.id.action_search -> {
                        onTabSearch()
                        viewPager.currentItem = 1
                    }
                    R.id.action_group -> {
                        onTabGroups()
                        viewPager.currentItem = 2
                    }
                    else -> {
                        onTabProfile()
                        viewPager.currentItem = 3
                    }
                }
            }
            profileImageHome.setOnClickListener {
                onTabProfile()
                bottomNavigation.setItemSelected(R.id.action_profile)
            }

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    when (position) {
                        0 -> {
                            bottomNavigation.setItemSelected(R.id.action_chats)
                            onTabChats()
                        }
                        1 -> {
                            bottomNavigation.setItemSelected(R.id.action_search)
                            onTabSearch()
                        }
                        2 -> {
                            bottomNavigation.setItemSelected(R.id.action_group)
                            onTabGroups()
                        }
                        else -> {
                            bottomNavigation.setItemSelected(R.id.action_profile)
                            onTabProfile()
                        }
                    }

                }
            })
        }
    }

    private fun onTabChats() {
        with(binding) {
            setLogoutIconColor(R.color.chats_100)
            usernameHome.setTextColor(getColorResource(requireActivity(), R.color.chats_100))
            rootView.setBackgroundColor(getColorResource(requireActivity(), R.color.chats_100))
            setStatusBarColor(
                requireActivity(),
                getColorResource(requireActivity(), R.color.chats_500)
            )
            toolbarHome.setBackgroundColor(getColorResource(requireActivity(), R.color.chats_500))
            setBottomNavigationBackgroundColor(
                getColorResource(
                    requireActivity(),
                    R.color.chats_200
                )
            )
        }
    }

    private fun onTabSearch() {
        with(binding) {
            setLogoutIconColor(R.color.search_100)
            usernameHome.setTextColor(getColorResource(requireActivity(), R.color.search_100))
            rootView.setBackgroundColor(getColorResource(requireActivity(), R.color.search_100))
            setStatusBarColor(
                requireActivity(),
                getColorResource(requireActivity(), R.color.search_500)
            )
            toolbarHome.setBackgroundColor(getColorResource(requireActivity(), R.color.search_500))
            setBottomNavigationBackgroundColor(
                getColorResource(
                    requireActivity(),
                    R.color.search_200
                )
            )
        }
    }

    private fun onTabGroups() {
        with(binding) {
            setLogoutIconColor(R.color.groups_100)
            usernameHome.setTextColor(getColorResource(requireActivity(), R.color.groups_100))
            rootView.setBackgroundColor(getColorResource(requireActivity(), R.color.groups_100))
            setStatusBarColor(
                requireActivity(),
                getColorResource(requireActivity(), R.color.groups_500)
            )
            toolbarHome.setBackgroundColor(getColorResource(requireActivity(), R.color.groups_500))
            setBottomNavigationBackgroundColor(
                getColorResource(
                    requireActivity(),
                    R.color.groups_200
                )
            )
        }
    }

    private fun onTabProfile() {
        with(binding) {
            setLogoutIconColor(R.color.profiles_100)
            usernameHome.setTextColor(getColorResource(requireActivity(), R.color.profiles_100))
            rootView.setBackgroundColor(getColorResource(requireActivity(), R.color.profiles_100))
            setStatusBarColor(
                requireActivity(),
                getColorResource(requireActivity(), R.color.profiles_500)
            )
            toolbarHome.setBackgroundColor(
                getColorResource(
                    requireActivity(),
                    R.color.profiles_500
                )
            )
            setBottomNavigationBackgroundColor(
                getColorResource(
                    requireActivity(),
                    R.color.profiles_200
                )
            )
        }
    }


    private fun setBottomNavigationBackgroundColor(@ColorInt color: Int) {
        val background = binding.bottomNavigation.background!!
        if (background is ShapeDrawable) {
            background.paint.color = color
        } else if (background is GradientDrawable) {
            background.setColor(color)
        } else if (background is ColorDrawable) {
            background.color = color
        }
    }

    private fun setLogoutIconColor(@ColorRes colorRes: Int) {
        var drawable = ContextCompat.getDrawable(requireActivity().applicationContext, R.drawable
            .ic_logout)
        drawable = DrawableCompat.wrap(drawable!!)
        DrawableCompat.setTint(drawable, getColorResource(requireActivity(), colorRes))
        binding.buttonLogout.setImageDrawable(drawable)
    }


    private fun getUserAuthToSignOut() {
        loginViewModel.firebaseAuth.observe(viewLifecycleOwner) {
            it.signOut()
            Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireActivity(), AccountActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

    }

}