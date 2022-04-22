package com.miro.miroappoauth.dto

data class CreateImageReq(
    val data: ImageData,
    val position: PositionDto
) {
    data class ImageData(
        val url: String
    )
}
