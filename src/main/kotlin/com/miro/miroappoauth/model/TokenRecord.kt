package com.miro.miroappoauth.model

import java.net.URI
import java.time.ZonedDateTime

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
    var state: String,
    val createdTime: ZonedDateTime,
    var lastAccessedTime: ZonedDateTime?,
    val checkValidUrl: URI,
    val refreshUrl: URI?,
    val revokeUrl: URI
)
