/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Jul 11, 2005
 */

package edu.uci.ics.jung.visualization.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;


/**
 * An interface for the graphics implementation responsible
 * for rendering onto a particular UI component. This interface
 * is intended to be independent of the underlying UI, but will 
 * reference some AWT classes, such as java.awt.Shape and
 * java.awt.Color as they are not associated with any UI components 
 * and are already available.
 * 
 * @author Jason A Wrang
 *
 *
 */
public interface IGraphics {
	public Label createLabel();
	

    /* (non-Javadoc)
     * @see java.awt.Graphics#clearRect(int, int, int, int)
     */
    public void clearRect(int x, int y, int width, int height);

    /* (non-Javadoc)
     * @see java.awt.Graphics2D#clip(java.awt.Shape)
     */
    public void clip(Shape s);

    /* (non-Javadoc)
     * @see java.awt.Graphics#clipRect(int, int, int, int)
     */
    public void clipRect(int x, int y, int width, int height);

    /* (non-Javadoc)
     * @see java.awt.Graphics#copyArea(int, int, int, int, int, int)
     */
    public void copyArea(int x, int y, int width, int height, int dx, int dy);


    /* (non-Javadoc)
     * @see java.awt.Graphics2D#draw(java.awt.Shape)
     */
    public void draw(Shape s);

    /* (non-Javadoc)
     * @see java.awt.Graphics2D#draw3DRect(int, int, int, int, boolean)
     */
    public void draw3DRect(int x, int y, int width, int height, boolean raised);

    /* (non-Javadoc)
     * @see java.awt.Graphics#drawArc(int, int, int, int, int, int)
     */
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle);


    /* (non-Javadoc)
     * @see java.awt.Graphics#drawChars(char[], int, int, int, int)
     */
    public void drawChars(char[] data, int offset, int length, int x, int y);

 
    /* (non-Javadoc)
     * @see java.awt.Graphics2D#drawImage(java.awt.Image, java.awt.geom.AffineTransform, java.awt.image.ImageObserver)
     */
    public boolean drawImage(Image img, AffineTransform xform);

    /* (non-Javadoc)
     * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, java.awt.image.ImageObserver)
     */
    public boolean drawImage(Image img, int x, int y);
    
    public boolean drawLabel(Label img, int x, int y);
    
    public boolean drawLabel(Label img, int x, int y, int w, int h);

    /* (non-Javadoc)
     * @see java.awt.Graphics#drawLine(int, int, int, int)
     */
    public void drawLine(int x1, int y1, int x2, int y2);

    /* (non-Javadoc)
     * @see java.awt.Graphics#drawOval(int, int, int, int)
     */
    public void drawOval(int x, int y, int width, int height);

    /* (non-Javadoc)
     * @see java.awt.Graphics#drawPolygon(int[], int[], int)
     */
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints);

    /* (non-Javadoc)
     * @see java.awt.Graphics#drawPolygon(java.awt.Polygon)
     */
    public void drawPolygon(Polygon p);

    /* (non-Javadoc)
     * @see java.awt.Graphics#drawPolyline(int[], int[], int)
     */
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints);

    /* (non-Javadoc)
     * @see java.awt.Graphics#drawRect(int, int, int, int)
     */
    public void drawRect(int x, int y, int width, int height);

    /* (non-Javadoc)
     * @see java.awt.Graphics#drawRoundRect(int, int, int, int, int, int)
     */
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight);

    /* (non-Javadoc)
     * @see java.awt.Graphics2D#drawString(java.lang.String, float, float)
     */
    public void drawString(String s, float x, float y);

    /* (non-Javadoc)
     * @see java.awt.Graphics2D#drawString(java.lang.String, int, int)
     */
    public void drawString(String str, int x, int y);


    /* (non-Javadoc)
     * @see java.awt.Graphics2D#fill(java.awt.Shape)
     */
    public void fill(Shape s);

    /* (non-Javadoc)
     * @see java.awt.Graphics2D#fill3DRect(int, int, int, int, boolean)
     */
    public void fill3DRect(int x, int y, int width, int height, boolean raised);

    /* (non-Javadoc)
     * @see java.awt.Graphics#fillArc(int, int, int, int, int, int)
     */
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle);

    /* (non-Javadoc)
     * @see java.awt.Graphics#fillOval(int, int, int, int)
     */
    public void fillOval(int x, int y, int width, int height);

    /* (non-Javadoc)
     * @see java.awt.Graphics#fillPolygon(int[], int[], int)
     */
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints);

    /* (non-Javadoc)
     * @see java.awt.Graphics#fillPolygon(java.awt.Polygon)
     */
    public void fillPolygon(Polygon p);

    /* (non-Javadoc)
     * @see java.awt.Graphics#fillRect(int, int, int, int)
     */
    public void fillRect(int x, int y, int width, int height);

    /* (non-Javadoc)
     * @see java.awt.Graphics#fillRoundRect(int, int, int, int, int, int)
     */
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight);

    /* (non-Javadoc)
     * @see java.awt.Graphics2D#getBackground()
     */
    public Color getBackground();
    
    public Color getColor();

    /* (non-Javadoc)
     * @see java.awt.Graphics#getClip()
     */
    public Shape getClip();

    /* (non-Javadoc)
     * @see java.awt.Graphics#getClipBounds()
     */
    public Rectangle getClipBounds();

    /* (non-Javadoc)
     * @see java.awt.Graphics#getClipBounds(java.awt.Rectangle)
     */
    public Rectangle getClipBounds(Rectangle r);


    /* (non-Javadoc)
     * @see java.awt.Graphics#getFont()
     */
    public Font getFont();


    /* (non-Javadoc)
     * @see java.awt.Graphics2D#getStroke()
     */
    public Stroke getStroke();

    public Paint getPaint();
    
    /* (non-Javadoc)
     * @see java.awt.Graphics2D#getTransform()
     */
    public AffineTransform getTransform();


    public boolean hit(Rectangle rect,
			Shape s,
			boolean onStroke);
    
    /* (non-Javadoc)
     * @see java.awt.Graphics2D#setBackground(java.awt.Color)
     */
    public void setBackground(Color color);

    /* (non-Javadoc)
     * @see java.awt.Graphics#setClip(int, int, int, int)
     */
    public void setClip(int x, int y, int width, int height);

    /* (non-Javadoc)
     * @see java.awt.Graphics#setClip(java.awt.Shape)
     */
    public void setClip(Shape clip);

    /* (non-Javadoc)
     * @see java.awt.Graphics#setColor(java.awt.Color)
     */
    public void setColor(Color c);

 
    /* (non-Javadoc)
     * @see java.awt.Graphics#setFont(java.awt.Font)
     */
    public void setFont(Font font);

    public void setPaint( Paint paint );
    
    /* (non-Javadoc)
     * @see java.awt.Graphics2D#setStroke(java.awt.Stroke)
     */
    public void setStroke(Stroke s);

    /* (non-Javadoc)
     * @see java.awt.Graphics2D#setTransform(java.awt.geom.AffineTransform)
     */
    public void setTransform(AffineTransform Tx);
}
