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

import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.transform.MutableAffineTransformer;
/**
 * A class to make it easy to add a Perspective projection
 * to a jung graph application. See PerspectiveTransformerDemo
 * for an example of how to use it.
 * 
 * @author Tom Nelson
 *
 *
 */
public class PerspectiveLayoutTransformSupport<V,E> extends AbstractPerspectiveTransformSupport<V,E> 
    implements PerspectiveTransformSupport {

    /**
     * @param vv the VisualizationViewer to work on
     */
    public PerspectiveLayoutTransformSupport(VisualizationViewer<V,E> vv) {
        super(vv);
        perspectiveTransformer = 
            new PerspectiveShapeTransformer(new PerspectiveTransform(), vv.getLayoutTransformer());
   }
    
    public void activate() {
        lens = new Lens(perspectiveTransformer, vv.getSize());
        vv.setLayoutTransformer(perspectiveTransformer);
        vv.setViewTransformer(new MutableAffineTransformer());
        vv.addPreRenderPaintable(lens);
        vv.setToolTipText(instructions);
        vv.repaint();
    }
    
    public void deactivate() {
        if(savedViewTransformer != null) {
            vv.setViewTransformer(savedViewTransformer);
        }
        if(perspectiveTransformer != null) {
            vv.removePreRenderPaintable(lens);
            vv.setLayoutTransformer(perspectiveTransformer.getDelegate());
        }
        vv.setToolTipText(defaultToolTipText);
        vv.repaint();
    }
}
