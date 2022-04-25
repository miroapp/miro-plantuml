package com.miro.miroappoauth.services

import com.miro.miroappoauth.client.MiroPublicClientV1
import com.miro.miroappoauth.client.MiroPublicClientV2
import com.miro.miroappoauth.client.v1.*
import com.miro.miroappoauth.client.v1.CreateLineReq.LineStyle
import com.miro.miroappoauth.client.v2.*
import com.miro.miroappoauth.client.v2.CreateTextReq.TextData
import com.miro.miroappoauth.client.v2.CreateTextReq.TextGeometry
import com.miro.miroappoauth.dto.SubmitPlantumlReq
import net.sourceforge.plantuml.ISourceFileReader
import net.sourceforge.plantuml.Option
import net.sourceforge.plantuml.SourceFileReader
import net.sourceforge.plantuml.ugraphic.miro.LineWidgetsId
import net.sourceforge.plantuml.ugraphic.miro.widgets.LineWidget
import net.sourceforge.plantuml.ugraphic.miro.widgets.ShapeWidget
import net.sourceforge.plantuml.ugraphic.miro.widgets.TextWidget
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.*

@Service
class RenderService(
    private val clientV1: MiroPublicClientV1,
    private val clientV2: MiroPublicClientV2
) {
    private val localAccessToken = ThreadLocal<String>()
    private val localBoardId = ThreadLocal<String>()

    fun createPreviewImage(accessToken: String, boardId: String, url: String) {
        clientV2.createImage(
            accessToken, boardId, CreateImageReq(
                CreateImageReq.ImageData(url),
                PositionDto(0.0, 0.0)
            )
        )
    }

    fun render(accessToken: String, req: SubmitPlantumlReq) {
        localAccessToken.set(accessToken)
        localBoardId.set(req.boardId)
        try {
            parse(req)
        } finally {
            localAccessToken.remove()
            localBoardId.remove()
        }
    }

    fun render(shape: ShapeWidget): String {
        val createRectReq = CreateShapeReq(
            data = CreateShapeReq.ShapeData(ShapeType.fromString(shape.form), ""),
            position = PositionDto(shape.x, shape.y),
            style = CreateShapeReq.ShapeStyle(
                borderColor = color(shape.color),
                fillColor = color(shape.backgroundColor)
            ),
            geometry = CreateShapeReq.ShapeGeometry(
                Math.max(MIN_SHAPE_WIDTH, shape.width.toInt()),
                Math.max(MIN_SHAPE_WIDTH, shape.height.toInt()),
                rotation = shape.rotation.toInt()
            )
        )
        val createRectResp = clientV2.createShape(localAccessToken.get(), localBoardId.get(), createRectReq)
        return createRectResp.id
    }

    fun prepareLineDots(line: LineWidget): LineWidgetsId {
        val createRectResp1 = clientV2.createShape(
            localAccessToken.get(), localBoardId.get(),
            CreateShapeReq(
                position = PositionDto(line.x, line.y),
                geometry = CreateShapeReq.ShapeGeometry(8, 8),
                style = CreateShapeReq.ShapeStyle(borderColor = BOARD_COLOR, fillColor = BOARD_COLOR)
            )
        )
        val createRectResp2 = clientV2.createShape(
            localAccessToken.get(), localBoardId.get(),
            CreateShapeReq(
                position = PositionDto(line.x2, line.y2),
                geometry = CreateShapeReq.ShapeGeometry(8, 8),
                style = CreateShapeReq.ShapeStyle(
                    borderColor = BOARD_COLOR,
                    fillColor = BOARD_COLOR
                )
            )
        )
        return LineWidgetsId(createRectResp1.id, createRectResp2.id)
    }

    fun render(line: LineWidget, lineWidgetsId: LineWidgetsId): String {
        val createLineReq = CreateLineReq(
            startWidget = WidgetId(lineWidgetsId.startWidgetId),
            endWidget = WidgetId(lineWidgetsId.endWidgetId),
            style = LineStyle(
                lineStartType = LineEndType.none,
                lineEndType = LineEndType.none,
                borderStyle = LineBorderStyle.fromString(line.stroke),
                lineType = LineType.fromString(line.type)
            )
        )
        val createLineResp = clientV1.createWidget(localAccessToken.get(), localBoardId.get(), createLineReq)
        return createLineResp.id
    }

    // TextWidget{uid=064b2e93-0ae3-4a10-a36e-145624e63793, id=0, x=579.4204790480273, y=82.88888888888889,
    // text='POST us.miro.com/oauth/authorize', orientation=0, color='ff000000', fontSize=14, fontFamily='SansSerif'}
    fun render(text: TextWidget): String? {
        if (!StringUtils.hasText(text.text)) {
            return null
        }
        val resp = clientV2.createText(
            localAccessToken.get(), localBoardId.get(), CreateTextReq(
                data = TextData(text.text),
                position = PositionDto(text.x, text.y),
                geometry = TextGeometry(
                    Math.max(MIN_TEXT_WIDTH.toDouble(), text.width)
                ),
                style = CreateTextReq.TextStyle(
                    fontSize = Integer.toString(text.fontSize),
                    //fontFamily = text.getFontFamily()
                )
            )
        )
        return resp.id

//        var resp = clientV1.createWidget(localAccessToken.get(), localBoardId.get(),
//                new CreateTextReqV1(text.getText(), (int) text.getX(), (int) text.getY())
//                        .setWidth((int)text.getWidth())
//                        .setStyle(new CreateTextReqV1.TextStyle()
//                                .setFontSize(text.getFontSize())));
//        return resp.getId();
    }

    private fun parse(req: SubmitPlantumlReq) {
        var inputFile: File? = null
        try {
            inputFile = Files.createTempFile("plantuml_", UUID.randomUUID().toString() + ".puml").toFile()
            println("File: " + inputFile.absolutePath)
            FileOutputStream(inputFile!!).use { fos -> fos.write(req.payload.toByteArray(StandardCharsets.UTF_8)) }
            val option = Option()
            val sourceFileReader: ISourceFileReader = SourceFileReader(
                option.getDefaultDefines(inputFile), inputFile, null, option.config,
                option.charset, option.fileFormatOption
            )
            val images = sourceFileReader.generatedImages
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputFile!!.delete()
        }
    }

    init {
        instance = this
    }

    companion object {
        private const val MIN_SHAPE_WIDTH = 8
        private const val MIN_TEXT_WIDTH = 24

        @JvmStatic
        lateinit var instance: RenderService
            private set

        private fun color(color: String?): String? {
            return if (color == null || !color.startsWith("ff")) {
                null
            } else "#" + color.substring(2)
        }

        private const val BOARD_COLOR = "#f2f2f2"
    }
}
