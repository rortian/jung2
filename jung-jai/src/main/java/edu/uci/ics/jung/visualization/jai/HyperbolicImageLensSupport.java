/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Jul 21, 2005
 */

package edu.uci.ics.jung.visualization.jai;

import java.awt.Dimension;

import edu.uci.ics.jung.visualization.BasicRenderer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalLensGraphMouse;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.transform.AbstractLensSupport;
import edu.uci.ics.jung.visualization.transform.LensTransformer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
import edu.uci.ics.jung.visualization.transform.shape.HyperbolicShapeTransformer;
import edu.uci.ics.jung.visualization.transform.shape.TransformingGraphics;
/**
 * A class to make it easy to add a Hyperbolic projection
 * examining lens to a jung graph application. See HyperbolicTransforerDemo
 * for an example of how to use it.
 * 
 * @author Tom Nelson
 *
 *
 */
public class HyperbolicImageLensSupport<V,E> extends AbstractLensSupport<V,E> {
    
    protected RenderContext<V,E> renderContext;
    protected GraphicsDecorator lensGraphicsDecorator;
    protected GraphicsDecorator savedGraphicsDecorator;
    protected Renderer<V,E> renderer;
    protected Renderer<V,E> transformingRenderer;
    
    static final String instructions = 
        "<html><center>Mouse-Drag the Lens center to move it<p>"+
        "Mouse-Drag the Lens edge to resize it<p>"+
        "Ctrl+MouseWheel to change magnification</center></html>";
    
    public HyperbolicImageLensSupport(VisualizationViewer<V,E> vv) {
        this(vv, new HyperbolicShapeTransformer(vv),
                new ModalLensGraphMouse());
    }
    /**
     * create the base class, setting common members and creating
     * a custom GraphMouse
     * @param vv the VisualizationViewer to work on
     */
    public HyperbolicImageLensSupport(VisualizationViewer<V,E> vv, LensTransformer lensTransformer,
            ModalGraphMouse lensGraphMouse) {
        super(vv, lensGraphMouse);
        this.renderContext = vv.getRenderContext();
        this.renderer = vv.getRenderer();
        this.transformingRenderer = new BasicRenderer<V,E>();
        this.transformingRenderer.setVertexRenderer(new TransformingImageVertexIconRenderer<V,E>());
        
        this.savedGraphicsDecorator = renderContext.getGraphicsContext();
        this.lensTransformer = lensTransformer;

        Dimension d = vv.getSize();
        if(d.width == 0 || d.height == 0) {
            d = vv.getPreferredSize();
        }
        lensTransformer.setViewRadius(d.width/5);
        this.lensGraphicsDecorator = new TransformingGraphics(lensTransformer);

    }
    
    public void activate() {
    	lensTransformer.setDelegate(vv.getViewTransformer());
        if(lens == null) {
            lens = new Lens(lensTransformer);
        }
        if(lensControls == null) {
            lensControls = new LensControls(lensTransformer);
        }
        lensTransformer.setDelegate(vv.getViewTransformer());
        vv.setViewTransformer(lensTransformer);
        this.renderContext.setGraphicsContext(lensGraphicsDecorator);
        vv.setRenderer(transformingRenderer);
        vv.addPreRenderPaintable(lens);
        vv.addPostRenderPaintable(lensControls);
        vv.setGraphMouse(lensGraphMouse);
        vv.setToolTipText(instructions);
        vv.repaint();
    }
    
    public void deactivate() {
//        vv.setViewTransformer(savedViewTransformer);
    	vv.setViewTransformer(lensTransformer.getDelegate());
        vv.removePreRenderPaintable(lens);
        vv.removePostRenderPaintable(lensControls);
        this.renderContext.setGraphicsContext(savedGraphicsDecorator);
        vv.setRenderer(renderer);
        vv.setToolTipText(defaultToolTipText);
        vv.setGraphMouse(graphMouse);
        vv.repaint();
    }
}
