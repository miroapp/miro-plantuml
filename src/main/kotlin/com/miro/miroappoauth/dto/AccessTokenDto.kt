package com.miro.miroappoauth.dto

import java.io.Serializable

data class AccessTokenDto(
    val userId: Long,
    val tokenType: String,
    val teamId: Long,
    val accessToken: String,
    val refreshToken: String?,
    val scope: String,
    val expiresIn: Int
) : Serializable
