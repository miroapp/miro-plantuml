package com.miro.miroappoauth.controllers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.miro.miroappoauth.client.MiroClient
import com.miro.miroappoauth.config.AppProperties
import com.miro.miroappoauth.dto.UserDto
import com.miro.miroappoauth.services.TokenStore
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class CallRestController(
    private val appProperties: AppProperties,
    private val tokenStore: TokenStore,
    private val miroClient: MiroClient
) {

    @GetMapping("/call")
    fun call(
        @RequestHeader("X-Miro-Token") idToken: String
    ): UserDto {
        val jwt = JWT.decode(idToken)
        try {
            JWT.require(Algorithm.HMAC256(appProperties.clientSecret))
                .build()
                .verify(jwt)
        } catch (e: JWTVerificationException) {
            throw IllegalStateException("Wrong JWT signature", e)
        }

        val userId = jwt.getClaim("user").asString()
        val teamId = jwt.getClaim("team").asString()
        val token = tokenStore.get(userId = userId.toLong(), teamId = teamId.toLong())
            ?: throw IllegalStateException("Token not found for userId=$userId, teamId=$teamId")
        return miroClient.getSelfUser(token.accessToken.accessToken)
    }
}
