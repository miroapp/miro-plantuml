package com.miro.miroappoauth.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.miro.miroappoauth.config.AppProperties
import com.miro.miroappoauth.dto.AccessTokenDto
import com.miro.miroappoauth.model.TokenRecord
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
import java.util.Collections
import java.util.UUID
import javax.servlet.http.HttpSession

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

        val accessToken = tokenService.getAccessToken(code, redirectUri)
        storeToSession(session, accessToken)

        session.setAttribute(SESSION_ATTR_MESSAGE, "Application successfully installed")
        return "redirect:/#access_tokens"
    }

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

    @GetMapping(ENDPOINT_REFRESH_TOKEN)
    fun refreshToken(
        session: HttpSession,
        @RequestParam("access_token") accessToken: String
    ): String {
        try {
            val token = tokenService.refreshToken(accessToken)
            session.setAttribute(SESSION_ATTR_MESSAGE, "Successfully refreshed")
            storeToSession(session, token)
        } catch (ignore: Unauthorized) {
            session.setAttribute(SESSION_ATTR_MESSAGE, "Failed to refresh token")
        }
        return "redirect:/#access_tokens"
    }

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

    private fun storeToSession(session: HttpSession, accessTokenDto: AccessTokenDto) {
        val attrName = "$ATTR_ACCESS_TOKEN_PREFIX${accessTokenDto.accessToken}"
        session.setAttribute(attrName, "")
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
            .replacePath("/webapp-sdk1/index.html")
            .query(null)
            .build().toUri()

        val referer = servletRequest.getHeader(HttpHeaders.REFERER)
        val userId = getUserId(session)

        model.addAttribute("sessionId", session.id)
        model.addAttribute("userId", userId)
        model.addAttribute("miroBaseUrl", appProperties.miroBaseUrl)
        model.addAttribute("clientId", appProperties.clientId)
        model.addAttribute("redirectUri", redirectUri)
        model.addAttribute("webPlugin", webPlugin)
        model.addAttribute("authorizeUrl", getAuthorizeUrl(redirectUri, state = userId))
        model.addAttribute("installationManagementUrl", getInstallationManagementUrl(appProperties.teamId))
        model.addAttribute("referer", referer)

        val tokenRecords = Collections.list(session.attributeNames)
            .filter { it.startsWith(ATTR_ACCESS_TOKEN_PREFIX) }
            .map {
                val accessToken = it.substring(ATTR_ACCESS_TOKEN_PREFIX.length)
                tokenService.getToken(accessToken)
            }
            .filterNotNull()
            .sortedByDescending { it.createdTime }
            .map { sessionToken ->
                val checkValidUrl = UriComponentsBuilder.fromHttpRequest(request)
                    .replacePath(ENDPOINT_CHECK_VALID_TOKEN)
                    .query(null)
                    .queryParam("access_token", sessionToken.accessToken.accessToken)
                    .build().toUri()
                val refreshUrl = if (sessionToken.accessToken.refreshToken == null) null else
                    UriComponentsBuilder.fromHttpRequest(request)
                        .replacePath(ENDPOINT_REFRESH_TOKEN)
                        .query(null)
                        .queryParam("access_token", sessionToken.accessToken.accessToken)
                        .build().toUri()
                val revokeUrl = UriComponentsBuilder.fromHttpRequest(request)
                    .replacePath(ENDPOINT_REVOKE_TOKEN)
                    .query(null)
                    .queryParam("access_token", sessionToken.accessToken.accessToken)
                    .build().toUri()
                TokenRecord(
                    accessTokenValue = sessionToken.accessToken.accessToken,
                    accessToken = objectMapper.writeValueAsString(sessionToken.accessToken),
                    state = sessionToken.state,
                    createdTime = sessionToken.createdTime,
                    lastAccessedTime = sessionToken.lastAccessedTime,
                    checkValidUrl = checkValidUrl,
                    refreshUrl = refreshUrl,
                    revokeUrl = revokeUrl
                )
            }
        model.addAttribute("tokenRecords", tokenRecords)
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

    private fun getAuthorizeUrl(redirectUri: URI, state: String): String {
        return UriComponentsBuilder.fromHttpUrl(appProperties.miroBaseUrl)
            .path("/oauth/authorize")
            .queryParam("response_type", "code")
            .queryParam("client_id", appProperties.clientId)
            .queryParam("redirect_uri", redirectUri)
            .queryParam("state", state)
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

fun getUserId(session: HttpSession): String {
    var userId = session.getAttribute(SESSION_ATTR_USER_ID) as String?
    if (userId == null) {
        userId = UUID.randomUUID().toString()
        session.setAttribute(SESSION_ATTR_USER_ID, userId)
    }
    return userId
}

const val SESSION_ATTR_USER_ID = "user_id"

const val SESSION_ATTR_MESSAGE = "message"

const val ATTR_ACCESS_TOKEN_PREFIX = "accessToken-"

const val ENDPOINT_CHECK_VALID_TOKEN = "/check-valid-token"
const val ENDPOINT_REFRESH_TOKEN = "/refresh-token"
const val ENDPOINT_REVOKE_TOKEN = "/revoke-token"
const val ENDPOINT_INSTALL = "/install"
