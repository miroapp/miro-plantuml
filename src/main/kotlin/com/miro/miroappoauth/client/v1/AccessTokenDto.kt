package com.miro.miroappoauth.client.v1

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

/**
 * Miro response payload Access Token data.
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class AccessTokenDto(
    val userId: Long,
    val tokenType: String,
    val teamId: Long,
    val accessToken: String,
    val refreshToken: String?,
    val scope: String,
    val expiresIn: Int
)
