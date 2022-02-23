package com.example.traders.dialogs.buyDialog

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.traders.database.Crypto
import com.example.traders.dialogs.DialogValidation
import com.example.traders.repository.CryptoRepository
import com.example.traders.utils.roundFormatBigDecimal
import com.example.traders.utils.roundNum
import com.example.traders.watchlist.TestDispatcherRule
import com.example.traders.watchlist.cryptoData.FixedCryptoList
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class BuyDialogViewModelTest : TestCoroutineScope by TestCoroutineScope() {

    @Rule
    @JvmField
    val rule: RuleChain =
        RuleChain.outerRule(TestDispatcherRule()).around(InstantTaskExecutorRule())

    private val repository: CryptoRepository = mock()

    @Before
    fun setUp() = runBlockingTest {
        // by default emit successful responses
        whenever(repository.getCryptoBySymbol("USD")).thenReturn(createUnemptyUsdResponse())
        whenever(repository.getCryptoBySymbol(CRYPTO.name)).thenReturn(createSuccessCryptoResponse())
    }

    @Test
    fun init_sucessfulUnemptyBalanceFetch_usdBalanceUnEmpty() = runBlockingTest {
        val expectedBalance = BigDecimal(1000)
        val fixture = init_fixture()

        fixture.state.test {
            assertEquals(expectedBalance, awaitItem().usdBalance.amount)
        }
    }

    @Test
    fun init_onNonexistantRoomUsdFetch_usdBalanceIsZero() = runBlockingTest {
        whenever(repository.getCryptoBySymbol("USD")).thenReturn(null)

        val fixture = init_fixture()

        fixture.state.test {
            assertEquals(Crypto(symbol = "USD"), awaitItem().usdBalance)
        }
    }

    @Test
    fun init_onSuccessfulRoomCryptoFetch_cryptoBalanceNonnull() = runBlockingTest {
        val fixture = init_fixture()

        fixture.state.test {
            assertTrue(awaitItem().cryptoBalance != null)
        }
    }

    @Test
    fun init_onNonexistantRoomCryptoFetch_cryptoBalanceZero() = runBlockingTest {
        whenever(repository.getCryptoBySymbol(CRYPTO.name)).thenReturn(null)

        val expectedBalance = Crypto(symbol = CRYPTO.name, amount = BigDecimal(0))
        val fixture = init_fixture()

        fixture.state.test {
            assertEquals(expectedBalance, awaitItem().cryptoBalance)
        }
    }

    @Test
    fun onInputChanged_onInvalidInput_emitTransactionButtonDisabled() = runBlockingTest {
        val fixture = init_fixture()

        fixture.onInputChanged("")
        fixture.state.test {
            assertFalse(awaitItem().isBtnEnabled)
        }

    }

    @Test
    fun onInputChanged_onValidInput_emitBuyButtonEnabled() = runBlockingTest {
        val fixture = init_fixture()

        fixture.onInputChanged("15.24")
        fixture.state.test {
            assertTrue(awaitItem().isBtnEnabled)
        }
    }

    @Test
    fun onInputChanged_inputValid_emitSuccessUsdLeftBalance() = runBlockingTest {
        val fixture = init_fixture()
        val expected = BigDecimal(1000).minus(BigDecimal(123.01)).roundNum()

        fixture.onInputChanged("123.01")
        fixture.state.test {
            assertEquals(expected, awaitItem().usdLeft)
        }
    }

    @Test
    fun onInputChanged_onInputInValid_emitSuccessUsdLeftBalance() = runBlockingTest {
        val fixture = init_fixture()
        val expected1 = USD_BALANCE.minus(BigDecimal(123.01)).roundNum()
        val expected2 = USD_BALANCE.minus(BigDecimal(1000.00)).roundNum()
        val expected3 = USD_BALANCE.minus(BigDecimal(10)).roundNum()

        val job = launch {
            fixture.state.test {
                awaitItem()
                awaitItem()
                assertEquals(expected1, awaitItem().usdLeft)
                awaitItem()
                assertEquals(expected2, awaitItem().usdLeft)
                awaitItem()
                assertEquals(expected3, awaitItem().usdLeft)
            }
        }
        fixture.onInputChanged("123.01")
        fixture.onInputChanged("1000")
        fixture.onInputChanged("10")
        job.join()
        job.cancel()
    }

    @Test
    fun onInputChanged_onInputInvalid_emitUsdLeftBalance_asOriginalBalance() = runBlockingTest {
        val fixture = init_fixture()
        val expected = USD_BALANCE.roundNum()

        val job = launch {
            fixture.state.test {
                awaitItem()
                awaitItem()
                assertEquals(expected, awaitItem().usdLeft)
                awaitItem()
                assertEquals(expected, awaitItem().usdLeft)
                awaitItem()
                assertEquals(expected, awaitItem().usdLeft)
            }
        }
        // Too high
        fixture.onInputChanged("1232.01")
        // Empty
        fixture.onInputChanged("")
        // Too low
        fixture.onInputChanged("4")
        job.join()
        job.cancel()
    }



    private fun init_fixture(): BuyDialogViewModel {
        return BuyDialogViewModel(
            repository,
            DialogValidation(),
            CRYPTO,
            CRYPTO_PRICE
        )
    }

    private fun createSuccessCryptoResponse(): Crypto {
        return Crypto(
            symbol = CRYPTO.name,
            amount = BigDecimal(10)
        )
    }

    private fun createUnemptyUsdResponse(): Crypto {
        return Crypto(
            symbol = "USD",
            amount = USD_BALANCE
        )
    }

    companion object {
        private val USD_BALANCE = BigDecimal(1000)
        private val CRYPTO = FixedCryptoList.BTC
        private val CRYPTO_PRICE = BigDecimal(100)
    }

}