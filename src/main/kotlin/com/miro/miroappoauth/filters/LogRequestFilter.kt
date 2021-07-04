package com.miro.miroappoauth.filters

import org.slf4j.LoggerFactory
import org.springframework.web.filter.AbstractRequestLoggingFilter
import javax.servlet.http.HttpServletRequest

class LogRequestFilter : AbstractRequestLoggingFilter() {

    private val log = LoggerFactory.getLogger(LogRequestFilter::class.java)

    override fun beforeRequest(request: HttpServletRequest, message: String) {
        log.info(message)
    }

    override fun afterRequest(request: HttpServletRequest, message: String) {}
}
