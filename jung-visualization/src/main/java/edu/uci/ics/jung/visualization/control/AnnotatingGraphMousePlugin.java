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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import edu.uci.ics.jung.visualization.AnnotationPaintable;
import edu.uci.ics.jung.visualization.MultiLayerTransformer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.VisualizationServer.Paintable;

/** 
 * PickingGraphMousePlugin supports the picking of graph elements
 * with the mouse. MouseButtonOne picks a single vertex
 * or edge, and MouseButtonTwo adds to the set of selected Vertices
 * or EdgeType. If a Vertex is selected and the mouse is dragged while
 * on the selected Vertex, then that Vertex will be repositioned to
 * follow the mouse until the button is released.
 * 
 * @author Tom Nelson
 */
public class AnnotatingGraphMousePlugin<V, E> extends AbstractGraphMousePlugin
    implements MouseListener, MouseMotionListener {

    /**
     * additional modifiers for the action of adding to an existing
     * selection
     */
    protected int additionalModifiers;
    
    /**
     * used to draw a rectangle to contain picked vertices
     */
    protected RectangularShape rect = new Rectangle2D.Float();
    
    /**
     * the Paintable for the lens picking rectangle
     */
    protected Paintable lensPaintable;
    
    protected AnnotationPaintable annotationPaintable;
    
    /**
     * color for the picking rectangle
     */
    protected Color lensColor = Color.cyan;
    
    MultiLayerTransformer basicTransformer;
    
    RenderContext rc;
    
    boolean added = false;
    
    /**
	 * create an instance with default settings
	 */
	public AnnotatingGraphMousePlugin(RenderContext rc) {
	    this(rc, InputEvent.BUTTON1_MASK, InputEvent.BUTTON1_MASK | InputEvent.SHIFT_MASK);
	}

	/**
	 * create an instance with overides
	 * @param selectionModifiers for primary selection
	 * @param addToSelectionModifiers for additional selection
	 */
    public AnnotatingGraphMousePlugin(RenderContext rc,
    		int selectionModifiers, int additionalModifiers) {
        super(selectionModifiers);
        this.rc = rc;
        this.basicTransformer = rc.getBasicTransformer();
        this.additionalModifiers = additionalModifiers;
        this.lensPaintable = new LensPaintable();
        this.annotationPaintable = new AnnotationPaintable(rc);
        this.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    }
    
    /**
     * @return Returns the lensColor.
     */
    public Color getLensColor() {
        return lensColor;
    }

    /**
     * @param lensColor The lensColor to set.
     */
    public void setLensColor(Color lensColor) {
        this.lensColor = lensColor;
    }

    class LensPaintable implements Paintable {

        public void paint(Graphics g) {
            Color oldColor = g.getColor();
            g.setColor(lensColor);
            ((Graphics2D)g).draw(rect);
            g.setColor(oldColor);
        }

        public boolean useTransform() {
            return false;
        }
    }

    /**
	 * For primary modifiers (default, MouseButton1):
	 * pick a single Vertex or Edge that
     * is under the mouse pointer. If no Vertex or edge is under
     * the pointer, unselect all picked Vertices and edges, and
     * set up to draw a rectangle for multiple selection
     * of contained Vertices.
     * For additional selection (default Shift+MouseButton1):
     * Add to the selection, a single Vertex or Edge that is
     * under the mouse pointer. If a previously picked Vertex
     * or Edge is under the pointer, it is un-picked.
     * If no vertex or Edge is under the pointer, set up
     * to draw a multiple selection rectangle (as above)
     * but do not unpick previously picked elements.
	 * 
	 * @param e the event
	 */
    @SuppressWarnings("unchecked")
    public void mousePressed(MouseEvent e) {
    	VisualizationViewer<V,E> vv = (VisualizationViewer)e.getSource();
    	down = e.getPoint();
    	
    	if(e.isPopupTrigger()) {
    		String annotation = JOptionPane.showInputDialog(vv,"Annotation:");
    		if(annotation != null && annotation.length() > 0) {
    			Point2D p = vv.getRenderContext().getBasicTransformer().inverseTransform(down);
    			annotationPaintable.add(p, annotation);
    		}
    	} else if(e.getModifiers() == additionalModifiers) {
    		rect = new Ellipse2D.Double();
    		rect.setFrameFromDiagonal(down,down);
    		vv.addPostRenderPaintable(lensPaintable);
    	} else if(e.getModifiers() == modifiers) {
    		rect = new Rectangle2D.Double();
    		rect.setFrameFromDiagonal(down,down);
    		vv.addPostRenderPaintable(lensPaintable);
    	}
    }

    /**
	 * If the mouse is dragging a rectangle, pick the
	 * Vertices contained in that rectangle
	 * 
	 * clean up settings from mousePressed
	 */
    @SuppressWarnings("unchecked")
    public void mouseReleased(MouseEvent e) {
        VisualizationViewer<V,E> vv = (VisualizationViewer)e.getSource();
        if(e.getModifiers() == additionalModifiers) {
        	if(down != null) {
        		Point2D out = e.getPoint();
        		Ellipse2D arect = new Ellipse2D.Double();
        		arect.setFrameFromDiagonal(down,out);
        		Shape s = vv.getRenderContext().getBasicTransformer().inverseTransform(arect);
        		annotationPaintable.add(s,Color.cyan);
        		if(added == false) {
        			vv.addPostRenderPaintable(annotationPaintable);
        			added = true;
        		}
        	} 
        } else if(e.getModifiers() == modifiers) {
        	if(down != null) {
        		Point2D out = e.getPoint();
        		Rectangle2D arect = new Rectangle2D.Double();
        		arect.setFrameFromDiagonal(down,out);
        		Shape s = vv.getRenderContext().getBasicTransformer().inverseTransform(arect);
        		annotationPaintable.add(s,Color.red);
        		if(added == false) {
        			vv.addPostRenderPaintable(annotationPaintable);
        			added = true;
        		}
        	}
        }
        down = null;
        vv.removePostRenderPaintable(lensPaintable);
    }
    
    /**
	 * If the mouse is over a picked vertex, drag all picked
	 * vertices with the mouse.
	 * If the mouse is not over a Vertex, draw the rectangle
	 * to select multiple Vertices
	 * 
	 */
    @SuppressWarnings("unchecked")
    public void mouseDragged(MouseEvent e) {
    	Point2D out = e.getPoint();
    	if(e.getModifiers() == additionalModifiers) {
            rect.setFrameFromDiagonal(down,out);
    		
    	} else if(e.getModifiers() == modifiers) {
            rect.setFrameFromDiagonal(down,out);
    		
    	}
        rect.setFrameFromDiagonal(down,out);
    }
    
     public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
        JComponent c = (JComponent)e.getSource();
        c.setCursor(cursor);
    }

    public void mouseExited(MouseEvent e) {
        JComponent c = (JComponent)e.getSource();
        c.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public void mouseMoved(MouseEvent e) {
    }

 }
