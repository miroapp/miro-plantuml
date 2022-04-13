package com.miro.miroappoauth.controllers

import com.miro.miroappoauth.dto.SubmitPlantumlReq
import com.miro.miroappoauth.services.MiroService
import com.miro.miroappoauth.services.RenderService
import net.sourceforge.plantuml.code.TranscoderUtil
import org.springframework.web.bind.annotation.*

@RestController
class CallRestRenderController(
    private val miroService: MiroService,
    private val renderService: RenderService
) {

    @GetMapping("/get-preview-url")
    fun getPreviewUrl(
        @RequestParam("payload") payload: String
    ): String {
        val t = TranscoderUtil.getDefaultTranscoder()
        val encoded = t.encode(payload)
        return "https://www.plantuml.com/plantuml/png/$encoded"
    }

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
