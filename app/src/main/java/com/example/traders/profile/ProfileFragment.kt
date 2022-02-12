package com.example.traders.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.traders.BaseFragment
import com.example.traders.R
import com.example.traders.databinding.FragmentUserProfileBinding
import com.example.traders.profile.adapters.UserViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        setUpTabs(binding.userPageViewer, binding.profileTab)

        return binding.root
    }

    private fun setUpTabs(viewPager: ViewPager2, tabLayout: TabLayout) {
        val viewPagerAdapter = UserViewPagerAdapter(requireActivity().supportFragmentManager, lifecycle)
        viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.portfolio)
                1 -> tab.text = getString(R.string.history)
            }
        }.attach()
    }

}
