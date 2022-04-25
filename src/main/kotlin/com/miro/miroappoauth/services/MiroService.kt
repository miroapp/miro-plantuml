package com.miro.miroappoauth.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.miro.miroappoauth.client.MiroPublicClientV2
import com.miro.miroappoauth.config.AppProperties
import com.miro.miroappoauth.exceptions.UnauthorizedException
import com.miro.miroappoauth.model.Token
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MiroService(
    private val appProperties: AppProperties,
    private val tokenService: TokenService,
    private val miroPublicClientV2: MiroPublicClientV2
) {

    private val log = LoggerFactory.getLogger(MiroService::class.java)

    fun getTokenByJwtToken(jwtToken: String): Token {
        val jwt = JWT.decode(jwtToken)
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

        return tokenService.getToken(
            userId = userId.toLong(),
            clientId = clientId.toLong(),
            teamId = teamId.toLong()
        ) ?: throw UnauthorizedException(
            "Token not found for userId=$userId, teamId=$teamId, clientId=$clientId"
        )
    }
}
