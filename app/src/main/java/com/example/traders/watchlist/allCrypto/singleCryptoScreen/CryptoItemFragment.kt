package com.example.traders.watchlist.allCrypto.singleCryptoScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.traders.BaseFragment
import com.example.traders.R
import com.example.traders.databinding.FragmentCryptoItemBinding
import com.example.traders.roundNumber
import com.example.traders.watchlist.adapters.CryptoViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class CryptoItemFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCryptoItemBinding.inflate(inflater, container, false)

        val receivedValues = CryptoItemFragmentArgs.fromBundle(requireArguments())
        binding.slug.text = receivedValues.symbol.uppercase()

        setUpTabs(binding.singleItemViewPager, binding.singleItemTablayout, receivedValues.symbol)

        binding.backButton.setOnClickListener {
            this.findNavController().navigateUp()
        }


        return binding.root
    }

    private fun setUpTabs(viewPager: ViewPager2, tabLayout: TabLayout, symbol: String) {
        val viewPagerAdapter =
            CryptoViewPagerAdapter(childFragmentManager, lifecycle, symbol.lowercase())
        viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.setIcon(R.drawable.ic_price_history)
                }
                1 -> {
                    tab.setIcon(R.drawable.ic_chart)
                }
                2 -> {
                    tab.setIcon(R.drawable.ic_description)
                }
            }
        }.attach()
    }

}