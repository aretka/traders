package com.example.traders.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthenticationInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
            .addHeader("X-MBX-APIKEY", "zHehIzQC9KdvgFnjc5P1LPFmWYRa4xwCxlE7uaITOKaxQxkKvcVtcUrczqyS8wTN")
            .build()

        return chain.proceed(newRequest)
    }
}