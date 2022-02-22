package com.example.traders.watchlist.singleCryptoScreen.chartTab

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.traders.repository.CryptoRepository
import com.example.traders.watchlist.TestDispatcherRule
import com.example.traders.watchlist.cryptoData.FixedCryptoList
import com.example.traders.watchlist.cryptoData.binance24hTickerData.PriceTickerData
import com.example.traders.watchlist.cryptoData.cryptoChartData.CryptoChartData
import com.example.traders.webSocket.BinanceWSClient
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.ResponseBody
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class CryptoChartViewModelTest : TestCoroutineScope by TestCoroutineScope() {

    @Rule
    @JvmField
    val rule: RuleChain =
        RuleChain.outerRule(TestDispatcherRule()).around(InstantTaskExecutorRule())

    private val repository: CryptoRepository = mock()
    private val webSocketClient: BinanceWSClient = mock()

    private var priceTicker = PriceTickerData()

    @Test
    fun init_emitPriceTickerData() = runBlockingTest {
        priceTicker = PriceTickerData(symbol = FixedCryptoList.BTC.name + "USDT")
        whenever(webSocketClient.state).thenReturn(createSharedFlowWithFirstEmit())

        val expectedState = ChartState(tickerData = priceTicker)

        val fixture = initFixture()
        fixture.collectBinanceTickerData()
        fixture.chartState.test {
            assertEquals(expectedState.tickerData, awaitItem().tickerData)
        }
    }

    @Test
    fun init_onSuccessfulChartDataFetch_emitNonemptyChartArrays() = runBlockingTest {
        whenever(repository.getCryptoChartData(any(), any(), any())).thenReturn(createSuccessChartData())

        val fixture = initFixture()
        fixture.chartState.test {
            val i = awaitItem()
            assertTrue(i.chartDataFor360d != null)
            assertTrue(i.chartDataFor90d != null)
        }
    }

    @Test
    fun init_onSuccessPriceEmitAndChartDataFetch_emitPriceTicker_emitNonemptyChartArray() = runBlockingTest {
        priceTicker = PriceTickerData(symbol = FixedCryptoList.BTC.name + "USDT")
        whenever(webSocketClient.state).thenReturn(createSharedFlowWithFirstEmit())
        whenever(repository.getCryptoChartData(any(), any(), any())).thenReturn(createSuccessChartData())

        val fixture = initFixture()
        val testing = fixture.chartState.take(5)
        testing.test {
            println(awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun onButtonClick_resetActiveButton() = runBlockingTest {
        whenever(repository.getCryptoChartData(any(), any(), any())).thenReturn(createSuccessChartData())

        val fixture = initFixture()
        val job = launch {
            fixture.chartState.test {
                awaitItem()
                assertEquals(BtnId.MONTH12_BTN, awaitItem().activeButtonId)
                assertEquals(BtnId.MONTH6_BTN, awaitItem().activeButtonId)
                assertEquals(BtnId.MONTH6_BTN, awaitItem().activeButtonId)
            }
        }
        fixture.onChartBtnSelected(BtnId.MONTH12_BTN)
        fixture.onChartBtnSelected(BtnId.MONTH6_BTN)
        fixture.onChartBtnSelected(BtnId.MONTH6_BTN)
        job.join()
        job.cancel()
    }

    @Test
    fun init_failedCollectPriceTicker_emitNothing() = runBlockingTest {
//        priceTicker = PriceTickerData(symbol = FixedCryptoList.BTC.name + "USDT")
//        whenever(webSocketClient.state).thenReturn(createSharedFlowWithFirstEmit())
        val fixture = initFixture()
        fixture.chartState.test {
            assertEquals(ChartState(), awaitItem())
        }
    }

    @Test
    fun init_failedChartDataFetch_emitNothing() = runBlockingTest {
        whenever(repository.getCryptoChartData(any(), any(), any())).thenReturn(createSuccessChartData())
        val fixture = initFixture()
        fixture.chartState.test {
            assertEquals(ChartState(), awaitItem())
        }
    }

    private fun initFixture(crypto: FixedCryptoList = FixedCryptoList.BTC): CryptoChartViewModel {
        return CryptoChartViewModel(
            repository = repository,
            webSocketClient = webSocketClient,
            crypto = crypto
        )
    }

    private fun createSuccessChartData(): Response<CryptoChartData> {
        return Response.success(CryptoChartData())
    }

    private fun createSharedFlowWithFirstEmit(): SharedFlow<PriceTickerData> {
        val sharedFlow = MutableSharedFlow<PriceTickerData>(
            1,
            1,
            BufferOverflow.DROP_OLDEST
        )
        sharedFlow.tryEmit(priceTicker)
        return sharedFlow
    }
}