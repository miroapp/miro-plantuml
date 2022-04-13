package com.miro.miroappoauth.client;

import com.miro.miroappoauth.dto.UserDto;
import lombok.val;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpMethod.GET;

/**
 * See <a href="https://developers.miro.com/reference">https://developers.miro.com/reference</a>.
 * Note: we use camelCase for json parsing here.
 */
public class MiroPublicV1Client {

    private final RestTemplate rest;

    public MiroPublicV1Client(RestTemplate rest) {
        this.rest = rest;
    }

    public UserDto getSelfUser(String accessToken)  {
        val headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        val request = new HttpEntity<>(null, headers);

        return rest.exchange("/v1/users/me", GET, request, UserDto.class).getBody();
    }
}
