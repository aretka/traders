package com.example.traders.watchlist

import com.example.traders.database.FavouriteCrypto
import com.example.traders.repository.enumContains
import com.example.traders.watchlist.cryptoData.FixedCryptoList
import com.example.traders.watchlist.cryptoData.binance24HourData.Binance24DataItem
import com.example.traders.watchlist.cryptoData.binance24HourData.BinanceDataItem
import javax.inject.Inject

class WatchListRepository @Inject constructor(
    private val watchListInteractor: WatchListInteractor
) {
    var cryptoPrices: List<BinanceDataItem> = emptyList()
        private set

    suspend fun refreshCryptoPrices() {
        val cryptoPricesResponse = watchListInteractor.getCryptoPricesList() ?: emptyList()
        val extractedPricesList =
            cryptoPricesResponse.getFixedCryptoList().map { it.toBinanceDataItem() }

        cryptoPrices = extractedPricesList.applyFavourites(getFavouriteCryptos())
    }

    suspend fun getFavourites(): List<BinanceDataItem> {
        return cryptoPrices.filter { it.isFavourite }
    }

    private fun List<Binance24DataItem>.getFixedCryptoList(): List<Binance24DataItem> {
        return filter { element ->
            enumContains<FixedCryptoList>(element.symbol.replace("USDT", ""))
        }
    }

    private fun getFavouriteCryptos() = watchListInteractor.getAllFavourites()

    private fun Binance24DataItem.toBinanceDataItem(): BinanceDataItem {
        return BinanceDataItem(
            symbol = symbol,
            last = last,
            high = high,
            low = low,
            open = open,
            priceChange = priceChange,
            priceChangePercent = priceChangePercent
        )
    }

    private fun List<BinanceDataItem>.applyFavourites(favouriteList: List<FavouriteCrypto>?): List<BinanceDataItem> {
        return map { item ->
            item.copy(
                isFavourite = favouriteList?.any {
                    it.symbol == item.symbol.replace(
                        "USDT",
                        ""
                    )
                } == true
            )
        }
    }
}
