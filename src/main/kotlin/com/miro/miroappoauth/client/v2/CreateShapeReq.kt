package com.miro.miroappoauth.client.v2

import com.miro.miroappoauth.dto.PositionDto
import com.miro.miroappoauth.dto.ShapeType

data class CreateShapeReq(
    val data: ShapeData = ShapeData(ShapeType.rectangle, ""),
    val position: PositionDto,
    val style: ShapeStyle = ShapeStyle(),
    val geometry: ShapeGeometry
) {
    data class ShapeData(
        val shape: ShapeType,
        val content: String
    )

    data class ShapeStyle(
        val fillColor: String? = "#fff9b1",
        /**
         * 0.0..1.0
         */
        val fillOpacity: String = "1.0",
        val fontFamily: String = "arial",
        val fontSize: String = "14",
        val borderColor: String? = "#1a1a1a",
        val borderWidth: String = "2.0",
        val borderOpacity: String = "1.0",
        val borderStyle: String = "normal",
        val textAlign: String = "center"
    )

    data class ShapeGeometry(
        val width: Int,
        val height: Int,
        val rotation: Int = 0
    )
}

/*
Response sample
{
    "id": "3458764523128925456",
    "type": "shape",
    "createdAt": "2022-04-12T18:32:38Z",
    "createdBy": {
        "id": "3074457358154717003",
        "type": "user"
    },
    "data": {
        "content": "sample shape content",
        "shape": "rectangle"
    },
    "geometry": {
        "width": 100.0,
        "height": 100.0,
        "rotation": 0.0
    },
    "modifiedAt": "2022-04-12T18:32:38Z",
    "modifiedBy": {
        "id": "3074457358154717003",
        "type": "user"
    },
    "position": {
        "x": 20.0,
        "y": 20.0,
        "origin": "center"
    },
    "style": {
        "fillColor": "#fff9b1",
        "fillOpacity": "1.0",
        "fontFamily": "arial",
        "fontSize": "14",
        "borderColor": "#1a1a1a",
        "borderWidth": "2.0",
        "borderOpacity": "1.0",
        "borderStyle": "normal",
        "textAlign": "center"
    }
}
*/
