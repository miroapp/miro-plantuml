package com.miro.miroappoauth.client;

import com.miro.miroappoauth.dto.CreateRectangleReq;
import com.miro.miroappoauth.dto.CreateRectangleResp;
import lombok.val;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpMethod.POST;

/**
 * See <a href="https://beta.developers.miro.com/reference/api-reference">miro api reference</a>.
 * Note: we use camelCase for json here.
 */
public class MiroPublicV2Client {

    private final RestTemplate rest;

    public MiroPublicV2Client(RestTemplate rest) {
        this.rest = rest;
    }

    public CreateRectangleResp createRectangle(
            String accessToken,
            String boardId,
            CreateRectangleReq createRectangleReq
    ) {
        val headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        val request = new HttpEntity<>(createRectangleReq, headers);
        return rest.exchange("/v2/boards/{board_id}/shapes", POST, request, CreateRectangleResp.class, boardId).getBody();
    }
}
