package com.miro.miroappoauth.client

import com.miro.miroappoauth.config.AppProperties
import com.miro.miroappoauth.dto.AccessTokenDto
import org.springframework.http.RequestEntity.post
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import java.net.URI

/**
 * Reference https://developers.miro.com/reference#oauth-20-authorization-v2
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

        val req = post(URI.create("${appProperties.miroApiBaseUrl}/v1/oauth/token"))
            .body(form)
        val resp = rest.exchange(req, AccessTokenDto::class.java)
        return resp.body!!
    }
}
