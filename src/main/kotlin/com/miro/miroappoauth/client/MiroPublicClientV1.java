package com.miro.miroappoauth.client;

import com.miro.miroappoauth.client.dtov1.WidgetV1;
import com.miro.miroappoauth.dto.CreateLineResp;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpMethod.POST;

/**
 * See <a href="https://developers.miro.com/reference">https://developers.miro.com/reference</a>.
 * Note: we use camelCase for json parsing here.
 */
public class MiroPublicClientV1 {

    private final RestTemplate rest;

    public MiroPublicClientV1(RestTemplate rest) {
        this.rest = rest;
    }

    public CreateLineResp createWidget(
            String accessToken,
            String boardId,
            WidgetV1 widget
    ) {
        var headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        var request = new HttpEntity<>(widget, headers);
        return rest.exchange("/v1/boards/{board_id}/widgets", POST, request, CreateLineResp.class, boardId).getBody();
    }
}
