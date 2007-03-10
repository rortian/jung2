/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 */
package edu.uci.ics.jung.visualization.transform;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.uci.ics.jung.visualization.ScreenDevice;
import edu.uci.ics.jung.visualization.event.ScreenDeviceListener;

/**
 * LensTransformer wraps a MutableAffineTransformer and modifies
 * the transform and inverseTransform methods so that they create a
 * projection of the graph points within an elliptical lens.
 * 
 * LensTransformer uses an
 * affine transform to cause translation, scaling, rotation, and shearing
 * while applying a possibly non-affine filter in its transform and
 * inverseTransform methods.
 * 
 * @author Tom Nelson
 *
 *
 */
public abstract class LensTransformer extends MutableTransformerDecorator implements MutableTransformer {

    /**
     * the area affected by the transform
     */
    protected Ellipse2D ellipse = new Ellipse2D.Float();
    
    protected float magnification = 0.7f;
    
    /**
     * create an instance, setting values from the passed component
     * and registering to listen for size changes on the component
     * @param component
     */
//    public LensTransformer(Component component) {
//        this(component, new MutableAffineTransformer());
//    }
    /**
     * create an instance with a possibly shared transform
     * @param component
     * @param delegate
     */
    public LensTransformer(ScreenDevice component, MutableTransformer delegate) {
    		super(delegate);
        setComponent(component);
        component.addScreenDeviceListener(new ComponentListenerImpl());
   }
    
    /**
     * set values from the passed component.
     * declared private so it can't be overridden
     * @param component
     */
    private void setComponent(ScreenDevice component) {
//    	this.viewTransformer = (ViewTransformer)component;
        Dimension d = component.getSize();
//        if(d.width <= 0 || d.height <= 0) {
//            d = component.getPreferredSize();
//        }
        float ewidth = d.width/1.5f;
        float eheight = d.height/1.5f;
        ellipse.setFrame(d.width/2-ewidth/2, d.height/2-eheight/2, ewidth, eheight);
    }
    
    /**
     * @return Returns the magnification.
     */
    public float getMagnification() {
        return magnification;
    }
    /**
     * @param magnification The magnification to set.
     */
    public void setMagnification(float magnification) {
        this.magnification = magnification;
    }
    /**
     * @return Returns the viewCenter.
     */
    public Point2D getViewCenter() {
        return new Point2D.Double(ellipse.getCenterX(), ellipse.getCenterY());
    }
    /**
     * @param viewCenter The viewCenter to set.
     */
    public void setViewCenter(Point2D viewCenter) {
        double width = ellipse.getWidth();
        double height = ellipse.getHeight();
        ellipse.setFrame(viewCenter.getX()-width/2,
                viewCenter.getY()-height/2,
                width, height);
    }

    /**
     * @return Returns the viewRadius.
     */
    public double getViewRadius() {
        return ellipse.getHeight()/2;
    }
    /**
     * @param viewRadius The viewRadius to set.
     */
    public void setViewRadius(double viewRadius) {
        double x = ellipse.getCenterX();
        double y = ellipse.getCenterY();
        double viewRatio = getRatio();
        ellipse.setFrame(x-viewRadius/viewRatio,
                y-viewRadius,
                2*viewRadius/viewRatio,
                2*viewRadius);
    }
    
    /**
     * @return Returns the ratio.
     */
    public double getRatio() {
        return ellipse.getHeight()/ellipse.getWidth();
    }
    
    public void setEllipse(Ellipse2D ellipse) {
        this.ellipse = ellipse;
    }
    public Ellipse2D getEllipse() {
        return ellipse;
    }
    public void setToIdentity() {
        this.delegate.setToIdentity();
    }

    /**
     * react to size changes on a component
     */
    protected class ComponentListenerImpl implements ScreenDeviceListener {
        public void screenResized(ScreenDevice e) {
            setComponent(e);
         }
    }
    
    /**
     * override base class transform to project the fisheye effect
     */
    public abstract Point2D transform(Point2D graphPoint);
    
    /**
     * override base class to un-project the fisheye effect
     */
    public abstract Point2D inverseTransform(Point2D viewPoint);
    
    public double getDistanceFromCenter(Point2D p) {
    	
        double dx = ellipse.getCenterX()-p.getX();
        double dy = ellipse.getCenterY()-p.getY();
        dx *= getRatio();
        return Math.sqrt(dx*dx + dy*dy);
    }
    
    /**
     * return the supplied shape, translated to the coordinates
     * that result from calling transform on its center
     */
    public Shape transform(Shape shape) {
    	Rectangle2D bounds = shape.getBounds2D();
    	Point2D center = new Point2D.Double(bounds.getCenterX(),bounds.getCenterY());
    	Point2D newCenter = transform(center);
    	double dx = newCenter.getX()-center.getX();
    	double dy = newCenter.getY()-center.getY();
    	AffineTransform at = AffineTransform.getTranslateInstance(dx,dy);
    	return at.createTransformedShape(shape);
    }
    
    /**
     * return the supplied shape, translated to the coordinates
     * that result from calling inverseTransform on its center
     */
    public Shape inverseTransform(Shape shape) {
    	Rectangle2D bounds = shape.getBounds2D();
    	Point2D center = new Point2D.Double(bounds.getCenterX(),bounds.getCenterY());
    	Point2D newCenter = inverseTransform(center);
    	double dx = newCenter.getX()-center.getX();
    	double dy = newCenter.getY()-center.getY();
    	AffineTransform at = AffineTransform.getTranslateInstance(dx,dy);
    	return at.createTransformedShape(shape);
    }

}