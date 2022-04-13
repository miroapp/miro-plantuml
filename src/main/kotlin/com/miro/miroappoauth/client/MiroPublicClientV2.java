package com.miro.miroappoauth.client;

import com.miro.miroappoauth.dto.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

/**
 * See <a href="https://beta.developers.miro.com/reference/api-reference">miro api reference</a>.
 * Note: we use camelCase for json here.
 */
public class MiroPublicClientV2 {

    private final RestTemplate rest;

    public MiroPublicClientV2(RestTemplate rest) {
        this.rest = rest;
    }

    public UserDto getSelfUser(String accessToken, long userId)  {
        var headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        var request = new HttpEntity<>(null, headers);

        return rest.exchange("/v2/users/{userId}", GET, request, UserDto.class, userId).getBody();
    }

    public CreateShapeResp createShape(
            String accessToken,
            String boardId,
            CreateShapeReq createShapeReq
    ) {
        var headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        var request = new HttpEntity<>(createShapeReq, headers);
        return rest.exchange("/v2/boards/{board_id}/shapes", POST, request, CreateShapeResp.class, boardId).getBody();
    }

    public CreateImageResp createImage(
            String accessToken,
            String boardId,
            CreateImageReq createImageReq
    ) {
        var headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        var request = new HttpEntity<>(createImageReq, headers);
        return rest.exchange("/v2/boards/{board_id}/images", POST, request, CreateImageResp.class, boardId).getBody();
    }
}
