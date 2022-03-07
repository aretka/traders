package com.example.traders.singleCryptoScreen.priceStatisticsTab

import androidx.lifecycle.*
import com.example.traders.BaseViewModel
import com.example.traders.repository.CryptoRepository
import com.example.traders.watchlist.cryptoData.FixedCryptoList
import com.example.traders.watchlist.cryptoData.cryptoStatsData.CryptoStatistics
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class CryptoPriceStatisticsViewModel @AssistedInject constructor(
    private val repository: CryptoRepository,
    @Assisted private val crypto: FixedCryptoList
) : BaseViewModel() {
    private val _cryptoStatsResponse = MutableLiveData<CryptoStatistics>()
    val cryptoStatsResponse: LiveData<CryptoStatistics> = _cryptoStatsResponse

    init {
        launch {
            val responseBody = repository.getCryptoPriceStatistics(crypto.slug).body() ?: return@launch
            _cryptoStatsResponse.value = responseBody
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