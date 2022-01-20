package com.example.traders.watchlist.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.traders.watchlist.allCrypto.singleCryptoScreen.chartTab.CryptoChartFragment
import com.example.traders.watchlist.allCrypto.singleCryptoScreen.descriptionTab.CryptoDescriptionFragment
import com.example.traders.watchlist.allCrypto.singleCryptoScreen.priceStatisticsTab.CryptoPriceStatistics

class CryptoViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, val id: String) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    companion object {
        private const val NUM_OF_FRAGMENTS = 3
        private const val FIRST_POSITION = 0
        private const val SECOND_POSITION = 1
        private const val THIRD_POSITION = 2
    }

    override fun getItemCount(): Int {
        return NUM_OF_FRAGMENTS
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            FIRST_POSITION -> {
                CryptoChartFragment(id)
            }
            SECOND_POSITION -> {
                CryptoPriceStatistics(id)
            }
            THIRD_POSITION -> {
                CryptoDescriptionFragment(id)
            }
            else -> {
                Fragment()
            }
        }
    }

}