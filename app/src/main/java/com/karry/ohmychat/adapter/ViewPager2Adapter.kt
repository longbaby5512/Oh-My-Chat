package com.karry.ohmychat.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPager2Adapter(
    private val listFragment: ArrayList<Fragment>,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount() = listFragment.size

    override fun createFragment(position: Int): Fragment {
        return listFragment[position]
    }
}