package com.example.traders.watchlist

import com.example.traders.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class WatchListViewModel @Inject constructor() : BaseViewModel() {
    private val _state = MutableStateFlow(WatchListState(emptyList()))
    val state = _state.asStateFlow()
}