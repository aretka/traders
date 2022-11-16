package com.example.traders.watchlist
//
// import androidx.arch.core.executor.testing.InstantTaskExecutorRule
// import app.cash.turbine.test
// import com.example.traders.TestDispatcherRule
// import com.example.traders.network.models.binance24HourData.Binance24DataItem
// import com.example.traders.network.models.binance24hTickerData.PriceTickerData
// import com.example.traders.network.repository.CryptoRepository
// import com.example.traders.network.webSocket.BinanceWSClient
// import com.example.traders.presentation.watchlist.WatchListViewModel
// import com.nhaarman.mockitokotlin2.mock
// import com.nhaarman.mockitokotlin2.whenever
// import kotlinx.coroutines.ExperimentalCoroutinesApi
// import kotlinx.coroutines.channels.BufferOverflow
// import kotlinx.coroutines.flow.MutableSharedFlow
// import kotlinx.coroutines.flow.SharedFlow
// import kotlinx.coroutines.launch
// import kotlinx.coroutines.test.TestCoroutineScope
// import kotlinx.coroutines.test.runBlockingTest
// import org.junit.Rule
// import org.junit.Test
// import org.junit.rules.RuleChain
// import retrofit2.Response
// import kotlin.test.assertEquals
// import kotlin.test.assertFalse
// import kotlin.test.assertTrue
//
// @ExperimentalCoroutinesApi
// class WatchListViewModelTest() : TestCoroutineScope by TestCoroutineScope() {
//
//    @Rule
//    @JvmField
//    val rule: RuleChain =
//        RuleChain.outerRule(TestDispatcherRule()).around(InstantTaskExecutorRule())
//
//    private val repository: CryptoRepository = mock()
//    private val webSocketClient: BinanceWSClient = mock()
//
//    @Test
//    fun init_onSuccessfulCryptoFetch_binanceCryptoDataNotNull() = runBlockingTest {
//        whenever(repository.getBinance24Data()).thenReturn(emitSuccessBinanceCryptoData())
//        val fixture = initFixture()
//        fixture.state.test {
//            assertTrue(awaitItem().binanceCryptoData.isNotEmpty())
//        }
//    }
//
//    @Test
//    fun init_onSuccessfulCryptoFetch_binanceCryptoDataEmptyList() = runBlockingTest {
//        whenever(repository.getBinance24Data()).thenReturn(null)
//        val fixture = initFixture()
//        fixture.state.test {
//            assertEquals(emptyList(), awaitItem().binanceCryptoData)
//        }
//    }
//
//    @Test
//    fun init_onStartCollectingWBData_emitListWithUpdatedPrices() = runBlockingTest {
//        whenever(repository.getBinance24Data()).thenReturn(emitSuccessBinanceCryptoData())
//        whenever(webSocketClient.state).thenReturn(emitSharedFlowWithFirstEmit())
//        val expected = listOf(
//            Binance24DataItem(symbol = "BTCUSDT", last = "48456.12"),
//            Binance24DataItem(symbol = "ADAUSDT")
//        )
//        val fixture = initFixture()
//        fixture.state.test {
//            assertEquals(expected, awaitItem().binanceCryptoData)
//        }
//    }
//
//    @Test
//    fun onGetCryptoOnRefresh_emitProperRefreshStates() = runBlockingTest {
//        whenever(repository.getBinance24Data()).thenReturn(emitSuccessBinanceCryptoData())
//        val fixture = initFixture()
//        val job = launch {
//            fixture.state.test {
//                assertFalse(awaitItem().isRefreshing)
//                assertTrue(awaitItem().isRefreshing)
//                assertFalse(awaitItem().isRefreshing)
//            }
//        }
//        fixture.getCryptoOnRefresh()
//        job.join()
//        job.cancel()
//    }
//
//    @Test
//    fun onGetCryptoOnRefresh_emitIsCryptoFetchedTrue() = runBlockingTest {
//        whenever(webSocketClient.state).thenReturn(emitSharedFlowWithFirstEmit())
//        val fixture = initFixture()
//        val job = launch {
//            fixture.state.test {
//                assertFalse(awaitItem().isRefreshing)
//                assertTrue(awaitItem().isRefreshing)
//                assertFalse(awaitItem().isRefreshing)
//            }
//        }
//        fixture.getCryptoOnRefresh()
//        job.join()
//        job.cancel()
//    }
//
//    private fun emitSharedFlowWithFirstEmit(): SharedFlow<PriceTickerData> {
//        val sharedFlow = MutableSharedFlow<PriceTickerData>(
//            replay = 1,
//            extraBufferCapacity = 1,
//            onBufferOverflow = BufferOverflow.DROP_OLDEST
//        )
//        sharedFlow.tryEmit(PriceTickerData(symbol = "BTCUSDT", last = "48456.12"))
//        return sharedFlow
//    }
//
//    private fun emitSuccessBinanceCryptoData(): Response<List<Binance24DataItem>>? {
//        return Response.success(
//            listOf(
//                Binance24DataItem(symbol = "BTCUSDT"),
//                Binance24DataItem(symbol = "ADAUSDT")
//            )
//        )
//    }
//
//    private fun initFixture() = WatchListViewModel(
//        repository = repository,
//        webSocketClient = webSocketClient
//    )
// }
