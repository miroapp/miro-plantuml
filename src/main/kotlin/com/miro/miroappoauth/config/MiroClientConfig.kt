package com.miro.miroappoauth.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies.LOWER_CAMEL_CASE
import com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT
import com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
import com.miro.miroappoauth.client.LoggingInterceptor
import com.miro.miroappoauth.client.MiroClient
import org.springframework.boot.web.client.RootUriTemplateHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate

@Configuration
class MiroClientConfig {

    @Bean
    fun miroClient(appProperties: AppProperties): MiroClient {
        val restTemplate = RestTemplate().apply {
            requestFactory = HttpClientFactory().defaultRequestFactory()
            messageConverters = listOf(
                FormHttpMessageConverter(),
                MappingJackson2HttpMessageConverter(clientObjectMapper()),
                StringHttpMessageConverter()
            )
            interceptors = listOf(LoggingInterceptor())
            uriTemplateHandler = RootUriTemplateHandler(appProperties.miroApiBaseUrl)
        }
        return MiroClient(appProperties, restTemplate)
    }

    private fun clientObjectMapper() = Jackson2ObjectMapperBuilder()
        .propertyNamingStrategy(LOWER_CAMEL_CASE)
        .featuresToEnable(INDENT_OUTPUT)
        .featuresToDisable(WRITE_DATES_AS_TIMESTAMPS)
        .build<ObjectMapper>()
}
