package com.miro.miroappoauth.client

import com.miro.miroappoauth.dto.AccessType
import com.miro.miroappoauth.dto.BoardDto
import com.miro.miroappoauth.dto.CreateBoardDto
import com.miro.miroappoauth.dto.CreateBoardDto.SharingPolicyDto
import com.miro.miroappoauth.dto.TeamAccessType
import com.miro.miroappoauth.dto.UserDto
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.web.client.RestTemplate

/**
 * See [Miro REST API](https://developers.miro.com/reference).
 * Note: we use camelCase for json parsing here.
 */
class MiroClient(
    private val rest: RestTemplate
) {
    fun getSelfUser(accessToken: String): UserDto {
        val headers = HttpHeaders().apply { setBearerAuth(accessToken) }
        val request = HttpEntity<Any>(null, headers)

        return rest.exchange("/v1/users/me", GET, request, UserDto::class.java).body!!
    }

    fun createBoard(
        accessToken: String,
        name: String,
        accessType: AccessType,
        teamAccessType: TeamAccessType
    ): BoardDto {
        val headers = HttpHeaders().apply { setBearerAuth(accessToken) }
        val body = CreateBoardDto(name, SharingPolicyDto(accessType, teamAccessType))
        val request = HttpEntity<Any>(body, headers)

        return rest.exchange("/v2alpha/boards", POST, request, BoardDto::class.java).body!!
    }

    // todo GET https://api.miro.com/v1/oauth-token
}
