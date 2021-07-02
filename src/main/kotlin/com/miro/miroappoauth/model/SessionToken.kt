package com.miro.miroappoauth.model

import com.miro.miroappoauth.dto.AccessTokenDto
import java.time.Instant

data class SessionToken(
    val accessToken: AccessTokenDto,
    var state: TokenState,
    var lastAccessedTime: Instant?
)

enum class TokenState {
    NEW,
    VALID,
    INVALID
}
