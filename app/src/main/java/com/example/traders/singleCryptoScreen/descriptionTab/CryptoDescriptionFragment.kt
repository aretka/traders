package com.example.traders.singleCryptoScreen.descriptionTab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.traders.databinding.FragmentCryptoItemDescriptionBinding
import com.example.traders.watchlist.cryptoData.FixedCryptoList
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CryptoDescriptionFragment(val crypto: FixedCryptoList) : Fragment() {
    private val viewModel: CryptoDescriptionViewModel by viewModels()
    private lateinit var binding: FragmentCryptoItemDescriptionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCryptoItemDescriptionBinding.inflate(inflater, container, false)
        viewModel.fetchCryptoPriceStatistics(crypto)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.descState.collect {
                fillDescriptionData(it)
            }
        }
    }

    private fun fillDescriptionData(state: DescriptionState) {
        binding.projectInfoDesc.text = HtmlCompat.fromHtml(state.projectInfoDesc?:"", HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
        binding.preHistoryDesc.text = HtmlCompat.fromHtml(state.preHistoryDesc?:"", HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
    }
}