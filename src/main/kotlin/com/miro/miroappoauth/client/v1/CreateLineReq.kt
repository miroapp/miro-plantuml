package com.miro.miroappoauth.client.v1

/**
 * [reference/line](https://developers.miro.com/reference/line)
 */
data class CreateLineReq(
    val startWidget: WidgetId,
    val endWidget: WidgetId,
    val style: LineStyle
) : WidgetV1 {
    override val type: String
        get() = "line"

    data class LineStyle(
        val borderColor: String = "#da0063",
        val borderStyle: LineBorderStyle = LineBorderStyle.normal,
        val borderWidth: Int = 1,
        val lineStartType: LineEndType = LineEndType.none,
        val lineEndType: LineEndType = LineEndType.none,
        val lineType: LineType = LineType.straight
    )
}
