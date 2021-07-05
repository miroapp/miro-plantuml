package com.miro.miroappoauth.client

import com.fasterxml.jackson.databind.JsonNode
import com.miro.miroappoauth.config.AppProperties
import com.miro.miroappoauth.dto.AccessTokenDto
import com.miro.miroappoauth.dto.UserDto
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate

/**
 * See [Miro REST API](https://developers.miro.com/reference).
 * Note: we use camelCase for json parsing here.
 */
class MiroClient(
    private val appProperties: AppProperties,
    private val rest: RestTemplate
) {

    /**
     * See [Miro REST API Authorization](https://developers.miro.com/reference#oauth-20-authorization-v2).
     * Note: we use snake_case for json parsing here.
     */
    fun getAccessToken(code: String, redirectUri: String): AccessTokenDto {
        val form = LinkedMultiValueMap<String, String>()
        form.add("grant_type", "authorization_code")
        form.add("client_id", appProperties.clientId.toString())
        form.add("client_secret", appProperties.clientSecret)
        form.add("code", code)
        form.add("redirect_uri", redirectUri)

        return rest.postForObject("/v1/oauth/token", form, AccessTokenDto::class.java)!!
    }

    /**
     * Note: we use snake_case for json parsing here.
     */
    fun refreshToken(refreshToken: String): AccessTokenDto {
        val form = LinkedMultiValueMap<String, String>()
        form.add("grant_type", "refresh_token")
        form.add("client_id", appProperties.clientId.toString())
        form.add("refresh_token", refreshToken)
        form.add("client_secret", appProperties.clientSecret)

        return rest.postForObject("/v1/oauth/token", form, AccessTokenDto::class.java)!!
    }

    /**
     * [Revoking tokens](https://developers.miro.com/reference#section-revoking-tokens)
     */
    fun revokeToken(accessToken: String) {
        val request = HttpEntity<Any>(null, HttpHeaders())

        rest.exchange(
            "/v1/oauth/revoke?access_token={access_token}", POST, request, JsonNode::class.java,
            accessToken
        )
    }

    fun getSelfUser(accessToken: String): UserDto {
        val headers = HttpHeaders().apply { setBearerAuth(accessToken) }
        val request = HttpEntity<Any>(null, headers)

        return rest.exchange("/v1/users/me", GET, request, UserDto::class.java).body!!
    }
}
