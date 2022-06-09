package com.example.traders.presentation.cryptoDetailsScreen.priceStatisticsTab

import android.util.Log
import androidx.lifecycle.*
import com.example.traders.presentation.BaseViewModel
import com.example.traders.network.repository.CryptoRepository
import com.example.traders.database.FixedCryptoList
import com.example.traders.network.models.cryptoStatsData.CryptoStatistics
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CryptoPriceStatisticsViewModel @AssistedInject constructor(
    private val repository: CryptoRepository,
    @Assisted private val crypto: FixedCryptoList
) : BaseViewModel() {
    private val _cryptoStatsResponse = MutableLiveData<CryptoStatistics>()
    val cryptoStatsResponse: LiveData<CryptoStatistics> = _cryptoStatsResponse

    init {
        launch {
            Log.d("PriceStatistics", "Running on thread: ${Thread.currentThread().name} ")
            val responseBody = repository.getCryptoPriceStatistics(crypto.slug).body() ?: return@launch
            _cryptoStatsResponse.postValue(responseBody)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(crypto: FixedCryptoList): CryptoPriceStatisticsViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            crypto: FixedCryptoList
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(crypto) as T
            }
        }
    }
}