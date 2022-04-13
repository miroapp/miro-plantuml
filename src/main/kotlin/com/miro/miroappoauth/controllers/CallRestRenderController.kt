package com.miro.miroappoauth.controllers

import com.miro.miroappoauth.dto.SubmitPlantumlReq
import com.miro.miroappoauth.dto.SubmitPreviewImageReq
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
        return formatPreviewUrl(payload)
    }

    @PostMapping("/submit-preview-image")
    fun submitPreviewImage(
        @RequestHeader(HEADER_X_MIRO_TOKEN) jwtToken: String,
        @RequestBody req: SubmitPreviewImageReq
    ): String {
        val token = miroService.getTokenByJwtToken(jwtToken)
        val url = getPreviewUrl(req.payload)
        renderService.createPreviewImage(token.accessTokenValue(), req.boardId, url)
        return "done"
    }

    private fun formatPreviewUrl(payload: String): String {
        val transcoder = TranscoderUtil.getDefaultTranscoder()
        val encoded = transcoder.encode(payload)
        return "https://www.plantuml.com/plantuml/png/$encoded"
    }

    @PostMapping("/submit-plantuml")
    fun submitPlantUml(
        @RequestHeader(HEADER_X_MIRO_TOKEN) jwtToken: String,
        @RequestBody submitPlantumlReq: SubmitPlantumlReq?
    ): String {
        val token = miroService.getTokenByJwtToken(jwtToken)
        renderService.render(token.accessTokenValue(), submitPlantumlReq!!)
        return "done"
    }
}

private const val HEADER_X_MIRO_TOKEN = "X-Miro-Token"
