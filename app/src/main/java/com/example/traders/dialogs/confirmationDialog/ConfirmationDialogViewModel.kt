package com.example.traders.dialogs.confirmationDialog

import android.util.Log
import com.example.traders.BaseViewModel
import com.example.traders.repository.CryptoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfirmationDialogViewModel @Inject constructor(
    private val repository: CryptoRepository
) : BaseViewModel() {

    private val _events = MutableSharedFlow<ConfirmationDialogEvent>()
    val events = _events.asSharedFlow()

    fun onAcceptButtonClicked() {
        launch {
            repository.deleteAllCryptoFromDb()
            _events.emit(ConfirmationDialogEvent.Dismiss)
        }
    }

    fun onCancelButtonClicked() {
        launch {
            _events.emit(ConfirmationDialogEvent.Dismiss)
        }
    }
}