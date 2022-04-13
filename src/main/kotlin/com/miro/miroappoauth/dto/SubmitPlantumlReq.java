package com.miro.miroappoauth.dto;

public class SubmitPlantumlReq {

    private String boardId;
    private String payload;

    public SubmitPlantumlReq setBoardId(String boardId) {
        this.boardId = boardId;
        return this;
    }

    public SubmitPlantumlReq setPayload(String payload) {
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
