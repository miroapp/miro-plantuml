package com.miro.miroappoauth.services;

import com.miro.miroappoauth.client.MiroPublicClientV1;
import com.miro.miroappoauth.client.MiroPublicClientV2;
import com.miro.miroappoauth.dto.*;
import org.springframework.stereotype.Service;

@Service
public class RenderService {

    private final MiroPublicClientV1 clientV1;
    private final MiroPublicClientV2 clientV2;

    public RenderService(MiroPublicClientV1 clientV1, MiroPublicClientV2 clientV2) {
        this.clientV1 = clientV1;
        this.clientV2 = clientV2;
    }

    public void render(String accessToken, SubmitPlantumlReq req) {
        String boardId = req.getBoardId();

        var createRectReq1 = new CreateRectangleReq()
                .setData(new CreateRectangleReq.ShapeData("text1"))
                .setPosition(new CreateRectangleReq.ShapePosition(-100.0, -100.0))
                .setStyle(new CreateRectangleReq.ShapeStyle());
        var createRectResp1 = clientV2.createRectangle(accessToken, boardId, createRectReq1);

        var createRectReq2 = new CreateRectangleReq()
                .setData(new CreateRectangleReq.ShapeData("text2"))
                .setPosition(new CreateRectangleReq.ShapePosition(100.0, 100.0))
                .setStyle(new CreateRectangleReq.ShapeStyle());
        var createRectResp2 = clientV2.createRectangle(accessToken, boardId, createRectReq2);

        var createLineReq = new CreateLineReq()
                .setStartWidget(new WidgetId(createRectResp1.getId()))
                .setEndWidget(new WidgetId(createRectResp2.getId()))
                .setStyle(new CreateLineReq.LineStyle()
                        .setLineStartType(LineEndType.none)
                        .setLineEndType(LineEndType.open_arrow));
        clientV1.createLine(accessToken, boardId, createLineReq);
    }
}
