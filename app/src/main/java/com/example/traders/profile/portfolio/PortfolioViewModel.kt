package com.example.traders.profile.portfolio

import android.util.Log
import com.example.traders.BaseViewModel
import com.example.traders.profile.cryptoData.CryptoInUsd
import com.example.traders.profile.cryptoData.CryptoTicker
import com.example.traders.repository.CryptoRepository
import com.example.traders.roundNum
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val repository: CryptoRepository
): BaseViewModel() {
    private val colors = getColors()

    private val _state = MutableStateFlow(PortfolioState(colors = colors))
    val state = _state.asStateFlow()

    init {
        fetchCryptoPortfolio()
    }

    fun chartUpdated() {
        _state.value = _state.value.copy(chartReadyForUpdate = false)
    }

    private fun fetchCryptoPortfolio() {
        launchWithProgress {
            Log.e("TAG", "this starts")
            val portfolioResponse = repository.getAllCryptoPortfolio()
            if(portfolioResponse.isEmpty()) {
                _state.value = _state.value.copy(isPortfolioEmpty = true)
            } else {
                _state.value = _state.value.copy(portfolioCryptoList = portfolioResponse)
            }
            if(_state.value.isPortfolioEmpty.not()) {
                collectRequiredPrices()
                calculateTotalBalance()
            }
            calculateChartData()
        }
    }

    private fun calculateTotalBalance() {
        var porfolioInUsd = BigDecimal(0)
        _state.value.cryptoListInUsd.forEach { crypto ->
            porfolioInUsd += (crypto.usdVal)
        }
        _state.value = _state.value.copy(portfolioInUsd = porfolioInUsd.roundNum())
    }

    private suspend fun collectRequiredPrices() {
        val binanceTickerTasks = mutableListOf<Deferred<CryptoTicker?>>()
        for(crypto in _state.value.portfolioCryptoList) {
            binanceTickerTasks.add(
                async { repository.getBinanceTickerBySymbol(crypto.symbol + "USDT").body() }
            )
        }
        val results = binanceTickerTasks.awaitAll()
        val cryptoWithUsdPricesList = mutableListOf<CryptoInUsd>()

        // Filter Usd amount
        val usdAmount = _state.value.portfolioCryptoList.first { it.symbol == "USD"}.amount
        cryptoWithUsdPricesList.add(CryptoInUsd("USD", usdAmount))

        // Calculate cryptoUsdVal
        for(cryptoTicker in results) {
            cryptoTicker?.let { cryptoTicker ->
                val amount = _state.value.portfolioCryptoList.first { it.symbol == cryptoTicker.symbol.replace("USDT", "") }.amount
                val amountInUsd = (cryptoTicker.price.toBigDecimal() * amount).roundNum()
                cryptoWithUsdPricesList.add(CryptoInUsd(cryptoTicker.symbol.replace("USDT", ""), amountInUsd))
            }
        }
        _state.value = _state.value.copy(cryptoListInUsd = cryptoWithUsdPricesList)
    }

    private fun calculateChartData() {
        val chartData = mutableListOf<PieEntry>()
        if(_state.value.cryptoListInUsd.isEmpty()) {
            chartData.add(PieEntry(100F, "Empty"))
        } else if(_state.value.cryptoListInUsd.size <= 6) {
            _state.value.cryptoListInUsd.forEach { crypto ->
                chartData.add(PieEntry(crypto.usdVal.toFloat() ?: 0F, crypto.symbol))
            }
        } else {
            val sortedList = state.value.cryptoListInUsd.toMutableList()
            sortedList.sortByDescending { it.usdVal }
            for(i in 0..4) {
                chartData.add(PieEntry(sortedList[i].usdVal.toFloat() ?: 0F, sortedList[i].symbol))
            }

            var othersUsdVal = BigDecimal(0)
            for( i in 5..(sortedList.size - 1)) {
                othersUsdVal += sortedList[i].usdVal
            }
            chartData.add(PieEntry(othersUsdVal.toFloat(), "Others"))
        }

        _state.value = _state.value.copy(
            chartData = chartData,
            chartReadyForUpdate = true
        )
    }

    private fun getColors(): List<Int> {
        val newColorArray = mutableListOf<Int>()
        for(color in ColorTemplate.MATERIAL_COLORS) {
            newColorArray.add(color)
        }
        for (color in ColorTemplate.VORDIPLOM_COLORS) {
            newColorArray.add(color)
        }
        return newColorArray
    }

}