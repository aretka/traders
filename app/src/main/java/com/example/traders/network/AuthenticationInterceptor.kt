package com.example.traders.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthenticationInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
            .addHeader("X-MBX-APIKEY", "")
            .build()

        return chain.proceed(newRequest)
    }
}