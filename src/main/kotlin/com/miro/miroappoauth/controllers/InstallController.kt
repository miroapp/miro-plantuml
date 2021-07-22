package com.miro.miroappoauth.controllers

import com.miro.miroappoauth.config.AppProperties
import com.miro.miroappoauth.services.TokenService
import com.miro.miroappoauth.utils.getCurrentRequest
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.util.UriComponentsBuilder
import javax.servlet.http.HttpSession

/**
 * Serves install action.
 */
@Controller
class InstallController(
    private val tokenService: TokenService,
    private val appProperties: AppProperties
) {

    @GetMapping(ENDPOINT_INSTALL)
    fun install(
        session: HttpSession,
        @RequestParam("code") code: String,
        @RequestParam(name = "state", required = false) state: String?
    ): String {
//        val userId = getUserId(session)
//        // todo state signed by JWT
//        if (state != null && state != userId) {
//            throw IllegalArgumentException("Unexpected state $state, should be $userId")
//        }

        // resolve redirectUri value by current request URL, but omit query parameters
        val servletRequest = getCurrentRequest()
        val request = ServletServerHttpRequest(servletRequest)
        val redirectUri = UriComponentsBuilder.fromHttpRequest(request)
            .query(null)
            .toUriString()

        val accessToken = tokenService.getAccessToken(code, redirectUri, appProperties.clientId)
        session.setAttribute(SESSION_ATTR_USER_ID, accessToken.userId)

        session.setAttribute(SESSION_ATTR_MESSAGE, "Application successfully authorized")
        return "redirect:/#access_tokens"
    }
}
