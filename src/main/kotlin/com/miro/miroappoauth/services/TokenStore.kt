package com.miro.miroappoauth.services

import com.miro.miroappoauth.model.Token
import org.springframework.stereotype.Service

@Service
class TokenStore {
    private val tokens: MutableMap<String, Token> = LinkedHashMap()

    fun store(accessToken: String, token: Token) {
        tokens[accessToken] = token
    }

    fun get(accessToken: String): Token? {
        return tokens[accessToken]
    }

    fun get(userId: Long, teamId: Long): Token? {
        var token: Token? = null
        tokens.values.forEach {
            // Note: we take the last one (not the first).
            // The order is by createdTime asc.
            if (it.accessToken.userId == userId && it.accessToken.teamId == teamId) {
                token = it
            }
        }
        return token
    }
}
