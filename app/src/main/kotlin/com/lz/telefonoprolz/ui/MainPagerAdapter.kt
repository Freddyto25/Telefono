package com.lz.telefonoprolz.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lz.telefonoprolz.ui.contacts.ContactsFragment
import com.lz.telefonoprolz.ui.keypad.KeypadFragment
import com.lz.telefonoprolz.ui.recents.RecentsFragment

class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> KeypadFragment()
        1 -> RecentsFragment()
        else -> ContactsFragment()
    }
}
