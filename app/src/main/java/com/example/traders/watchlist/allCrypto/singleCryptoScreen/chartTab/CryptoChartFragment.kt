package com.example.traders.watchlist.allCrypto.singleCryptoScreen.chartTab

import android.os.Bundle
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
import com.example.traders.dialogs.buyDialog.BuyDialogFragment
import com.example.traders.dialogs.sellDialog.SellDialogFragment
import com.example.traders.getCryptoPriceChangeText
import com.example.traders.roundAndFormatDouble
import com.example.traders.watchlist.cryptoData.FixedCryptoList
import com.example.traders.watchlist.cryptoData.binance24hTickerData.PriceTickerData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@AndroidEntryPoint
class CryptoChartFragment(val slug: String) : BaseFragment() {
    private lateinit var binding: FragmentCryptoItemChartBinding

    @Inject
    lateinit var viewModelAssistedFactory: CryptoChartViewModel.Factory

    private val viewModel: CryptoChartViewModel by viewModels() {
        CryptoChartViewModel.provideFactory(viewModelAssistedFactory, slug)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCryptoItemChartBinding.inflate(inflater, container, false)
        viewModel.fetchAllChartData()
        binding.initUi()
        lifecycleScope.launch {
            viewModel.chartState.collect { state ->
                binding.setHeaderPrices(state.tickerData)
                binding.updateBuySellBtnAccessibility(state)
                setBtnStyles(state)
                updateChart(state)
            }
        }
        return binding.root
    }


    private fun FragmentCryptoItemChartBinding.setHeaderPrices(priceTicker: PriceTickerData?) {
        priceTicker?.let {
            livePriceText.text = "$ " + roundAndFormatDouble(it.last.toDouble(), viewModel.chartState.value.priceNumToRound)
            getCryptoPriceChangeText(
                roundAndFormatDouble(it.priceChange.toDouble(), viewModel.chartState.value.priceNumToRound),
                roundAndFormatDouble(it.priceChangePercent.toDouble()),
                priceChangeText
            )
        }
    }

    private fun updateChart(state: ChartState) {
        if (state.isMonth1BtnActive) {
            state.chartDataFor90d?.let {
                importValuesToChart(binding.candleChart, it.subList(it.size - 30, it.size))
            }
        } else if (state.isMonth3BtnActive) {
            state.chartDataFor90d?.let {
                importValuesToChart(binding.candleChart, it)
            }
        } else if (state.isMonth6BtnActive) {
            state.chartDataFor360d?.let {
                importValuesToChart(binding.candleChart, it.subList(it.size - 25, it.size))
            }
        } else if (state.isMonth12BtnActive) {
            state.chartDataFor360d?.let {
                importValuesToChart(binding.candleChart, it)
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
    }

    private fun setBtnStyle(chartBtn: Button, active: Boolean) {
        if (active) {
            chartBtn.setTextColor(ContextCompat.getColor(chartBtn.context, R.color.white))
            chartBtn.background =
                ContextCompat.getDrawable(chartBtn.context, R.drawable.active_chart_btn)
        } else {
            chartBtn.setTextColor(ContextCompat.getColor(chartBtn.context, R.color.dark_gray))
            chartBtn.background =
                ContextCompat.getDrawable(chartBtn.context, R.drawable.inactive_chart_btn)
        }
    }

    private fun FragmentCryptoItemChartBinding.initUi() {
        month1Btn.setOnClickListener { viewModel.onChartBtnSelected(BtnId.MONTH1_BTN) }
        month3Btn.setOnClickListener { viewModel.onChartBtnSelected(BtnId.MONTH3_BTN) }
        month6Btn.setOnClickListener { viewModel.onChartBtnSelected(BtnId.MONTH6_BTN) }
        month12Btn.setOnClickListener { viewModel.onChartBtnSelected(BtnId.MONTH12_BTN) }
        buyBtn.setOnClickListener { showBuyDialog() }
        sellBtn.setOnClickListener { showSellDialog() }
    }

    private fun FragmentCryptoItemChartBinding.updateBuySellBtnAccessibility(state: ChartState) {
        if(state.tickerData != null) {
            buyBtn.isEnabled = true
            sellBtn.isEnabled = true
        } else {
            buyBtn.isEnabled = false
            sellBtn.isEnabled = false
        }
    }

    private fun showSellDialog() {
        val newSellFragment = SellDialogFragment(
            lastPrice = BigDecimal(viewModel.chartState.value.tickerData?.last),
            symbol = FixedCryptoList.getEnumName(slug)?.name.toString()
        )
        newSellFragment.show(parentFragmentManager, "sell_dialog")
    }

    private fun showBuyDialog() {
        val newBuyFragment = BuyDialogFragment(
            lastPrice = BigDecimal(viewModel.chartState.value.tickerData?.last),
            symbol = FixedCryptoList.getEnumName(slug)?.name.toString()
        )
        newBuyFragment.show(parentFragmentManager, "buy_dialog")
    }
}


