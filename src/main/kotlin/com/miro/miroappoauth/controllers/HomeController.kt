package com.miro.miroappoauth.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.miro.miroappoauth.client.MiroClient
import com.miro.miroappoauth.config.AppProperties
import com.miro.miroappoauth.dto.AccessTokenDto
import com.miro.miroappoauth.dto.UserDto
import com.miro.miroappoauth.model.Token
import com.miro.miroappoauth.model.TokenRecord
import com.miro.miroappoauth.model.TokenState
import com.miro.miroappoauth.model.TokenState.INVALID
import com.miro.miroappoauth.model.TokenState.NEW
import com.miro.miroappoauth.model.TokenState.VALID
import com.miro.miroappoauth.services.TokenStore
import com.miro.miroappoauth.utils.getCurrentRequest
import org.springframework.dao.DuplicateKeyException
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
    private val miroClient: MiroClient,
    private val tokenStore: TokenStore,
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

        // resolve value by current request URL, but omit query parameters
        val servletRequest = getCurrentRequest()
        val request = ServletServerHttpRequest(servletRequest)
        val redirectUri = UriComponentsBuilder.fromHttpRequest(request)
            .query(null)
            .toUriString()

        val accessToken = miroClient.getAccessToken(code, redirectUri)
        storeToken(session, accessToken)

        val user = doGetSelfUser(accessToken.accessToken)

        session.setAttribute(SESSION_ATTR_MESSAGE, "Application successfully installed for ${user.name}")
        return "redirect:/"
    }

    @GetMapping(ENDPOINT_CHECK_VALID_TOKEN)
    fun checkValidToken(
        session: HttpSession,
        @RequestParam("access_token") accessToken: String
    ): String {
        try {
            doGetSelfUser(accessToken)
            session.setAttribute(SESSION_ATTR_MESSAGE, "Token is valid")
        } catch (ignore: HttpClientErrorException.Unauthorized) {
            session.setAttribute(SESSION_ATTR_MESSAGE, "Token is not valid")
        }
        return "redirect:/"
    }

    @GetMapping(ENDPOINT_REVOKE_TOKEN)
    fun revokeToken(
        session: HttpSession,
        @RequestParam("access_token") accessToken: String
    ): String {
        try {
            doRevokeToken(accessToken)
            session.setAttribute(SESSION_ATTR_MESSAGE, "Token revoked")
        } catch (e: RestClientException) {
            session.setAttribute(SESSION_ATTR_MESSAGE, "Failed to revoke token")
        }
        return "redirect:/"
    }

    @GetMapping(ENDPOINT_REFRESH_TOKEN)
    fun refreshToken(
        session: HttpSession,
        @RequestParam("access_token") accessToken: String
    ): String {
        val token = tokenStore.get(accessToken)
            ?: throw IllegalStateException("Missing accessToken $accessToken")
        try {
            doRefreshToken(session, token.accessToken)
            session.setAttribute(SESSION_ATTR_MESSAGE, "Successfully refreshed")
        } catch (ignore: HttpClientErrorException.Unauthorized) {
            session.setAttribute(SESSION_ATTR_MESSAGE, "Failed to refresh token")
        }
        return "redirect:/"
    }

    private fun doGetSelfUser(accessToken: String): UserDto {
        try {
            val self = miroClient.getSelfUser(accessToken)
            updateToken(accessToken, VALID)
            return self
        } catch (e: RestClientException) {
            updateToken(accessToken, INVALID)
            throw e
        }
    }

    private fun doRevokeToken(accessToken: String) {
        try {
            miroClient.revokeToken(accessToken)
            updateToken(accessToken, INVALID)
        } catch (e: RestClientException) {
            // todo more precise catch
            updateToken(accessToken, INVALID)
            throw e
        }
    }

    private fun doRefreshToken(session: HttpSession, accessToken: AccessTokenDto): AccessTokenDto {
        try {
            if (accessToken.refreshToken == null) {
                throw IllegalStateException("refresh_token is null for $accessToken")
            }
            val refreshedToken = miroClient.refreshToken(accessToken.refreshToken)
            storeToken(session, refreshedToken)
            updateToken(accessToken.accessToken, INVALID)
            return refreshedToken
        } catch (e: RestClientException) {
            updateToken(accessToken.accessToken, INVALID)
            throw e
        }
    }

    private fun storeToken(
        session: HttpSession,
        accessToken: AccessTokenDto
    ) {
        val token = Token(accessToken, NEW, Instant.now(), null)
        try {
            tokenStore.insert(token)
        } catch (e: DuplicateKeyException) {
            tokenStore.update(token)
        }
        session.setAttribute(sessionAttrName(accessToken.accessToken), "")
    }

    private fun updateToken(accessToken: String, state: TokenState) {
        val token = tokenStore.get(accessToken) ?: throw IllegalStateException("Missing token $accessToken")
        token.state = state
        token.lastAccessedTime = Instant.now()
        tokenStore.update(token)
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
            .map {
                val accessToken = it.substring(ATTR_ACCESS_TOKEN_PREFIX.length)
                tokenStore.get(accessToken)
            }
            .filterNotNull()
            .sortedByDescending { it.createdTime }
            .map { sessionToken ->
                val checkValidUrl = UriComponentsBuilder.fromHttpRequest(request)
                    .replacePath(ENDPOINT_CHECK_VALID_TOKEN)
                    .query(null)
                    .queryParam("access_token", sessionToken.accessToken.accessToken)
                    .build().toUri()
                val revokeUrl = UriComponentsBuilder.fromHttpRequest(request)
                    .replacePath(ENDPOINT_REVOKE_TOKEN)
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
                    revokeUrl = revokeUrl,
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
const val ENDPOINT_REVOKE_TOKEN = "/revoke-token"
const val ENDPOINT_INSTALL = "/install"
