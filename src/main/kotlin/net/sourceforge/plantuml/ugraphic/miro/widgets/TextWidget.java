package net.sourceforge.plantuml.ugraphic.miro.widgets;

public class TextWidget extends AbstractWidget {

	private final String text;
	private int orientation;
	private String font;
	private String color;

	public TextWidget(double x, double y, String text) {
		super(x, y);
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return "TextWidget{" +
						"uid=" + uid +
						", id=" + id +
						", x=" + x +
						", y=" + y +
						", text='" + text + '\'' +
						", orientation=" + orientation +
						", font='" + font + '\'' +
						", color='" + color + '\'' +
						'}';
	}
}
