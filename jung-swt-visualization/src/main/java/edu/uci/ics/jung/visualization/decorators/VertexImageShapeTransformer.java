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

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;


import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.visualization.graphics.Image;
import edu.uci.ics.jung.visualization.graphics.LayeredImage;
import edu.uci.ics.jung.visualization.graphics.ShapeProducer;


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
     protected Map<V,Image> imageMap;
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
		Image image = imageMap.get(v);
		if (image != null) {
			Shape shape = getShape(image);
			if (shape == null) return delegate.transform(v);
			return shape;
		} else {
			return delegate.transform(v);
		}
	}
    
    private Shape getShape(Image image) {
    	Shape shape = (Shape) shapeMap.get(image);
		if (shape == null) {
			shape = _getShape(image);
		}

		if (shape != null) {
			shapeMap.put(image, shape);
		} 
		
		return shape;
    }
    
    private Shape _getShape(Image image) {
    	Shape shape = null;
		if (image instanceof ShapeProducer)
			shape = ((ShapeProducer)image).getShape();
		
		if (shape != null) {
		    if (shape.getBounds().getWidth() > 0 && 
		            shape.getBounds().getHeight() > 0) {
                // don't cache a zero-sized shape, wait for the image
		       // to be ready
                int width = image.getWidth();
                int height = image.getHeight();
                AffineTransform transform = AffineTransform
					.getTranslateInstance(-width / 2, -height / 2);
                shape = transform.createTransformedShape(shape);
            }
		} else if (image instanceof LayeredImage) {
			shape = _getShape(((LayeredImage)image).getBaseImage());
		}
		
		return shape;
    }

    /**
	 * @return the iconMap
	 */
	public Map<V, Image> getImageMap() {
		return imageMap;
	}

	/**
	 * @param iconMap the iconMap to set
	 */
	public void setImageMap(Map<V, Image> imageMap) {
		this.imageMap = imageMap;
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
