package com.miro.miroappoauth.dto;

import net.sourceforge.plantuml.ugraphic.miro.widgets.ShapeWidget;

public class CreateImageReq {

    private ImageData data;
    private ShapePosition position;

    public CreateImageReq setData(ImageData data) {
        this.data = data;
        return this;
    }

    public CreateImageReq setPosition(ShapePosition position) {
        this.position = position;
        return this;
    }

    public ImageData getData() {
        return data;
    }

    public ShapePosition getPosition() {
        return position;
    }

    public static class ImageData {
        private final String url;

        public ImageData(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }
}
