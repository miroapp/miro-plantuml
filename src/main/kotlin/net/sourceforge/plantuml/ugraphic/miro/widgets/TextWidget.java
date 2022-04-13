package net.sourceforge.plantuml.ugraphic.miro.widgets;

public class TextWidget extends AbstractWidget {

	private final String text;
	private int orientation;
	private String color;
	private int fontSize;
	private String fontFamily;

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


	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}

	public String getFontFamily() {
		return fontFamily;
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
				", color='" + color + '\'' +
				", fontSize=" + fontSize +
				", fontFamily='" + fontFamily + '\'' +
				'}';
	}
}
