package edu.uci.ics.jung.visualization.swt.graphics;

import java.awt.Shape;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;

import edu.uci.ics.jung.visualization.swt.FourPassImageShaper;
import edu.uci.ics.jung.visualization.graphics.GraphicsContext;
import edu.uci.ics.jung.visualization.graphics.Image;
import edu.uci.ics.jung.visualization.graphics.ShapeProducer;

public class SWTImageImpl extends Image implements ShapeProducer {
	org.eclipse.swt.graphics.Image image;
	
	public SWTImageImpl(Device dev, int width, int height) {
		image = new org.eclipse.swt.graphics.Image(dev, width, height);
	}
	
	public SWTImageImpl(org.eclipse.swt.graphics.Image image) {
		this.image = image;
	}
	
	public org.eclipse.swt.graphics.Image getSWTImage() {
		return image;
	}
	
	public GraphicsContext getGraphicsContext() {
		GC gc = new GC(image);
		return new GCGraphicsContext(gc);
	}

	public int getWidth() {
		return image.getBounds().width;
	}
	
	public int getHeight() {
		return image.getBounds().height;
	}

	public Image getScaledInstance(int width, int height, int hints) {
//		java.awt.Image i = image.getScaledInstance(width, height, hints);
//		return new SWTImageImpl(i);
		// FIXME
		return null;
	}
	
	
	public Shape getShape() {
		return FourPassImageShaper.getShape(image, 30);
	}
}
