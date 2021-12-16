package com.example.traders.watchlist.allCrypto.singleCryptoScreen.priceStatisticsTab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.traders.databinding.FragmentCryptoItemBinding
import com.example.traders.databinding.FragmentCryptoItemPriceStatisticsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CryptoPriceStatistics : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCryptoItemPriceStatisticsBinding.inflate(inflater, container, false)

        return binding.root
    }
}