package edu.uci.ics.jung.visualization.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

public class GraphicsWrapper implements GraphicsContext {
	protected GraphicsContext delegate;
    
    public GraphicsWrapper() {
        this(null);
    }
    public GraphicsWrapper(GraphicsContext delegate) {
        this.delegate = delegate;
    }
    
    public void setDelegate(GraphicsContext delegate) {
        this.delegate = delegate;
    }
    
    public GraphicsContext getDelegate() {
        return delegate;
    }
    
    
    
    public int getFontAscent() {
		return delegate.getFontAscent();
	}
	public int getFontDescent() {
		return delegate.getFontDescent();
	}
	public int getStringWidth(String str) {
		return delegate.getStringWidth(str);
	}
	public int getFontHeight() {
		return delegate.getFontHeight();
	}
	public int getCharWidth(char c) {
		return delegate.getCharWidth(c);
	}
	
	
	public Boolean getAntialiasing() {
		return delegate.getAntialiasing();
	}
	public Boolean getTextAntialiasing() {
		return delegate.getTextAntialiasing();
	}
	public void setAntialiasing(Boolean on) {
		delegate.setAntialiasing(on);
	}
	public void setTextAntialiasing(Boolean on) {
		delegate.setTextAntialiasing(on);
	}
	public void dispose() {
    	delegate.dispose();
	}
	public Label createLabel() {
    	return delegate.createLabel();
    }
    
    public Image createImage(int width, int height) {
    	return delegate.createImage(width, height);
    }
	
	public void clearRect(int x, int y, int width, int height) {
		delegate.clearRect(x, y, width, height);
	}

	public void clip(Shape s) {
		delegate.clip(s);
	}

	public void clipRect(int x, int y, int width, int height) {
		delegate.clipRect(x, y, width, height);
	}

	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		delegate.copyArea(x, y, width, height, dx, dy);
	}

	public void draw(Shape s) {
		delegate.draw(s);
	}

	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		delegate.drawArc(x, y, width, height, startAngle, arcAngle);
	}

	public void drawChars(char[] data, int offset, int length, int x, int y) {
		delegate.drawChars(data, offset, length, x, y);
	}

	public void drawImage(Image img, int x, int y) {
		delegate.drawImage(img, x, y);
	}
	
	public void drawImage(Image img, AffineTransform xform) {
		delegate.drawImage(img, xform);
	}

	
	public void drawLabel(Label img, int x, int y, int w, int h) {
		delegate.drawLabel(img, x, y, w, h);
	}
	public void drawLabel(Label img, int x, int y) {
		delegate.drawLabel(img, x, y);
	}
	
	
	public void drawLine(int x1, int y1, int x2, int y2) {
		delegate.drawLine(x1, y1, x2, y2);
	}

	public void drawOval(int x, int y, int width, int height) {
		delegate.drawOval(x, y, width, height);
	}

	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		delegate.drawPolygon(xPoints, yPoints, nPoints);
	}

	public void drawPolygon(Polygon p) {
		delegate.drawPolygon(p);
	}

	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		delegate.drawPolyline(xPoints, yPoints, nPoints);
	}

	public void drawRect(int x, int y, int width, int height) {
		delegate.drawRect(x, y, width, height);
	}

	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		delegate.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	public void drawString(String s, float x, float y) {
		delegate.drawString(s, x, y);
	}

	public void drawString(String str, int x, int y) {
		delegate.drawString(str, x, y);
	}

	public void fill(Shape s) {
		delegate.fill(s);
	}

	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		delegate.fillArc(x, y, width, height, startAngle, arcAngle);
	}

	public void fillOval(int x, int y, int width, int height) {
		delegate.fillOval(x, y, width, height);
	}

	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		delegate.fillPolygon(xPoints, yPoints, nPoints);
	}

	public void fillPolygon(Polygon p) {
		delegate.fillPolygon(p);
	}

	public void fillRect(int x, int y, int width, int height) {
		delegate.fillRect(x, y, width, height);
	}

	public void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		delegate.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	public Color getBackground() {
		return delegate.getBackground();
	}

	public Shape getClip() {
		return delegate.getClip();
	}

	public Rectangle getClipBounds() {
		return delegate.getClipBounds();
	}

	public Rectangle getClipBounds(Rectangle r) {
		return delegate.getClipBounds(r);
	}

	public Font getFont() {
		return delegate.getFont();
	}

	public Color getColor() {
		return delegate.getColor();
	}

	public Stroke getStroke() {
		return delegate.getStroke();
	}

	public AffineTransform getTransform() {
		return delegate.getTransform();
	}
	
	 public boolean hit(Rectangle rect,
				Shape s,
				boolean onStroke) {
		 return delegate.hit(rect, s, onStroke);
	 }

	public void setBackground(Color color) {
		delegate.setBackground(color);
	}

	public void setClip(int x, int y, int width, int height) {
		delegate.setClip(x, y, width, height);
	}

	public void setClip(Shape clip) {
		delegate.setClip(clip);
	}

	public void setFont(Font font) {
		delegate.setFont(font);
	}

	public void setColor(Color c) {
		delegate.setColor(c);
	}

	public void setStroke(Stroke s) {
		delegate.setStroke(s);
	}

	public void setTransform(AffineTransform Tx) {
		delegate.setTransform(Tx);
	}
	public Paint getPaint() {
		return delegate.getPaint();
	}
	public void setPaint(Paint paint) {
		delegate.setPaint(paint);
	}
	
	
	
}
