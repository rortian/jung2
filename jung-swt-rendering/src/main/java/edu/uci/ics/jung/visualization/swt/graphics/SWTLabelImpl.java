package edu.uci.ics.jung.visualization.swt.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;

import edu.uci.ics.jung.visualization.graphics.Image;
import edu.uci.ics.jung.visualization.graphics.Label;

public class SWTLabelImpl implements Label {
	Color background;
	Color foreground;
	Font font;
	String text;
	Image image;
	boolean opaque;
	
	GC delegate;
	
	
	public SWTLabelImpl(GC delegate) {
		this.delegate = delegate;
	}

	public Color getBackground() {
		return background;
	}

	public Font getFont() {
		return font;
	}

	public Color getForeground() {
		return foreground;
	}

	public Image getImage() {
		return image;
	}

	public String getText() {
		return text;
	}

	public boolean isOpaque() {
		return opaque;
	}

	public void setBackground(Color color) {
		this.background  = color;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public void setForeground(Color color) {
		this.foreground = color;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public void setOpaque(boolean opaque) {
		this.opaque = opaque;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
	public Dimension getTextPreferredSize() {
		org.eclipse.swt.graphics.Font oldFont = delegate.getFont();
		FontData fd = GCGraphicsContext.getSWTFontData(delegate.getDevice(), font);
		org.eclipse.swt.graphics.Font newFont = new org.eclipse.swt.graphics.Font(delegate.getDevice(), fd);
		try {
			delegate.setFont(newFont);
			FontMetrics fm = delegate.getFontMetrics();
			int height = text==null?0:fm.getHeight();
			int width = 0;
			for (int i = 0; text != null && i < text.length(); i++) {
				width += delegate.getAdvanceWidth(text.charAt(i));
			}
			
			return new Dimension(width, height);
		} finally {
			delegate.setFont(oldFont);
			newFont.dispose();
		}
	}
	
	public Dimension getPreferredSize() {
		Dimension size = getTextPreferredSize();
		int width = size.width;
		int height = size.height;
		if (image != null) {
			height = Math.max(height, image.getHeight());
			width += image.getWidth() + (text==null?0:5);
		}
		
		return new Dimension(width, height);
	}
}
