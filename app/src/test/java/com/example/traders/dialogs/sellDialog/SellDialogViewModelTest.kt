package com.example.traders.dialogs.sellDialog

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.traders.database.Crypto
import com.example.traders.dialogs.DialogValidation
import com.example.traders.network.repository.CryptoRepository
import com.example.traders.utils.roundNum
import com.example.traders.TestDispatcherRule
import com.example.traders.database.FixedCryptoList
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import java.math.BigDecimal

@ExperimentalCoroutinesApi
class SellDialogViewModelTest() : TestCoroutineScope by TestCoroutineScope() {

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
        val fixture = initFixture()

        fixture.state.test {
            assertEquals(expectedBalance, awaitItem().usdBalance.amount)
        }
    }

    @Test
    fun init_onNonexistantRoomUsdFetch_usdBalanceIsZero() = runBlockingTest {
        whenever(repository.getCryptoBySymbol("USD")).thenReturn(null)

        val fixture = initFixture()

        fixture.state.test {
            assertEquals(Crypto(symbol = "USD"), awaitItem().usdBalance)
        }
    }

    @Test
    fun init_onSuccessfulRoomCryptoFetch_cryptoBalanceNonnull() = runBlockingTest {
        val fixture = initFixture()

        fixture.state.test {
            assertTrue(awaitItem().cryptoBalance != null)
        }
    }

    @Test
    fun init_onNonexistantRoomCryptoFetch_cryptoBalanceZero() = runBlockingTest {
        whenever(repository.getCryptoBySymbol(CRYPTO.name)).thenReturn(null)

        val expectedBalance = Crypto(symbol = CRYPTO.name, amount = BigDecimal(0))
        val fixture = initFixture()

        fixture.state.test {
            assertEquals(expectedBalance, awaitItem().cryptoBalance)
        }
    }

    @Test
    fun onInputChanged_onInvalidInput_emitTransactionButtonDisabled() = runBlockingTest {
        val fixture = initFixture()

        val job = launch {
            fixture.state.test {
                awaitItem()
                awaitItem()
                assertFalse(awaitItem().isBtnEnabled)
                awaitItem()
                assertFalse(awaitItem().isBtnEnabled)
            }
        }
        fixture.onInputChanged("")
        fixture.onInputChanged("100")
        job.join()
        job.cancel()

    }

    @Test
    fun onInputChanged_onValidInput_emitBuyButtonEnabled() = runBlockingTest {
        val fixture = initFixture()

        fixture.onInputChanged("1")
        fixture.state.test {
            assertTrue(awaitItem().isBtnEnabled)
        }
    }

    @Test
    fun onInputChanged_inputValid_emitSuccessUsdToGetBalance() = runBlockingTest {
        val fixture = initFixture()
        val expected = BigDecimal(1).times(CRYPTO_PRICE).roundNum()

        fixture.onInputChanged("1")
        fixture.state.test {
            assertEquals(expected, awaitItem().usdToGet)
        }
    }

    @Test
    fun onInputChanged_onValid_emitSuccessUsdToGetBalance() = runBlockingTest {
        val fixture = initFixture()
        val expected1 = CRYPTO_BALANCE.minus(BigDecimal(1)).roundNum(CRYPTO.amountToRound)
        val expected2 = CRYPTO_BALANCE.minus(BigDecimal(1.12)).roundNum(CRYPTO.amountToRound)
        val expected3 = CRYPTO_BALANCE.minus(BigDecimal(10)).roundNum(CRYPTO.amountToRound)

        val job = launch {
            fixture.state.test {
                awaitItem()
                awaitItem()
                kotlin.test.assertEquals(expected1, awaitItem().cryptoLeft)
                awaitItem()
                kotlin.test.assertEquals(expected2, awaitItem().cryptoLeft)
                awaitItem()
                kotlin.test.assertEquals(expected3, awaitItem().cryptoLeft)
            }
        }
        fixture.onInputChanged("1")
        fixture.onInputChanged("1.12")
        fixture.onInputChanged("10")
        job.join()
        job.cancel()
    }

    @Test
    fun onInputChanged_onInputInvalid_emitCryptoToGet_asOriginalBalance() = runBlockingTest {
        val fixture = initFixture()
        val expected = CRYPTO_BALANCE.roundNum(CRYPTO.amountToRound)

        val job = launch {
            fixture.state.test {
                awaitItem()
                awaitItem()
                assertEquals(expected, awaitItem().cryptoLeft)
                awaitItem()
                assertEquals(expected, awaitItem().cryptoLeft)
                awaitItem()
                assertEquals(expected, awaitItem().cryptoLeft)
            }
        }
        // Too high
        fixture.onInputChanged("1232.01")
        // Empty
        fixture.onInputChanged("")
        // Too low
        fixture.onInputChanged("13")
        job.join()
        job.cancel()
    }

    @Test
    fun onInputChanged_onIlleagalChar_emitSuccessValidatedInputValue() = runBlockingTest {
        val fixture = initFixture()
        val job = launch {
            fixture.state.test {
                awaitItem()
                assertEquals("", awaitItem().validatedInputValue)
                assertEquals("1.24", awaitItem().validatedInputValue)
                assertEquals("1.", awaitItem().validatedInputValue)
                assertEquals("1.", awaitItem().validatedInputValue)
            }
        }
        fixture.onInputChanged(".")
        fixture.onInputChanged("1.24.")
        fixture.onInputChanged(".1.")
        fixture.onInputChanged("1..")
        job.join()
        job.cancel()
    }

    @Test
    fun onInputChanged_onIlleagalChar_emitUpdateInputTRUE() = runBlockingTest {
        val fixture = initFixture()
        fixture.onInputChanged("1..")
        fixture.state.test {
            assertTrue(awaitItem().updateInput)
        }
    }

    @Test
    fun onInputFieldUpdated_emitUpdateInputFALSE() = runBlockingTest {
        val fixture = initFixture()
        val job = launch {
            fixture.state.test {
                awaitItem()
                kotlin.test.assertTrue(awaitItem().updateInput)
                kotlin.test.assertFalse(awaitItem().updateInput)
            }
        }
        fixture.onInputChanged("1..")
        fixture.inputUpdated()

        job.join()
        job.cancel()
    }

    @Test
    fun onBuyButtonClicked_emitDismissEvent() = runBlockingTest {
        val fixture = initFixture()
        val job = launch {
            fixture.events.test {
                assertEquals(SellDialogEvent.Dismiss, awaitItem())
            }
        }
        fixture.onSellButtonClicked()
        job.join()
        job.cancel()
    }

    private fun initFixture(): SellDialogViewModel {
        return SellDialogViewModel(
            repository,
            DialogValidation(),
            CRYPTO,
            CRYPTO_PRICE
        )
    }

    private fun createSuccessCryptoResponse(): Crypto {
        return Crypto(
            symbol = CRYPTO.name,
            amount = CRYPTO_BALANCE
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
        private val CRYPTO_BALANCE = BigDecimal(10)
    }
}