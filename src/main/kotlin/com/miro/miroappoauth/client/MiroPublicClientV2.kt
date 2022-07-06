package com.miro.miroappoauth.client

import com.miro.miroappoauth.client.v2.*
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpMethod.POST
import org.springframework.web.client.RestTemplate

/**
 * See [miro api reference](https://beta.developers.miro.com/reference/api-reference).
 * Note: we use camelCase for json here.
 */
class MiroPublicClientV2(
    private val rest: RestTemplate
) {
    fun getSelfUser(accessToken: String, userId: Long): UserDto {
        val headers = HttpHeaders()
        headers.setBearerAuth(accessToken!!)
        val request = HttpEntity<Any>(null, headers)
        return rest.exchange("/v2/users/{userId}", HttpMethod.GET, request, UserDto::class.java, userId).body!!
    }

    fun createShape(
        accessToken: String,
        boardId: String,
        createShapeReq: CreateShapeReq
    ): IdResp {
        val headers = HttpHeaders()
        headers.setBearerAuth(accessToken)
        val request = HttpEntity(createShapeReq, headers)
        return rest.exchange(
            "/v2/boards/{board_id}/shapes",
            POST, request, IdResp::class.java,
            boardId
        ).body!!
    }

    fun createImage(
        accessToken: String,
        boardId: String,
        createImageReq: CreateImageReq
    ): IdResp {
        val headers = HttpHeaders()
        headers.setBearerAuth(accessToken)
        val request = HttpEntity(createImageReq, headers)
        return rest.exchange(
            "/v2/boards/{board_id}/images",
            POST, request, IdResp::class.java,
            boardId
        ).body!!
    }

    fun createText(
        accessToken: String,
        boardId: String,
        createTextReq: CreateTextReq
    ): IdResp {
        val headers = HttpHeaders()
        headers.setBearerAuth(accessToken)
        val request = HttpEntity(createTextReq, headers)
        return rest.exchange(
            "/v2/boards/{board_id}/texts",
            POST, request, IdResp::class.java,
            boardId
        ).body!!
    }

    fun createConnector(
        accessToken: String,
        boardId: String,
        createConnectorReq: CreateConnectorReq
    ): IdResp {
        val headers = HttpHeaders().apply { setBearerAuth(accessToken) }
        val request = HttpEntity(createConnectorReq, headers)

        return rest.exchange(
            "/v2-experimental/boards/{board_id}/connectors",
            POST, request, IdResp::class.java,
            boardId
        ).body!!
    }
}
