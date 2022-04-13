package com.miro.miroappoauth.dto;

public class CreateLineReq {

    private final String type = "line";

    private WidgetId startWidget;
    private WidgetId endWidget;
    private LineStyle style;

    public CreateLineReq setStartWidget(WidgetId startWidget) {
        this.startWidget = startWidget;
        return this;
    }

    public CreateLineReq setEndWidget(WidgetId endWidget) {
        this.endWidget = endWidget;
        return this;
    }

    public CreateLineReq setStyle(LineStyle style) {
        this.style = style;
        return this;
    }

    public String getType() {
        return type;
    }

    public WidgetId getStartWidget() {
        return startWidget;
    }

    public WidgetId getEndWidget() {
        return endWidget;
    }

    public LineStyle getStyle() {
        return style;
    }

    public static class LineStyle {
        private String borderColor = "#da0063";
        private LineBorderStyle borderStyle = LineBorderStyle.normal;
        private int borderWidth = 1;
        private LineEndType lineStartType = LineEndType.none;
        private LineEndType lineEndType = LineEndType.none;
        private LineType lineType = LineType.orthogonal;

        public LineStyle setBorderColor(String borderColor) {
            this.borderColor = borderColor;
            return this;
        }

        public LineStyle setBorderStyle(LineBorderStyle borderStyle) {
            this.borderStyle = borderStyle;
            return this;
        }

        public LineStyle setBorderWidth(int borderWidth) {
            this.borderWidth = borderWidth;
            return this;
        }

        public LineStyle setLineStartType(LineEndType lineStartType) {
            this.lineStartType = lineStartType;
            return this;
        }

        public LineStyle setLineEndType(LineEndType lineEndType) {
            this.lineEndType = lineEndType;
            return this;
        }

        public LineStyle setLineType(LineType lineType) {
            this.lineType = lineType;
            return this;
        }

        public String getBorderColor() {
            return borderColor;
        }

        public LineBorderStyle getBorderStyle() {
            return borderStyle;
        }

        public int getBorderWidth() {
            return borderWidth;
        }

        public LineEndType getLineStartType() {
            return lineStartType;
        }

        public LineEndType getLineEndType() {
            return lineEndType;
        }

        public LineType getLineType() {
            return lineType;
        }
    }
}
