package com.digitalsln.stanserhorn.ui.trip_log.add_trip_log_dialog

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class AddTripLogDialogAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    private val fragments = mutableListOf<Fragment>()

    fun addFragment(fragment: Fragment) {
        fragments.add(fragment)
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}