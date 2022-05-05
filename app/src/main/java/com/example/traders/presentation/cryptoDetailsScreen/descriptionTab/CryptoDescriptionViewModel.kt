package com.example.traders.presentation.cryptoDetailsScreen.descriptionTab

import androidx.lifecycle.viewModelScope
import com.example.traders.presentation.BaseViewModel
import com.example.traders.network.repository.CryptoRepository
import com.example.traders.database.FixedCryptoList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CryptoDescriptionViewModel
@Inject constructor(private val repository: CryptoRepository) : BaseViewModel() {
    private val _descState = MutableStateFlow(DescriptionState())
    val descState = _descState.asStateFlow()

    fun fetchCryptoPriceStatistics(crypto: FixedCryptoList) {
        viewModelScope.launch {
            val responseBody =
                repository.getCryptoDescriptionData(crypto.slug).body() ?: return@launch
            _descState.value = _descState.value.copy(
                projectInfoDesc = responseBody.data.profile.general.overview.project_details,
                preHistoryDesc = responseBody.data.profile.general.background.background_details
            )
        }
    }
}