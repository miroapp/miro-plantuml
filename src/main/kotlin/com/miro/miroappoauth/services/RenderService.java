package com.miro.miroappoauth.services;

import com.miro.miroappoauth.client.MiroPublicClientV1;
import com.miro.miroappoauth.client.MiroPublicClientV2;
import com.miro.miroappoauth.dto.*;
import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.ISourceFileReader;
import net.sourceforge.plantuml.Option;
import net.sourceforge.plantuml.SourceFileReader;
import net.sourceforge.plantuml.ugraphic.miro.widgets.LineWidget;
import net.sourceforge.plantuml.ugraphic.miro.widgets.ShapeWidget;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

@Service
public class RenderService {

    private static RenderService instance;

    private final ThreadLocal<String> localAccessToken = new ThreadLocal<>();
    private final ThreadLocal<String> localBoardId = new ThreadLocal<>();

    private final MiroPublicClientV1 clientV1;
    private final MiroPublicClientV2 clientV2;

    public RenderService(MiroPublicClientV1 clientV1, MiroPublicClientV2 clientV2) {
        this.clientV1 = clientV1;
        this.clientV2 = clientV2;

        instance = this;
    }

    public static RenderService getInstance() {
        return instance;
    }

    public void createPreviewImage(String accessToken, String boardId, String url) {
        clientV2.createImage(accessToken, boardId, new CreateImageReq()
                .setData(new CreateImageReq.ImageData(url))
                .setPosition(new ShapePosition(0.0, 0.0d)));
    }

    public void render(String accessToken, SubmitPlantumlReq req) {
        localAccessToken.set(accessToken);
        localBoardId.set(req.getBoardId());
        try {
            parse(req);
        } finally {
            localAccessToken.remove();
            localBoardId.remove();
        }
    }

    // ShapeWidget{uid=4eacb9d9..., id=0, x=31.0, y=5.5, width=16.0, height=16.0, color='ff181818', backgroundColor='ffe2e2f0'}
    public String render(ShapeWidget shape) {
        var createRectReq = new CreateRectangleReq()
                .setData(new CreateRectangleReq.ShapeData(""))
                .setPosition(new ShapePosition(shape.getX(), shape.getY()))
                .setStyle(new CreateRectangleReq.ShapeStyle()
                        .setBorderColor(color(shape.getColor()))
                        .setFillColor(color(shape.getBackgroundColor())))
                .setGeometry(new CreateRectangleReq.ShapeGeometry((int) (shape.getWidth() * 2), (int) (shape.getHeight() * 2)));
        var createRectResp = clientV2.createRectangleShape(localAccessToken.get(), localBoardId.get(), createRectReq);
        return createRectResp.getId();
    }

    // LineWidget{uid=ecec37..., id=0, x=39.0, y=81.48828125, x2=39.0, y2=540.767578125, stroke='dashed', color='ff181818', type='straight'}
    public String render(LineWidget line) {
        var createRectResp1 = clientV2.createRectangleShape(localAccessToken.get(), localBoardId.get(),
                new CreateRectangleReq()
                        .setPosition(new ShapePosition(line.getX(), line.getY()))
                        // todo small
                        .setGeometry(new CreateRectangleReq.ShapeGeometry(8, 8)));

        var createRectResp2 = clientV2.createRectangleShape(localAccessToken.get(), localBoardId.get(),
                new CreateRectangleReq()
                        .setPosition(new ShapePosition(line.getX2(), line.getY2()))
                        // todo small
                        .setGeometry(new CreateRectangleReq.ShapeGeometry(8, 8)));


        var createLineReq = new CreateLineReq()
                .setStartWidget(new WidgetId(createRectResp1.getId()))
                .setEndWidget(new WidgetId(createRectResp2.getId()))
                .setStyle(new CreateLineReq.LineStyle()
                        .setLineStartType(LineEndType.none)
                        .setLineEndType(LineEndType.none)
                        .setBorderStyle(LineBorderStyle.fromString(line.getStroke())));
        var createLineResp = clientV1.createLine(localAccessToken.get(), localBoardId.get(), createLineReq);
        return createLineResp.getId();
    }

    private static String color(String color) {
        if (color == null || !color.startsWith("ff")) {
            return "";
        }
        return "#" + color.substring(2);
    }

    private void parse(SubmitPlantumlReq req) {
        Option option = new Option();
        try {
            File f = Files.createTempFile("foo", "bar").toFile();
            try (FileOutputStream fos = new FileOutputStream(f)) {
                fos.write(req.getPayload().getBytes(StandardCharsets.UTF_8));
            }
            ISourceFileReader sourceFileReader = new SourceFileReader(option.getDefaultDefines(f), f, null, option.getConfig(),
                    option.getCharset(), option.getFileFormatOption());

            List<GeneratedImage> images = sourceFileReader.getGeneratedImages();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
