/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Aug 15, 2005
 */

package edu.uci.ics.jung.visualization.control;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.layout.Layout;
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
 * @author Tom Nelson - RABA Technologies
 *
 * 
 */
public class SatelliteVisualizationViewer<V, E> 
	extends VisualizationViewer<V,E> {
    
    /**
     * the master VisualizationViewer that this is a satellite view for
     */
    protected VisualizationViewer<V,E> master;
    
    /**
     * @param layout
     * @param renderer
     */
    public SatelliteVisualizationViewer(VisualizationViewer<V,E> master, Layout<V,E> layout) {
        this(master, new DefaultVisualizationModel<V,E>(layout));
    }

    /**
     * @param layout
     * @param renderer
     * @param preferredSize
     */
    public SatelliteVisualizationViewer(VisualizationViewer<V,E> master, Layout<V,E> layout,
            Dimension preferredSize) {
        this(master, new DefaultVisualizationModel<V,E>(layout, preferredSize), preferredSize);
    }

    /**
     * @param model
     * @param renderer
     */
    public SatelliteVisualizationViewer(VisualizationViewer<V,E> master, VisualizationModel<V,E> model) {
        this(master, model, new Dimension(300,300));
    }

    /**
     * @param master the master view
     * @param model
     * @param renderer
     * @param preferredSize
     */
    public SatelliteVisualizationViewer(VisualizationViewer<V,E> master, VisualizationModel<V,E> model,
            Dimension preferredSize) {
        super(model, preferredSize);
        this.master = master;
        
        // create a graph mouse with custom plugins to affect the master view
        ModalGraphMouse gm = new ModalSatelliteGraphMouse();
        setGraphMouse(gm);
        
        // this adds the Lens to the satellite view
        addPreRenderPaintable(new ViewLens(this, master));
        
        // get a copy of the current layout transform
        // it may have been scaled to fit the graph
        AffineTransform modelLayoutTransform =
            new AffineTransform(master.getLayoutTransformer().getTransform());
        
        // I want no layout transformations in the satellite view
        // this resets the auto-scaling that occurs in the super constructor
        setLayoutTransformer(new MutableAffineTransformer(modelLayoutTransform));
        
        // make sure the satellite listens for changes in the master
        master.addChangeListener(this);
        
        // share the picked state of the master
        setPickedVertexState(master.getPickedVertexState());
        setPickedEdgeState(master.getPickedEdgeState());
//        setPickSupport(new ShapePickSupport());
    }

    /**
     * @return Returns the master.
     */
    public VisualizationViewer<V,E> getMaster() {
        return master;
    }
    
    protected void renderGraph(Graphics2D g2d) {
        if(renderContext.getGraphicsContext() == null) {
            renderContext.setGraphicsContext(new GraphicsDecorator(g2d));
        } else {
        renderContext.getGraphicsContext().setDelegate(g2d);
        }
        renderContext.setScreenDevice(this);
        Layout<V,E> layout = model.getGraphLayout();

        g2d.setRenderingHints(renderingHints);
        
        long start = System.currentTimeMillis();
        
        // the size of the VisualizationViewer
        Dimension d = getSize();
        
        // clear the offscreen image
        g2d.setColor(getBackground());
        g2d.fillRect(0,0,d.width,d.height);

        AffineTransform oldXform = g2d.getTransform();
        AffineTransform newXform = new AffineTransform(oldXform);
        newXform.concatenate(viewTransformer.getTransform());
        
        g2d.setTransform(newXform);

        // if there are  preRenderers set, paint them
        for(Iterator iterator=preRenderers.iterator(); iterator.hasNext(); ) {
            Paintable paintable = (Paintable)iterator.next();
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
                
                p = layout.getLocation(v1);
                p = layoutTransformer.transform(p);
                locationMap.put(v1, p);
            }
            Point2D q = (Point2D) locationMap.get(v2);
            if(q == null) {
                q = layout.getLocation(v2);
                q = layoutTransformer.transform(q);
                locationMap.put(v2, q);
            }

            if(p != null && q != null) {
//              renderer.paintEdge(
                        renderer.renderEdge(
                        renderContext,
                        layout.getGraph(),
                        e,
                        (int) p.getX(),
                        (int) p.getY(),
                        (int) q.getX(),
                        (int) q.getY());
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
                p = layout.getLocation(v);
                p = layoutTransformer.transform(p);
                locationMap.put(v, p);
            }
            if(p != null) {
//              renderer.paintVertex(
                renderer.renderVertex(
                        renderContext,
                        v,
                        (int) p.getX(),
                        (int) p.getY());
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
        for(Iterator iterator=postRenderers.iterator(); iterator.hasNext(); ) {
            Paintable paintable = (Paintable)iterator.next();
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
     * @author Tom Nelson - RABA Technologies
     *
     *
     */
    static class ViewLens<V,E> implements Paintable {

        VisualizationViewer<V,E> master;
        VisualizationViewer<V,E> vv;
        
        public ViewLens(VisualizationViewer<V,E> vv, VisualizationViewer<V,E> master) {
            this.vv = vv;
            this.master = master;
        }
        public void paint(Graphics g) {
            ShapeTransformer masterViewTransformer = master.getViewTransformer();
            ShapeTransformer masterLayoutTransformer = master.getLayoutTransformer();
            ShapeTransformer vvLayoutTransformer = vv.getLayoutTransformer();

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
