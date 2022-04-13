package com.miro.miroappoauth.client;

import com.miro.miroappoauth.dto.CreateRectangleReq;
import com.miro.miroappoauth.dto.CreateRectangleResp;
import com.miro.miroappoauth.dto.UserDto;
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

    public CreateRectangleResp createRectangle(
            String accessToken,
            String boardId,
            CreateRectangleReq createRectangleReq
    ) {
        var headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        var request = new HttpEntity<>(createRectangleReq, headers);
        return rest.exchange("/v2/boards/{board_id}/shapes", POST, request, CreateRectangleResp.class, boardId).getBody();
    }
}
