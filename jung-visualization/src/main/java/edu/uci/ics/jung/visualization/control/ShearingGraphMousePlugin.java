/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 * Created on Mar 8, 2005
 *
 */
package edu.uci.ics.jung.visualization.control;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.cursor.Cursor;
import edu.uci.ics.jung.visualization.event.Event;
import edu.uci.ics.jung.visualization.event.MouseEvent;
import edu.uci.ics.jung.visualization.event.MouseListener;
import edu.uci.ics.jung.visualization.event.MouseMotionListener;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;

/** 
 * ShearingGraphMousePlugin allows the user to drag with the mouse
 * to shear the transform either in the horizontal or vertical direction.
 * By default, the control or meta key must be depressed to activate
 * shearing. 
 * 
 * 
 * @author Tom Nelson
 */
public class ShearingGraphMousePlugin extends AbstractGraphMousePlugin
    implements MouseListener, MouseMotionListener {

    private static int mask = Event.CTRL_MASK;
    
    static {
        if(System.getProperty("os.name").startsWith("Mac")) {
            mask = Event.META_MASK;
        }
    }
	/**
	 * create an instance with default modifier values
	 */
	public ShearingGraphMousePlugin() {
	    this(Event.BUTTON1_MASK | mask);
	}

	/**
	 * create an instance with passed modifier values
	 * @param modifiers the mouse modifiers to use
	 */
	public ShearingGraphMousePlugin(int modifiers) {
	    super(modifiers);
	    cursor = new Cursor(Cursor.ROTATE_CURSOR);
	}

	/**
	 * 
	 * @param e the event
	 */
	public void mousePressed(MouseEvent e) {
	    VisualizationViewer vv = (VisualizationViewer)e.getSource();
	    boolean accepted = checkModifiers(e);
	    down = e.getPoint();
	    if(accepted) {
	        vv.setCursor(cursor);
	    }
	}
    
	/**
	 * 
	 */
    public void mouseReleased(MouseEvent e) {
        VisualizationViewer vv = (VisualizationViewer)e.getSource();
        down = null;
        vv.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
	 * 
	 * 
	 * 
	 * 
	 */
    public void mouseDragged(MouseEvent e) {
        if(down == null) return;
        VisualizationViewer vv = (VisualizationViewer)e.getSource();
        boolean accepted = checkModifiers(e);
        if(accepted) {
            MutableTransformer modelTransformer = 
            	vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);
            vv.setCursor(cursor);
            Point2D q = down;
            Point2D p = e.getPoint();
            float dx = (float) (p.getX()-q.getX());
            float dy = (float) (p.getY()-q.getY());

            Dimension d = vv.getSize();
            float shx = 2.f*dx/d.height;
            float shy = 2.f*dy/d.width;
            Point2D center = vv.getCenter();
            if(p.getX() < center.getX()) {
                shy = -shy;
            }
            if(p.getY() < center.getY()) {
                shx = -shx;
            }
            modelTransformer.shear(shx, shy, center);
            down.x = e.getX();
            down.y = e.getY();
        
            e.consume();
        }
    }

    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

	public void mouseDoubleClicked(MouseEvent mouseEvent) {
		// TODO Auto-generated method stub
		
	}
}
