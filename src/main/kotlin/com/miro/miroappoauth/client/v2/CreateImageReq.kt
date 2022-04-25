package com.miro.miroappoauth.client.v2

data class CreateImageReq(
    val data: ImageData,
    val position: PositionDto
) {
    data class ImageData(
        val url: String
    )
}
