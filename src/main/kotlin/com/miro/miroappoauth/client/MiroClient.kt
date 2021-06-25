package com.miro.miroappoauth.client

import com.miro.miroappoauth.dto.UserDto
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.GET
import org.springframework.web.client.RestTemplate

/**
 * Reference https://developers.miro.com/reference#oauth-20-authorization-v2
 */
class MiroClient(
    private val rest: RestTemplate
) {

    fun getSelfUser(token: String): UserDto {
        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        val request = HttpEntity<Any>(null, headers)

        return rest.exchange("/v1/users/me", GET, request, UserDto::class.java).body!!
    }
}
