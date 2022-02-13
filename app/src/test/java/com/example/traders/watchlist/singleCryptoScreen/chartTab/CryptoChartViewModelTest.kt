package com.example.traders.watchlist.singleCryptoScreen.chartTab

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.traders.repository.CryptoRepository
import com.example.traders.watchlist.TestDispatcherRule
import com.example.traders.watchlist.cryptoData.FixedCryptoList
import com.example.traders.watchlist.cryptoData.binance24hTickerData.PriceTicker
import com.example.traders.watchlist.cryptoData.binance24hTickerData.PriceTickerData
import com.example.traders.webSocket.BinanceWSClient
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class CryptoChartViewModelTest : TestCoroutineScope by TestCoroutineScope() {

    @Rule
    @JvmField
    val rule: RuleChain = RuleChain.outerRule(TestDispatcherRule()).around(InstantTaskExecutorRule())

    private val repository: CryptoRepository = mock()
    private val webSocketClient: BinanceWSClient = mock()

    private var priceTicker = PriceTicker()

    @Test
    fun init_noMatchedSymbol_emitDefaultState() = runBlockingTest {
        whenever(webSocketClient.state).thenReturn(createSharedFlowWithFirstEmit())

        val fixture = initFixture()

        fixture.chartState.test {
            assertEquals(ChartState(), awaitItem())
        }
    }

    @Test
    fun init_matchedSymbol_emitPriceTickerData() = runBlockingTest {
        priceTicker = PriceTicker(PriceTickerData(symbol = FixedCryptoList.BTC.name + "USDT"))
        whenever(webSocketClient.state).thenReturn(createSharedFlowWithFirstEmit())

        val fixture = initFixture("bitcoin")

        fixture.chartState.test {
            assertEquals(ChartState(tickerData = priceTicker), awaitItem())
        }
    }

    private fun initFixture(slug: String = ""): CryptoChartViewModel {
        return CryptoChartViewModel(
            repository = repository,
            webSocketClient = webSocketClient,
            slug = slug
        )
    }

    private fun createSharedFlowWithFirstEmit(): MutableSharedFlow<PriceTicker> {
        val sharedFlow = MutableSharedFlow<PriceTicker>(
            1,
            1,
            BufferOverflow.DROP_OLDEST
        )
        sharedFlow.tryEmit(priceTicker)
        return sharedFlow
    }
}
