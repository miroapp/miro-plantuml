package com.miro.miroappoauth.dto;

public class CreateLineResp {

    private String id;

    public CreateLineResp setId(String id) {
        this.id = id;
        return this;
    }

    public String getId() {
        return id;
    }
}

/*
{
    "id": "3458764523135799050",
    "startWidget": {
        "id": "3458764523128507498"
    },
    "endWidget": {
        "id": "3458764523128925456"
    },
    "style": {
        "borderColor": "#da0063",
        "borderStyle": "normal",
        "borderWidth": 1.0,
        "lineType": "orthogonal"
    },
    "type": "line",
    "createdAt": "2022-04-12T18:32:46Z",
    "modifiedAt": "2022-04-12T18:32:46Z",
    "modifiedBy": {
        "type": "user",
        "name": "Sergey Chernov",
        "id": "3074457358154717003"
    },
    "createdBy": {
        "type": "user",
        "name": "Sergey Chernov",
        "id": "3074457358154717003"
    },
    "capabilities": {
        "editable": true
    }
}
 */