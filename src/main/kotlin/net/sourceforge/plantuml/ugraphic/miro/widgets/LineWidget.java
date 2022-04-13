package net.sourceforge.plantuml.ugraphic.miro.widgets;

public class LineWidget extends AbstractWidget {

	private final double x2;
	private final double y2;
	private String stroke;
	private String color;

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
						'}';
	}
}
