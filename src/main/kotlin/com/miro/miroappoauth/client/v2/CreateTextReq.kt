package com.miro.miroappoauth.client.v2

data class CreateTextReq(
    val data: TextData,
    val position: PositionDto,
    val geometry: TextGeometry,
    val style: TextStyle
) {

    data class TextData(
        // todo html? or plain
        val content: String
    )

    data class TextStyle(
        val fillColor: String? = null,
        val fillOpacity: String = "1.0",
        val fontFamily: String = "pt_sans",
        val fontSize: String = "14",
        val textAlign: String = "center"
    )

    data class TextGeometry(
        val width: Double
    )
}
