package com.miro.miroappoauth.dto

import java.time.Instant

data class UserDto(
    // "user", todo enum?
    val type: String,
    val id: Long,
    val name: String,
    val createdAt: Instant,
    val role: String,
    val email: String,
    // "registered", todo enum?
    val state: String
)
