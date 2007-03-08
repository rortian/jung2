package edu.uci.ics.jung.visualization.awt;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.LinkedList;

public class ScreenDevice implements edu.uci.ics.jung.visualization.ScreenDevice {
	Component comp;
	LinkedList<ScreenDeviceListener> listeners = new LinkedList<ScreenDeviceListener>();
	ComponentListener componentListener;
	
	public ScreenDevice(Component comp) {
		this.comp = comp;
		componentListener = new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				synchronized (listeners) {
					for (ScreenDeviceListener l : listeners) {
						l.screenResized(ScreenDevice.this);
					}
				}
			}
		};
		this.comp.addComponentListener(componentListener);
	}
	
	public Color getBackground() {
		return comp.getBackground();
	}
	public Rectangle getBounds() {
		return comp.getBounds();
	}
	public Font getFont() {
		return comp.getFont();
	}
	public Color getForeground() {
		return comp.getForeground();
	}
	public Dimension getSize() {
		return comp.getSize();
	}
	public void repaint() {
		comp.repaint();
	}
	public void setBackground(Color c) {
		comp.setBackground(c);
	}
	public void setFont(Font font) {
		comp.setFont(font);
	}
	public void setForeground(Color c) {
		comp.setForeground(c);
	}

	public void addScreenDeviceListener(ScreenDeviceListener l) {
		synchronized (listeners) {
			listeners.add(l);
		}
	}
}
