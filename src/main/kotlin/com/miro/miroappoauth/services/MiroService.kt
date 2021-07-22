package com.miro.miroappoauth.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.miro.miroappoauth.client.MiroClient
import com.miro.miroappoauth.config.AppProperties
import com.miro.miroappoauth.dto.UserDto
import com.miro.miroappoauth.exceptions.UnauthorizedException
import com.miro.miroappoauth.model.Token
import com.miro.miroappoauth.model.TokenState.INVALID
import com.miro.miroappoauth.model.TokenState.VALID
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException.Unauthorized

@Service
class MiroService(
    private val appProperties: AppProperties,
    private val tokenService: TokenService,
    private val miroClient: MiroClient
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

    fun getSelfUser(token: Token): UserDto {
        return doRequest(token) { accessToken -> miroClient.getSelfUser(accessToken) }
    }

    /**
     * Do an HTTP request re-authorizing (via refresh_token) if current access token has expired.
     */
    private fun <T> doRequest(token: Token, caller: (accessToken: String) -> T): T {
        return try {
            val result = caller(token.accessTokenValue())
            tokenService.updateToken(token.accessTokenValue(), VALID)
            result
        } catch (e: Unauthorized) {
            tokenService.updateToken(token.accessTokenValue(), INVALID)
            if (token.accessToken.refreshToken == null) {
                throw e
            }
            val newAccessToken = tokenService.refreshToken(token.accessTokenValue())
            caller(newAccessToken.accessToken)
        }
    }
}
