package net.sourceforge.plantuml.ugraphic.miro.widgets;

public class ShapeWidget extends AbstractWidget {
    private final double width;
    private final double height;
    private String color;
    private String backgroundColor;
    private String form;

    public ShapeWidget(double x, double y, double width, double height) {
        super(x, y);
        this.width = width;
        this.height = height;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    @Override
    public String toString() {
        return "ShapeWidget{" +
                "uid=" + uid +
                ", id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", color='" + color + '\'' +
                ", backgroundColor='" + backgroundColor + '\'' +
                ", form='" + form + '\'' +
                '}';
    }
}
