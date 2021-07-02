package com.miro.miroappoauth.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.miro.miroappoauth.client.MiroAuthClient
import com.miro.miroappoauth.client.MiroClient
import com.miro.miroappoauth.config.AppProperties
import com.miro.miroappoauth.dto.AccessTokenDto
import com.miro.miroappoauth.dto.UserDto
import com.miro.miroappoauth.model.SessionToken
import com.miro.miroappoauth.model.TokenRecord
import com.miro.miroappoauth.model.TokenState
import com.miro.miroappoauth.model.TokenState.INVALID
import com.miro.miroappoauth.model.TokenState.NEW
import com.miro.miroappoauth.model.TokenState.VALID
import com.miro.miroappoauth.utils.getCurrentRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClientException
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.time.Instant
import java.util.Collections
import java.util.UUID
import javax.servlet.http.HttpSession

@Controller
class HomeController(
    private val appProperties: AppProperties,
    private val miroAuthClient: MiroAuthClient,
    private val miroClient: MiroClient,
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
        val userId = getUserId(session)
        // todo state signed by JWT
        if (state != userId) {
            throw IllegalArgumentException("Unexpected state $state, should be $userId")
        }

        // resolve value by current request URL, but omit query parameters
        val servletRequest = getCurrentRequest()
        val request = ServletServerHttpRequest(servletRequest)
        val redirectUri = UriComponentsBuilder.fromHttpRequest(request)
            .query(null)
            .toUriString()

        val accessToken = miroAuthClient.getAccessToken(code, redirectUri)
        storeToken(session, accessToken)

        val user = getSelfUser(session, accessToken.accessToken)

        session.setAttribute(SESSION_ATTR_MESSAGE, "Application successfully installed for ${user.name}")
        return "redirect:/"
    }

    @GetMapping(ENDPOINT_CHECK_VALID_TOKEN)
    fun checkValidToken(
        session: HttpSession,
        @RequestParam("access_token") accessToken: String
    ): String {
        try {
            getSelfUser(session, accessToken)
        } catch (ignore: HttpClientErrorException.Unauthorized) {
        }
        return "redirect:/"
    }

    @GetMapping(ENDPOINT_REFRESH_TOKEN)
    fun refreshToken(
        session: HttpSession,
        @RequestParam("access_token") accessToken: String
    ): String {
        val attrName = sessionAttrName(accessToken)
        val sessionToken = session.getAttribute(attrName) as SessionToken?
            ?: throw IllegalStateException("Missing accessToken $accessToken")
        try {
            refreshToken(session, sessionToken.accessToken)
        } catch (ignore: HttpClientErrorException.Unauthorized) {
        }
        return "redirect:/"
    }

    private fun getSelfUser(session: HttpSession, accessToken: String): UserDto {
        try {
            val self = miroClient.getSelfUser(accessToken)
            updateToken(session, accessToken, VALID)
            return self
        } catch (e: RestClientException) {
            updateToken(session, accessToken, INVALID)
            throw e
        }
    }

    private fun refreshToken(session: HttpSession, accessToken: AccessTokenDto): AccessTokenDto {
        try {
            if (accessToken.refreshToken == null) {
                throw IllegalStateException("refresh_token is null for $accessToken")
            }
            val refreshedToken = miroAuthClient.refreshToken(accessToken.refreshToken)
            storeToken(session, refreshedToken)
            updateToken(session, accessToken.accessToken, INVALID)
            return refreshedToken
        } catch (e: RestClientException) {
            updateToken(session, accessToken.accessToken, INVALID)
            throw e
        }
    }

    private fun storeToken(
        session: HttpSession,
        accessToken: AccessTokenDto
    ) {
        // usually we should not store DTO objects, just to simplify for now
        val attrName = sessionAttrName(accessToken.accessToken)
        session.setAttribute(attrName, SessionToken(accessToken, NEW, Instant.now(), null))
    }

    private fun updateToken(session: HttpSession, accessToken: String, state: TokenState) {
        val attrName = sessionAttrName(accessToken)
        val sessionToken = session.getAttribute(attrName) as SessionToken
        sessionToken.state = state
        sessionToken.lastAccessedTime = Instant.now()
        session.setAttribute(attrName, sessionToken)
    }

    private fun sessionAttrName(accessToken: String) = "$ATTR_ACCESS_TOKEN_PREFIX$accessToken"

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
            .map { session.getAttribute(it) as SessionToken }
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
                TokenRecord(
                    accessTokenValue = sessionToken.accessToken.accessToken,
                    accessToken = objectMapper.writeValueAsString(sessionToken.accessToken),
                    state = sessionToken.state,
                    createdTime = sessionToken.createdTime,
                    lastAccessedTime = sessionToken.lastAccessedTime,
                    checkValidUrl = checkValidUrl,
                    refreshUrl = refreshUrl
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
const val ENDPOINT_INSTALL = "/install"
