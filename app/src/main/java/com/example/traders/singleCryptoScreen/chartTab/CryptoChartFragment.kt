package com.example.traders.singleCryptoScreen.chartTab

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.traders.*
import com.example.traders.customviews.CandleChart
import com.example.traders.databinding.FragmentCryptoItemChartBinding
import com.example.traders.dialogs.buyDialog.BuyDialogFragment
import com.example.traders.dialogs.sellDialog.SellDialogFragment
import com.example.traders.utils.roundAndFormatDouble
import com.example.traders.utils.setPriceChangeText
import com.example.traders.utils.setPriceChangeTextColor
import com.example.traders.watchlist.cryptoData.FixedCryptoList
import com.example.traders.watchlist.cryptoData.binance24hTickerData.PriceTickerData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class CryptoChartFragment(val crypto: FixedCryptoList) : BaseFragment() {
    private lateinit var binding: FragmentCryptoItemChartBinding
    private lateinit var buttonList: List<AppCompatButton>

    @Inject
    lateinit var viewModelAssistedFactory: CryptoChartViewModel.Factory

    private val viewModel: CryptoChartViewModel by viewModels() {
        CryptoChartViewModel.provideFactory(viewModelAssistedFactory, crypto)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCryptoItemChartBinding.inflate(inflater, container, false)
        buttonList = listOf(binding.month1Btn, binding.month3Btn, binding.month6Btn, binding.month12Btn)
        binding.initUi()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            viewModel.chartState.collect { state ->
                binding.setHeaderPrices(state.tickerData)
                binding.updateBuySellBtnAccessibility(state)
                updateChartBtnStyles(state)
                updateChart(state)
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun FragmentCryptoItemChartBinding.setHeaderPrices(priceTicker: PriceTickerData?) {
        priceTicker?.let {
            livePriceText.text = "$ " + roundAndFormatDouble(it.last.toDouble(), crypto.priceToRound)
            priceChangeText.setPriceChangeText(
                roundAndFormatDouble(it.priceChange.toDouble(), crypto.priceToRound),
                roundAndFormatDouble(it.priceChangePercent.toDouble()),
            )
            priceChangeText.setPriceChangeTextColor()
        }
    }

    private fun updateChart(state: ChartState) {
        when(state.activeButtonId) {
            BtnId.MONTH1_BTN -> {
                state.chartDataFor90d?.let {
                    importValuesToChart(binding.candleChart, it.subList(it.size - 30, it.size))
                }
            }
            BtnId.MONTH3_BTN -> {
                state.chartDataFor90d?.let {
                    importValuesToChart(binding.candleChart, it)
                }
            }
            BtnId.MONTH6_BTN -> {
                state.chartDataFor360d?.let {
                    importValuesToChart(binding.candleChart, it.subList(it.size - 25, it.size))
                }
            }
            BtnId.MONTH12_BTN -> {
                state.chartDataFor360d?.let {
                    importValuesToChart(binding.candleChart, it)
                }
            }
        }
    }

    private fun updateChartBtnStyles(state: ChartState) {
        // Setting previous active button to default style
        when(state.prevActiveButtonId) {
            BtnId.MONTH1_BTN -> setInactiveButtonStyle(binding.month1Btn)
            BtnId.MONTH3_BTN -> setInactiveButtonStyle(binding.month3Btn)
            BtnId.MONTH6_BTN -> setInactiveButtonStyle(binding.month6Btn)
            BtnId.MONTH12_BTN -> setInactiveButtonStyle(binding.month12Btn)
        }

        // Setting new active button to active style
        when(state.activeButtonId) {
            BtnId.MONTH1_BTN -> setActiveButtonStyle(binding.month1Btn)
            BtnId.MONTH3_BTN -> setActiveButtonStyle(binding.month3Btn)
            BtnId.MONTH6_BTN -> setActiveButtonStyle(binding.month6Btn)
            BtnId.MONTH12_BTN -> setActiveButtonStyle(binding.month12Btn)
        }
    }

    private fun importValuesToChart(candleChart: CandleChart, values: List<List<Float>>) {
        candleChart.importListValues(values)
    }

    private fun setActiveButtonStyle(chartBtn: Button) {
        chartBtn.setTextColor(ContextCompat.getColor(chartBtn.context, R.color.white))
        chartBtn.background =
            ContextCompat.getDrawable(chartBtn.context, R.drawable.active_chart_btn)
    }

    private fun setInactiveButtonStyle(chartBtn: Button) {
        chartBtn.setTextColor(ContextCompat.getColor(chartBtn.context, R.color.dark_gray))
        chartBtn.background =
            ContextCompat.getDrawable(chartBtn.context, R.drawable.inactive_chart_btn)
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
            crypto = crypto
        )
        newSellFragment.show(parentFragmentManager, "sell_dialog")
    }

    private fun showBuyDialog() {
        val newBuyFragment = BuyDialogFragment(
            lastPrice = BigDecimal(viewModel.chartState.value.tickerData?.last ?: "0"),
            crypto = crypto
        )
        newBuyFragment.show(parentFragmentManager, "buy_dialog")
    }
}

