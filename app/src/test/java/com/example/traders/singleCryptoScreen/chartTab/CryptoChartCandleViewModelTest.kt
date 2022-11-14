package com.example.traders.singleCryptoScreen.chartTab

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.traders.TestDispatcherRule
import com.example.traders.database.FixedCryptoList
import com.example.traders.extensions.ChartDataExtensions.createChartCandle
import com.example.traders.extensions.ChartDataExtensions.createChartCandleResponse
import com.example.traders.extensions.ChartDataExtensions.createPriceTicker
import com.example.traders.network.models.binance24hTickerData.PriceTickerData
import com.example.traders.network.webSocket.BinanceWSClient
import com.example.traders.presentation.cryptoDetailsScreen.chartTab.BtnId
import com.example.traders.presentation.cryptoDetailsScreen.chartTab.ChartRepository
import com.example.traders.presentation.cryptoDetailsScreen.chartTab.ChartState
import com.example.traders.presentation.cryptoDetailsScreen.chartTab.CryptoChartViewModel
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class CryptoChartCandleViewModelTest : TestCoroutineScope by TestCoroutineScope() {

    @Rule
    @JvmField
    val rule: RuleChain =
        RuleChain.outerRule(TestDispatcherRule()).around(InstantTaskExecutorRule())

    private val repository: ChartRepository = mock()
    private val webSocketClient: BinanceWSClient = mock()
    private lateinit var fixture: CryptoChartViewModel

    private val testTickerData = createPriceTicker()
    private val testCandleChartData = createChartCandleResponse()

    @Before
    fun setUp() = runBlocking {
        whenever(webSocketClient.state).thenReturn(createSharedFlowWithFirstEmit(testTickerData))
        whenever(repository.getBinanceCandleData(any(), any())).thenReturn(testCandleChartData)
        initFixture()
    }

    @Test
    fun `on new ticker update state tickerData`() = runBlockingTest {
        val expectedState = ChartState(mainTickerData = testTickerData.toCryptoChartCandle())

        fixture.chartState.test {
            assertEquals(expectedState.mainTickerData, awaitItem().mainTickerData)
        }
    }

    @Test
    fun `on new ticker update state latestCryptoTickerPrice`() = runBlockingTest {
        val expectedState = ChartState(
            latestCryptoTickerPrice = testTickerData.toCryptoChartCandle()
        )

        fixture.chartState.test {
            assertEquals(expectedState.latestCryptoTickerPrice, awaitItem().latestCryptoTickerPrice)
        }
    }

    @Test
    fun `on init and success candle response update 90d candle data`() = runBlockingTest {
        fixture.chartState.test {
            val actualState = awaitItem()
            assertEquals(testCandleChartData, actualState.chartCandleDataFor90D)
        }
    }

    @Test
    fun `on init and success candle response update 360d candle data`() = runBlockingTest {
        fixture.chartState.test {
            val actualState = awaitItem()
            assertEquals(testCandleChartData, actualState.chartCandleDataFor360D)
        }
    }

    @Test
    fun `on init and success candle response enable buttons`() = runBlockingTest {
        fixture.chartState.test {
            val actualState = awaitItem()
            assertTrue(actualState.chartBtnsEnabled)
        }
    }

    @Test
    fun `on chart button pressed active button updated`() = runBlockingTest {
        val pressedBtnId = BtnId.MONTH1_BTN
        fixture.onChartBtnSelected(pressedBtnId)
        fixture.chartState.test {
            assertEquals(pressedBtnId, awaitItem().activeButtonId)
        }
    }

    @Test
    fun `on multiple chart button press previous btn state saved`() = runBlockingTest {
        val firstPressId = BtnId.MONTH1_BTN
        val secondPressId = BtnId.MONTH3_BTN
        fixture.onChartBtnSelected(firstPressId)
        fixture.onChartBtnSelected(secondPressId)
        fixture.chartState.test {
            assertEquals(firstPressId, awaitItem().prevActiveButtonId)
        }
    }

    @Test
    fun `chart long click when valid candle show chart price`() = runBlockingTest {
        val clickedCandle = createChartCandle()
        fixture.onChartLongPressClick(clickedCandle)
        fixture.chartState.test {
            assertTrue(awaitItem().showChartPrice)
        }
    }

    @Test
    fun `chart long click when null candle dont show chart price`() = runBlockingTest {
        val clickedCandle = null
        fixture.onChartLongPressClick(clickedCandle)
        fixture.chartState.test {
            assertTrue(!awaitItem().showChartPrice)
        }
    }

    @Test
    fun `chart long click when valid candle update main ticker`() = runBlockingTest {
        val clickedCandle = createChartCandle()
        fixture.onChartLongPressClick(clickedCandle)
        fixture.chartState.test {
            assertEquals(clickedCandle, awaitItem().mainTickerData)
        }
    }

    @Test
    fun `chart long click when null candle update main ticker with previous`() = runBlockingTest {
        val firstCandleClick = createChartCandle()
        val secondCandleClick = null
        fixture.onChartLongPressClick(firstCandleClick)
        fixture.onChartLongPressClick(secondCandleClick)
        fixture.chartState.test {
            assertEquals(testTickerData.toCryptoChartCandle(), awaitItem().mainTickerData)
        }
    }

    private fun initFixture(crypto: FixedCryptoList = FixedCryptoList.BTC) {
        fixture =  CryptoChartViewModel(
            repository = repository,
            webSocketClient = webSocketClient,
            crypto = crypto
        )
    }

    private fun createSharedFlowWithFirstEmit(
        priceTicker: PriceTickerData
    ) : SharedFlow<PriceTickerData> {
        val sharedFlow = MutableSharedFlow<PriceTickerData>(
            1,
            1,
            BufferOverflow.DROP_OLDEST
        )
        sharedFlow.tryEmit(priceTicker)
        return sharedFlow
    }
}
