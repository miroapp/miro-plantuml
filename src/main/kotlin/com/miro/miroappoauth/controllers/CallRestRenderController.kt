package com.miro.miroappoauth.controllers

import com.miro.miroappoauth.dto.SubmitPlantumlReq
import com.miro.miroappoauth.services.MiroService
import com.miro.miroappoauth.services.RenderService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class CallRestRenderController(
    private val miroService: MiroService,
    private val renderService: RenderService
) {
    @PostMapping("/submit-plantuml")
    fun submitPlantUml(
        @RequestHeader(HEADER_X_MIRO_TOKEN) jwtToken: String?,
        @RequestBody submitPlantumlReq: SubmitPlantumlReq?
    ): String {
        val token = miroService.getTokenByJwtToken(jwtToken!!)
        renderService.render(token.accessTokenValue(), submitPlantumlReq!!)
        return "done"
    }
}

private const val HEADER_X_MIRO_TOKEN = "X-Miro-Token"
