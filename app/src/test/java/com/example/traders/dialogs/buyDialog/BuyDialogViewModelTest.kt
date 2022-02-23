package com.example.traders.dialogs.buyDialog

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.traders.database.Crypto
import com.example.traders.dialogs.DialogValidation
import com.example.traders.repository.CryptoRepository
import com.example.traders.watchlist.TestDispatcherRule
import com.example.traders.watchlist.cryptoData.FixedCryptoList
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class BuyDialogViewModelTest : TestCoroutineScope by TestCoroutineScope() {

    @Rule
    @JvmField
    val rule: RuleChain =
        RuleChain.outerRule(TestDispatcherRule()).around(InstantTaskExecutorRule())

    private val repository: CryptoRepository = mock()

    @Test
    fun init_sucessfulUnemptyBalanceFetch_stateUsdBalanceUnEmpty() = runBlockingTest {
        whenever(repository.getCryptoBySymbol("USD")).thenReturn(createUnemptyUsdResponse())

        val expectedBalance = BigDecimal(1000)
        val fixture = init_fixture()

        fixture.state.test {
            assertEquals(expectedBalance, awaitItem().usdBalance.amount)
        }
    }

    @Test
    fun init_onNonexistantRoomUsdFetch_stateUsdBalanceIsZero() = runBlockingTest {
        whenever(repository.getCryptoBySymbol("USD")).thenReturn(null)

        val fixture = init_fixture()

        fixture.state.test {
            assertEquals(Crypto(symbol = "USD"), awaitItem().usdBalance)
        }
    }

    @Test
    fun init_onSuccessfulRoomCryptoFetch_stateCryptoBalanceNonnull() = runBlockingTest {
        whenever(repository.getCryptoBySymbol("BTC")).thenReturn(createSuccessCryptoResponse())

        val fixture = init_fixture()

        fixture.state.test {
            assertTrue(awaitItem().cryptoBalance != null)
        }
    }

    @Test
    fun init_onNonexistantRoomCryptoFetch_stateCryptoBalanceZero() = runBlockingTest {
        whenever(repository.getCryptoBySymbol("BTC")).thenReturn(null)

        val expectedBalance = Crypto(symbol = "BTC", amount = BigDecimal(0))
        val fixture = init_fixture()

        fixture.state.test {
            assertEquals(expectedBalance, awaitItem().cryptoBalance)
        }
    }

    private fun init_fixture(): BuyDialogViewModel {
        return BuyDialogViewModel(
            repository,
            DialogValidation(),
            FixedCryptoList.BTC,
            BigDecimal(10000)
        )
    }

    private fun createSuccessCryptoResponse(): Crypto {
        return Crypto(
            symbol = "BTC",
            amount = BigDecimal(1000)
        )
    }

    private fun createUnemptyUsdResponse(): Crypto {
        return Crypto(
            symbol = "USD",
            amount = BigDecimal(1000)
        )
    }

}