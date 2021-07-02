package com.miro.miroappoauth.model

import com.miro.miroappoauth.dto.AccessTokenDto
import java.io.Serializable
import java.time.Instant

/**
 * Stored in TokenStore
 */
data class Token(
    val accessToken: AccessTokenDto,
    var state: TokenState,
    val createdTime: Instant,
    var lastAccessedTime: Instant?
) : Serializable

enum class TokenState {
    NEW,
    VALID,
    INVALID
}
