package com.example.traders.singleCryptoScreen.descriptionTab

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.traders.network.repository.CryptoRepository
import com.example.traders.TestDispatcherRule
import com.example.traders.database.FixedCryptoList
import com.example.traders.network.models.cryptoDescData.CryptoDescData
import com.example.traders.presentation.cryptoDetailsScreen.descriptionTab.CryptoDescriptionViewModel
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import retrofit2.Response

@ExperimentalCoroutinesApi
class CryptoDescriptionViewModelTest() : TestCoroutineScope by TestCoroutineScope() {
    @Rule
    @JvmField
    val rule: RuleChain =
        RuleChain.outerRule(TestDispatcherRule()).around(InstantTaskExecutorRule())

    private val repository: CryptoRepository = mock()

    @Test
    fun init_successfulFetch_valuesNotNull() = runBlockingTest {
        whenever(repository.getCryptoDescriptionData(any())).thenReturn(emitSuccessDescriptionData())
        val fixture = initFixture()
        val job = launch {
            fixture.descState.test {
                awaitItem()
                assertEquals("", awaitItem().preHistoryDesc)
                assertEquals("", awaitItem().projectInfoDesc)
            }
        }
        fixture.fetchCryptoPriceStatistics(FixedCryptoList.BTC)
        job.join()
        job.cancel()
    }

    private fun emitSuccessDescriptionData(): Response<CryptoDescData>? {
        return Response.success(CryptoDescData())
    }

    @Test
    fun init_successfulFetch_valuesNull() = runBlockingTest {
        whenever(repository.getCryptoDescriptionData(any())).thenReturn(null)
        val fixture = initFixture()
        val job = launch {
            fixture.descState.test {
                awaitItem()
                assertEquals(null, awaitItem().preHistoryDesc)
                assertEquals(null, awaitItem().projectInfoDesc)
            }
        }
        fixture.fetchCryptoPriceStatistics(FixedCryptoList.BTC)
        job.join()
        job.cancel()
    }

    private fun initFixture() = CryptoDescriptionViewModel(repository)
}