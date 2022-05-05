package com.example.traders.presentation.profile.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.traders.presentation.profile.history.HistoryFragment
import com.example.traders.presentation.profile.portfolio.PortfolioFragment

class UserViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    companion object {
        private const val NUM_OF_FRAGMENTS = 2
        private const val FIRST_POSITION = 0
        private const val SECOND_POSITION = 1
    }

    override fun getItemCount(): Int {
        return NUM_OF_FRAGMENTS
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            FIRST_POSITION -> PortfolioFragment()
            SECOND_POSITION -> HistoryFragment()
            else -> Fragment()
        }
    }

}
