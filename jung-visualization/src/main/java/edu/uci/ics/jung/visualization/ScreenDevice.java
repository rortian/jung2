package edu.uci.ics.jung.visualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;

public interface ScreenDevice {
	Color getBackground();
	void setBackground(Color c);
	
	Color getForeground();
	void setForeground(Color c);
	
	Font getFont();
	void setFont(Font font);
	
	Dimension getSize();
	Rectangle getBounds();
	
	void repaint();
	
	void addScreenDeviceListener(ScreenDeviceListener l);
	
	interface ScreenDeviceListener {
		void screenResized(ScreenDevice target);
	}
}
