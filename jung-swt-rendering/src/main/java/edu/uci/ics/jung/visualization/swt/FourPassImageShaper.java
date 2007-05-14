/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Jun 17, 2005
 */

package edu.uci.ics.jung.visualization.swt;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

/**
 * Provides factory methods that, given a BufferedImage, an Image,
 * or the fileName of an image, will return a java.awt.Shape that
 * is the contiguous traced outline of the opaque part of the image.
 * This could be used to define an image for use in a Vertex, where
 * the shape used for picking and edge-arrow placement follows the
 * opaque part of an image that has a transparent background.
 * The methods try to detect lines in order to minimize points
 * in the path
 * 
 * @author Tom Nelson
 *
 * 
 */
public class FourPassImageShaper {
    
    /**
     * given the fileName of an image, possibly with a transparent
     * background, return the Shape of the opaque part of the image
     * @param fileName name of the image, loaded from the classpath
     * @return the Shape
     */
    public static Shape getShape(String fileName) {
        return getShape(fileName, Integer.MAX_VALUE);
    }
    public static Shape getShape(String fileName, int max) {
        Image image = null;
        try {
        	ImageLoader loader = new ImageLoader();
        	ImageData[] data = loader.load(fileName);
        	image = new Image(Display.getCurrent(), data[0]);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return getShape(image, max);
    }
    
    /**
     * Given an image, possibly with a transparent background, return
     * the Shape of the opaque part of the image
     * @param image
     * @return the Shape
     */
    public static Shape getShape(Image image) {
        return getShape(image, Integer.MAX_VALUE);
    }
    
    /**
     * Given an image, possibly with a transparent background, return
     * the Shape of the opaque part of the image
     * 
     * If the image is larger than max in either direction, scale the
     * image down to max-by-max, do the trace (on fewer points) then
     * scale the resulting shape back up to the size of the original
     * image.
     * 
     * @param image the image to trace
     * @param max used to restrict number of points in the resulting shape
     * @return the Shape
     */
    public static Shape getShape(Image image, int max) {
        int width = image.getBounds().width;
        int height = image.getBounds().height;
        if(width > max || height > max) {
        	// FIXME: shrink SWT image while presrving transparency
        	return getShapeFromImageData(image.getImageData());
        } else {
            return getShapeFromImageData(image.getImageData());
        }
    }
    
    public static Shape getShapeFromImageData(ImageData imageData) {
        Area area = new Area(leftEdge(imageData));
        area.intersect(new Area(bottomEdge(imageData)));
        area.intersect(new Area(rightEdge(imageData)));
        area.intersect(new Area(topEdge(imageData)));
        return area;
    }
    /**
     * Checks to see if point p is on a line that passes thru
     * points p1 and p2. If p is on the line, extend the line
     * segment so that it is from p1 to the location of p.
     * If the point p is not on the line, update my shape
     * with a line extending to the old p2 location, make
     * the old p2 the new p1, and make p2 the old p
     * @param p1
     * @param p2
     * @param p
     * @param line
     * @param path
     * @return
     */
    private static Point2D detectLine(Point2D p1, Point2D p2, Point2D p, 
            Line2D line, GeneralPath path) {
        if(p2 == null) {
            p2 = p;
            line.setLine(p1,p2);
        }
        // check for line
        else if(line.ptLineDistSq(p) < 1) { // its on the line
            // make it p2
            p2.setLocation(p);
        } else { // its not on the current line
            p1.setLocation(p2);
            p2.setLocation(p);
            line.setLine(p1,p2);
            path.lineTo((float)p1.getX(), (float)p1.getY());
        }
        return p2;
    }
    /**
     * trace the left side of the image
     * @param image
     * @param path
     * @return
     */
    private static Shape leftEdge(ImageData imageData) {
    	int width = imageData.width;
    	int height = imageData.height;
        GeneralPath path = new GeneralPath();
        Point2D p1 = new Point2D.Float(width-1, 0);
        Point2D p2 = null;
        Line2D line = new Line2D.Float();
        Point2D p = new Point2D.Float();
        path.moveTo(width-1, 0);
        
        ImageData tmask = null;
        int ttype = imageData.getTransparencyType();
        if ( ttype == SWT.TRANSPARENCY_MASK || ttype == SWT.TRANSPARENCY_PIXEL )
        	tmask = imageData.getTransparencyMask();
        
        for(int i=0; i<height; i++) {
            p.setLocation(width-1, i);
            // go until we reach an opaque point, then stop
            for(int j=0; j<width; j++) {
            	boolean stop = ttype == SWT.TRANSPARENCY_NONE;
            	if (!stop && ttype == SWT.TRANSPARENCY_ALPHA) {
            		int alpha = imageData.getAlpha(j, i);
            		stop = alpha != 0;
            	}
            	if (!stop && tmask != null) {
            		int tpixel = tmask.getPixel(j, i);
            		stop = tpixel != 0;
            	}
            	
                if(stop) {
                    // this is a point I want
                    p.setLocation(j,i);
                    break;
                }
            }
            p2 = detectLine(p1, p2, p, line, path);
        }
        p.setLocation(width-1, height-1);
        detectLine(p1, p2, p, line, path);
        path.closePath();
        return path;
    }
    
    /**
     * trace the bottom of the image
     * @param image
     * @param path
     * @param start
     * @return
     */
    private static Shape bottomEdge(ImageData imageData) {
    	int width = imageData.width;
    	int height = imageData.height;
        GeneralPath path = new GeneralPath();
        Point2D p1 = new Point2D.Float(0, 0);
        Point2D p2 = null;
        Line2D line = new Line2D.Float();
        Point2D p = new Point2D.Float();
        path.moveTo(0, 0);
        
        ImageData tmask = null;
        int ttype = imageData.getTransparencyType();
        if ( ttype == SWT.TRANSPARENCY_MASK || ttype == SWT.TRANSPARENCY_PIXEL )
        	tmask = imageData.getTransparencyMask();
        
        for(int i=0; i<width; i++) {
            p.setLocation(i, 0);
            for(int j=height-1; j>=0; j--) {
            	boolean stop = ttype == SWT.TRANSPARENCY_NONE;
            	if (!stop && ttype == SWT.TRANSPARENCY_ALPHA) {
            		int alpha = imageData.getAlpha(i, j);
            		stop = alpha != 0;
            	}
            	if (!stop && tmask != null) {
            		int tpixel = tmask.getPixel(i, j);
            		stop = tpixel != 0;
            	}
            	
                if(stop) {
                    // this is a point I want
                    p.setLocation(i,j);
                    break;
                }
            }
            p2 = detectLine(p1, p2, p, line, path);
        }
        p.setLocation(width-1, 0);
        detectLine(p1, p2, p, line, path);
        path.closePath();
        return path;
    }
    
    /**
     * trace the right side of the image
     * @param image
     * @param path
     * @param start
     * @return
     */
    private static Shape rightEdge(ImageData imageData) {
    	int width = imageData.width;
    	int height = imageData.height;
        GeneralPath path = new GeneralPath();
        Point2D p1 = new Point2D.Float(0, height-1);
        Point2D p2 = null;
        Line2D line = new Line2D.Float();
        Point2D p = new Point2D.Float();
        path.moveTo(0, height-1);
        
        ImageData tmask = null;
        int ttype = imageData.getTransparencyType();
        if ( ttype == SWT.TRANSPARENCY_MASK || ttype == SWT.TRANSPARENCY_PIXEL )
        	tmask = imageData.getTransparencyMask();
        
        for(int i=height-1; i>=0; i--) {
            p.setLocation(0, i);
            for(int j=width-1; j>=0; j--) {
            	boolean stop = ttype == SWT.TRANSPARENCY_NONE;
            	if (!stop && ttype == SWT.TRANSPARENCY_ALPHA) {
            		int alpha = imageData.getAlpha(j, i);
            		stop = alpha != 0;
            	}
            	if (!stop && tmask != null) {
            		int tpixel = tmask.getPixel(j, i);
            		stop = tpixel != 0;
            	}
            	
                if(stop) {
                    // this is a point I want
                    p.setLocation(j,i);
                    break;
                }
            }
            p2 = detectLine(p1, p2, p, line, path);
        }
        p.setLocation(0, 0);
        detectLine(p1, p2, p,line, path);
        path.closePath();
        return path;
    }
    
    /**
     * trace the top of the image
     * @param image
     * @param path
     * @param start
     * @return
     */
    private static Shape topEdge(ImageData imageData) {
    	int width = imageData.width;
    	int height = imageData.height;
        GeneralPath path = new GeneralPath();
        Point2D p1 = new Point2D.Float(width-1, height-1);
        Point2D p2 = null;
        Line2D line = new Line2D.Float();
        Point2D p = new Point2D.Float();
        path.moveTo(width-1, height-1);
        
        ImageData tmask = null;
        int ttype = imageData.getTransparencyType();
        if ( ttype == SWT.TRANSPARENCY_MASK || ttype == SWT.TRANSPARENCY_PIXEL )
        	tmask = imageData.getTransparencyMask();

        
        for(int i=width-1; i>=0; i--) {
            p.setLocation(i, height-1);
            for(int j=0; j<height; j++) {
            	boolean stop = ttype == SWT.TRANSPARENCY_NONE;
            	if (!stop && ttype == SWT.TRANSPARENCY_ALPHA) {
            		int alpha = imageData.getAlpha(i, j);
            		stop = alpha != 0;
            	}
            	if (!stop && tmask != null) {
            		int tpixel = tmask.getPixel(i, j);
            		stop = tpixel != 0;
            	}
            	
                if(stop) {
                    // this is a point I want
                    p.setLocation(i,j);
                    break;
                }
            }
            p2 = detectLine(p1, p2, p, line, path);
        }
        p.setLocation(0, height-1);
        detectLine(p1, p2, p, line, path);
        path.closePath();
        return path;
    }
    
//    private static void showImage(final Image image) {
//        Display display = Display.getCurrent();
//        Shell shell = new Shell (display);
//    	shell.setLayout (new FillLayout ());
//    	Group group = new Group (shell, SWT.NONE);
//    	group.setLayout (new FillLayout ());
//    	group.setText ("a square");
//    	Canvas canvas = new Canvas (group, SWT.NONE);
//    	canvas.addPaintListener (new PaintListener () {
//    		public void paintControl (PaintEvent e) {
//    			e.gc.drawImage (image, 0, 0);
//    		}
//    	});
//
//    	shell.pack ();
//    	shell.open ();
//    	while (!shell.isDisposed ()) {
//    		if (!display.readAndDispatch ())
//    			display.sleep ();
//    	}
//    }
}
