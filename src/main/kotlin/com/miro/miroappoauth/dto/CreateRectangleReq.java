package com.miro.miroappoauth.dto;

public class CreateRectangleReq {

    private ShapeData data;
    private ShapePosition position;
    private ShapeStyle style;

    public CreateRectangleReq setData(ShapeData data) {
        this.data = data;
        return this;
    }

    public CreateRectangleReq setPosition(ShapePosition position) {
        this.position = position;
        return this;
    }

    public CreateRectangleReq setStyle(ShapeStyle style) {
        this.style = style;
        return this;
    }

    public ShapeData getData() {
        return data;
    }

    public ShapePosition getPosition() {
        return position;
    }

    public ShapeStyle getStyle() {
        return style;
    }

    public static class ShapeData {
        private final String content;

        private final String shapeType = "rectangle";

        public ShapeData(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public String getShapeType() {
            return shapeType;
        }
    }


    public static class ShapePosition {
        private final double x;
        private final double y;

        private String origin = "center";

        public ShapePosition(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public ShapePosition setOrigin(String origin) {
            this.origin = origin;
            return this;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public String getOrigin() {
            return origin;
        }
    }

    public static class ShapeStyle {
        private String fillColor = "#fff9b1";
        /**
         * 0.0..1.0
         */
        private String fillOpacity = "1.0";
        private String fontFamily = "arial";
        private String fontSize = "14";
        private String borderColor = "#1a1a1a";
        private String borderWidth = "2.0";
        private String borderOpacity = "1.0";
        private String borderStyle = "normal";
        private String textAlign = "center";

        public ShapeStyle setFillColor(String fillColor) {
            this.fillColor = fillColor;
            return this;
        }

        public ShapeStyle setFillOpacity(String fillOpacity) {
            this.fillOpacity = fillOpacity;
            return this;
        }

        public ShapeStyle setFontFamily(String fontFamily) {
            this.fontFamily = fontFamily;
            return this;
        }

        public ShapeStyle setFontSize(String fontSize) {
            this.fontSize = fontSize;
            return this;
        }

        public ShapeStyle setBorderColor(String borderColor) {
            this.borderColor = borderColor;
            return this;
        }

        public ShapeStyle setBorderWidth(String borderWidth) {
            this.borderWidth = borderWidth;
            return this;
        }

        public ShapeStyle setBorderOpacity(String borderOpacity) {
            this.borderOpacity = borderOpacity;
            return this;
        }

        public ShapeStyle setBorderStyle(String borderStyle) {
            this.borderStyle = borderStyle;
            return this;
        }

        public ShapeStyle setTextAlign(String textAlign) {
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

        public String getBorderColor() {
            return borderColor;
        }

        public String getBorderWidth() {
            return borderWidth;
        }

        public String getBorderOpacity() {
            return borderOpacity;
        }

        public String getBorderStyle() {
            return borderStyle;
        }

        public String getTextAlign() {
            return textAlign;
        }
    }

}