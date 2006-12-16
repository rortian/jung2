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

import javax.media.jai.PerspectiveTransform;

import edu.uci.ics.jung.visualization.BasicRenderer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
import edu.uci.ics.jung.visualization.transform.shape.TransformingGraphics;
/**
 * A class to make it easy to add a Perspective projection
 * to a jung graph application. See PerspectiveTransformerDemo
 * for an example of how to use it.
 * 
 * @author Tom Nelson
 */
public class PerspectiveImageLensSupport<V,E> extends AbstractPerspectiveTransformSupport<V,E> {
    
    protected RenderContext<V,E> renderContext;
    protected GraphicsDecorator lensGraphicsDecorator;
    protected GraphicsDecorator savedGraphicsDecorator;
    protected Renderer<V,E> renderer;
    protected Renderer<V,E> transformingRenderer;
    
    static final String instructions = "";
    
    /**
     * @param vv the VisualizationViewer to work on
     */
    public PerspectiveImageLensSupport(VisualizationViewer<V,E> vv) {
        super(vv);
        this.renderContext = vv.getRenderContext();
        this.renderer = vv.getRenderer();
        this.transformingRenderer = new BasicRenderer<V,E>();
        this.perspectiveTransformer = 
            new PerspectiveShapeTransformer(new PerspectiveTransform(), vv.getViewTransformer());
        this.transformingRenderer.setVertexRenderer(new TransformingImageVertexIconRenderer<V,E>());
        this.lensGraphicsDecorator = new TransformingGraphics(perspectiveTransformer);
        this.savedGraphicsDecorator = renderContext.getGraphicsContext();

        Dimension d = vv.getSize();
        if(d.width == 0 || d.height == 0) {
            d = vv.getPreferredSize();
        }
        this.renderer = vv.getRenderer();
        
    }
    
    public void activate() {
        lens = new Lens(perspectiveTransformer, vv.getSize());
        vv.setViewTransformer(perspectiveTransformer);
        vv.getRenderContext().setGraphicsContext(lensGraphicsDecorator);
        vv.setRenderer(transformingRenderer);
        vv.addPreRenderPaintable(lens);
        vv.setToolTipText(instructions);
        vv.repaint();
    }
    
    public void deactivate() {
        vv.setViewTransformer(savedViewTransformer);
        vv.removePreRenderPaintable(lens);
        vv.getRenderContext().setGraphicsContext(savedGraphicsDecorator);
        vv.setRenderer(renderer);
        vv.setToolTipText(defaultToolTipText);
        vv.repaint();
    }
}
