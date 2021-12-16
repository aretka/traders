package com.example.traders.watchlist.allCrypto.singleCryptoScreen.chartTab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.traders.databinding.FragmentCryptoItemBinding
import com.example.traders.databinding.FragmentCryptoItemChartBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CryptoChartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCryptoItemChartBinding.inflate(inflater, container, false)

        return binding.root
    }
}