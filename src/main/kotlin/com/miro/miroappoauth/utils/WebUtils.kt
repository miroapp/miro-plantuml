package com.miro.miroappoauth.utils

import org.springframework.util.Assert
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import javax.servlet.http.HttpServletRequest

fun getCurrentRequest(): HttpServletRequest {
    val attrs = RequestContextHolder.getRequestAttributes()
    Assert.state(attrs is ServletRequestAttributes, "No current ServletRequestAttributes")
    return (attrs as ServletRequestAttributes?)!!.request
}
