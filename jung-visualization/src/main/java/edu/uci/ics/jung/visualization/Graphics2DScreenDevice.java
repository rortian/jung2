package edu.uci.ics.jung.visualization;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.util.LinkedHashSet;

import edu.uci.ics.jung.visualization.event.Event;
import edu.uci.ics.jung.visualization.event.KeyEvent;
import edu.uci.ics.jung.visualization.event.KeyListener;
import edu.uci.ics.jung.visualization.event.MouseEvent;
import edu.uci.ics.jung.visualization.event.MouseListener;
import edu.uci.ics.jung.visualization.event.MouseMotionListener;
import edu.uci.ics.jung.visualization.event.MouseWheelEvent;
import edu.uci.ics.jung.visualization.event.MouseWheelListener;
import edu.uci.ics.jung.visualization.event.ScreenDeviceListener;

/**
 * An AWT implementation of the screen device.
 * @author Jason A Wrang
 *
 */
public class Graphics2DScreenDevice implements edu.uci.ics.jung.visualization.ScreenDevice {
	Graphics2D g2d;
	public Graphics2DScreenDevice(Graphics2D g2d) {
		this.g2d = g2d;
	}
	
	public Color getBackground() {return g2d.getBackground();}
	public Rectangle getBounds() {return null; }
	public Font getFont() {return g2d.getFont();}
	public Color getForeground() {return g2d.getColor();}
	public Dimension getSize() {return null;}
	public void repaint() {}
	public void setBackground(Color c) {}
	public void setFont(Font font) {}
	public void setForeground(Color c) {}


	public void addScreenDeviceListener(ScreenDeviceListener l) {}
	
	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.ScreenDevice#removeScreenDeviceListener(edu.uci.ics.jung.visualization.event.ScreenDeviceListener)
	 */
	public void removeScreenDeviceListener(ScreenDeviceListener l) {}

	public ScreenDeviceListener[] getScreenDeviceListeners() {return new ScreenDeviceListener[0];}
	
	
	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.ScreenDevice#addKeyListener(edu.uci.ics.jung.visualization.event.MouseListener)
	 */
	public void addKeyListener(KeyListener l) {}

	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.ScreenDevice#removeKeyListener(edu.uci.ics.jung.visualization.event.MouseListener)
	 */
	public void removeKeyListener(KeyListener l) {}
	
	public KeyListener[] getKeyListeners() {return new KeyListener[0];}
	
	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.ScreenDevice#addMouseListener(edu.uci.ics.jung.visualization.event.MouseListener)
	 */
	public void addMouseListener(MouseListener l) {}

	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.ScreenDevice#removeMouseListener(edu.uci.ics.jung.visualization.event.MouseListener)
	 */
	public void removeMouseListener(MouseListener l) {}
	
	public MouseListener[] getMouseListeners() {return new MouseListener[0];}
	
	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.ScreenDevice#addMouseMotionListener(edu.uci.ics.jung.visualization.event.MouseMotionListener)
	 */
	public void addMouseMotionListener(MouseMotionListener l) {}

	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.ScreenDevice#removeMouseMotionListener(edu.uci.ics.jung.visualization.event.MouseMotionListener)
	 */
	public void removeMouseMotionListener(MouseMotionListener l) {}
	
	public MouseMotionListener[] getMouseMotionListeners() {return new MouseMotionListener[0];}
	
	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.ScreenDevice#addMouseWheelListener(edu.uci.ics.jung.visualization.event.MouseWheelListener)
	 */
	public void addMouseWheelListener(MouseWheelListener l) {}

	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.ScreenDevice#removeMouseWheelListener(edu.uci.ics.jung.visualization.event.MouseWheelListener)
	 */
	public void removeMouseWheelListener(MouseWheelListener l) {}
	
	public MouseWheelListener[] getMouseWheelListeners() {return new MouseWheelListener[0];}
}
