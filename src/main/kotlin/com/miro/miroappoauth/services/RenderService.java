package com.miro.miroappoauth.services;

import com.miro.miroappoauth.client.MiroPublicV1Client;
import com.miro.miroappoauth.client.MiroPublicV2Client;
import com.miro.miroappoauth.dto.SubmitPlantumlReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RenderService {

    private final MiroPublicV1Client miroPublicV1Client;
    private final MiroPublicV2Client miroPublicV2Client;

    public void render(String token, SubmitPlantumlReq submitPlantumlReq) {

    }
}
