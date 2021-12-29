package com.example.traders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

open class BaseViewModel : ViewModel(), CoroutineScope {

    private val _errorEvents = MutableSharedFlow<ErrorEvent>()
    val errorEvent = _errorEvents.asSharedFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        launch {
            _errorEvents.emit(ErrorEvent(Exception(throwable)))
        }
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext + exceptionHandler
}