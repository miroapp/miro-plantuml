package com.miro.miroappoauth.dto;

import lombok.Data;

@Data
public class CreateRectangleReq {

    private ShapeData data;
    private ShapePosition position;
    private ShapeStyle style;

    @Data
    public static class ShapeData {
        private final String content;

        private final String shapeType = "rectangle";
    }

    @Data
    public static class ShapePosition {
        private final double x;
        private final double y;

        private String origin = "center";
    }

    @Data
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
    }

}
