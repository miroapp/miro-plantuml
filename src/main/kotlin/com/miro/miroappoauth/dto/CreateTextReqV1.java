package com.miro.miroappoauth.dto;

// https://developers.miro.com/reference/text
public class CreateTextReqV1 implements WidgetV1 {
    private final String type = "text";

    private final String text;
    private final int x;
    private final int y;

    private int width = 100;
    private double rotation = 0.0d;

    //private double scale = 1.714d;

    private TextStyle style = new TextStyle();

    public CreateTextReqV1(String text, int x, int y) {
        this.text = text;
        this.x = x;
        this.y = y;
    }

    public CreateTextReqV1 setWidth(int width) {
        this.width = width;
        return this;
    }

    public CreateTextReqV1 setRotation(double rotation) {
        this.rotation = rotation;
        return this;
    }

//    public CreateTextReqV1 setScale(double scale) {
//        this.scale = scale;
//        return this;
//    }

    public CreateTextReqV1 setStyle(TextStyle style) {
        this.style = style;
        return this;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public double getRotation() {
        return rotation;
    }

//    public double getScale() {
//        return scale;
//    }

    public TextStyle getStyle() {
        return style;
    }

    public static class TextStyle {
        // supports short hex code color format
        private String backgroundColor = "#ffffff";
        // allowed values: 0.0, 0.25, 0.5, 0.75, 1.0
        private double backgroundOpacity = 0.0d;
        //private String borderColor = "#808080", // Deprecated. Default: "#ffffff"
        // allowed values: 0.0, 0.25, 0.5, 0.75, 1.0
        //private double borderOpacity = 0.6, // Deprecated. Default: 1.0
        //"borderStyle": "normal", // Deprecated. Default: normal
        // allowed values: 2.0, 4.0, 8.0, 16.0, 24.0
//        "borderWidth": 4.0, // Deprecated. Default: 2.0
        // allowed values:
        //  "Arial", "Abril Fatface", "Bangers", "EB Garamond", "Georgia", "Graduate",
        //  "Gravitas One", "Fredoka One", "Nixie One", "OpenSans", "Permanent Marker",
        //  "PT Sans", "PT Sans Narrow", "PT Serif", "Rammetto One", "Roboto",
        //  "Roboto Condensed", "Roboto Slab", "Caveat", "Times New Roman", "Titan One",
        //  "Lemon Tuesday", "Roboto Mono", "Noto Sans", "IBM Plex Sans", "IBM Plex Serif",
        //  "IBM Plex Mono"
        private String fontFamily = "OpenSans";
        // allowed values between 10 to 999 inclusive
        private int fontSize = 14;
        // allowed values: "center", "right", "left"
        private String textAlign = "center";
        private String textColor = "#1a1a1a";
        // allowed values: 0, 8
        //"padding": 0 // Deprecated. Default: 0

        public TextStyle setBackgroundColor(String backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public TextStyle setBackgroundOpacity(double backgroundOpacity) {
            this.backgroundOpacity = backgroundOpacity;
            return this;
        }

        public TextStyle setFontFamily(String fontFamily) {
            this.fontFamily = fontFamily;
            return this;
        }

        public TextStyle setFontSize(int fontSize) {
            this.fontSize = fontSize;
            return this;
        }

        public TextStyle setTextAlign(String textAlign) {
            this.textAlign = textAlign;
            return this;
        }

        public TextStyle setTextColor(String textColor) {
            this.textColor = textColor;
            return this;
        }

        public String getBackgroundColor() {
            return backgroundColor;
        }

        public double getBackgroundOpacity() {
            return backgroundOpacity;
        }

        public String getFontFamily() {
            return fontFamily;
        }

        public int getFontSize() {
            return fontSize;
        }

        public String getTextAlign() {
            return textAlign;
        }

        public String getTextColor() {
            return textColor;
        }
    }

}
