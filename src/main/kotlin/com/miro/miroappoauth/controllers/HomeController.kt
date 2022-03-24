package com.miro.miroappoauth.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.miro.miroappoauth.config.AppProperties
import com.miro.miroappoauth.model.TokenRecord
import com.miro.miroappoauth.model.TokenState.INVALID
import com.miro.miroappoauth.services.TokenService
import com.miro.miroappoauth.utils.getCurrentRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException.Unauthorized
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.Collections.emptyList
import javax.servlet.http.HttpSession

/**
 * Main page with listing tokens and installation.
 */
@Controller
class HomeController(
    private val appProperties: AppProperties,
    private val tokenService: TokenService,
    private val objectMapper: ObjectMapper
) {

    @GetMapping("/")
    fun indexPage(
        session: HttpSession,
        model: Model
    ): String? {
        initModelAttributes(session, model)
        val message = session.getAttribute(SESSION_ATTR_MESSAGE)
        if (message != null) {
            model.addAttribute("message", message)
            session.removeAttribute(SESSION_ATTR_MESSAGE)
        }
        return "index"
    }

    // todo AJAX action
    @GetMapping(ENDPOINT_CHECK_VALID_TOKEN)
    fun checkValidToken(
        session: HttpSession,
        @RequestParam("access_token") accessToken: String
    ): String {
        try {
            tokenService.getSelfUser(accessToken)
            session.setAttribute(SESSION_ATTR_MESSAGE, "Token is valid")
        } catch (e: Unauthorized) {
            session.setAttribute(SESSION_ATTR_MESSAGE, "Token is not valid")
        }
        return "redirect:/#access_tokens"
    }

    // todo AJAX action
    @GetMapping(ENDPOINT_REFRESH_TOKEN)
    fun refreshToken(
        session: HttpSession,
        @RequestParam("access_token") accessToken: String
    ): String {
        try {
            tokenService.refreshToken(accessToken)
            session.setAttribute(SESSION_ATTR_MESSAGE, "Successfully refreshed")
        } catch (ignore: Unauthorized) {
            session.setAttribute(SESSION_ATTR_MESSAGE, "Failed to refresh token")
        }
        return "redirect:/#access_tokens"
    }

    // todo AJAX action
    @GetMapping(ENDPOINT_REVOKE_TOKEN)
    fun revokeToken(
        session: HttpSession,
        @RequestParam("access_token") accessToken: String
    ): String {
        try {
            tokenService.revokeToken(accessToken)
            session.setAttribute(SESSION_ATTR_MESSAGE, "Token revoked")
        } catch (e: Unauthorized) {
            session.setAttribute(SESSION_ATTR_MESSAGE, "Failed to revoke token")
        }
        return "redirect:/#access_tokens"
    }

    private fun initModelAttributes(session: HttpSession, model: Model) {
        val servletRequest = getCurrentRequest()
        val request = ServletServerHttpRequest(servletRequest)
        // note: we call UriComponentsBuilder.fromHttpRequest here
        // to resolve ngrok-proxied Host header
        // Alternative solution: "server.forward-headers-strategy: framework" in yaml config
        val redirectUri = UriComponentsBuilder.fromHttpRequest(request)
            .replacePath(ENDPOINT_INSTALL)
            .query(null)
            .build().toUri()
        val webPlugin = UriComponentsBuilder.fromHttpRequest(request)
            .replacePath("/webapp-sdk1/index.html").apply {
                query(null)
                if (appProperties.appName != null) {
                    queryParam("appName", appProperties.appName)
                }
            }
            .build().toUri()

        val referer = servletRequest.getHeader(HttpHeaders.REFERER)
        val userId = session.getAttribute(SESSION_ATTR_USER_ID) as Long?

        model.addAttribute("sessionId", session.id)
        model.addAttribute("userId", userId)
        model.addAttribute("miroBaseUrl", appProperties.miroBaseUrl)
        model.addAttribute("clientId", appProperties.clientId)
        model.addAttribute("redirectUri", redirectUri)
        model.addAttribute("webPlugin", webPlugin)
        model.addAttribute("authorizeUrl", getAuthorizeUrl(redirectUri))
        model.addAttribute("installationManagementUrl", getInstallationManagementUrl(appProperties.teamId))
        model.addAttribute("referer", if (referer != servletRequest.requestURL.toString()) referer else null)

        val tokenRecords = if (userId == null) emptyList() else getTokenRecords(userId, request)
        model.addAttribute("tokenRecords", tokenRecords)
    }

    private fun getTokenRecords(userId: Long, request: ServletServerHttpRequest): List<TokenRecord> {
        return tokenService.getTokens(userId, appProperties.clientId)
            .map { token ->
                val checkValidUrl = UriComponentsBuilder.fromHttpRequest(request)
                    .replacePath(ENDPOINT_CHECK_VALID_TOKEN)
                    .query(null)
                    .queryParam("access_token", token.accessTokenValue())
                    .build().toUri()
                val refreshUrl = if (token.accessToken.refreshToken == null) null else
                    UriComponentsBuilder.fromHttpRequest(request)
                        .replacePath(ENDPOINT_REFRESH_TOKEN)
                        .query(null)
                        .queryParam("access_token", token.accessTokenValue())
                        .build().toUri()
                val revokeUrl = UriComponentsBuilder.fromHttpRequest(request)
                    .replacePath(ENDPOINT_REVOKE_TOKEN)
                    .query(null)
                    .queryParam("access_token", token.accessTokenValue())
                    .build().toUri()
                TokenRecord(
                    accessTokenValue = token.accessTokenValue(),
                    accessToken = objectMapper.writeValueAsString(token.accessToken),
                    state = (if (token.state == INVALID) "❌" else "✅") + " ${token.state}",
                    createdTime = OffsetDateTime.ofInstant(token.createdTime, ZoneId.systemDefault()),
                    lastAccessedTime = if (token.lastAccessedTime == null)
                        null else OffsetDateTime.ofInstant(token.lastAccessedTime, ZoneId.systemDefault()),
                    checkValidUrl = checkValidUrl,
                    refreshUrl = refreshUrl,
                    revokeUrl = revokeUrl
                )
            }
    }

    private fun getInstallationManagementUrl(teamId: Long?): String? {
        if (teamId == null) {
            return null
        }
        return UriComponentsBuilder.fromHttpUrl(appProperties.miroBaseUrl)
            .path("/app/settings/team/{teamId}/app-settings/{clientId}")
            .buildAndExpand(teamId, appProperties.clientId)
            .toUriString()
    }

    private fun getAuthorizeUrl(redirectUri: URI/*, state: String*/): String {
        return UriComponentsBuilder.fromHttpUrl(appProperties.miroBaseUrl)
            .path("/oauth/authorize")
            .queryParam("response_type", "code")
            .queryParam("client_id", appProperties.clientId)
            .queryParam("redirect_uri", redirectUri)
            // .queryParam("state", state)
            .apply {
                if (appProperties.teamId != null) {
                    queryParam("team_id", appProperties.teamId)
                }
            }
            .build(false)
            .encode()
            .toUriString()
    }
}

const val SESSION_ATTR_USER_ID = "miro_user_id"

const val SESSION_ATTR_MESSAGE = "message"

const val ENDPOINT_CHECK_VALID_TOKEN = "/check-valid-token"
const val ENDPOINT_REFRESH_TOKEN = "/refresh-token"
const val ENDPOINT_REVOKE_TOKEN = "/revoke-token"
const val ENDPOINT_INSTALL = "/install"
