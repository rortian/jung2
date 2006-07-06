/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Aug 1, 2005
 */

package edu.uci.ics.jung.visualization.decorators;

import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.uci.ics.jung.visualization.FourPassImageShaper;

/**
 * A default implementation that stores images in a Map keyed on the
 * vertex. Also applies a shaping function to images to extract the
 * shape of the opaque part of a transparent image.
 * 
 * @author Tom Nelson - RABA Technologies
 *
 *
 */public class VertexIconAndShapeFunction<V> extends DefaultVertexIconFunction<V>
     implements VertexShapeFunction<V> {
     
     protected Map<Image, Shape> shapeMap = new HashMap<Image, Shape>();
     protected VertexShapeFunction<V> delegate;
     /**
      * 
      *
      */
    public VertexIconAndShapeFunction(VertexShapeFunction<V> delegate) {
        this.delegate = delegate;
    }

    /**
     * @return Returns the delegate.
     */
    public VertexShapeFunction<V> getDelegate() {
        return delegate;
    }

    /**
     * @param delegate The delegate to set.
     */
    public void setDelegate(VertexShapeFunction<V> delegate) {
        this.delegate = delegate;
    }

    /**
     * get the shape from the image. If not available, get
     * the shape from the delegate VertexShapeFunction
     */
    public Shape getShape(V v) {
		Icon icon = getIcon(v);
		if (icon != null && icon instanceof ImageIcon) {
			Image image = ((ImageIcon) icon).getImage();
			Shape shape = (Shape) shapeMap.get(image);
			if (shape == null) {
			    shape = FourPassImageShaper.getShape(image, 30);
			    if(shape.getBounds().getWidth() > 0 && 
			            shape.getBounds().getHeight() > 0) {
                    // don't cache a zero-sized shape, wait for the image
			       // to be ready
                    int width = image.getWidth(null);
                    int height = image.getHeight(null);
                    AffineTransform transform = AffineTransform
						.getTranslateInstance(-width / 2, -height / 2);
                    shape = transform.createTransformedShape(shape);
                    shapeMap.put(image, shape);
                }
			}
			return shape;
		} else {
			return delegate.getShape(v);
		}

	}
}
