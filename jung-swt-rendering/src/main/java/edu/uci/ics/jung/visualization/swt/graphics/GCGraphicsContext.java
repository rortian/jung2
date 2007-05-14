package edu.uci.ics.jung.visualization.swt.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.graphics.Transform;

import edu.uci.ics.jung.visualization.graphics.GraphicsContext;
import edu.uci.ics.jung.visualization.graphics.Image;
import edu.uci.ics.jung.visualization.graphics.Label;

/**
 * An adapter that translates invokations of the GraphicsContext
 * interface to the necessary invokations on its org.eclipse.swt.graphics.GC
 * delegate.
 * 
 * Special thanks to the developers of the Holongate project, http://www.holongate.org,
 * for helping me get started in the right direction.
 * @author Jason A Wrang
 *
 */
public class GCGraphicsContext implements GraphicsContext {
	private final static AffineTransform IDENTITY = new AffineTransform();
	private static final int MAX_ENTRIES = 20;
	
	private Map<RGB, org.eclipse.swt.graphics.Color> swtColors = new LinkedHashMap<RGB, org.eclipse.swt.graphics.Color>() {
		@Override
		protected boolean removeEldestEntry(Entry<RGB, org.eclipse.swt.graphics.Color> eldest) {
			if ( size() > MAX_ENTRIES ) {
				removeEldestColorEntry(this);
			}
			return false;
		}
	};
	
	private Map<FontData, org.eclipse.swt.graphics.Font> swtFonts = new LinkedHashMap<FontData, org.eclipse.swt.graphics.Font>() {
		@Override
		protected boolean removeEldestEntry(Entry<FontData, org.eclipse.swt.graphics.Font> eldest) {
			if ( size() > MAX_ENTRIES ) {
				removeEldestFontEntry(this);
			}
			return false;
		}
	};
	
	private void removeEldestColorEntry(Map<RGB, org.eclipse.swt.graphics.Color> colors) {
		RGB key = null;
		org.eclipse.swt.graphics.Color value = null;
		for (RGB k : colors.keySet()) {
			org.eclipse.swt.graphics.Color v = colors.get(k);
			org.eclipse.swt.graphics.Color fg = delegate.getForeground();
			org.eclipse.swt.graphics.Color bg = delegate.getBackground();
			if ( !v.equals(fg) && !v.equals(bg) ) {
				key = k;
				value = v;
				break;
			}
		}
		
		if (key != null) {
			colors.remove(key);
			value.dispose();
		}
	}
	
	private void removeEldestFontEntry(Map<FontData, org.eclipse.swt.graphics.Font> fonts) {
		FontData key = null;
		org.eclipse.swt.graphics.Font value = null;
		for (FontData k : fonts.keySet()) {
			org.eclipse.swt.graphics.Font v = fonts.get(k);
			org.eclipse.swt.graphics.Font f = delegate.getFont();
			if ( !v.equals(f) ) {
				key = k;
				value = v;
				break;
			}
		}
		
		if (key != null) {
			fonts.remove(key);
			value.dispose();
		}
	}
	
	
	
	protected GC delegate;
    
    public GCGraphicsContext() {
        this(null);
    }
    public GCGraphicsContext(GC delegate) {
    	this.delegate = delegate;
    }
    

    public void dispose() {
    	for (org.eclipse.swt.graphics.Color color : swtColors.values()) {
    		color.dispose();
    	}
    	swtColors.clear();
    	
    	for (org.eclipse.swt.graphics.Font font : swtFonts.values()) {
    		font.dispose();
    	}
    	swtFonts.clear();
    }
	

    
    
	
	/**
	 * Converts a Java2D AffineTransform into an SWT Transform.
	 * <p>
	 * The caller must call dispose() on the returned instance.
	 * </p>
	 * <p>
	 * This method is not static because the returned transform is bound to the same
	 * Device as the orginal GC this instance represents.
	 * </p>
	 * 
	 * @param t
	 *            The Java2D affine transform
	 * @return An SWT Transform initialized with the affine transform values
	 */
	public static Transform toSWTTransform(Device dev, AffineTransform t) {
		Transform ret = null;
			double[] m = new double[6];
			t.getMatrix(m);
			ret =  new Transform(dev, (float) m[0], (float) m[2], (float) m[1],
					(float) m[3], (float) m[4], (float) m[5]);
		
		return ret;
	}
	
	public static AffineTransform toAWTTransform(Transform t) {
		float[] m = new float[6];
		t.getElements(m);
		AffineTransform at = new AffineTransform(m[0], m[2], m[1],
			m[3], m[4], m[5]);
		return at;
	}
    
    
    
	/**
	 * Converts an SWT Color into an AWT Color.
	 * <p>
	 * The returned Color have its alpha channel set to the current alpha value of the GC.
	 * As a consequence, colors cannot be cached or resused.
	 * </p>
	 * 
	 * @param rgb
	 *            The SWT color
	 * @return The AWT color
	 */
	public static Color toAWTColor(GC gc, org.eclipse.swt.graphics.Color rgb) {
		Color c = new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), gc.getAlpha());
		return c;
	}
	
	/**
	 * Converts an AWT Color into an SWT Color.
	 * <p>
	 * The alpha channel of the AWT color is lost in the process.
	 * </p>
	 * <p>
	 * The caller must call dispose() on the returned instance.
	 * </p>
	 * <p>
	 * This method is not static because the returned color is bound to the same Device as
	 * the orginal GC this instance represents.
	 * </p>
	 * 
	 * @param rgba
	 * @return
	 */
	public org.eclipse.swt.graphics.Color toSWTColor(Color color) {
		RGB rgb = new RGB(color.getRed(), color.getGreen(), color.getBlue());
		org.eclipse.swt.graphics.Color ret = swtColors.get(rgb);
		if (ret == null) {
			ret =  new org.eclipse.swt.graphics.Color(delegate.getDevice(), rgb);
			swtColors.put(rgb, ret);
		}
		
		return ret;
	}
	
	
	/**
	 * Converts a Java2D Shape into an SWT Path object.
	 * <p>
	 * Coordinates are supposed to be expressed in the original (untransformed) user space
	 * following the Java2D convention for coordinates.
	 * </p>
	 * <p>
	 * This method uses the PathIterator mechanism to iterate over the shape.
	 * </p>
	 * <p>
	 * The caller must call dispose() on the returned instance.
	 * </p>
	 * <p>
	 * This method is not static because the returned path is bound to the same Device as
	 * the orginal GC this instance represents.
	 * </p>
	 * 
	 * @param s
	 *            The Shape to convert
	 * @return The equivalent Path
	 */
	public static synchronized Path toPath(Device dev, Shape s) {
		Path ret = null;
			float[] coords = new float[6];
			ret = new Path(dev);
			PathIterator it = s.getPathIterator(IDENTITY);
			while (!it.isDone()) {
				int type = it.currentSegment(coords);
				switch (type) {
				case PathIterator.SEG_MOVETO :
					ret.moveTo(coords[0], coords[1]);
					break;
				case PathIterator.SEG_LINETO :
					ret.lineTo(coords[0], coords[1]);
					break;
				case PathIterator.SEG_QUADTO :
					ret.quadTo(coords[0], coords[1], coords[2], coords[3]);
					break;
				case PathIterator.SEG_CUBICTO :
					ret.cubicTo(coords[0], coords[1], coords[2], coords[3], coords[4],
							coords[5]);
					break;
					// FIXME: after the SEG_CLOSE, we should be prepared for a new path.
					// FIXME: we should consider the winding rule.
				case PathIterator.SEG_CLOSE :
					ret.close();
					break;
				}
				it.next();
			}
			
		return ret;
	}
    
	
	public static FontData getSWTFontData(Device dev, Font font) {
		int style = SWT.NORMAL;
		if (font.isBold()) {
			style |= SWT.BOLD;
		}
		if (font.isItalic()) {
			style |= SWT.ITALIC;
		}
		// FIXME: the font used is the font size not pixel size as per Java2D
		int points = (int) (font.getSize2D() * 72.0 / dev.getDPI().x);
		
		return new FontData(font.getFamily(), points, style);
	}
	
	/**
	 * Font size in Java2D is expressed in pixels??? The javadoc says that font sizes are
	 * in points!
	 * <p>
	 * Font size is converted in points by multiplying AWT font size by 72/ screen DPI.
	 * </p>
	 */
	public org.eclipse.swt.graphics.Font toSWTFont(Font font) {
		FontData fd = getSWTFontData(delegate.getDevice(), font);
		org.eclipse.swt.graphics.Font ret = swtFonts.get(fd);
		if (ret == null) {
			ret = new org.eclipse.swt.graphics.Font(delegate.getDevice(), fd);
			swtFonts.put(fd, ret);
		}
		
		return ret;
	}
    
	public static synchronized Font toAWTFont(Device dev, org.eclipse.swt.graphics.Font font) {
		FontData f = font.getFontData()[0];
		int style = Font.PLAIN;
		if ((f.getStyle() & SWT.BOLD) != 0) {
			style = Font.BOLD;
		}
		if ((f.getStyle() & SWT.ITALIC) != 0) {
			style |= Font.ITALIC;
		}
		// FIXME: the font used is the font size not pixel size as per Java2D
		int pixels = (int) (f.getHeight() * dev.getDPI().x / 72.0);
		Font af = new Font(f.getName(), style, pixels);
		
		return af;
	}
    
    
    
    
    
    
    
    
    
    
        
    
    
    
    


	public int getCharWidth(char c) {
		return delegate.getAdvanceWidth(c);
	}

	public int getFontAscent() {
		return delegate.getFontMetrics().getAscent();
	}

	public int getFontDescent() {
		return delegate.getFontMetrics().getDescent();
	}

	public int getFontHeight() {
		return delegate.getFontMetrics().getHeight();
	}

	public int getStringWidth(String str) {
		int width = 0;
		for (int i = 0; i < str.length(); i++) {
			width += delegate.getAdvanceWidth(str.charAt(i));
		}
		return width;
	}

    public Boolean getAntialiasing() {
		int a = delegate.getAntialias();
		if (a == SWT.ON) return Boolean.TRUE;
		if (a == SWT.OFF) return Boolean.FALSE;
		if (a == SWT.DEFAULT) return null;
		return null;
	}
	
	public Boolean getTextAntialiasing() {
		int a = delegate.getTextAntialias();
		if (a == SWT.ON) return Boolean.TRUE;
		if (a == SWT.OFF) return Boolean.FALSE;
		if (a == SWT.DEFAULT) return null;
		return null;
	}

	public void setAntialiasing(Boolean on) {
		if (on == null) delegate.setAntialias(SWT.DEFAULT);
		else if (on) delegate.setAntialias(SWT.ON);
		else delegate.setAntialias(SWT.OFF);
	}

	public void setTextAntialiasing(Boolean on) {
		if (on == null) delegate.setTextAntialias(SWT.DEFAULT);
		else if (on) delegate.setTextAntialias(SWT.ON);
		else delegate.setTextAntialias(SWT.OFF);
	}
	

	
	
	public Label createLabel() {
    	return new SWTLabelImpl(delegate);
    }
    
    public Image createImage(int width, int height) {
    	return new SWTImageImpl(delegate.getDevice(), width, height);
    }
	
	public void clearRect(int x, int y, int width, int height) {
		int alpha = delegate.getAlpha();
		delegate.setAlpha(0);
		// FIXME: check which color is actually used in fillRectangle!
		delegate.fillRectangle(x, y, width, height);
		delegate.setAlpha(alpha);
	}

	public void clip(Shape s) {
		Path p = toPath(delegate.getDevice(), s);
		delegate.setClipping(p);
		p.dispose();
	}

	public void clipRect(int x, int y, int width, int height) {
		org.eclipse.swt.graphics.Rectangle clip = delegate.getClipping();
		clip.intersect(new org.eclipse.swt.graphics.Rectangle(x, y, width, height));
		delegate.setClipping(clip);
	}

	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		delegate.copyArea(x, y, width, height, dx, dy);
	}

	public void draw(Shape s) {
		Path p = toPath(delegate.getDevice(), s);
		delegate.drawPath(p);
		p.dispose();
	}

	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		delegate.drawArc(x, y, width, height, startAngle, arcAngle);
	}

	public void drawChars(char[] data, int offset, int length, int x, int y) {
		drawString(new String(data, offset, length), x, y);
	}

	public void drawImage(Image img, int x, int y) {
		if (img instanceof SWTImageImpl) {
			delegate.drawImage(
					((SWTImageImpl)img).image, x, y);
		}
	}

	public void drawImage(Image img, AffineTransform xform) {
		AffineTransform cur = getTransform();
		AffineTransform newXForm = new AffineTransform(cur);
		newXForm.concatenate(xform);
		setTransform(newXForm);
		drawImage(img, 0, 0);
		setTransform(cur);
	}
	
	
	public void drawLabel(Label label, int x, int y, int w, int h) {
		org.eclipse.swt.graphics.Rectangle clip = delegate.getClipping();
		org.eclipse.swt.graphics.Color fg = delegate.getForeground();
		org.eclipse.swt.graphics.Color bg = delegate.getBackground();
		org.eclipse.swt.graphics.Font font = delegate.getFont();
		
		try {
			clipRect(x-1, y-1, w+1, h+1);
			setFont(label.getFont());
			Color b = label.getBackground();
			Color f = label.getForeground();
			
			setBackground(b);
			setColor(f);
			
			int tx = x;
			int ty = y;
			// place image left aligned, bottom valign with text
			String text = label.getText();
			Image image = label.getImage();
			if (image != null) {
				Dimension tsize = ((SWTLabelImpl)label).getTextPreferredSize();
				org.eclipse.swt.graphics.Image swtImage = ((SWTImageImpl)image).image;
				int iheight = swtImage.getBounds().height;
				int iwidth = swtImage.getBounds().width;
				tx += iwidth + 5;
				if (iheight >= tsize.height) {
					delegate.drawImage(swtImage, x, y);
					ty += (iheight - tsize.height);
				} else {
					delegate.drawImage(swtImage, x, y + (tsize.height - iheight));
				}
			}
			if (text != null) delegate.drawText(text, tx, ty, !label.isOpaque());
		} finally {
			delegate.setFont(font);
			delegate.setBackground(bg);
			delegate.setForeground(fg);
			delegate.setClipping(clip);
		}
	}
	public void drawLabel(Label label, int x, int y) {
		Dimension s = label.getPreferredSize();
		drawLabel(label, x, y, s.width, s.height);
	}
	
	
	public void drawLine(int x1, int y1, int x2, int y2) {
		delegate.drawLine(x1, y1, x2, y2);
	}

	public void drawOval(int x, int y, int width, int height) {
		delegate.drawOval(x, y, width, height);
	}

	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		int[] buf = new int[nPoints * 2];
		int j = 0;
		for (int i = 0; i < nPoints; i++) {
			buf[j++] = xPoints[i];
			buf[j++] = yPoints[i];
		}
		delegate.drawPolygon(buf);
		buf = null;
	}

	public void drawPolygon(Polygon p) {
		drawPolygon(p.xpoints, p.ypoints, p.npoints);
	}

	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		int[] buf = new int[nPoints * 2];
		int j = 0;
		for (int i = 0; i < nPoints; i++) {
			buf[j++] = xPoints[i];
			buf[j++] = yPoints[i];
		}
		delegate.drawPolyline(buf);
		buf = null;
	}

	public void drawRect(int x, int y, int width, int height) {
		if ((width < 0) || (height < 0)) {
			return;
		}
		
		if (width == 0 || height == 0) {
			drawLine(x, y, x + width, y + width);
		} else {
			drawLine(x, y, x + width - 1, y);
			drawLine(x + width, y, x + width, y + height - 1);
			drawLine(x + width, y + height, x + 1, y + height);
			drawLine(x, y + height, x, y + 1);
		}
	}

	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		delegate.drawRoundRectangle(x, y, width, height, arcWidth, arcHeight);
	}

	public void drawString(String s, float x, float y) {
		int fh = delegate.getFontMetrics().getAscent();
		delegate.drawString(s, (int)x, (int)y - fh, true);
	}

	public void drawString(String str, int x, int y) {
		int fh = delegate.getFontMetrics().getAscent();
		delegate.drawString(str, x, y - fh, true);
	}

	public void fill(Shape s) {
		Path p = toPath(delegate.getDevice(), s);
		org.eclipse.swt.graphics.Color bg = delegate.getBackground();
		org.eclipse.swt.graphics.Color fg = delegate.getForeground();
		delegate.setBackground(fg);
		delegate.fillPath(p);
		delegate.setBackground(bg);
		p.dispose();
	}

	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		org.eclipse.swt.graphics.Color bg = delegate.getBackground();
		org.eclipse.swt.graphics.Color fg = delegate.getForeground();
		delegate.setBackground(fg);
		delegate.fillArc(x, y, width, height, startAngle, arcAngle);
		delegate.setBackground(bg);
	}

	public void fillOval(int x, int y, int width, int height) {
		org.eclipse.swt.graphics.Color bg = delegate.getBackground();
		org.eclipse.swt.graphics.Color fg = delegate.getForeground();
		delegate.setBackground(fg);
		delegate.fillOval(x, y, width, height);
		delegate.setBackground(bg);
	}

	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		int[] buf = new int[nPoints * 2];
		int j = 0;
		for (int i = 0; i < nPoints; i++) {
			buf[j++] = xPoints[i];
			buf[j++] = yPoints[i];
		}

		
		org.eclipse.swt.graphics.Color bg = delegate.getBackground();
		org.eclipse.swt.graphics.Color fg = delegate.getForeground();
		delegate.setBackground(fg);
		delegate.fillPolygon(buf);
		delegate.setBackground(bg);
		buf = null;
	}

	public void fillPolygon(Polygon p) {
		org.eclipse.swt.graphics.Color bg = delegate.getBackground();
		org.eclipse.swt.graphics.Color fg = delegate.getForeground();
		delegate.setBackground(fg);
		fillPolygon(p.xpoints, p.ypoints, p.npoints);
		delegate.setBackground(bg);
	}

	public void fillRect(int x, int y, int width, int height) {
		org.eclipse.swt.graphics.Color bg = delegate.getBackground();
		org.eclipse.swt.graphics.Color fg = delegate.getForeground();
		delegate.setBackground(fg);
		delegate.fillRectangle(x, y, width, height);
		delegate.setBackground(bg);
	}

	public void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		org.eclipse.swt.graphics.Color bg = delegate.getBackground();
		org.eclipse.swt.graphics.Color fg = delegate.getForeground();
		delegate.setBackground(fg);
		delegate.fillRoundRectangle(x, y, width, height, arcWidth, arcHeight);
		delegate.setBackground(bg);
	}

	public Color getBackground() {
		return toAWTColor(delegate, delegate.getBackground());
	}

	public Shape getClip() {
		return getClipBounds();
	}

	public Rectangle getClipBounds() {
		org.eclipse.swt.graphics.Rectangle r = delegate.getClipping();
		return new Rectangle(r.x, r.y, r.width, r.height);
	}

	public Rectangle getClipBounds(Rectangle r) {
		Rectangle clipRect = getClipBounds();
		if (clipRect != null) {
			r.x = clipRect.x;
			r.y = clipRect.y;
			r.width = clipRect.width;
			r.height = clipRect.height;
		} else if (r == null) {
			throw new NullPointerException("null rectangle parameter");
		}
		
		return r;
	}

	public Font getFont() {
		return toAWTFont(delegate.getDevice(), delegate.getFont());
	}

	public Color getColor() {
		return toAWTColor(delegate, delegate.getForeground());
	}

	public Stroke getStroke() {
		// FIXME
		return null;
	}

	public AffineTransform getTransform() {
		Transform t = new Transform(delegate.getDevice());
		delegate.getTransform(t);
		AffineTransform at = toAWTTransform(t);
		t.dispose();
		return at;
	}
	
	 public boolean hit(Rectangle rect,
				Shape s,
				boolean onStroke) {
		return s.intersects(rect);
	 }

	public void setBackground(Color color) {
		org.eclipse.swt.graphics.Color c = toSWTColor(color);
		delegate.setBackground(c);
		delegate.setAlpha(color.getAlpha());
	}

	public void setClip(int x, int y, int width, int height) {
		delegate.setClipping(x, y, width, height);
	}

	public void setClip(Shape clip) {
		Path p = toPath(delegate.getDevice(), clip);
		delegate.setClipping(p);
		p.dispose();
	}

	public void setFont(Font font) {
		org.eclipse.swt.graphics.Font f = toSWTFont(font);
		delegate.setFont(f);
	}

	public void setColor(Color color) {
		org.eclipse.swt.graphics.Color c = toSWTColor(color);
		delegate.setForeground(c);
		delegate.setAlpha(color.getAlpha());
	}

	public void setStroke(Stroke s) {
		// We can only do things for BasicStroke objects!
		if (s instanceof BasicStroke) {
			BasicStroke bs = (BasicStroke) s;

			// Set the line width
			delegate.setLineWidth((int) bs.getLineWidth());

			// Set the line join
			switch (bs.getLineJoin()) {
				case BasicStroke.JOIN_BEVEL :
					delegate.setLineJoin(SWT.JOIN_BEVEL);
					break;
				case BasicStroke.JOIN_MITER :
					delegate.setLineJoin(SWT.JOIN_MITER);
					break;
				case BasicStroke.JOIN_ROUND :
					delegate.setLineJoin(SWT.JOIN_ROUND);
					break;
			}

			// set the line cap
			switch (bs.getEndCap()) {
				case BasicStroke.CAP_BUTT :
					delegate.setLineCap(SWT.CAP_FLAT);
					break;
				case BasicStroke.CAP_ROUND :
					delegate.setLineCap(SWT.CAP_ROUND);
					break;
				case BasicStroke.CAP_SQUARE :
					delegate.setLineCap(SWT.CAP_SQUARE);
					break;
			}

			// Set the line style to solid by default
			delegate.setLineStyle(SWT.LINE_SOLID);

			// Look for any line style
			float[] dashes = bs.getDashArray();
			if (dashes != null) {
				// Dumb approximation here!
				// FIXME: should look closer at units for lines dashes
				int[] a = new int[dashes.length];
				for (int i = 0; i < a.length; i++) {
					a[i] = (int) dashes[i];
				}
				// this also sets the line style to LINE_CUSTOM
				delegate.setLineDash(a);
				a = null;
				dashes = null;
			}
		}
	}

	public void setTransform(AffineTransform tx) {
		Transform t = toSWTTransform(delegate.getDevice(), tx);
		delegate.setTransform(t);
		t.dispose();
	}
	
	public Paint getPaint() {
		// FIXME
		return getColor();
	}
	
	public void setPaint(Paint paint) {
		if (paint instanceof Color) {
			setColor((Color) paint);
		} else if (paint instanceof GradientPaint) {
			GradientPaint gp = (GradientPaint) paint;
			Point2D p1 = gp.getPoint1();
			Point2D p2 = gp.getPoint2();
			org.eclipse.swt.graphics.Color c1 = toSWTColor(gp.getColor1());
			org.eclipse.swt.graphics.Color c2 = toSWTColor((gp.getColor2()));
			Pattern p = new Pattern(delegate.getDevice(), (float) p1.getX(), (float) p1.getY(),
				(float) p2.getX(), (float) p2.getY(), c1, c2);
			delegate.setBackgroundPattern(p);
		} else if (paint instanceof TexturePaint) {
			TexturePaint tp = (TexturePaint) paint;
			BufferedImage awtImg = tp.getImage();
			org.eclipse.swt.graphics.Image swtImg = new org.eclipse.swt.graphics.Image(
					delegate.getDevice(), awtImg.getWidth(), awtImg.getHeight());
			// FIXME: convert a BufferedImage to an SWT Image
			Pattern p = new Pattern(delegate.getDevice(), swtImg);
			delegate.setForegroundPattern(p);
			swtImg.dispose();
		}
	}
}
