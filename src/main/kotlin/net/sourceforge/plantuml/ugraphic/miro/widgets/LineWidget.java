package net.sourceforge.plantuml.ugraphic.miro.widgets;

public class LineWidget extends AbstractWidget {

    private final double x2;
    private final double y2;
    private String stroke;
    private String color;
    private String type;

    public LineWidget(double x1, double y1, double x2, double y2) {
        super(x1, y1);
        this.x2 = x2;
        this.y2 = y2;
    }

    public String getStroke() {
        return stroke;
    }

    public void setStroke(String stroke) {
        this.stroke = stroke;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getX2() {
        return x2;
    }

    public double getY2() {
        return y2;
    }

    @Override
    public String toString() {
        return "LineWidget{" +
                "uid=" + uid +
                ", id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", x2=" + x2 +
                ", y2=" + y2 +
                ", stroke='" + stroke + '\'' +
                ", color='" + color + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
