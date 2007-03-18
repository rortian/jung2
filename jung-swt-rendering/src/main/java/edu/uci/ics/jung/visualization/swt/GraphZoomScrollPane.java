/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * Created on Feb 2, 2005
 *
 */
package edu.uci.ics.jung.visualization.swt;


import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Slider;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.transform.BidirectionalTransformer;
import edu.uci.ics.jung.visualization.transform.shape.Intersector;



/**
 * GraphZoomScrollPane is a Container for the Graph's VisualizationViewer
 * and includes custom horizontal and vertical scrollbars.
 * GraphZoomScrollPane listens for changes in the scale and
 * translation of the VisualizationViewer, and will update the
 * scrollbar positions and sizes accordingly. Changes in the
 * scrollbar positions will cause the corresponding change in
 * the translation component (offset) of the VisualizationViewer.
 * The scrollbars are modified so that they will allow panning
 * of the graph when the scale has been changed (e.g. zoomed-in
 * or zoomed-out).
 * 
 * The lower-right corner of this component is available to
 * use as a small button or menu.
 * 
 * samples.graph.GraphZoomScrollPaneDemo shows the use of this component.
 * 
 * @author Tom Nelson 
 *
 * 
 */
@SuppressWarnings("serial")
public class GraphZoomScrollPane<V,E> extends Composite {
	public VisualizationComposite<V, E> vv;
    protected Slider horizontalScrollBar;
    protected Slider verticalScrollBar;
    protected Composite corner;
    protected boolean scrollBarsMayControlAdjusting = true;
    protected Composite south;
    
    
    
    /**
     * Create an instance of the GraphZoomScrollPane to contain the
     * VisualizationViewer
     * @param vv
     */
    public GraphZoomScrollPane(Composite parent, int style, Layout<V,E> layout, Dimension preferredSize) {
        super(parent, style);
        GridLayout grid = new GridLayout();
        grid.numColumns = 2;
        grid.horizontalSpacing = 0;
        grid.verticalSpacing = 0;
        setLayout(grid);
        
        addControlListener(new ResizeListener());        
//        Dimension d = vv.getGraphLayout().getSize();
        this.vv = new VisualizationComposite<V,E>(this, SWT.NONE, layout, new Dimension(600,600));
        GridData gdc = new GridData();
        gdc.grabExcessHorizontalSpace = true;
        gdc.grabExcessVerticalSpace = true;
        gdc.horizontalAlignment = GridData.FILL;
        gdc.verticalAlignment = GridData.FILL;
        vv.dest.setLayoutData(gdc);
        
        verticalScrollBar = new Slider(this, SWT.VERTICAL);
        GridData gdv = new GridData();
        gdv.grabExcessHorizontalSpace = false;
        gdv.grabExcessVerticalSpace = true;
        gdv.horizontalAlignment = GridData.FILL;
        gdv.verticalAlignment = GridData.FILL;
        verticalScrollBar.setLayoutData(gdv);
        
        horizontalScrollBar = new Slider(this, SWT.HORIZONTAL);
        GridData gdh = new GridData();
        gdh.grabExcessHorizontalSpace = true;
        gdh.grabExcessVerticalSpace = false;
        gdh.horizontalAlignment = GridData.FILL;
        gdh.verticalAlignment = GridData.FILL;
        horizontalScrollBar.setLayoutData(gdh);
        
        
        Label filler = new Label(this, SWT.NONE);
        verticalScrollBar.addListener(SWT.Selection, new VerticalAdjustmentListenerImpl());
        horizontalScrollBar.addListener(SWT.Selection, new HorizontalAdjustmentListenerImpl());
//        verticalScrollBar.setUnitIncrement(20);
//        horizontalScrollBar.setUnitIncrement(20);
        // respond to changes in the VisualizationViewer's transform
        // and set the scroll bar parameters appropriately
        vv.addChangeListener(
                new ChangeListener(){
            public void stateChanged(ChangeEvent evt) {
//                VisualizationComposite vv = 
//                    (VisualizationComposite)evt.getSource();
                setScrollBars(GraphZoomScrollPane.this.vv);
            }
        });
//        add(vv);
//        add(verticalScrollBar, BorderLayout.EAST);
//        south = new JPanel(new BorderLayout());
//        south.add(horizontalScrollBar);
//        setCorner(new JPanel());
//        add(south, BorderLayout.SOUTH);
    }
    
    /**
     * listener for adjustment of the horizontal scroll bar.
     * Sets the translation of the VisualizationViewer
     */
    class HorizontalAdjustmentListenerImpl implements Listener {
        int previous = 0;
        public void handleEvent (Event event) {
        	Slider sb = (Slider)event.widget;
        	int hval = sb.getSelection();
            float dh = previous - hval;
            previous = hval;
            if(dh != 0 && scrollBarsMayControlAdjusting) {
                // get the uniform scale of all transforms
                float layoutScale = (float) vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).getScale();
                dh *= layoutScale;
                AffineTransform at = AffineTransform.getTranslateInstance(dh, 0);
                vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).preConcatenate(at);
            }
        }
    }
    
    /**
     * Listener for adjustment of the vertical scroll bar.
     * Sets the translation of the VisualizationViewer
     */
    class VerticalAdjustmentListenerImpl implements Listener {
        int previous = 0;
        public void handleEvent (Event event) {
        	Slider sb = (Slider)event.widget;
        	int vval = sb.getSelection();
            float dv = previous - vval;
            previous = vval;
            if(dv != 0 && scrollBarsMayControlAdjusting) {
            
                // get the uniform scale of all transforms
                float layoutScale = (float) vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).getScale();
                dv *= layoutScale;
                AffineTransform at = AffineTransform.getTranslateInstance(0, dv);
                vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).preConcatenate(at);
            }	
		}
    }
    
    /**
     * use the supplied vv characteristics to set the position and
     * dimensions of the scroll bars. Called in response to
     * a ChangeEvent from the VisualizationViewer
     * @param xform the transform of the VisualizationViewer
     */
    private void setScrollBars(VisualizationComposite vv) {
        Dimension d = vv.getGraphLayout().getSize();
        Rectangle vvBounds = vv.getBounds();
        
        // a rectangle representing the layout
        Rectangle layoutRectangle = 
            new Rectangle(0,0,d.width,d.height);
            		//-d.width/2, -d.height/2, 2*d.width, 2*d.height);
        
        BidirectionalTransformer viewTransformer = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW);
        BidirectionalTransformer layoutTransformer = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);

        Point2D h0 = new Point2D.Double(vvBounds.getMinX(), vvBounds.getCenterY());
        Point2D h1 = new Point2D.Double(vvBounds.getMaxX(), vvBounds.getCenterY());
        Point2D v0 = new Point2D.Double(vvBounds.getCenterX(), vvBounds.getMinY());
        Point2D v1 = new Point2D.Double(vvBounds.getCenterX(), vvBounds.getMaxY());
        
        h0 = viewTransformer.inverseTransform(h0);
        h0 = layoutTransformer.inverseTransform(h0);
        h1 = viewTransformer.inverseTransform(h1);
        h1 = layoutTransformer.inverseTransform(h1);
        v0 = viewTransformer.inverseTransform(v0);
        v0 = layoutTransformer.inverseTransform(v0);
        v1 = viewTransformer.inverseTransform(v1);
        v1 = layoutTransformer.inverseTransform(v1);
        
        scrollBarsMayControlAdjusting = false;
        setScrollBarValues(layoutRectangle, h0, h1, v0, v1);
        scrollBarsMayControlAdjusting = true;
    }
    
    @SuppressWarnings("unchecked")
    protected void setScrollBarValues(Rectangle rectangle, 
            Point2D h0, Point2D h1, 
            Point2D v0, Point2D v1) {
        boolean containsH0 = rectangle.contains(h0);
        boolean containsH1 = rectangle.contains(h1);
        boolean containsV0 = rectangle.contains(v0);
        boolean containsV1 = rectangle.contains(v1);
        
        // horizontal scrollbar:
        
        Intersector intersector = new Intersector(rectangle, new Line2D.Double(h0, h1));
        
        int min = 0;
        int ext;
        int val = 0;
        int max;
        
        Set points = intersector.getPoints();
        Point2D first = null;
        Point2D second = null;
        
        Point2D[] pointArray = (Point2D[])points.toArray(new Point2D[points.size()]);
        if(pointArray.length > 1) {
            first = pointArray[0];
            second = pointArray[1];
        } else if(pointArray.length > 0) {
            first = second = pointArray[0];
        }
        
        if(first != null && second != null) {
            // correct direction of intersect points
            if((h0.getX() - h1.getX()) * (first.getX() - second.getX()) < 0) {
                // swap them
                Point2D temp = first;
                first = second;
                second = temp;
            }

            if(containsH0 && containsH1) {
                max = (int)first.distance(second);
                val = (int)first.distance(h0);
                ext = (int)h0.distance(h1);
                
            } else if(containsH0) {
                max = (int)first.distance(second);
                val = (int)first.distance(h0);
                ext = (int)h0.distance(second);
                
            } else if(containsH1) {
                max = (int) first.distance(second);
                val = 0;
                ext = (int) first.distance(h1);
                
            } else {
                max = ext = rectangle.width;
                val = min;
            }
            horizontalScrollBar.setValues(val, min, max, ext+1, 20, 20);
        }
        
        // vertical scroll bar
        min = val = 0;
        
        intersector.intersectLine(new Line2D.Double(v0, v1));
        points = intersector.getPoints();
        
        pointArray = (Point2D[])points.toArray(new Point2D[points.size()]);
        if(pointArray.length > 1) {
            first = pointArray[0];
            second = pointArray[1];
        } else if(pointArray.length > 0) {
            first = second = pointArray[0];
        }
        
        if(first != null && second != null) {
            
            // arrange for direction
            if((v0.getY() - v1.getY()) * (first.getY() - second.getY()) < 0) {
                // swap them
                Point2D temp = first;
                first = second;
                second = temp;
            }
            
            if(containsV0 && containsV1) {
                max = (int)first.distance(second);
                val = (int)first.distance(v0);
                ext = (int)v0.distance(v1);
                
            } else if(containsV0) {
                max = (int)first.distance(second);
                val = (int)first.distance(v0);
                ext = (int)v0.distance(second);
                
            } else if(containsV1) {
                max = (int) first.distance(second);
                val = 0;
                ext = (int) first.distance(v1);
                
            } else {
                max = ext = rectangle.height;
                val = min;
            }
            verticalScrollBar.setValues(val, min, max, ext+1, 20, 20);
        }
    }

    /**
     * Listener to adjust the scroll bar parameters when the window
     * is resized
     */
	protected class ResizeListener extends ControlAdapter {
		@Override
		public void controlResized(ControlEvent e) {
			 setScrollBars(vv);
		}
	}

    /**
     * @return Returns the corner component.
     */
    public Composite getCorner() {
        return corner;
    }
}
