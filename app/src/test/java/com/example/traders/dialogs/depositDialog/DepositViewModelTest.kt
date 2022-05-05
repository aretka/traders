package com.example.traders.dialogs.depositDialog

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.traders.presentation.dialogs.DialogValidation
import com.example.traders.network.repository.CryptoRepository
import com.example.traders.TestDispatcherRule
import com.example.traders.presentation.dialogs.depositDialog.DepositDialogEvent
import com.example.traders.presentation.dialogs.depositDialog.DepositViewModel
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.mockito.Mock

@ExperimentalCoroutinesApi
class DepositViewModelTest(): TestCoroutineScope by TestCoroutineScope() {

    @Rule
    @JvmField
    val rule: RuleChain =
        RuleChain.outerRule(TestDispatcherRule()).around(InstantTaskExecutorRule())

    @Mock
    private val repository: CryptoRepository = mock()

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
        val job = launch {
            fixture.state.test {
                awaitItem()
                assertTrue(awaitItem().updateInput)
            }
        }
        fixture.onInputChanged(".")
        job.join()
        job.cancel()
    }

    @Test
    fun onInputChanged_onIlleagalChar_emitUpdateInputFALSE() = runBlockingTest {
        val fixture = initFixture()
        val job = launch {
            fixture.state.test {
                awaitItem()
                assertTrue(awaitItem().updateInput)
                assertFalse(awaitItem().updateInput)
            }
        }
        fixture.onInputChanged(".")
        fixture.inputUpdated()
        job.join()
        job.cancel()
    }

    @Test
    fun onInputChanged_inputValid_emitButtonEnabledTRUE() = runBlockingTest {
        val fixture = initFixture()

        val job = launch {
            fixture.state.test {
                awaitItem()
                assertFalse(awaitItem().isBtnEnabled)
                assertFalse(awaitItem().isBtnEnabled)
                assertFalse(awaitItem().isBtnEnabled)
            }
        }
        fixture.onInputChanged("")
        fixture.onInputChanged("10001")
        fixture.onInputChanged("1")

        job.join()
        job.cancel()
    }

    @Test
    fun onInputChanged_inputValid_emitButtonEnabledFALSE() = runBlockingTest {
        val fixture = initFixture()

        val job = launch {
            fixture.state.test {
                awaitItem()
                assertTrue(awaitItem().isBtnEnabled)
                assertTrue(awaitItem().isBtnEnabled)
            }
        }
        fixture.onInputChanged("100")
        fixture.onInputChanged("10000")

        job.join()
        job.cancel()
    }

    @Test
    fun onDepositButtonClicked_emitDissmissEvent() = runBlockingTest {
        val fixture = initFixture()

        val job = launch {
            fixture.events.test {
                awaitItem()
                assertEquals(DepositDialogEvent.Dismiss, awaitItem())
            }
        }
        fixture.onDepositButtonClicked()
        job.join()
        job.cancel()
    }

    private fun initFixture(): DepositViewModel {
        return DepositViewModel(
            repository,
            DialogValidation()
        )
    }
}