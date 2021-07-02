package com.miro.miroappoauth.model

import com.miro.miroappoauth.dto.AccessTokenDto
import java.io.Serializable
import java.time.Instant

data class SessionToken(
    val accessToken: AccessTokenDto,
    var state: TokenState,
    var lastAccessedTime: Instant?
) : Serializable

enum class TokenState {
    NEW,
    VALID,
    INVALID
}
