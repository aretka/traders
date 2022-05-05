package com.example.traders.profile.portfolio

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.cash.turbine.test
import com.example.traders.TestDispatcherRule
import com.example.traders.database.Crypto
import com.example.traders.network.models.CryptoTicker
import com.example.traders.network.repository.CryptoRepository
import com.example.traders.utils.roundNum
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieEntry
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import retrofit2.Response
import java.math.BigDecimal

@ExperimentalCoroutinesApi
class PortfolioViewModelTest() : TestCoroutineScope by TestCoroutineScope() {
    @Rule
    @JvmField
    val rule: RuleChain =
        RuleChain.outerRule(TestDispatcherRule()).around(InstantTaskExecutorRule())

    private val repository: CryptoRepository = mock()

    @Test
    fun onChartUpdated_prevListIsRenewed() = runBlockingTest {
        whenever(repository.getLiveAllCryptoPortfolio()).thenReturn(return2ItemPortfolioList())
        whenever(repository.getBinanceTickerBySymbol(any())).thenReturn(returnSuccessBTCTicker())
        val fixture = initFixture()
        fixture.updatePortfolioState()
        fixture.state.test {
            assertEquals(CRYPTO_LIST_2, awaitItem().prevList)
        }
    }

    @Test
    fun onChartUpdated_emitCorrectCryptoListInUsd() = runBlockingTest {
        whenever(repository.getLiveAllCryptoPortfolio()).thenReturn(return2ItemPortfolioList())
        whenever(repository.getBinanceTickerBySymbol(any())).thenReturn(returnSuccessBTCTicker())
        val fixture = initFixture()
        val expectedList1 = listOf(
            CryptoInUsd(symbol = "USD", BigDecimal(500), BigDecimal(500)),
            CryptoInUsd(symbol = "BTC", BigDecimal(1), BigDecimal(100).roundNum())
        )
        val expectedList2 = listOf(
            CryptoTicker(symbol = "BTC", price = "100")
        )
        fixture.updatePortfolioState()
        fixture.state.test {
            val actual = awaitItem()
            assertEquals(expectedList1, actual.cryptoListInUsd)
            assertEquals(expectedList2, actual.usdPricesFromBinance)
        }
    }

    @Test
    fun onChartUpdated_correctTotalBalance() = runBlockingTest {
        whenever(repository.getLiveAllCryptoPortfolio()).thenReturn(return2ItemPortfolioList())
        whenever(repository.getBinanceTickerBySymbol(any())).thenReturn(returnSuccessBTCTicker())
        val fixture = initFixture()
        fixture.updatePortfolioState()
        fixture.state.test {
            assertEquals(BigDecimal(600).roundNum(), awaitItem().totalPortfolioBalance)
        }
    }

    @Test
    fun onChartUpdated_listSizeBetween1And5_correctChartState() = runBlockingTest {
        //TODO(Finish middle size chart data test)
        whenever(repository.getLiveAllCryptoPortfolio()).thenReturn(return2ItemPortfolioList())
        whenever(repository.getBinanceTickerBySymbol(any())).thenReturn(returnSuccessBTCTicker())
        val fixture = initFixture()
        val expectedChartData = listOf(
            PieEntry(100F)
        )
        fixture.updatePortfolioState()
        fixture.state.test {
            val actual = awaitItem()
//            assertEquals(expectedChartData, actual.chartData)
            assertTrue(actual.chartReadyForUpdate)
            assertTrue(actual.chartDataLoaded)
        }
    }

    @Test
    fun onChartUpdated_listSizeHigherThan5_correctChartState() = runBlockingTest {
        //TODO(Finish big size chart data test)
        whenever(repository.getLiveAllCryptoPortfolio()).thenReturn(return2ItemPortfolioList())
        whenever(repository.getBinanceTickerBySymbol(any())).thenReturn(returnSuccessBTCTicker())
        val fixture = initFixture()
        val expectedChartData = listOf(
            PieData()
        )
        fixture.updatePortfolioState()
        fixture.state.test {
            val actual = awaitItem()
//            assertEquals(expectedChartData, actual.chartData)
            assertTrue(actual.chartReadyForUpdate)
            assertTrue(actual.chartDataLoaded)
        }
    }

    @Test
    fun onChartUpdated_listEmpty_correctChartState() = runBlockingTest {
        whenever(repository.getLiveAllCryptoPortfolio()).thenReturn(returnEmptyPortfolioList())
        val fixture = initFixture()
        val expectedChartData = mutableListOf(PieEntry(100F, "Empty"))
        fixture.updatePortfolioState()
        fixture.state.test {
            val actual = awaitItem()
//            assertEquals(expectedChartData, actual.chartData)
            assertTrue(actual.chartReadyForUpdate)
            assertTrue(actual.chartDataLoaded)
        }
    }
//    expected: java.util.ArrayList<[Entry, x: 0.0 y: 100.0]>
//    actual:   java.util.ArrayList<[Entry, x: 0.0 y: 100.0]>

    private fun returnEmptyPortfolioList(): LiveData<List<Crypto>> {
        return MutableLiveData(emptyList())
    }

    private fun return2ItemPortfolioList(): LiveData<List<Crypto>> {
        return MutableLiveData(CRYPTO_LIST_2)
    }

    private fun returnSuccessBTCTicker() = Response.success(BTC_TICKER)
    private fun returnSuccessADATicker() = Response.success(ADA_TICKER)
    private fun returnSuccessXRPTicker() = Response.success(XRP_TICKER)
    private fun returnSuccessBCHTicker() = Response.success(BCH_TICKER)
    private fun returnSuccessETCTicker() = Response.success(ETC_TICKER)

    private fun initFixture() = PortfolioViewModel(repository)

    companion object{
        private val CRYPTO_LIST_2 = listOf(
            Crypto(symbol = "USD", amount = BigDecimal(500)),
            Crypto(symbol = "BTC", amount = BigDecimal(1))
        )

        private val CRYPTO_LIST_6 = listOf(
            Crypto(symbol = "USD", amount = BigDecimal(500)),
            Crypto(symbol = "BTC", amount = BigDecimal(1)),
            Crypto(symbol = "ADA", amount = BigDecimal(2)),
            Crypto(symbol = "XRP", amount = BigDecimal(3)),
            Crypto(symbol = "BCH", amount = BigDecimal(4)),
            Crypto(symbol = "ETC", amount = BigDecimal(5))
        )

        private val BTC_TICKER = CryptoTicker("BTC", "100")
        private val ADA_TICKER = CryptoTicker("ADA", "100")
        private val XRP_TICKER = CryptoTicker("XRP", "100")
        private val BCH_TICKER = CryptoTicker("BCH", "100")
        private val ETC_TICKER = CryptoTicker("ETC", "100")
    }

}