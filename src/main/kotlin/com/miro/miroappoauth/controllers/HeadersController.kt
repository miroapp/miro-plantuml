package com.miro.miroappoauth.controllers

import com.miro.miroappoauth.utils.getCurrentRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.util.Collections

/**
 * Helper controller to list HTTP headers of the request.
 */
@Controller
class HeadersController {

    @GetMapping("/headers")
    fun listUsers(model: Model): String? {
        initModelAttributes(model)
        return "headers"
    }

    private fun initModelAttributes(model: Model) {
        val request = getCurrentRequest()

        val httpHeaders = ArrayList<Pair<String, String>>()
        Collections.list(request.headerNames).forEach { headerName ->
            Collections.list(request.getHeaders(headerName)).forEach { headerValue ->
                httpHeaders.add(headerName to headerValue)
            }
        }
        httpHeaders.sortBy { pair -> pair.first }
        model.addAttribute("httpHeaders", httpHeaders)
    }
}
