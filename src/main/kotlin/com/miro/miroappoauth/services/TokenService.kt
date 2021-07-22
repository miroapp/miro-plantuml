package com.miro.miroappoauth.services

import com.miro.miroappoauth.client.MiroAuthClient
import com.miro.miroappoauth.client.MiroClient
import com.miro.miroappoauth.dto.AccessTokenDto
import com.miro.miroappoauth.dto.UserDto
import com.miro.miroappoauth.model.Token
import com.miro.miroappoauth.model.TokenState
import com.miro.miroappoauth.model.TokenState.INVALID
import com.miro.miroappoauth.model.TokenState.NEW
import com.miro.miroappoauth.model.TokenState.VALID
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException.Unauthorized
import java.time.Instant

@Service
class TokenService(
    private val tokenStore: TokenStore,
    private val miroAuthClient: MiroAuthClient,
    private val miroClient: MiroClient
) {

    fun getAccessToken(code: String, redirectUri: String, clientId: Long): AccessTokenDto {
        val accessToken = miroAuthClient.getAccessToken(code, redirectUri)
        storeToken(accessToken, clientId)
        return accessToken
    }

    fun revokeToken(accessToken: String) {
        try {
            miroAuthClient.revokeToken(accessToken)
            updateToken(accessToken, INVALID)
        } catch (e: Unauthorized) {
            updateToken(accessToken, INVALID)
            throw e
        }
    }

    fun refreshToken(accessTokenValue: String): AccessTokenDto {
        val token = tokenStore.getToken(accessTokenValue)
            ?: throw IllegalStateException("Missing accessToken $accessTokenValue")
        val accessToken = token.accessToken
        try {
            if (accessToken.refreshToken == null) {
                throw IllegalStateException("refresh_token is null for $accessToken")
            }
            val refreshedToken = miroAuthClient.refreshToken(accessToken.refreshToken)
            storeToken(refreshedToken, token.clientId)
            updateToken(accessToken.accessToken, INVALID)
            return refreshedToken
        } catch (e: Unauthorized) {
            updateToken(accessToken.accessToken, INVALID)
            throw e
        }
    }

    fun getSelfUser(accessToken: String): UserDto {
        try {
            val self = miroClient.getSelfUser(accessToken)
            updateToken(accessToken, VALID)
            return self
        } catch (e: Unauthorized) {
            updateToken(accessToken, INVALID)
            throw e
        }
    }

    fun getToken(userId: Long, clientId: Long, teamId: Long): Token? {
        return tokenStore.getToken(userId, clientId, teamId)
    }

    fun getTokens(userId: Long, clientId: Long): List<Token> {
        return tokenStore.getTokens(userId, clientId)
    }

    fun updateToken(accessToken: String, state: TokenState) {
        val token = tokenStore.getToken(accessToken) ?: throw IllegalStateException("Missing token $accessToken")
        token.state = state
        token.lastAccessedTime = Instant.now()
        tokenStore.update(token)
    }

    private fun storeToken(accessToken: AccessTokenDto, clientId: Long) {
        val token = Token(
            accessToken = accessToken, clientId = clientId, state = NEW,
            createdTime = Instant.now(), lastAccessedTime = null
        )
        try {
            tokenStore.insert(token)
        } catch (e: DuplicateKeyException) {
            tokenStore.update(token)
        }
    }
}
