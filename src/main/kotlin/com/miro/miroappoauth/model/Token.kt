package com.miro.miroappoauth.model

import com.miro.miroappoauth.dto.AccessTokenDto
import java.time.Instant

/**
 * Stored in TokenStore
 */
data class Token(
    val accessToken: AccessTokenDto,
    val clientId: Long,
    var state: TokenState,
    val createdTime: Instant,
    var lastAccessedTime: Instant?
)

enum class TokenState {
    NEW,
    VALID,
    INVALID
}
