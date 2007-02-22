/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Jul 11, 2005
 */

package edu.uci.ics.jung.visualization.transform.shape;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.Icon;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.renderers.BasicVertexRenderer;

/**
 * a subclass to apply a TransformingGraphics to certain operations
 * @author Tom Nelson
 *
 *
 */
public class MagnifyImageVertexIconRenderer<V,E> extends BasicVertexRenderer<V,E> {
    
    public void paintIconForVertex(RenderContext<V,E> rc, V v, Layout<V,E> layout) {

        GraphicsDecorator g = rc.getGraphicsContext();
        TransformingGraphics g2d = (TransformingGraphics)g;
        boolean vertexHit = true;
        // get the shape to be rendered
        Shape shape = rc.getVertexShapeTransformer().transform(v);
        
        Point2D p = layout.transform(v);
        p = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p);
        float x = (float)p.getX();
        float y = (float)p.getY();

        // create a transform that translates to the location of
        // the vertex to be rendered
        AffineTransform xform = AffineTransform.getTranslateInstance(x,y);
        // transform the vertex shape with xtransform
        shape = xform.createTransformedShape(shape);
        
        vertexHit = vertexHit(rc, shape);
        if (vertexHit) {
        	if(rc.getVertexIconTransformer() != null) {
        		Icon icon = rc.getVertexIconTransformer().transform(v);
        		if(icon != null) {
        		
                    BufferedImage image = new BufferedImage(icon.getIconWidth(), 
                            icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D ig = image.createGraphics();
                    icon.paintIcon(rc.getScreenDevice(), ig, 0, 0);
                    int imageWidth = image.getWidth(null);
                    int imageHeight = image.getHeight(null);
                    
                    int xLoc = (int) (x - imageWidth / 2);
                    int yLoc = (int) (y - imageHeight / 2);
                    Rectangle2D imageRectangle = new Rectangle2D.Double(xLoc, yLoc,
                            imageWidth, imageHeight);
                    
                    Shape perspectiveShape = 
                        ((ShapeTransformer) g2d.getTransformer()).transform(imageRectangle);
                    
                    // see if the transformer will affect the imageRectangle
                    if(imageRectangle.equals(perspectiveShape.getBounds2D()) == false) {
                        Shape clipShape = 
                            ((ShapeTransformer) g2d.getTransformer()).transform(shape);

                        Rectangle sb = perspectiveShape.getBounds();
                        Shape clip = g2d.getClip();
                        g2d.clip(clipShape);
                        g2d.drawImage(image.getScaledInstance(sb.width, sb.height, Image.SCALE_SMOOTH), 
                        		sb.x, sb.y, null);
                        g2d.setClip(clip);
                    } else {
                        g2d.drawImage(image, AffineTransform.getTranslateInstance(xLoc,
                                yLoc), null);
                    }

        		} else {
        			paintShapeForVertex(rc, v, shape);
        		}
        	} else {
        		paintShapeForVertex(rc, v, shape);
        	}
        }
    }
}
