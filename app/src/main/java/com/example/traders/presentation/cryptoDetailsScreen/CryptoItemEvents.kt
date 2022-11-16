package com.example.traders.presentation.cryptoDetailsScreen

sealed class CryptoItemEvents {
    object AddToFavourites : CryptoItemEvents()
    object RemoveFromFavourites : CryptoItemEvents()
}
