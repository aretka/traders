package com.example.traders.presentation.profile.portfolio

import com.example.traders.presentation.BaseViewModel
import com.example.traders.database.Crypto
import com.example.traders.network.models.CryptoTicker
import com.example.traders.network.repository.CryptoRepository
import com.example.traders.utils.roundNum
import com.github.mikephil.charting.data.PieEntry
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

    private val _state = MutableStateFlow(
        PortfolioState(
            colors = colors
        )
    )
    val state = _state.asStateFlow()
//    val state = _state.asStateFlow().combine(repository.getLiveAllCryptoPortfolio().asFlow()) {
//        portfolioState, liveAllCryptoPortfolio ->
//        updateStateData()
//    }

    val livePortfolioList = repository.getLiveAllCryptoPortfolio()

    fun chartUpdated() {
        _state.value = _state.value.copy(chartReadyForUpdate = false)
    }

    fun updatePortfolioState() {
        if (listNotEmptyAndNotEqualsToPrevList()) {
            _state.value = _state.value.copy(prevList = livePortfolioList.value ?: emptyList())
            launch {
                collectRequiredPrices(sublistOfNewCrypto())
                calculateTotalBalance()
                calculateChartData()
            }
        } else if (listIsEmpty()) {
            setStateListsToEmpty()
            launch {
                calculateChartData()
            }
        }
        // if we wrote else it would mean nothing has changed and we dont need to change anything
    }

    private fun listIsEmpty(): Boolean {
        return livePortfolioList.value?.isEmpty() ?: false
    }

    private fun listNotEmptyAndNotEqualsToPrevList(): Boolean {
        return livePortfolioList.value?.isNotEmpty() ?: false && _state.value.prevList != livePortfolioList.value
    }

    private fun sublistOfNewCrypto(): List<Crypto> {
        return livePortfolioList.value?.filter { newState ->
            _state.value.cryptoListInUsd.filter { newState.symbol == it.symbol }.isEmpty()
        } ?: emptyList()
    }

    private fun setStateListsToEmpty() {
        _state.value = _state.value.copy(
            usdPricesFromBinance = emptyList(),
            cryptoListInUsd = emptyList(),
            prevList = emptyList(),
            totalPortfolioBalance = BigDecimal(0.00)
        )
    }

    private fun calculateTotalBalance() {
        var porfolioInUsd = BigDecimal(0)
        _state.value.cryptoListInUsd.forEach { crypto ->
            porfolioInUsd += (crypto.amountInUsd)
        }
        _state.value = _state.value.copy(totalPortfolioBalance = porfolioInUsd.roundNum())
    }

    private suspend fun collectRequiredPrices(listToFetch: List<Crypto>) {

        // Waits for required cryptoTickers to fetch
        val results = fetchBinanceTickerTasks(listToFetch).awaitAll()

        // Takes all crypto elements into one array
        val allPricesFromApi = listOfSameItems() + results

        _state.value = _state.value.copy(
            cryptoListInUsd = getCryptoWithUsdPricesList(allPricesFromApi),
            usdPricesFromBinance = allPricesFromApi
        )
    }

    private fun getCryptoWithUsdPricesList(allPricesFromApi: List<CryptoTicker?>): MutableList<CryptoInUsd> {
        val cryptoWithUsdPricesList = mutableListOf<CryptoInUsd>()

        // Add Usd to list if not exists
        val usdAmount = livePortfolioList.value!!.first { it.symbol == "USD" }.amount
        cryptoWithUsdPricesList.add(CryptoInUsd("USD", usdAmount, usdAmount))

        // Calculate cryptoUsdVal
        for (cryptoTicker in allPricesFromApi) {
            cryptoTicker?.let { cryptoTicker ->
                val amount = getCryptoAmount(cryptoTicker)
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

        return cryptoWithUsdPricesList
    }

    private fun getCryptoAmount(cryptoTicker: CryptoTicker): BigDecimal {
        return livePortfolioList.value!!.first {
            it.symbol == cryptoTicker.symbol.replace(
                "USDT",
                ""
            )
        }.amount
    }

    private suspend fun fetchBinanceTickerTasks(listToFetch: List<Crypto>): MutableList<Deferred<CryptoTicker?>> {
        val binanceTickerTasks = mutableListOf<Deferred<CryptoTicker?>>()
        for (crypto in listToFetch) {
            if (crypto.symbol != "USD") {
//                Log.e("API_FETCH", "NEW REQUEST OF ${crypto.symbol}")
                binanceTickerTasks.add(
                    async { repository.getBinanceTickerBySymbol(crypto.symbol + "USDT").body() }
                )
            }
        }
        return binanceTickerTasks
    }

    private fun listOfSameItems(): List<CryptoTicker?> {
        return _state.value.usdPricesFromBinance.filter { prevItem ->
            livePortfolioList.value?.filter {
                it.symbol == prevItem?.symbol?.replace("USDT", "")
            }!!.isNotEmpty()
        }
    }

    private fun calculateChartData() {
        val chartData: List<PieEntry> = when(_state.value.cryptoListInUsd.size) {
            0 -> emptyChartData()
            in 1..5 -> middleSizeChartData()
            else -> largeSizeChartData()
        }

        _state.value = _state.value.copy(
            chartData = chartData,
            chartReadyForUpdate = true,
            chartDataLoaded = true
        )
    }

    private fun emptyChartData(): List<PieEntry> {
        return listOf(PieEntry(100F, "Empty"))
    }

    private fun middleSizeChartData(): List<PieEntry> {
        return _state.value.cryptoListInUsd.map { crypto ->
            PieEntry(crypto.amountInUsd.toFloat() ?: 0F, crypto.symbol)
        }
    }

    private fun largeSizeChartData(): List<PieEntry> {
        val chartData: MutableList<PieEntry> = mutableListOf()
        val sortedList = _state.value.cryptoListInUsd.sortedByDescending { it.amountInUsd }

        sortedList.subList(0, 4).forEach {
            chartData.add(
                PieEntry(
                    it.amountInUsd.toFloat(),
                    it.symbol
                )
            )
        }

        val othersUsdVal = sortedList.subList(4, sortedList.size).sumOf { it.amountInUsd }
        chartData.add(PieEntry(othersUsdVal.toFloat(), "Others"))

        return chartData
    }

    companion object {
        private val colors =
            listOf(-13710223, -932849, -1618884, -13330213, -4128884, -2164, -12148, -7542017, -29539)
    }

}