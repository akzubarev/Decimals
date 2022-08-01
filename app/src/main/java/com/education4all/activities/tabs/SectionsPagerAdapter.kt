package com.education4all.activities.tabs

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class SectionsPagerAdapter(fm: FragmentActivity?) : FragmentStateAdapter(fm!!) {
    fun getPageTitle(position: Int): CharSequence {
        return TAB_TITLES[position]
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        fragment = when (position) {
            0 -> SettingsTaskTab()
            1 -> SettingsAppTab()
            else -> SettingsTaskTab()
        }
        return fragment
    }

    override fun getItemCount(): Int {
        return 2
    }

    companion object {
        private val TAB_TITLES = arrayOf("Задания", "Интерфейс")
    }
}