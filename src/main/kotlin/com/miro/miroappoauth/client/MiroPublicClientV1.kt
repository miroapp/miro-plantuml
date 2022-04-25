package com.miro.miroappoauth.client

import com.miro.miroappoauth.client.v1.CreateLineResp
import com.miro.miroappoauth.client.v1.WidgetV1
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.POST
import org.springframework.web.client.RestTemplate

/**
 * See [https://developers.miro.com/reference](https://developers.miro.com/reference).
 * Note: we use camelCase for json parsing here.
 */
class MiroPublicClientV1(
    private val rest: RestTemplate
) {
    fun createWidget(
        accessToken: String,
        boardId: String,
        widget: WidgetV1
    ): CreateLineResp {
        val headers = HttpHeaders()
        headers.setBearerAuth(accessToken)
        val request = HttpEntity(widget, headers)
        return rest.exchange(
            "/v1/boards/{board_id}/widgets",
            POST, request, CreateLineResp::class.java, boardId
        ).body!!
    }
}
