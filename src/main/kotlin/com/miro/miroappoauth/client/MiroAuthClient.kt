package com.miro.miroappoauth.client

import com.miro.miroappoauth.config.AppProperties
import com.miro.miroappoauth.dto.AccessTokenDto
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate

/**
 * See [Miro REST API Authorization](https://developers.miro.com/reference#oauth-20-authorization-v2).
 * Note: we use snake_case for json parsing here.
 */
class MiroAuthClient(
    private val appProperties: AppProperties,
    private val rest: RestTemplate
) {

    fun getAccessToken(code: String, redirectUri: String): AccessTokenDto {
        val form = LinkedMultiValueMap<String, String>()
        form.add("grant_type", "authorization_code")
        form.add("client_id", appProperties.clientId.toString())
        form.add("client_secret", appProperties.clientSecret)
        form.add("code", code)
        form.add("redirect_uri", redirectUri)

        return rest.postForObject("/v1/oauth/token", form, AccessTokenDto::class.java)!!
    }

    fun refreshToken(refreshToken: String): AccessTokenDto {
        val form = LinkedMultiValueMap<String, String>()
        form.add("grant_type", "refresh_token")
        form.add("client_id", appProperties.clientId.toString())
        form.add("refresh_token", refreshToken)
        form.add("client_secret", appProperties.clientSecret)

        return rest.postForObject("/v1/oauth/token", form, AccessTokenDto::class.java)!!
    }
}
