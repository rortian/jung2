package edu.uci.ics.jung.visualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;

import edu.uci.ics.jung.visualization.event.KeyListener;
import edu.uci.ics.jung.visualization.event.MouseListener;
import edu.uci.ics.jung.visualization.event.MouseMotionListener;
import edu.uci.ics.jung.visualization.event.MouseWheelListener;
import edu.uci.ics.jung.visualization.event.ScreenDeviceListener;

public interface ScreenDevice<C> {
	C getUIComponent();
	
	Color getBackground();
	void setBackground(Color c);
	
	Color getForeground();
	void setForeground(Color c);
	
	Font getFont();
	void setFont(Font font);
	
	Dimension getSize();
	Rectangle getBounds();
	
	void repaint();
	
	void addScreenDeviceListener(ScreenDeviceListener<C> l);
	void addKeyListener(KeyListener l);
	void addMouseListener(MouseListener l);
	void addMouseMotionListener(MouseMotionListener l);
	void addMouseWheelListener(MouseWheelListener l);
	
	void removeScreenDeviceListener(ScreenDeviceListener<C> l);
	void removeKeyListener(KeyListener l);
	void removeMouseListener(MouseListener l);
	void removeMouseMotionListener(MouseMotionListener l);
	void removeMouseWheelListener(MouseWheelListener l);
	
	ScreenDeviceListener[] getScreenDeviceListeners();
	KeyListener[] getKeyListeners();
	MouseListener[] getMouseListeners();
	MouseMotionListener[] getMouseMotionListeners();
	MouseWheelListener[] getMouseWheelListeners();
}
