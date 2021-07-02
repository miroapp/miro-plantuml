package com.miro.miroappoauth.config

import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory

class HttpClientFactory {

    private val connectTimeoutMillis = 10000
    private val readTimeoutMillis = 60000

    fun defaultRequestFactory(): ClientHttpRequestFactory {
        return OkHttp3ClientHttpRequestFactory().apply {
            setConnectTimeout(connectTimeoutMillis)
            setReadTimeout(readTimeoutMillis)
        }
    }
}
