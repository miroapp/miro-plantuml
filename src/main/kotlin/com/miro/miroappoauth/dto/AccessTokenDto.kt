package com.miro.miroappoauth.dto

data class AccessTokenDto(
    val userId: Long,
    val tokenType: String,
    val teamId: Long,
    val accessToken: String,
    val refreshToken: String?,
    val scope: String,
    val expiresIn: Int
)
