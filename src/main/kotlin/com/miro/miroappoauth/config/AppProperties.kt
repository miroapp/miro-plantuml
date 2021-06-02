package com.miro.miroappoauth.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "app")
data class AppProperties(
    val miroBaseUrl: String,
    val miroApiBaseUrl: String,
    val clientId: String,
    val clientSecret: String
)
