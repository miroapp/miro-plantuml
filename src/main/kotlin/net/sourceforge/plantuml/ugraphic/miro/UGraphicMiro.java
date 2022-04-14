/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 *
 * If you like this project or if you find it useful, you can support us at:
 *
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
 *
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 *
 * Original Author:  Arnaud Roques
 *
 */
package net.sourceforge.plantuml.ugraphic.miro;

import com.miro.miroappoauth.services.RenderService;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.posimo.DotPath;
import net.sourceforge.plantuml.ugraphic.*;
import net.sourceforge.plantuml.ugraphic.color.*;
import net.sourceforge.plantuml.ugraphic.debug.StringBounderDebug;
import net.sourceforge.plantuml.ugraphic.miro.widgets.LineWidget;
import net.sourceforge.plantuml.ugraphic.miro.widgets.ShapeWidget;
import net.sourceforge.plantuml.ugraphic.miro.widgets.TextWidget;
import net.sourceforge.plantuml.ugraphic.miro.widgets.Widget;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class UGraphicMiro extends AbstractCommonUGraphic implements ClipContainer {

	private final List<String> output;
	private final List<Widget> widgets;
	private final double scaleFactor;
	private final Dimension2D dim;
	private final String svgLinkTarget;
	private final String hoverPathColorRGB;
	private final long seed;
	private final String preserveAspectRatio;

	@Override
	protected AbstractCommonUGraphic copyUGraphic() {
		return new UGraphicMiro(this, output, widgets, scaleFactor, dim, svgLinkTarget, hoverPathColorRGB, seed,
				preserveAspectRatio);
	}

	private UGraphicMiro(UGraphicMiro other, List<String> output, List<Widget> widgets, double scaleFactor, Dimension2D dim,
                         String svgLinkTarget, String hoverPathColorRGB, long seed, String preserveAspectRatio) {
		super(other);
		this.output = output;
		this.widgets = widgets;
		this.scaleFactor = scaleFactor;
		this.dim = dim;
		this.svgLinkTarget = svgLinkTarget;
		this.hoverPathColorRGB = hoverPathColorRGB;
		this.seed = seed;
		this.preserveAspectRatio = preserveAspectRatio;
	}

	public UGraphicMiro(double scaleFactor, Dimension2D dim, String svgLinkTarget, String hoverPathColorRGB, long seed,
                        String preserveAspectRatio) {
		super(HColorUtils.WHITE, new ColorMapperIdentity(), new StringBounderDebug());
		this.output = new ArrayList<>();
		this.widgets = new ArrayList<>();
		this.scaleFactor = scaleFactor;
		this.dim = dim;
		this.svgLinkTarget = svgLinkTarget;
		this.hoverPathColorRGB = hoverPathColorRGB;
		this.seed = seed;
		this.preserveAspectRatio = preserveAspectRatio;
	}

	public void draw(UShape shape) {
		if (shape instanceof ULine) {
			outLine((ULine) shape);
		} else if (shape instanceof URectangle) {
			outRectangle((URectangle) shape);
		} else if (shape instanceof UText) {
			outText((UText) shape);
		} else if (shape instanceof UPolygon) {
			outPolygon((UPolygon) shape);
		} else if (shape instanceof UEllipse) {
			outEllipse((UEllipse) shape);
		} else if (shape instanceof UEmpty) {
			outEmpty((UEmpty) shape);
		} else if (shape instanceof UPath) {
			outPath((UPath) shape);
		} else if (shape instanceof UComment) {
			outComment((UComment) shape);
		} else if (shape instanceof DotPath) {
			outPath(((DotPath) shape).toUPath());
		} else if (shape instanceof UCenteredCharacter) {
			outCenteredCharacter(((UCenteredCharacter) shape));
		} else {
			System.err.println("UGraphicDebug " + shape.getClass().getSimpleName());
			output.add("UGraphicDebug " + shape.getClass().getSimpleName() + " " + new Date());
		}
	}

	private void outCenteredCharacter(UCenteredCharacter shape) {
		output.add("CENTERED_CHAR:");
		output.add("  char: " + shape.getChar());
		output.add("  position: " + pointd(getTranslateX(), getTranslateY()));
		output.add("  font: " + shape.getFont().toStringDebug());
		output.add("  color: " + colorToString(getParam().getColor()));
		output.add("");
	}

	private void outComment(UComment shape) {
		output.add("COMMENT: " + shape.getComment());
	}

	private void outPath(UPath shape) {
		output.add("PATH:");
		for (USegment seg : shape) {
			final USegmentType type = seg.getSegmentType();
			final double coord[] = seg.getCoord();
			output.add("   - type: " + type);
			if (type == USegmentType.SEG_ARCTO) {
				output.add("     radius: " + pointd(coord[0], coord[1]));
				output.add("     angle: " + coord[2]);
				output.add("     largeArcFlag: " + (coord[3] != 0));
				output.add("     sweepFlag: " + (coord[4] != 0));
				output.add("     dest: " + pointd(coord[5], coord[6]));
			} else
				for (int i = 0; i < type.getNbPoints(); i++) {
					final String key = "     pt" + (i + 1) + ": ";
					output.add(key + pointd(coord[2 * i], coord[2 * i + 1]));
				}
		}

		output.add("  stroke: " + getParam().getStroke());
		output.add("  shadow: " + (int) shape.getDeltaShadow());
		output.add("  color: " + colorToString(getParam().getColor()));
		output.add("  backcolor: " + colorToString(getParam().getBackcolor()));
		output.add("");
	}

	private void outPolygon(UPolygon shape) {
		output.add("POLYGON:");
		output.add("  points:");
		List<Point2D.Double> translatedPoints = new ArrayList<>();
		for (Point2D pt : shape.getPoints()) {
			final double xp = getTranslateX() + pt.getX();
			final double yp = getTranslateY() + pt.getY();
			translatedPoints.add(new Point2D.Double(xp, yp));
			output.add("   - " + pointd(xp, yp));
		}
		output.add("  stroke: " + getParam().getStroke());
		output.add("  shadow: " + (int) shape.getDeltaShadow());
		output.add("  color: " + colorToString(getParam().getColor()));
		output.add("  backcolor: " + colorToString(getParam().getBackcolor()));
		output.add("");

		// try to detect arrow heads
		if (translatedPoints.size() != 4) {
			return;
		}
		Point2D.Double firstPoint = translatedPoints.get(0);
		Point2D.Double leftTop = new Point2D.Double(firstPoint.x, firstPoint.y);
		Point2D.Double rightBottom = new Point2D.Double(firstPoint.x, firstPoint.y);
		for (Point2D.Double point : translatedPoints) {
			if (point.x < leftTop.x) {
				leftTop.x = point.x;
			}
			if (point.x > rightBottom.x) {
				rightBottom.x = point.x;
			}
			if (point.y < leftTop.y) {
				leftTop.y = point.y;
			}
			if (point.y > rightBottom.y) {
				rightBottom.y = point.y;
			}
		}
		double width = rightBottom.x - leftTop.x;
		double height = rightBottom.y - leftTop.y;
		double rotation = 0.0;

		if (width > height) {
			// the head is either to the left or to the right
			Point2D.Double right = translatedPoints.stream()
					.filter(p -> p.y != leftTop.y)
					.filter(p -> p.y != rightBottom.y)
					.max(Comparator.comparing(p -> p.x))
					.get();
			if (right.x == rightBottom.x) {
				rotation = 90.0;
			} else {
				rotation = -90.0;
			}
		} else {
			// the head is either to the top or to the bottom
			Point2D.Double bottom = translatedPoints.stream()
					.filter(p -> p.x != leftTop.x)
					.filter(p -> p.x != rightBottom.x)
					.max(Comparator.comparing(p -> p.y))
					.get();
			if (bottom.y == rightBottom.y) {
				rotation = 180.0;
			}
		}
		Point2D.Double center = new Point2D.Double(
				midPoint(leftTop.x, width),
				midPoint(leftTop.y, height)
		);

		ShapeWidget widget = new ShapeWidget(
				center.x,
				center.y,
				Math.min(width, height),
				Math.max(width, height)
		);
		widget.setForm("triangle");
		widget.setRotation(rotation);
		widget.setColor(colorToString(getParam().getColor()));
		widget.setBackgroundColor(colorToString(getParam().getBackcolor()));
		widgets.add(widget);
	}

	private void outText(UText shape) {
		output.add("TEXT:");
		output.add("  text: " + shape.getText());
		output.add("  position: " + pointd(getTranslateX(), getTranslateY()));
		output.add("  orientation: " + shape.getOrientation());
		output.add("  font: " + shape.getFontConfiguration().toStringDebug());
		output.add("  color: " + colorToString(shape.getFontConfiguration().getColor()));
		output.add("  extendedColor: " + colorToString(shape.getFontConfiguration().getExtendedColor()));
		output.add("");

		Dimension2D rect = getStringBounder().calculateDimension(shape.getFontConfiguration().getFont(), shape.getText());
		TextWidget widget = new TextWidget(
				midPoint(getTranslateX(), rect.getWidth()),
				midPoint(getTranslateY(), rect.getHeight()),
				shape.getText());
		widget.setWidth(rect.getWidth());
		widget.setOrientation(shape.getOrientation()); // TODO convert to Miro orientation
		widget.setFontSize(shape.getFontConfiguration().getFont().getSize());
		widget.setFontFamily(shape.getFontConfiguration().getFont().getFamily(null));
		widget.setColor(colorToString(shape.getFontConfiguration().getColor())); // TODO convert to MIRO color
		widgets.add(widget);
	}

	private void outEmpty(UEmpty shape) {
		output.add("EMPTY:");
		output.add("  pt1: " + pointd(getTranslateX(), getTranslateY()));
		output.add("  pt2: " + pointd(getTranslateX() + shape.getWidth(), getTranslateY() + shape.getHeight()));
		output.add("");

	}

	private void outEllipse(UEllipse shape) {
		output.add("ELLIPSE:");
		output.add("  pt1: " + pointd(getTranslateX(), getTranslateY()));
		output.add("  pt2: " + pointd(getTranslateX() + shape.getWidth(), getTranslateY() + shape.getHeight()));
		output.add("  start: " + shape.getStart());
		output.add("  extend: " + shape.getExtend());
		output.add("  stroke: " + getParam().getStroke());
		output.add("  shadow: " + (int) shape.getDeltaShadow());
		output.add("  color: " + colorToString(getParam().getColor()));
		output.add("  backcolor: " + colorToString(getParam().getBackcolor()));
		output.add("");

		ShapeWidget shapeWidget = new ShapeWidget(
				midPoint(getTranslateX(), shape.getWidth()),
				midPoint(getTranslateY(), shape.getHeight()),
				shape.getWidth(),
				shape.getHeight());
		shapeWidget.setForm("circle");
		shapeWidget.setColor(colorToString(getParam().getColor())); // todo convert to miro
		shapeWidget.setBackgroundColor(colorToString(getParam().getBackcolor())); // todo convert to miro
		widgets.add(shapeWidget);

	}

	private static double midPoint(double min, double size) {
		return min + size / 2.0;
	}

	private void outRectangle(URectangle shape) {
		output.add("RECTANGLE:");
		output.add("  pt1: " + pointd(getTranslateX(), getTranslateY()));
		output.add("  pt2: " + pointd(getTranslateX() + shape.getWidth(), getTranslateY() + shape.getHeight()));
		output.add("  xCorner: " + (int) shape.getRx());
		output.add("  yCorner: " + (int) shape.getRy());
		output.add("  stroke: " + getParam().getStroke());
		output.add("  shadow: " + (int) shape.getDeltaShadow());
		output.add("  color: " + colorToString(getParam().getColor()));
		output.add("  backcolor: " + colorToString(getParam().getBackcolor()));
		output.add("");

		ShapeWidget widget = new ShapeWidget(
				midPoint(getTranslateX(), shape.getWidth()),
				midPoint(getTranslateY(), shape.getHeight()),
				shape.getWidth(),
				shape.getHeight()
		);
		widget.setForm("rectangle");
		widget.setColor(colorToString(getParam().getColor()));// todo convert to miro
		widget.setBackgroundColor(colorToString(getParam().getBackcolor()));// todo convert to miro
		widgets.add(widget);
	}

	private void outLine(ULine shape) {
		output.add("LINE:");
		output.add("  pt1: " + pointd(getTranslateX(), getTranslateY()));
		output.add("  pt2: " + pointd(getTranslateX() + shape.getDX(), getTranslateY() + shape.getDY()));
		output.add("  stroke: " + getParam().getStroke());
		output.add("  shadow: " + (int) shape.getDeltaShadow());
		output.add("  color: " + colorToString(getParam().getColor()));
		output.add("");

		LineWidget line = new LineWidget(
				getTranslateX(), getTranslateY(),
				getTranslateX() + shape.getDX(), getTranslateY() + shape.getDY());
		line.setStroke(convertStroke(getParam().getStroke()));
		line.setType("straight");
		line.setColor(colorToString(getParam().getColor())); // TODO convert to MIRO color
		widgets.add(line);

	}

	private static String convertStroke(UStroke stroke){
		if (stroke.getDashVisible() > 0.0) {
			return "dashed";
		}
		if (stroke.getThickness() > 0.0) {
			return "dotted";
		}
		return "normal";
	}

	private String pointd(double x, double y) {
		return String.format(Locale.US, "[ %.4f ; %.4f ]", x, y);
	}

	private String colorToString(HColor color) {
		if (color == null) {
			return "NULL_COLOR";
		}
		if (color instanceof HColorSimple) {
			final HColorSimple simple = (HColorSimple) color;
			final Color internal = simple.getColor999();
			if (simple.isMonochrome()) {
				return "monochrome " + Integer.toHexString(internal.getRGB());
			}
			return Integer.toHexString(internal.getRGB());
		}
		if (color instanceof HColorMiddle) {
			final HColorMiddle middle = (HColorMiddle) color;
			return "middle(" + colorToString(middle.getC1()) + " & " + colorToString(middle.getC1()) + " )";
		}
		System.err.println("Error colorToString " + color.getClass().getSimpleName());
		return color.getClass().getSimpleName() + " " + new Date();
	}

	@Override
	public void writeToStream(OutputStream os, String metadata, int dpi) throws IOException {
		print(os, "DPI: " + dpi);
		print(os, "dimension: " + pointd(dim.getWidth(), dim.getHeight()));
		print(os, "scaleFactor: " + String.format(Locale.US, "%.4f", scaleFactor));
		print(os, "seed: " + seed);
		print(os, "svgLinkTarget: " + svgLinkTarget);
		print(os, "hoverPathColorRGB: " + hoverPathColorRGB);
		print(os, "preserveAspectRatio: " + preserveAspectRatio);
		print(os, "");

		for (String s : output) {
			print(os, s);
			System.out.println(s);
		}
		os.flush();

		widgets.forEach(System.out::println);

		Map<UUID, LineWidgetsId> map = new LinkedHashMap<>();
		try {
			for (Widget widget : widgets) {
				if (widget instanceof LineWidget) {
					var lineWidgetsId = RenderService.getInstance().prepareLineDots((LineWidget) widget);
					map.put(widget.getUid(), lineWidgetsId);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}

		try {
			for (Widget widget : widgets) {
				if (widget instanceof ShapeWidget) {
					RenderService.getInstance().render((ShapeWidget) widget);
				} else if (widget instanceof LineWidget) {
					LineWidgetsId lineWidgetsId = Objects.requireNonNull(map.get(widget.getUid()), "Missing");
					RenderService.getInstance().render((LineWidget) widget, lineWidgetsId);
				} else if (widget instanceof TextWidget) {
					RenderService.getInstance().render((TextWidget) widget);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void print(OutputStream os, String out) throws IOException {
		os.write(out.getBytes(UTF_8));
		os.write("\n".getBytes(UTF_8));
	}

}
