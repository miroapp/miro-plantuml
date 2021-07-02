package com.miro.miroappoauth.model

import com.miro.miroappoauth.dto.AccessTokenDto
import java.io.Serializable
import java.time.Instant

/**
 * Stored in HttpSession
 */
data class SessionToken(
    val accessToken: AccessTokenDto,
    var state: TokenState,
    var lastAccessedTime: Instant?
) : Serializable {
    val created: Instant = Instant.now()
}

enum class TokenState {
    NEW,
    VALID,
    INVALID
}
