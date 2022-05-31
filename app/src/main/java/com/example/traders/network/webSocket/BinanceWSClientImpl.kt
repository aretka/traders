package com.example.traders.network.webSocket

import android.os.Build
import android.util.Log
import com.example.traders.utils.MappingUtils
import com.example.traders.utils.MappingUtils.enumConstantNames
import com.example.traders.utils.returnTickerWithRoundedPrice
import com.example.traders.database.FixedCryptoList
import com.example.traders.network.models.binance24hTickerData.PriceTicker
import com.example.traders.network.models.binance24hTickerData.PriceTickerData
import com.google.gson.Gson
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.inject.Inject

class BinanceWSClientImpl @Inject constructor() : WebSocketClient(WEB_SOCKET_URI), BinanceWSClient {

    private val _state = MutableSharedFlow<PriceTickerData>(
        extraBufferCapacity = 30,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val state: SharedFlow<PriceTickerData>
        get() = _state.asSharedFlow()

    override fun onOpen(handshakedata: ServerHandshake?) {
        // Geting string list of enum names
        val cryptoList = FixedCryptoList::class.enumConstantNames().toMutableList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cryptoList.replaceAll { slug ->
                slug.lowercase() + "usdt"
            }
        }

        Log.e(TAG, "Http status message: ${handshakedata?.httpStatusMessage.orEmpty()}")
        subscribe(cryptoList, "ticker")
    }

    override fun onMessage(message: String?) {
        val crypto = gson.fromJson(message, PriceTicker::class.java)
        crypto.data?.let {
            _state.tryEmit(returnTickerWithRoundedPrice(it))
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.e(TAG, "onClose called")
    }

    override fun onError(ex: Exception?) {
        Log.e(TAG, "onError called", ex)
    }

    override fun subscribe(params: List<String>, type: String) {
        if (isOpen) {
            send(
                MappingUtils.paramsToJson(
                    params = params,
                    subscription = "SUBSCRIBE",
                    type = type
                )
            )
        }
    }

    override fun unsubscribe(params: List<String>, type: String) {
        if (isOpen) {
            send(
                MappingUtils.paramsToJson(
                    params = params,
                    subscription = "UNSUBSCRIBE",
                    type = type
                )
            )
        }
    }

    override fun startConnection() {
        if (!isOpen) {
            try {
                connect()
            } catch (ex: Exception) {
                Log.e(TAG, "coudln't start connection", ex)
            }
        }
    }

    override fun stopConnection() {
        if (isOpen) close()
    }

    override fun restartConnection() {
        if (isClosed) reconnect()
    }

    companion object {
        private const val TAG = "WebSocketClient"
        private val gson = Gson()
        private val WEB_SOCKET_URI = URI("wss://stream.binance.com:9443/stream")
    }
}