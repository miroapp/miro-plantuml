package com.miro.miroappoauth.dto;

public class CreateTextReq {

    private TextData data;
    private PositionDto position;
    private TextStyle style;

    public CreateTextReq setData(TextData data) {
        this.data = data;
        return this;
    }

    public CreateTextReq setPosition(PositionDto position) {
        this.position = position;
        return this;
    }

    public CreateTextReq setStyle(TextStyle style) {
        this.style = style;
        return this;
    }

    public TextData getData() {
        return data;
    }

    public PositionDto getPosition() {
        return position;
    }

    public TextStyle getStyle() {
        return style;
    }

    public static class TextData {
        // can be html (in v1)
        private final String content;

        public TextData(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }

    public static class TextStyle {
        private String fillColor;
        private String fillOpacity = "1.0";
        private String fontFamily = "arial";
        private String fontSize = "14";
        private String textAlign = "center";

        public TextStyle setFillColor(String fillColor) {
            this.fillColor = fillColor;
            return this;
        }

        public TextStyle setFillOpacity(String fillOpacity) {
            this.fillOpacity = fillOpacity;
            return this;
        }

        public TextStyle setFontFamily(String fontFamily) {
            this.fontFamily = fontFamily;
            return this;
        }

        public TextStyle setFontSize(String fontSize) {
            this.fontSize = fontSize;
            return this;
        }

        public TextStyle setTextAlign(String textAlign) {
            this.textAlign = textAlign;
            return this;
        }

        public String getFillColor() {
            return fillColor;
        }

        public String getFillOpacity() {
            return fillOpacity;
        }

        public String getFontFamily() {
            return fontFamily;
        }

        public String getFontSize() {
            return fontSize;
        }

        public String getTextAlign() {
            return textAlign;
        }
    }
}

/*
     "position": {
          "x": 100,
          "y": 100,
          "origin": "center"
     },
     "geometry": {
          "width": 0,
          "rotation": 0
     },
 */