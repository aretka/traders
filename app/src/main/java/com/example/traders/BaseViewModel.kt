package com.example.traders

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

open class BaseViewModel : ViewModel(), CoroutineScope {

    private val _isLoading = MutableLiveData(false)
    val isLoading
        get() = _isLoading

    private val _errorEvents = MutableSharedFlow<ErrorEvent>()
    val errorEvent = _errorEvents.asSharedFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        launchWithProgress {
            _errorEvents.emit(ErrorEvent(Exception(throwable)))
        }
    }

    protected fun CoroutineScope.launchWithProgress(block: suspend CoroutineScope.() -> Unit) {
        launch {
            try {
                _isLoading.postValue(true)
                block()
            } catch (e: Exception) {
                //Do nothing
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext + exceptionHandler
}