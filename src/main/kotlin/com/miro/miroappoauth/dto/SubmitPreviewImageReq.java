package com.miro.miroappoauth.dto;

public class SubmitPreviewImageReq {
    private String boardId;
    private String payload;

    public SubmitPreviewImageReq setBoardId(String boardId) {
        this.boardId = boardId;
        return this;
    }

    public SubmitPreviewImageReq setPayload(String payload) {
        this.payload = payload;
        return this;
    }

    public String getBoardId() {
        return boardId;
    }

    public String getPayload() {
        return payload;
    }
}
