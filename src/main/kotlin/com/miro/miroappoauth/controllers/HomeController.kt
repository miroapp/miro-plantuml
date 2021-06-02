package com.miro.miroappoauth.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.miro.miroappoauth.client.MiroAuthClient
import com.miro.miroappoauth.config.AppProperties
import com.miro.miroappoauth.dto.AccessTokenDto
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.util.Collections
import java.util.UUID
import javax.servlet.http.HttpSession

@Controller
class HomeController(
    private val appProperties: AppProperties,
    private val miroAuthClient: MiroAuthClient,
    private val objectMapper: ObjectMapper
) {

    @GetMapping("/")
    fun listUsers(
        session: HttpSession,
        model: Model
    ): String? {
        initModelAttributes(session, model)
        return "index"
    }

    @GetMapping("/install")
    fun install(
        session: HttpSession,
        model: Model,
        @RequestParam("code") code: String,
        @RequestParam(name = "state", required = false) state: String?
    ): String {
        val userId = getUserId(session)
        // todo state signed by JWT
        if (state != userId) {
            throw IllegalArgumentException("Unexpected state $state, should be $userId")
        }

        // resolve value by current request URL, but omit query parameters
        val redirectUri = ServletUriComponentsBuilder.fromCurrentRequest()
            .query(null)
            .toUriString()

        val accessToken = miroAuthClient.getAccessToken(code, redirectUri)
        storeToken(session, accessToken)

        initModelAttributes(session, model)
        return "index"
    }

    private fun storeToken(session: HttpSession, accessToken: AccessTokenDto) {
        // usually we should not store DTO objects, just to simplify for now
        val attrName = "$ATTR_ACCESS_TOKEN_PREFIX${accessToken.accessToken}"
        session.setAttribute(attrName, objectMapper.writeValueAsString(accessToken))
    }

    private fun initModelAttributes(session: HttpSession, model: Model) {
        val redirectUri = ServletUriComponentsBuilder.fromCurrentRequest()
            .replacePath("/install")
            .query(null)
            .build().toUri()
        val webPlugin = ServletUriComponentsBuilder.fromCurrentRequest()
            .replacePath("/webapp-sdk1/index.html")
            .query(null)
            .build().toUri()
        val userId = getUserId(session)

        model.addAttribute("sessionId", session.id)
        model.addAttribute("userId", userId)
        model.addAttribute("miroBaseUrl", appProperties.miroBaseUrl)
        model.addAttribute("clientId", appProperties.clientId)
        model.addAttribute("redirectUri", redirectUri)
        model.addAttribute("webPlugin", webPlugin)
        model.addAttribute("authorizeLink", getAuthorizeLink(redirectUri, state = userId))

        val accessTokens = Collections.list(session.attributeNames)
            .filter { it.startsWith(ATTR_ACCESS_TOKEN_PREFIX) }
            .map { attrName -> session.getAttribute(attrName) }
        model.addAttribute("accessTokens", accessTokens)
    }

    private fun getAuthorizeLink(redirectUri: URI, state: String): String {
        return UriComponentsBuilder.fromHttpUrl(appProperties.miroBaseUrl)
            .path("/oauth/authorize")
            .queryParam("response_type", "code")
            .queryParam("client_id", appProperties.clientId)
            .queryParam("redirect_uri", redirectUri)
            .queryParam("state", state)
//            .queryParam("team_id", teamId)
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

const val ATTR_ACCESS_TOKEN_PREFIX = "accessToken-"
