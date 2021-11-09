package com.example.traders.watchlist

import androidx.lifecycle.ViewModel
import com.example.traders.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class WatchListViewModel @Inject constructor(): BaseViewModel() {
    private val _state = MutableStateFlow(WatchListState())
    val state = _state.asStateFlow()
}
