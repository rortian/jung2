/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Aug 1, 2005
 */

package edu.uci.ics.jung.visualization.swt.decorators;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.visualization.swt.FourPassImageShaper;
import edu.uci.ics.jung.visualization.swt.graphics.SWTImageImpl;
import edu.uci.ics.jung.visualization.graphics.Image;
import edu.uci.ics.jung.visualization.graphics.LayeredImage;

/**
 * A default implementation that stores images in a Map keyed on the
 * vertex. Also applies a shaping function to images to extract the
 * shape of the opaque part of a transparent image.
 * 
 * @author Tom Nelson 
 *
 *
 */public class VertexImageShapeTransformer<V> implements Transformer<V,Shape> {
     
     protected Map<Image, Shape> shapeMap = new HashMap<Image, Shape>();
     protected Map<V,Image> iconMap;
     protected Transformer<V,Shape> delegate;
     /**
      * 
      *
      */
    public VertexImageShapeTransformer(Transformer<V,Shape> delegate) {
        this.delegate = delegate;
    }

    /**
     * @return Returns the delegate.
     */
    public Transformer<V,Shape> getDelegate() {
        return delegate;
    }

    /**
     * @param delegate The delegate to set.
     */
    public void setDelegate(Transformer<V,Shape> delegate) {
        this.delegate = delegate;
    }

    /**
     * get the shape from the image. If not available, get
     * the shape from the delegate VertexShapeFunction
     */
    public Shape transform(V v) {
    	Image icon = iconMap.get(v);
    	while (icon instanceof LayeredImage) {
    		icon = ((LayeredImage)icon).getBaseImage();
    	}
		if (icon != null && icon instanceof SWTImageImpl) {
			org.eclipse.swt.graphics.Image image = ((SWTImageImpl) icon).getSWTImage();
			Shape shape = (Shape) shapeMap.get(icon);
			if (shape == null) {
			    shape = FourPassImageShaper.getShape(image, 30);
			    if(shape.getBounds().getWidth() > 0 && 
			            shape.getBounds().getHeight() > 0) {
                    // don't cache a zero-sized shape, wait for the image
			       // to be ready
                    int width = image.getBounds().width;
                    int height = image.getBounds().height;
                    AffineTransform transform = AffineTransform
						.getTranslateInstance(-width / 2, -height / 2);
                    shape = transform.createTransformedShape(shape);
                    shapeMap.put(icon, shape);
                }
			}
			return shape;
		} else {
			return delegate.transform(v);
		}
	}

    /**
	 * @return the iconMap
	 */
	public Map<V, Image> getIconMap() {
		return iconMap;
	}

	/**
	 * @param iconMap the iconMap to set
	 */
	public void setIconMap(Map<V, Image> iconMap) {
		this.iconMap = iconMap;
	}

	/**
	 * @return the shapeMap
	 */
	public Map<Image, Shape> getShapeMap() {
		return shapeMap;
	}

	/**
	 * @param shapeMap the shapeMap to set
	 */
	public void setShapeMap(Map<Image, Shape> shapeMap) {
		this.shapeMap = shapeMap;
	}
}
