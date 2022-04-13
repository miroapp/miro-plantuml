package com.miro.miroappoauth.controllers

import com.miro.miroappoauth.dto.UserDto
import com.miro.miroappoauth.services.MiroService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

/**
 * Backend calls from web-plugin (see app.tsx).
 */
@RestController
class CallRestController(
    private val miroService: MiroService
) {

    @GetMapping("/get-self-user")
    fun getSelfUser(
        @RequestHeader(HEADER_X_MIRO_TOKEN) jwtToken: String
    ): UserDto {
        val token = miroService.getTokenByJwtToken(jwtToken)

        return miroService.getSelfUser(token)
    }
}

private const val HEADER_X_MIRO_TOKEN = "X-Miro-Token"
