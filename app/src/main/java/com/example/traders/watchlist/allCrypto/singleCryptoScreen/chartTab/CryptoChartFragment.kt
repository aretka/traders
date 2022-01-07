package com.example.traders.watchlist.allCrypto.singleCryptoScreen.chartTab

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.AppCompatButton
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.traders.BaseFragment
import com.example.traders.R
import com.example.traders.databinding.FragmentCryptoItemChartBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CryptoChartFragment(val slug: String) : BaseFragment() {
    private val viewModel: CryptoChartViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCryptoItemChartBinding.inflate(inflater, container, false)
        viewModel.assignSlug(slug)
        viewModel.fetchCryptoPriceStatistics()
        viewModel.cryptoStatsResponse.observe(viewLifecycleOwner, {
            viewModel.findMinAndMaxValues()
        })

        viewModel.maxVal.observe(viewLifecycleOwner, { maxVal ->
            binding.candleChart.importListValues(viewModel.cryptoStatsResponse.value?.data?.values!!, viewModel.minVal.value!!, maxVal)
            binding.candleChart.invalidate()
        })

        binding.month1Btn.setOnClickListener {
            viewModel.onChartBtnSelected(BtnId.MONTH1_BTN)
        }
        binding.month3Btn.setOnClickListener {
            viewModel.onChartBtnSelected(BtnId.MONTH3_BTN)
        }
        binding.month6Btn.setOnClickListener {
            viewModel.onChartBtnSelected(BtnId.MONTH6_BTN)
        }
        binding.month12Btn.setOnClickListener {
            viewModel.onChartBtnSelected(BtnId.MONTH12_BTN)
        }

        viewModel.isMonth1BtnActive.observe(viewLifecycleOwner, { isActive ->
            setBtnStyle(binding.month1Btn, isActive)
        })
        viewModel.isMonth3BtnActive.observe(viewLifecycleOwner, { isActive ->
            setBtnStyle(binding.month3Btn, isActive)
        })
        viewModel.isMonth6BtnActive.observe(viewLifecycleOwner, { isActive ->
            setBtnStyle(binding.month6Btn, isActive)
        })
        viewModel.isMonth12BtnActive.observe(viewLifecycleOwner, { isActive ->
            setBtnStyle(binding.month12Btn, isActive)
        })



        return binding.root
    }

    private fun setBtnStyle(chartBtn: Button, active: Boolean) {
        if(active) {
            chartBtn.setTextColor(ContextCompat.getColor(chartBtn.context, R.color.white))
            chartBtn.background = ContextCompat.getDrawable(chartBtn.context, R.drawable.active_chart_btn)
        } else {
            chartBtn.setTextColor(ContextCompat.getColor(chartBtn.context, R.color.dark_gray))
            chartBtn.background = ContextCompat.getDrawable(chartBtn.context, R.drawable.inactive_chart_btn)
        }
    }
}