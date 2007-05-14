package edu.uci.ics.jung.visualization.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;


/**
 * An abstract wrapper image class using generics to wrap a UI
 * specific label. It is the responsibility of the particular
 * rendering engine to know how to draw the underlying image.
 * @author Jason A Wrang
 *
 * @param <I>
 */
public interface Label {
	String getText();
	
	void setText(String text);
	
	
	Image getImage();
	
	void setImage(Image img);
	
	
	Color getForeground();
	
	void setForeground(Color color);
	
	
	Color getBackground();
	
	void setBackground(Color color);
	
	
	Font getFont();
	
	void setFont(Font font);
	
	
	boolean isOpaque();
	
	void setOpaque(boolean opaque);
	
	
	Dimension getPreferredSize();	
}
