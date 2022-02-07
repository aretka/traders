package com.example.traders.profile.portfolio

import android.util.Log
import com.example.traders.BaseViewModel
import com.example.traders.database.Crypto
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
) : BaseViewModel() {
    private val colors = getColors()

    private val _state = MutableStateFlow(PortfolioState(colors = colors))
    val state = _state.asStateFlow()

    val livePortfolioList = repository.getLiveAllCryptoPortfolio()

    fun chartUpdated() {
        _state.value = _state.value.copy(chartReadyForUpdate = false)
    }

    fun deleteAllDbRows() {
        launch {
            repository.deleteAllCryptoFromDb()
        }
    }

    fun updateData() {
        // filters sublist of new crypto
        if (livePortfolioList.value?.isNotEmpty() ?: false) {
            val listToFetch = livePortfolioList.value?.filter { newState ->
                _state.value.cryptoListInUsd.filter { newState.symbol == it.symbol }.isEmpty()
            } ?: emptyList()
            launch {
                collectRequiredPrices(listToFetch)
                calculateTotalBalance()
                calculateChartData()
            }
        } else {
            _state.value = _state.value.copy(
                usdPricesFromBinance = emptyList(),
                cryptoListInUsd = emptyList()
            )
            launch {
                calculateTotalBalance()
                calculateChartData()
            }
        }
    }

    private fun calculateTotalBalance() {
        var porfolioInUsd = BigDecimal(0)
        _state.value.cryptoListInUsd.forEach { crypto ->
            porfolioInUsd += (crypto.amountInUsd)
        }
        _state.value = _state.value.copy(totalPortfolioBalance = porfolioInUsd.roundNum())
    }

    private suspend fun collectRequiredPrices(listToFetch: List<Crypto>) {
        val binanceTickerTasks = mutableListOf<Deferred<CryptoTicker?>>()
        for (crypto in listToFetch) {
            if (crypto.symbol != "USD") {
                Log.e("API_FETCH", "NEW REQUEST OF ${crypto.symbol}")
                binanceTickerTasks.add(
                    async { repository.getBinanceTickerBySymbol(crypto.symbol + "USDT").body() }
                )
            }
        }
        // Waits for required cryptoTickers to fetch
        val results = binanceTickerTasks.awaitAll()
        // Remove unnecessary tickers
        val allPricesFromApi = listOfSameItems() + results
        val cryptoWithUsdPricesList = mutableListOf<CryptoInUsd>()

        // Add Usd to list if not exists
        val usdAmount = livePortfolioList.value!!.first { it.symbol == "USD" }.amount
        cryptoWithUsdPricesList.add(CryptoInUsd("USD", usdAmount, usdAmount))

        // Calculate cryptoUsdVal
        for (cryptoTicker in allPricesFromApi) {
            cryptoTicker?.let { cryptoTicker ->
                val amount = livePortfolioList.value!!.first {
                    it.symbol == cryptoTicker.symbol.replace(
                        "USDT",
                        ""
                    )
                }.amount
                val amountInUsd = (cryptoTicker.price.toBigDecimal() * amount).roundNum()
                cryptoWithUsdPricesList.add(
                    CryptoInUsd(
                        cryptoTicker.symbol.replace("USDT", ""),
                        amount,
                        amountInUsd
                    )
                )
            }
        }
        _state.value = _state.value.copy(
            cryptoListInUsd = cryptoWithUsdPricesList,
            usdPricesFromBinance = allPricesFromApi
        )
    }

    private fun listOfSameItems(): List<CryptoTicker?> {
        return _state.value.usdPricesFromBinance.filter { prevItem ->
            livePortfolioList.value?.filter {
                it.symbol == prevItem?.symbol?.replace("USDT", "")
            }!!.isNotEmpty()
        }
    }

    private fun calculateChartData() {
        val chartData = mutableListOf<PieEntry>()
        if (_state.value.cryptoListInUsd.isEmpty()) {
            chartData.add(PieEntry(100F, "Empty"))
        } else if (_state.value.cryptoListInUsd.size <= 5) {
            _state.value.cryptoListInUsd.forEach { crypto ->
                chartData.add(PieEntry(crypto.amountInUsd.toFloat() ?: 0F, crypto.symbol))
            }
        } else {
            val sortedList = state.value.cryptoListInUsd.toMutableList()
            sortedList.sortByDescending { it.amountInUsd }
            for (i in 0..3) {
                chartData.add(
                    PieEntry(
                        sortedList[i].amountInUsd.toFloat() ?: 0F,
                        sortedList[i].symbol
                    )
                )
            }

            var othersUsdVal = BigDecimal(0)
            for (i in 4..(sortedList.size - 1)) {
                othersUsdVal += sortedList[i].amountInUsd
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
        for (color in ColorTemplate.MATERIAL_COLORS) {
            newColorArray.add(color)
        }
        for (color in ColorTemplate.VORDIPLOM_COLORS) {
            newColorArray.add(color)
        }
        return newColorArray
    }

}