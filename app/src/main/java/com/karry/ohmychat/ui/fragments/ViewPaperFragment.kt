package com.karry.ohmychat.ui.fragments

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.karry.ohmychat.R
import com.karry.ohmychat.adapter.ViewPager2Adapter
import com.karry.ohmychat.databinding.FragmentViewPaperBinding
import com.karry.ohmychat.ui.activities.AccountActivity

class ViewPaperFragment : Fragment() {
    private var _binding: FragmentViewPaperBinding? = null
    private val binding get() = _binding!!
    private lateinit var rootView: View

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
                getUserAuthToSignOut()
               Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
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
            usernameHome.setTextColor(getColorResource(R.color.chats_100))
            rootView.setBackgroundColor(getColorResource(R.color.chats_100))
            setStatusBarColor(getColorResource(R.color.chats_500))
            toolbarHome.setBackgroundColor(getColorResource(R.color.chats_500))
            setBottomNavigationBackgroundColor(getColorResource(R.color.chats_200))
        }
    }

    private fun onTabSearch() {
        with(binding) {
            setLogoutIconColor(R.color.search_100)
            usernameHome.setTextColor(getColorResource(R.color.search_100))
            rootView.setBackgroundColor(getColorResource(R.color.search_100))
            setStatusBarColor(getColorResource(R.color.search_500))
            toolbarHome.setBackgroundColor(getColorResource(R.color.search_500))
            setBottomNavigationBackgroundColor(getColorResource(R.color.search_200))
        }
    }

    private fun onTabGroups() {
        with(binding) {
            setLogoutIconColor(R.color.groups_100)
            usernameHome.setTextColor(getColorResource(R.color.groups_100))
            rootView.setBackgroundColor(getColorResource(R.color.groups_100))
            setStatusBarColor(getColorResource(R.color.groups_500))
            toolbarHome.setBackgroundColor(getColorResource(R.color.groups_500))
            setBottomNavigationBackgroundColor(getColorResource(R.color.groups_200))
        }
    }

    private fun onTabProfile() {
        with(binding) {
            setLogoutIconColor(R.color.profiles_100)
            usernameHome.setTextColor(getColorResource(R.color.profiles_100))
            rootView.setBackgroundColor(getColorResource(R.color.profiles_100))
            setStatusBarColor(getColorResource(R.color.profiles_500))
            toolbarHome.setBackgroundColor(getColorResource(R.color.profiles_500))
            setBottomNavigationBackgroundColor(getColorResource(R.color.profiles_200))
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

    private fun setStatusBarColor(@ColorInt color: Int) {
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        requireActivity().window.statusBarColor = color
    }

    private fun setLogoutIconColor(@ColorRes colorRes: Int) {
        var drawable = ContextCompat.getDrawable(requireActivity().applicationContext, R.drawable
            .ic_logout)
        drawable = DrawableCompat.wrap(drawable!!)
        DrawableCompat.setTint(drawable, getColorResource(colorRes))
        binding.buttonLogout.setImageDrawable(drawable)
    }

    private fun getColorResource(@ColorRes colorRes: Int) =
        ContextCompat.getColor(requireActivity().applicationContext, colorRes)

    private fun getUserAuthToSignOut() {
        val intent = Intent(requireActivity(), AccountActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

}