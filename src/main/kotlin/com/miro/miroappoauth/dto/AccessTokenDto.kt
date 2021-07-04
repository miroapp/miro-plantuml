package com.miro.miroappoauth.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.io.Serializable

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class AccessTokenDto(
    val userId: Long,
    val tokenType: String,
    val teamId: Long,
    val accessToken: String,
    val refreshToken: String?,
    val scope: String,
    val expiresIn: Int
) : Serializable
