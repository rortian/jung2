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

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import edu.uci.ics.jung.visualization.graphics.GraphicsContext;
import edu.uci.ics.jung.visualization.graphics.Image;
import edu.uci.ics.jung.visualization.transform.BidirectionalTransformer;


/**
 * subclassed to pass certain operations thru the transformer
 * before the base class method is applied
 * This is useful when you want to apply non-affine transformations
 * to the Graphics2D used to draw elements of the graph.
 * 
 * @author Tom Nelson 
 *
 *
 */
public class TransformingGraphics extends GraphicsDecorator {
    
    /**
     * the transformer to apply
     */
    protected BidirectionalTransformer transformer;
    
    public TransformingGraphics(BidirectionalTransformer transformer) {
        this(transformer, null);
    }
    
    public TransformingGraphics(BidirectionalTransformer transformer, GraphicsContext delegate) {
        super(delegate);
        this.transformer = transformer;
    }
    
    /**
     * @return Returns the transformer.
     */
    public BidirectionalTransformer getTransformer() {
        return transformer;
    }
    
    /**
     * @param transformer The transformer to set.
     */
    public void setTransformer(BidirectionalTransformer transformer) {
        this.transformer = transformer;
    }
    
    /**
     * transform the shape before letting the delegate draw it
     */
    public void draw(Shape s) {
        Shape shape = ((ShapeTransformer)transformer).transform(s);
        delegate.draw(shape);
    }
    
    public void draw(Shape s, float flatness) {
        Shape shape = null;
        if(transformer instanceof ShapeFlatnessTransformer) {
            shape = ((ShapeFlatnessTransformer)transformer).transform(s, flatness);
        } else {
            shape = ((ShapeTransformer)transformer).transform(s);
        }
        delegate.draw(shape);
        
    }
    
    /**
     * transform the shape before letting the delegate fill it
     */
    public void fill(Shape s) {
        Shape shape = ((ShapeTransformer)transformer).transform(s);
        delegate.fill(shape);
    }
    
    public void fill(Shape s, float flatness) {
        Shape shape = null;
        if(transformer instanceof ShapeFlatnessTransformer) {
            shape = ((ShapeFlatnessTransformer)transformer).transform(s, flatness);
        } else {
            shape = ((ShapeTransformer)transformer).transform(s);
        }
        delegate.fill(shape);
    }
    
    public void drawImage(Image img, int x, int y) {
    	Image image = null;
        if(transformer instanceof ShapeFlatnessTransformer) {
        	Rectangle2D r = new Rectangle2D.Double(x,y,img.getWidth(),img.getHeight());
        	Rectangle2D s = ((ShapeTransformer)transformer).transform(r).getBounds2D();
        	image = img.getScaledInstance((int)s.getWidth(), (int)s.getHeight(), Image.SCALE_SMOOTH);
        	x = (int) s.getMinX();
        	y = (int) s.getMinY();
        } else {
            image = img;
        }
         delegate.drawImage(image, x, y);
    }

    public void drawImage(Image img, AffineTransform at) {
    	Image image = null;
    	int x = (int)at.getTranslateX();
    	int y = (int)at.getTranslateY();
        if(transformer instanceof ShapeFlatnessTransformer) {
        	Rectangle2D r = new Rectangle2D.Double(x,y,img.getWidth(),img.getHeight());
        	Rectangle2D s = ((ShapeTransformer)transformer).transform(r).getBounds2D();
        	image = img.getScaledInstance((int)s.getWidth(), (int)s.getHeight(), Image.SCALE_SMOOTH);
        	x = (int) s.getMinX();
        	y = (int) s.getMinY();
        	at.setToTranslation(s.getMinX(), s.getMinY());
        } else {
            image = img;
        }
        super.drawImage(image, at);
    }

    /**
     * transform the shape before letting the delegate apply 'hit'
     * with it
     */
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        Shape shape = ((ShapeTransformer)transformer).transform(s);
        return delegate.hit(rect, shape, onStroke);
    }
}
