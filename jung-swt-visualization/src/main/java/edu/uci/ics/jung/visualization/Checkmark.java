package edu.uci.ics.jung.visualization;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import edu.uci.ics.jung.visualization.graphics.GraphicsContext;
import edu.uci.ics.jung.visualization.graphics.Image;

/**
 * a simple Icon that draws a checkmark in the lower-right quadrant of its
 * area. Used to draw a checkmark on Picked Vertices.
 * @author Tom Nelson
 */
public class Checkmark extends Image {

	GeneralPath path = new GeneralPath();
	AffineTransform highlight = AffineTransform.getTranslateInstance(-1,-1);
	AffineTransform lowlight = AffineTransform.getTranslateInstance(1,1);
	AffineTransform shadow = AffineTransform.getTranslateInstance(2,2);
	Color color;
	public Checkmark() {
		this(Color.green);
	}
	public Checkmark(Color color) {
		this.color = color;
		path.moveTo(10,17);
		path.lineTo(13,20);
		path.lineTo(20,13);
	}
	public void draw(GraphicsContext g, int x, int y) {
		Shape shape = AffineTransform.getTranslateInstance(x, y).createTransformedShape(path);
		
		Stroke stroke = g.getStroke();
		boolean aa = g.getAntialiasing();
		
		g.setAntialiasing(true);
		g.setStroke(new BasicStroke(4));
		g.setColor(Color.darkGray);
		g.draw(shadow.createTransformedShape(shape));
		g.setColor(Color.black);
		g.draw(lowlight.createTransformedShape(shape));
		g.setColor(Color.white);
		g.draw(highlight.createTransformedShape(shape));
		g.setColor(color);
		g.draw(shape);
		
		g.setStroke(stroke);
		g.setAntialiasing(aa);
	}

	public int getWidth() {
		return 20;
	}

	public int getHeight() {
		return 20;
	}
	
	@Override
	public GraphicsContext getGraphicsContext() {
		throw new UnsupportedOperationException();
	}
	@Override
	public Image getScaledInstance(int width, int height, int hints) {
		throw new UnsupportedOperationException();
	}
}