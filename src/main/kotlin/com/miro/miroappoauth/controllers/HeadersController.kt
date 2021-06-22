package com.miro.miroappoauth.controllers

import com.miro.miroappoauth.utils.getCurrentRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.util.Collections
import javax.servlet.http.HttpSession

@Controller
class HeadersController {

    @GetMapping("/headers")
    fun listUsers(
        session: HttpSession,
        model: Model
    ): String? {
        initModelAttributes(session, model)
        return "headers"
    }

    private fun initModelAttributes(session: HttpSession, model: Model) {
        val request = getCurrentRequest()

        val httpHeaders = Collections.list(request.headerNames)
            .map { headerName -> headerName to request.getHeader(headerName) }
        model.addAttribute("httpHeaders", httpHeaders)
    }
}
