package com.miro.miroappoauth.model

import java.net.URI
import java.time.Instant

/**
 * For UI view.
 */
data class TokenRecord(
    /**
     * Only access token value
     */
    val accessTokenValue: String,
    /**
     * Serialized AccessTokenDto
     */
    val accessToken: String,
    var state: TokenState,
    val createdTime: Instant,
    var lastAccessedTime: Instant?,
    val checkValidUrl: URI,
    val refreshUrl: URI?
)
