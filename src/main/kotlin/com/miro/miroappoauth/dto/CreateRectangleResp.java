package com.miro.miroappoauth.dto;

public class CreateRectangleResp {
    private String id;

    public CreateRectangleResp setId(String id) {
        this.id = id;
        return this;
    }

    public String getId() {
        return id;
    }
}

/*
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
        "shapeType": "rectangle"
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