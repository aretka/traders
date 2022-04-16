package com.example.traders.singleCryptoScreen

sealed class CryptoItemEvents {
    object AddToFavourites : CryptoItemEvents()
    object RemoveFromFavourites : CryptoItemEvents()
}