package com.example.traders.watchlist.allCrypto.singleCryptoScreen.chartTab

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.traders.BaseFragment
import com.example.traders.R
import com.example.traders.customviews.CandleChart
import com.example.traders.databinding.FragmentCryptoItemChartBinding
import com.example.traders.getCryptoPriceChangeText
import com.example.traders.roundNumber
import com.example.traders.watchlist.cryptoData.binance24hTickerData.PriceTicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CryptoChartFragment(val id: String) : BaseFragment() {
    private val viewModel: CryptoChartViewModel by viewModels()
    private lateinit var binding: FragmentCryptoItemChartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCryptoItemChartBinding.inflate(inflater, container, false)
        viewModel.assignId(id)
        viewModel.fetchAllChartData()
        binding.initUi()
        lifecycleScope.launch {
            viewModel.chartState.collect { state ->
                binding.setHeaderPrices(state.tickerData)
                setBtnStyles(state)
                updateChart(state)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    private fun FragmentCryptoItemChartBinding.setHeaderPrices(priceTicker: PriceTicker?) {
        Log.e("CryptoChartfragment", "setHeaderPrices called")
        priceTicker?.data?.let {
            livePriceText.text = "$ " + roundNumber(priceTicker.data.last.toDouble())
            getCryptoPriceChangeText(
                roundNumber(it.priceChange.toDouble()),
                roundNumber(it.priceChangePercent.toDouble()),
                priceChangeText
            )
        }
    }

    private fun updateChart(state: ChartState) {
        if(state.isMonth1BtnActive) {
            state.chartDataFor90d?.let {
                importValuesToChart( binding.candleChart, it.subList(it.size - 30, it.size) )
            }
        } else if(state.isMonth3BtnActive) {
            state.chartDataFor90d?.let {
                importValuesToChart( binding.candleChart, it )
            }
        } else if(state.isMonth6BtnActive) {
            state.chartDataFor360d?.let {
                importValuesToChart( binding.candleChart, it.subList(it.size - 25, it.size) )
            }
        } else if(state.isMonth12BtnActive) {
            state.chartDataFor360d?.let {
                importValuesToChart( binding.candleChart, it )
            }
        }
    }

    private fun setBtnStyles(state: ChartState) {
        setBtnStyle(binding.month1Btn, state.isMonth1BtnActive)
        setBtnStyle(binding.month3Btn, state.isMonth3BtnActive)
        setBtnStyle(binding.month6Btn, state.isMonth6BtnActive)
        setBtnStyle(binding.month12Btn, state.isMonth12BtnActive)
    }

    private fun importValuesToChart(candleChart: CandleChart, values: List<List<Float>>) {
        candleChart.importListValues(values)
        candleChart.invalidate()
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

    private fun FragmentCryptoItemChartBinding.initUi() {
        month1Btn.setOnClickListener { viewModel.onChartBtnSelected(BtnId.MONTH1_BTN) }
        month3Btn.setOnClickListener { viewModel.onChartBtnSelected(BtnId.MONTH3_BTN) }
        month6Btn.setOnClickListener { viewModel.onChartBtnSelected(BtnId.MONTH6_BTN) }
        month12Btn.setOnClickListener { viewModel.onChartBtnSelected(BtnId.MONTH12_BTN) }
    }
}


