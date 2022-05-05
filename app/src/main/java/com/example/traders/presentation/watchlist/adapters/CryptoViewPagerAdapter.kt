package com.example.traders.presentation.watchlist.adapters

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.traders.database.FixedCryptoList
import com.example.traders.presentation.cryptoDetailsScreen.chartTab.CryptoChartFragment
import com.example.traders.presentation.cryptoDetailsScreen.descriptionTab.CryptoDescriptionFragment
import com.example.traders.presentation.cryptoDetailsScreen.priceStatisticsTab.CryptoPriceStatisticsFragment

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

    @RequiresApi(Build.VERSION_CODES.O)
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