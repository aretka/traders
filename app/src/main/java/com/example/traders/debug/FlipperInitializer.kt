package com.example.traders.debug

import android.app.Application
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import com.facebook.soloader.SoLoader
import okhttp3.OkHttpClient

object FlipperInitializer {
    private val networkFlipperPlugin = NetworkFlipperPlugin()

    fun init(application: Application) {
        SoLoader.init(application, false)

        if (!FlipperUtils.shouldEnableFlipper(application)) return

        AndroidFlipperClient.getInstance(application).apply {
            addPlugin(InspectorFlipperPlugin(application, DescriptorMapping.withDefaults()))
            addPlugin(networkFlipperPlugin)
            addPlugin(DatabasesFlipperPlugin(application))
            addPlugin(SharedPreferencesFlipperPlugin(application))
        }.start()
    }

    fun OkHttpClient.Builder.applyFlipperNetworkInterceptor(): OkHttpClient.Builder {
        return this.addNetworkInterceptor(FlipperOkhttpInterceptor(networkFlipperPlugin))
    }
}
