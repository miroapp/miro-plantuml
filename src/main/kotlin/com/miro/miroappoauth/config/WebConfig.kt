package com.miro.miroappoauth.config

import com.miro.miroappoauth.filters.LogRequestFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebConfig {

    @Bean
    fun loggingFilter(): FilterRegistrationBean<LogRequestFilter> {
        val filter = LogRequestFilter()
        filter.setIncludeQueryString(true)
        filter.setIncludeHeaders(true)
        filter.setIncludePayload(true)
        filter.setMaxPayloadLength(1024)
        return FilterRegistrationBean(filter).apply {
            order = 10
        }
    }
}
