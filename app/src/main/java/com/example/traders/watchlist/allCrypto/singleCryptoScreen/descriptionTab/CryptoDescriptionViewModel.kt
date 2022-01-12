package com.example.traders.watchlist.allCrypto.singleCryptoScreen.descriptionTab

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.traders.BaseViewModel
import com.example.traders.network.CryptoApi
import com.example.traders.repository.CryptoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CryptoDescriptionViewModel
@Inject constructor(private val repository: CryptoRepository): BaseViewModel() {
    private val _descState = MutableStateFlow(DescriptionState())
    val descState
        get() = _descState.asStateFlow()

    fun fetchCryptoPriceStatistics(id: String) {
        viewModelScope.launch {

            var response = try {
                repository.getCryptoDescriptionData(id)
            } catch (e: IOException) {
                Log.e("ResponseCryptoDesc", "IOException, internet connection interference: ${e}")
                return@launch
            } catch (e: HttpException) {
                Log.e("ResponseCryptoDesc", "HttpException, unexpected response: ${e}")
                return@launch
            }

            if (response.isSuccessful && response.body() != null) {
                val responseData = response.body()
                _descState.value = _descState.value.copy(
                    projectInfoDesc = responseData?.data?.profile?.general?.overview?.project_details,
                    preHistoryDesc = responseData?.data?.profile?.general?.background?.background_details
                )
            } else {
                Log.e("ResponseCryptoDesc", "Response not successful")
            }

        }
    }
}