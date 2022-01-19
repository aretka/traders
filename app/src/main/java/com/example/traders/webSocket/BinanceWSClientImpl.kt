package com.example.traders.webSocket

import android.util.Log
import com.example.traders.paramsToJson
import com.example.traders.watchlist.cryptoData.binance24hTickerData.PriceTicker
import com.google.gson.Gson
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class BinanceWSClientImpl(uri: URI) : WebSocketClient(uri), BinanceWSClient {

    private val _state = MutableSharedFlow<PriceTicker>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val state: SharedFlow<PriceTicker>
        get() = _state.asSharedFlow()

    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.e(TAG, "Http status message: ${handshakedata?.httpStatusMessage.orEmpty()}")
        subscribe(listOf("btcusdt", "bnbusdt"), "ticker")
    }

    override fun onMessage(message: String?) {
        val crypto = gson.fromJson(message, PriceTicker::class.java)
        _state.tryEmit(crypto)
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
                paramsToJson(
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
                paramsToJson(
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
        if(isOpen) close()
    }

    override fun restartConnection() {
        if(isClosed) reconnect()
    }

    companion object {
        private const val TAG = "WebSocketClient"
        private val gson = Gson()
    }
}