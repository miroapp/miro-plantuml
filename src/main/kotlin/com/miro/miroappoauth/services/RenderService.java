package com.miro.miroappoauth.services;

import com.miro.miroappoauth.client.MiroPublicClientV1;
import com.miro.miroappoauth.client.MiroPublicClientV2;
import com.miro.miroappoauth.dto.*;
import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.ISourceFileReader;
import net.sourceforge.plantuml.Option;
import net.sourceforge.plantuml.SourceFileReader;
import net.sourceforge.plantuml.ugraphic.miro.LineWidgetsId;
import net.sourceforge.plantuml.ugraphic.miro.widgets.LineWidget;
import net.sourceforge.plantuml.ugraphic.miro.widgets.ShapeWidget;
import net.sourceforge.plantuml.ugraphic.miro.widgets.TextWidget;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

@Service
public class RenderService {

    private static final int MIN_WIDTH = 8;

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
                .setPosition(new PositionDto(0.0, 0.0d)));
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

    public String render(ShapeWidget shape) {
        var createRectReq = new CreateShapeReq()
                .setData(new CreateShapeReq.ShapeData(ShapeType.fromString(shape.getForm()), ""))
                .setPosition(new PositionDto(shape.getX(), shape.getY()))
                .setStyle(new CreateShapeReq.ShapeStyle()
                        .setBorderColor(color(shape.getColor()))
                        .setFillColor(color(shape.getBackgroundColor())))
                .setGeometry(new CreateShapeReq.ShapeGeometry(
                        Math.max(MIN_WIDTH, (int) (shape.getWidth())),
                        Math.max(MIN_WIDTH, (int) (shape.getHeight())))
                        .setRotation((int)shape.getRotation()));
        var createRectResp = clientV2.createShape(localAccessToken.get(), localBoardId.get(), createRectReq);
        return createRectResp.getId();
    }

    public LineWidgetsId prepareLineDots(LineWidget line) {
        var createRectResp1 = clientV2.createShape(localAccessToken.get(), localBoardId.get(),
                new CreateShapeReq()
                        .setPosition(new PositionDto(line.getX(), line.getY()))
                        .setGeometry(new CreateShapeReq.ShapeGeometry(8, 8))
                        .setStyle(new CreateShapeReq.ShapeStyle()
                                .setBorderColor(BOARD_COLOR)
                                .setFillColor(BOARD_COLOR)));

        var createRectResp2 = clientV2.createShape(localAccessToken.get(), localBoardId.get(),
                new CreateShapeReq()
                        .setPosition(new PositionDto(line.getX2(), line.getY2()))
                        .setGeometry(new CreateShapeReq.ShapeGeometry(8, 8))
                        .setStyle(new CreateShapeReq.ShapeStyle()
                                .setBorderColor(BOARD_COLOR)
                                .setFillColor(BOARD_COLOR)));

        return new LineWidgetsId(createRectResp1.getId(), createRectResp2.getId());
    }

    public String render(LineWidget line, LineWidgetsId lineWidgetsId) {
        var createLineReq = new CreateLineReq()
                .setStartWidget(new WidgetId(lineWidgetsId.getStartWidgetId()))
                .setEndWidget(new WidgetId(lineWidgetsId.getEndWidgetId()))
                .setStyle(new CreateLineReq.LineStyle()
                        .setLineStartType(LineEndType.none)
                        .setLineEndType(LineEndType.none)
                        .setBorderStyle(LineBorderStyle.fromString(line.getStroke())));
        var createLineResp = clientV1.createWidget(localAccessToken.get(), localBoardId.get(), createLineReq);
        return createLineResp.getId();
    }

    // TextWidget{uid=064b2e93-0ae3-4a10-a36e-145624e63793, id=0, x=579.4204790480273, y=82.88888888888889,
    // text='POST us.miro.com/oauth/authorize', orientation=0, color='ff000000', fontSize=14, fontFamily='SansSerif'}
    public String render(TextWidget text) {
        // todo text in v2 is created with border
//        var resp = clientV2.createText(localAccessToken.get(), localBoardId.get(), new CreateTextReq()
//                .setData(new CreateTextReq.TextData(text.getText()))
//                        .setPosition(new PositionDto(text.getX(), text.getY()))
//                .setStyle(new CreateTextReq.TextStyle()
//                        .setFontSize(Integer.toString(text.getFontSize()))));
//        return resp.getId();

        var resp = clientV1.createWidget(localAccessToken.get(), localBoardId.get(),
                new CreateTextReqV1(text.getText(), (int) text.getX(), (int) text.getY())
                        .setWidth((int)text.getWidth())
                        .setStyle(new CreateTextReqV1.TextStyle()
                                .setFontSize(text.getFontSize())));
        return resp.getId();
    }

    @Nullable
    private static String color(String color) {
        if (color == null || !color.startsWith("ff")) {
            return null;
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

    private static final String BOARD_COLOR = "#f2f2f2";
}
