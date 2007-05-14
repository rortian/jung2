/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Jul 11, 2005
 */

package edu.uci.ics.jung.visualization.transform.shape;

import java.awt.Shape;
import java.awt.geom.AffineTransform;

import edu.uci.ics.jung.visualization.graphics.GraphicsWrapper;
import edu.uci.ics.jung.visualization.graphics.GraphicsContext;
import edu.uci.ics.jung.visualization.graphics.Image;
import edu.uci.ics.jung.visualization.graphics.ImageDrawDelegate;


/**
 * an extendion of Graphics2DWrapper that adds enhanced
 * methods for drawing icons and components
 * 
 * @see TransformingGraphics as an example subclass
 * 
 * @author Tom Nelson 
 *
 *
 */
public class GraphicsDecorator extends GraphicsWrapper {
    
    public GraphicsDecorator() {
        this(null);
    }
    public GraphicsDecorator(GraphicsContext delegate) {
        super(delegate);
    }
    
    
    
	public void drawImage(Image img, AffineTransform xform) {
    	xform = new AffineTransform(xform);
    	xform.translate(-img.getWidth()/2, -img.getHeight()/2);
    	AffineTransform oldXform = getTransform();
    	setTransform(xform);
		drawImage(img, 0, 0);
		setTransform(oldXform);
	}
	@Override
	public void drawImage(Image img, int x, int y) {
		if (img instanceof ImageDrawDelegate)
			img.draw(delegate, x, y);
		else super.drawImage(img, x, y);
	}
	
	public void drawImage(Image img, Shape clip, int x, int y) {
		if (img instanceof ImageDrawDelegate)
			img.draw(delegate, x, y);
		else super.drawImage(img, x, y);
	}
}
