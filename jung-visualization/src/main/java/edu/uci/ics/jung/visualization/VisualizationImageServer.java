/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.visualization;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.uci.ics.jung.algorithms.layout.Layout;


@SuppressWarnings("serial")
public class VisualizationImageServer<V,E> extends BasicVisualizationServer<V,E> {
	protected Dimension preferredSize;
	
    public VisualizationImageServer(Layout<V,E> layout, Dimension preferredSize) {
        super(layout, preferredSize);
        this.preferredSize = preferredSize;
//        addNotify();
    }
    
    public Image getImage(Point2D center, Dimension d) {
        
            int width = preferredSize.width;
            int height = preferredSize.height;
            
            float scalex = (float)width/d.width;
            float scaley = (float)height/d.height;
            try {
            renderContext.getMultiLayerTransformer().getTransformer(Layer.VIEW).scale(scalex, scaley, center);

            BufferedImage bi = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = bi.createGraphics();
            renderGraph(graphics);
            graphics.dispose();
            return bi;

            } finally {
            	renderContext.getMultiLayerTransformer().getTransformer(Layer.VIEW).setToIdentity();
            }
        }

}
