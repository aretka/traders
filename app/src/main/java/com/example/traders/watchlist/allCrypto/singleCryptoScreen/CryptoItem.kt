package com.example.traders.watchlist.allCrypto.singleCryptoScreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.traders.R
import com.example.traders.databinding.FragmentCryptoItemBinding
import com.example.traders.watchlist.WatchListFragmentDirections
import com.example.traders.watchlist.adapters.CryptoViewPagerAdapter
import com.example.traders.watchlist.adapters.ViewPagerAdapter
import com.example.traders.watchlist.allCrypto.singleCryptoScreen.CryptoItemViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CryptoItem : Fragment() {

    private val viewModel: CryptoItemViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCryptoItemBinding.inflate(inflater, container, false)

        val receivedValues = CryptoItemArgs.fromBundle(requireArguments())
        binding.textView.text = receivedValues.slug + " " + receivedValues.symbol

        setUpTabs(binding.singleItemViewPager, binding.singleItemTablayout)
        binding.backButton.setOnClickListener {
            this.findNavController().navigate(
                CryptoItemDirections
                    .actionCryptoItemToWatchListFragment())
        }

        return binding.root
    }

    private fun setUpTabs(viewPager: ViewPager2, tabLayout: TabLayout) {
        val viewPagerAdapter = CryptoViewPagerAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Statistics"
                    tab.setIcon(R.drawable.ic_price_history)
                }
                1 -> {
                    tab.text = "Chart"
                    tab.setIcon(R.drawable.ic_chart)
                }
                2 -> {
                    tab.text = "Description"
                    tab.setIcon(R.drawable.ic_description)
                }
            }
        }.attach()
    }

}