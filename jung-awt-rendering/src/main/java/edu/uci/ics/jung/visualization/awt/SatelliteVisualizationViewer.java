/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Aug 15, 2005
 */

package edu.uci.ics.jung.visualization.awt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalSatelliteGraphMouse;
import edu.uci.ics.jung.visualization.transform.MutableAffineTransformer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
import edu.uci.ics.jung.visualization.transform.shape.ShapeTransformer;

/**
 * A VisualizationViewer that can act as a satellite view for another
 * (master) VisualizationViewer. In this view, the full graph is always visible
 * and all mouse actions affect the graph in the master view.
 * 
 * A rectangular shape in the satellite view shows the visible bounds of
 * the master view. 
 * 
 * @author Tom Nelson 
 *
 * 
 */
@SuppressWarnings("serial")
public class SatelliteVisualizationViewer<V, E> 
	extends VisualizationComponent<V,E> {
	protected Map<V,Point2D> locationMap = new HashMap<V,Point2D>();
	
    /**
     * the master VisualizationViewer that this is a satellite view for
     */
    protected VisualizationComponent<V,E> master;
    
    /**
     * @param layout
     * @param renderer
     */
    public SatelliteVisualizationViewer(VisualizationComponent<V,E> master) {
        this(master, master.getServer().getModel());
    }

    /**
     * @param layout
     * @param renderer
     * @param preferredSize
     */
    public SatelliteVisualizationViewer(VisualizationComponent<V,E> master,
            Dimension preferredSize) {
        this(master, master.getServer().getModel(), preferredSize);
    }

    /**
     * used internally, as the sattellite should always share the model of
     * the master
     * @param model
     * @param renderer
     */
    protected SatelliteVisualizationViewer(VisualizationComponent<V,E> master, VisualizationModel<V,E> model) {
        this(master, model, new Dimension(300,300));
    }

    /**
     * Used internally, as the satellite should always share the model of the master
     * @param master the master view
     * @param model
     * @param renderer
     * @param preferredSize
     */
    protected SatelliteVisualizationViewer(VisualizationComponent<V,E> master, VisualizationModel<V,E> model,
            Dimension preferredSize) {
        super(model, preferredSize);
        this.master = master;
        
        // create a graph mouse with custom plugins to affect the master view
        ModalGraphMouse gm = new ModalSatelliteGraphMouse();
        setGraphMouse(gm);
        
        // this adds the Lens to the satellite view
        visualizationServer.addPreRenderPaintable(new ViewLens<V,E>(this, master));
        
        // get a copy of the current layout transform
        // it may have been scaled to fit the graph
        AffineTransform modelLayoutTransform =
            new AffineTransform(master.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).getTransform());
        
        // I want no layout transformations in the satellite view
        // this resets the auto-scaling that occurs in the super constructor
        getRenderContext().getMultiLayerTransformer().setTransformer(Layer.LAYOUT, new MutableAffineTransformer(modelLayoutTransform));
        
        // make sure the satellite listens for changes in the master
        master.addChangeListener(this);
        
        // share the picked state of the master
        visualizationServer.setPickedVertexState(master.getServer().getPickedVertexState());
        visualizationServer.setPickedEdgeState(master.getServer().getPickedEdgeState());
    }

    /**
     * @return Returns the master.
     */
    public VisualizationViewer<V,E> getMaster() {
        return master;
    }
    
    protected void paintComponent(Graphics g) {
    	renderGraph((Graphics2D)g);
    }
    
    protected void renderGraph(Graphics2D g2d) {
        if(visualizationServer.getRenderContext().getGraphicsContext() == null) {
        	visualizationServer.getRenderContext().setGraphicsContext(new GraphicsDecorator(g2d));
        } else {
        	visualizationServer.getRenderContext().getGraphicsContext().setDelegate(g2d);
        }
        Layout<V,E> layout = visualizationServer.getModel().getGraphLayout();

        g2d.setRenderingHints(renderingHints);
        
        // the size of the VisualizationViewer
        Dimension d = getSize();
        
        // clear the offscreen image
        g2d.setColor(getBackground());
        g2d.fillRect(0,0,d.width,d.height);

        AffineTransform oldXform = g2d.getTransform();
        AffineTransform newXform = new AffineTransform(oldXform);
        newXform.concatenate(getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getTransform());
        
        g2d.setTransform(newXform);

        // if there are  preRenderers set, paint them
        for(Iterator iterator=visualizationServer.getPreRenderPaintables().iterator(); iterator.hasNext(); ) {
        	VisualizationServer.Paintable paintable = (VisualizationServer.Paintable)iterator.next();
            if(paintable.useTransform()) {
                paintable.paint(g2d);
            } else {
                g2d.setTransform(oldXform);
                paintable.paint(g2d);
                g2d.setTransform(newXform);
            }
        }
        
        locationMap.clear();
        
        // paint all the edges
        try {
            for(E e : layout.getGraph().getEdges()) {

            V v1 = layout.getGraph().getEndpoints(e).getFirst();
            V v2 = layout.getGraph().getEndpoints(e).getSecond();
            
            Point2D p = (Point2D) locationMap.get(v1);
            if(p == null) {
                
                p = layout.transform(v1);
//                p = getRenderContext().getBasicTransformer().getLayoutTransformer().transform(p);
                locationMap.put(v1, p);
            }
            Point2D q = (Point2D) locationMap.get(v2);
            if(q == null) {
                q = layout.transform(v2);
//                q = getRenderContext().getBasicTransformer().getLayoutTransformer().transform(q);
                locationMap.put(v2, q);
            }

            if(p != null && q != null) {
//              renderer.paintEdge(
                        getServer().getRenderer().renderEdge(
                        getServer().getRenderContext(),
                        layout,
                        e);
            }
        }
        } catch(ConcurrentModificationException cme) {
            repaint();
        }
        
        // paint all the vertices
        try {
            for(V v : layout.getGraph().getVertices()) {

            Point2D p = (Point2D) locationMap.get(v);
            if(p == null) {
                p = layout.transform(v);
//                p = getRenderContext().getBasicTransformer().getLayoutTransformer().transform(p);
                locationMap.put(v, p);
            }
            if(p != null) {
//              renderer.paintVertex(
            	getServer().getRenderer().renderVertex(
            			getServer().getRenderContext(),
                        layout,
                        v);
            }
        }
        } catch(ConcurrentModificationException cme) {
            repaint();
        }
        
//        long delta = System.currentTimeMillis() - start;
//        paintTimes[paintIndex++] = delta;
//        paintIndex = paintIndex % paintTimes.length;
//        paintfps = average(paintTimes);
        
        // if there are postRenderers set, do it
        for(Iterator iterator=visualizationServer.getPostRenderPaintables().iterator(); iterator.hasNext(); ) {
            VisualizationServer.Paintable paintable = (VisualizationServer.Paintable)iterator.next();
            if(paintable.useTransform()) {
                paintable.paint(g2d);
            } else {
                g2d.setTransform(oldXform);
                paintable.paint(g2d);
                g2d.setTransform(newXform);
            }
        }
        g2d.setTransform(oldXform);
    }


    /**
     * A four-sided shape that represents the visible part of the
     * master view and is drawn in the satellite view
     * 
     * @author Tom Nelson 
     *
     *
     */
    static class ViewLens<V,E> implements VisualizationServer.Paintable {

        VisualizationViewer<V,E> master;
        VisualizationViewer<V,E> vv;
        
        public ViewLens(VisualizationViewer<V,E> vv, VisualizationViewer<V,E> master) {
            this.vv = vv;
            this.master = master;
        }
        public void paint(Graphics g) {
            ShapeTransformer masterViewTransformer = 
            	master.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW);
            ShapeTransformer masterLayoutTransformer = master.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);
            ShapeTransformer vvLayoutTransformer = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);

            Shape lens = master.getBounds();
            lens = masterViewTransformer.inverseTransform(lens);
            lens = masterLayoutTransformer.inverseTransform(lens);
            lens = vvLayoutTransformer.transform(lens);
            Graphics2D g2d = (Graphics2D)g;
            Color old = g.getColor();
            Color lensColor = master.getBackground();
            vv.setBackground(lensColor.darker());
            g.setColor(lensColor);
            g2d.fill(lens);
            g.setColor(Color.gray);
            g2d.draw(lens);
            g.setColor(old);
        }

        public boolean useTransform() {
            return true;
        }
    }

}