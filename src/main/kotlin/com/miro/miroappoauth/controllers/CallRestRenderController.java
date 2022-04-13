package com.miro.miroappoauth.controllers;

import com.miro.miroappoauth.dto.SubmitPlantumlReq;
import com.miro.miroappoauth.services.MiroService;
import com.miro.miroappoauth.services.RenderService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CallRestRenderController {

    private static final String HEADER_X_MIRO_TOKEN = "X-Miro-Token";

    private final MiroService miroService;
    private final RenderService renderService;

    @PostMapping("/submit-plantuml")
    String submitPlantUml(
            @RequestHeader(HEADER_X_MIRO_TOKEN) String jwtToken,
            @RequestBody SubmitPlantumlReq submitPlantumlReq
    ) {
        val token = miroService.getTokenByJwtToken(jwtToken);

        renderService.render(token.accessTokenValue(), submitPlantumlReq);

        return "done";
    }
}
