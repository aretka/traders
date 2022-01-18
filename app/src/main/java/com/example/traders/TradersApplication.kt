package com.example.traders

import android.app.Application
import com.example.traders.debug.FlipperInitializer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
open class TradersApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        FlipperInitializer.init(this)
    }
}
