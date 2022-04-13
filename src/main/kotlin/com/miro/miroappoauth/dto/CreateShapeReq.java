package com.miro.miroappoauth.dto;

public class CreateShapeReq {

    private ShapeData data = new ShapeData(ShapeType.rectangle, "");
    private ShapePosition position;
    private ShapeStyle style = new ShapeStyle();
    private ShapeGeometry geometry;

    public CreateShapeReq setData(ShapeData data) {
        this.data = data;
        return this;
    }

    public CreateShapeReq setPosition(ShapePosition position) {
        this.position = position;
        return this;
    }

    public CreateShapeReq setStyle(ShapeStyle style) {
        this.style = style;
        return this;
    }

    public CreateShapeReq setGeometry(ShapeGeometry geometry) {
        this.geometry = geometry;
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

    public ShapeGeometry getGeometry() {
        return geometry;
    }

    public static class ShapeData {
        private final ShapeType shapeType;
        private final String content;

        public ShapeData(ShapeType shapeType, String content) {
            this.shapeType = shapeType;
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public ShapeType getShapeType() {
            return shapeType;
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

    public static class ShapeGeometry {
        private final int width;
        private final int height;

        private int rotation = 0;

        public ShapeGeometry(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public ShapeGeometry setRotation(int rotation) {
            this.rotation = rotation;
            return this;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getRotation() {
            return rotation;
        }
    }
}
