package com.miro.miroappoauth.controllers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.miro.miroappoauth.client.MiroClient
import com.miro.miroappoauth.config.AppProperties
import com.miro.miroappoauth.dto.UserDto
import com.miro.miroappoauth.exceptions.UnauthorizedException
import com.miro.miroappoauth.services.TokenService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException.Unauthorized

@RestController
class CallRestController(
    private val appProperties: AppProperties,
    private val tokenService: TokenService,
    private val miroClient: MiroClient
) {

    private val log = LoggerFactory.getLogger(CallRestController::class.java)

    @GetMapping("/call")
    fun call(
        @RequestHeader("X-Miro-Token") idToken: String
    ): UserDto {
        val jwt = JWT.decode(idToken)
        val claims = jwt.claims
            .map { it.key to it.value.asString() }
            .toMap()
        log.info("Got request $claims")

        val userId = jwt.getClaim("user").asString()
        val clientId = jwt.getClaim("sub").asString()
        val teamId = jwt.getClaim("team").asString()

        if (appProperties.clientId.toString() != clientId) {
            throw UnauthorizedException("Wrong clientId, check backend configuration")
        }

        try {
            JWT.require(Algorithm.HMAC256(appProperties.clientSecret))
                // to avoid clock non-sync issues
                .acceptLeeway(180)
                .build()
                .verify(jwt)
        } catch (e: JWTVerificationException) {
            throw UnauthorizedException("Wrong JWT signature: $e")
        }

        val token = tokenService.getToken(
            userId = userId.toLong(),
            clientId = clientId.toLong(),
            teamId = teamId.toLong()
        ) ?: throw UnauthorizedException(
            "Token not found for userId=$userId, teamId=$teamId, clientId=$clientId"
        )

        return try {
            miroClient.getSelfUser(token.accessTokenValue())
        } catch (e: Unauthorized) {
            // todo refreshToken interceptor
            if (token.accessToken.refreshToken == null) {
                throw e
            }
            val newAccessToken = tokenService.refreshToken(token.accessTokenValue())
            miroClient.getSelfUser(newAccessToken.accessToken)
        }
    }
}
