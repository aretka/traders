package com.example.traders.watchlist.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.traders.watchlist.cryptoData.FixedCryptoList
import com.example.traders.watchlist.singleCryptoScreen.chartTab.CryptoChartFragment
import com.example.traders.watchlist.singleCryptoScreen.descriptionTab.CryptoDescriptionFragment
import com.example.traders.watchlist.singleCryptoScreen.priceStatisticsTab.CryptoPriceStatisticsFragment

class CryptoViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, val crypto: FixedCryptoList) :
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
                CryptoChartFragment(crypto)
            }
            SECOND_POSITION -> {
                CryptoPriceStatisticsFragment(crypto)
            }
            THIRD_POSITION -> {
                CryptoDescriptionFragment(crypto)
            }
            else -> {
                Fragment()
            }
        }
    }

}