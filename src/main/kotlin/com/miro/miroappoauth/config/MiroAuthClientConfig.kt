package com.miro.miroappoauth.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.miro.miroappoauth.client.LoggingInterceptor
import com.miro.miroappoauth.client.MiroAuthClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate

@Configuration
class MiroAuthClientConfig {

    @Bean
    fun crossoverClient(appProperties: AppProperties): MiroAuthClient {
        val objectMapper = Jackson2ObjectMapperBuilder()
            .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .featuresToEnable(SerializationFeature.INDENT_OUTPUT)
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build<ObjectMapper>()
        val restTemplate = RestTemplate().apply {
            requestFactory = HttpClientFactory().defaultRequestFactory()
            messageConverters = listOf(
                FormHttpMessageConverter(),
                MappingJackson2HttpMessageConverter(objectMapper),
                StringHttpMessageConverter()
            )
            interceptors = listOf(LoggingInterceptor())
        }
        return MiroAuthClient(appProperties, restTemplate)
    }
}
