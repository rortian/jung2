package edu.uci.ics.jung.visualization.swt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;

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
public class ScreenDevice<C extends Control> implements edu.uci.ics.jung.visualization.ScreenDevice<C> {
	C comp;
	LinkedHashSet<ScreenDeviceListener<C>> screenDeviceListeners = new LinkedHashSet<ScreenDeviceListener<C>>();
	LinkedHashSet<KeyListener> keyListeners = new LinkedHashSet<KeyListener>();
	LinkedHashSet<MouseListener> mouseListeners = new LinkedHashSet<MouseListener>();
	LinkedHashSet<MouseMotionListener> mouseMotionListeners = new LinkedHashSet<MouseMotionListener>();
	LinkedHashSet<MouseWheelListener> mouseWheelListeners = new LinkedHashSet<MouseWheelListener>();
	
	ControlListener controlListener;
	Listener keyListener;
	Listener mouseListener;
	Listener mouseWheelListener;
	
	private void createControlListener() {
		controlListener = new ControlListener() {
			public void controlResized(ControlEvent e) {
				synchronized (screenDeviceListeners) {
					for (ScreenDeviceListener<C> l : screenDeviceListeners) {
						l.screenResized(ScreenDevice.this);
					}
				}
			}
			public void controlMoved(ControlEvent arg0) {}
		};
	}
	
//	private static void debugModifiers(String prefix, int modifiers) {
//		System.out.print(prefix+" mouse event modifiers: ");
//		if ( (modifiers & Event.SHIFT_MASK) != 0) System.out.print("SHIFT ");
//		if ( (modifiers & Event.CTRL_MASK) != 0) System.out.print("CTRL ");
//		if ( (modifiers & Event.ALT_MASK) != 0) System.out.print("ALT ");
//		if ( (modifiers & Event.META_MASK) != 0) System.out.print("META ");
//		if ( (modifiers & Event.BUTTON1_MASK) != 0) System.out.print("BUTTON1 ");
//		if ( (modifiers & Event.BUTTON1_DOWN_MASK) != 0) System.out.print("BUTTON1_DOWN ");
//		if ( (modifiers & Event.BUTTON2_MASK) != 0) System.out.print("BUTTON2 ");
//		if ( (modifiers & Event.BUTTON2_DOWN_MASK) != 0) System.out.print("BUTTON2_DOWN ");
//		if ( (modifiers & Event.BUTTON3_MASK) != 0) System.out.print("BUTTON3 ");
//		if ( (modifiers & Event.BUTTON3_DOWN_MASK) != 0) System.out.print("BUTTON3_DOWN ");
//		System.out.println();
//	}
	
	
	
	private static int getModifiers(org.eclipse.swt.widgets.Event event) {
		int modifiers = 0;
		if ( (event.stateMask & SWT.SHIFT) > 0 ) modifiers |= Event.SHIFT_MASK;
		if ( (event.stateMask & SWT.CTRL) > 0 ) modifiers |= Event.CTRL_MASK;
		if ( (event.stateMask & SWT.ALT) > 0 ) modifiers |= Event.ALT_MASK;
		if ( (event.stateMask & SWT.COMMAND) > 0 ) modifiers |= Event.META_MASK;;
		
		if ( (event.stateMask & SWT.BUTTON1) != 0) {
			modifiers |= Event.BUTTON1_MASK;
		}
		if ( (event.stateMask & SWT.BUTTON2) != 0) {
			modifiers |= Event.BUTTON2_MASK;
		}
		if ( (event.stateMask & SWT.BUTTON3) != 0) {
			modifiers |= Event.BUTTON3_MASK;
		}
		
		int button = event.button;  // returns the button #
		if (button == 1) {
			modifiers |= Event.BUTTON1_MASK;
		} else if (button == 2) {
			modifiers |= Event.BUTTON2_MASK;
		} else if (button == 3) {
			modifiers |= Event.BUTTON3_MASK;
		}
		
		return modifiers;
	}
	
	private static int getExtendedModifiers(org.eclipse.swt.widgets.Event event) {
		int modifiers = 0;
		
		if ( (event.stateMask & SWT.BUTTON1) != 0) {
			modifiers |= Event.BUTTON1_DOWN_MASK;
		}
		if ( (event.stateMask & SWT.BUTTON2) != 0) {
			modifiers |= Event.BUTTON2_DOWN_MASK;
		}
		if ( (event.stateMask & SWT.BUTTON3) != 0) {
			modifiers |= Event.BUTTON3_DOWN_MASK;
		}
		
		return modifiers;
	}
	
	public static <E extends org.eclipse.swt.widgets.Event> MouseEvent<E> createMouseEvent(E event) {
		int modifiers = getModifiers(event);
		int extendedModifiers = getExtendedModifiers(event);
		
		int button = event.button;  // returns the button #
		if (button == 1) {
			button = 1;
		} else if (button == 2) {
			button = 2;
		} else if (button == 3) {
			button = 3;
		} else {
			button = 0;
		}
		
//		debugModifiers("reg:",modifiers);
//		debugModifiers("ext:",extendedModifiers);
		MouseEvent<E> e = new MouseEvent<E>(event, event.widget, 
				event.time&0xFFFFFFFFL, modifiers, extendedModifiers,
				button, event.x, event.y, false, 1) {
					@Override
					public void consume() {
						getUiEvent().doit = false;
						super.consume();
					}
		};

		return e;
	}
	
	public static <E extends org.eclipse.swt.widgets.Event> MouseWheelEvent<E> createMouseWheelEvent(E event) {
		MouseEvent<E> evt = createMouseEvent(event);

		int scrollType = event.detail;
		if (scrollType == SWT.SCROLL_LINE)
			scrollType = Event.WHEEL_UNIT_SCROLL;
		else if (scrollType == SWT.SCROLL_PAGE)
			scrollType = Event.WHEEL_BLOCK_SCROLL;
		else scrollType = Event.WHEEL_UNIT_SCROLL;
		
		int scrollAmount = event.count;
		int wheelRotation = 1;
		if (scrollAmount < 0) {
			scrollAmount = -scrollAmount;
			wheelRotation = -wheelRotation;
		}
		
		MouseWheelEvent<E> e = new MouseWheelEvent<E>(evt.getUiEvent(), evt.getSource(), 
				evt.getTime(), evt.getModifiers(), evt.getExtendedModifiers(),
				evt.getButton(), evt.getX(), evt.getY(), evt.isPopupTrigger(), evt.getClickCount(),
				scrollType, scrollAmount, wheelRotation) {
					@Override
					public void consume() {
						getUiEvent().doit = false;
						super.consume();
					}
		};
		return e;
	}
	
	private void createMouseListener() {
		mouseListener = new org.eclipse.swt.widgets.Listener() {
			Set<Integer> buttonsDown = new HashSet<Integer>();
			public void handleEvent(org.eclipse.swt.widgets.Event e) {
				MouseEvent<org.eclipse.swt.widgets.Event> evt = createMouseEvent(e);
				
				if ( e.type == SWT.MenuDetect) {
					System.out.println("Menu detect");
				} else if ( e.type == SWT.MouseDown) {
					buttonsDown.add(evt.getButton());
					mousePressed(evt);
				} else if ( e.type == SWT.MouseUp) {
					buttonsDown.remove(evt.getButton());
					mouseReleased(evt);
					mouseClicked(evt);
				} else if ( e.type == SWT.MouseEnter) {
					mouseEntered(evt);
				} else if ( e.type == SWT.MouseExit) {
					mouseExited(evt);
				} else if ( e.type == SWT.MouseDoubleClick) {
					mouseDoubleClicked(evt);
				} else if ( e.type == SWT.MouseMove) {
					if (buttonsDown.isEmpty()) mouseMoved(evt);
					else mouseDragged(evt);
				}
			}

			private void mouseEntered(MouseEvent<org.eclipse.swt.widgets.Event> evt) {
				synchronized (mouseListeners) {
					for (MouseListener<org.eclipse.swt.widgets.Event> l : mouseListeners) {
						if ( evt.isConsumed() ) break;
						l.mouseEntered(evt);
					}
				}
			}

			private void mouseExited(MouseEvent<org.eclipse.swt.widgets.Event> evt) {
				synchronized (mouseListeners) {
					for (MouseListener<org.eclipse.swt.widgets.Event> l : mouseListeners) {
						if ( evt.isConsumed() ) break;
						l.mouseExited(evt);
					}
				}
			}

			private void mousePressed(MouseEvent<org.eclipse.swt.widgets.Event> evt) {
				synchronized (mouseListeners) {
					for (MouseListener<org.eclipse.swt.widgets.Event> l : mouseListeners) {
						if ( evt.isConsumed() ) break;
						l.mousePressed(evt);
					}
				}
			}

			private void mouseReleased(MouseEvent<org.eclipse.swt.widgets.Event> evt) {
				synchronized (mouseListeners) {
					for (MouseListener<org.eclipse.swt.widgets.Event> l : mouseListeners) {
						if ( evt.isConsumed() ) break;
						l.mouseReleased(evt);
					}
				}
			}
			
			private void mouseClicked(MouseEvent<org.eclipse.swt.widgets.Event> evt) {
				synchronized (mouseListeners) {
					for (MouseListener<org.eclipse.swt.widgets.Event> l : mouseListeners) {
						if ( evt.isConsumed() ) break;
						l.mouseClicked(evt);
					}
				}
			}
			
			private void mouseDoubleClicked(MouseEvent<org.eclipse.swt.widgets.Event> evt) {
				synchronized (mouseListeners) {
					for (MouseListener<org.eclipse.swt.widgets.Event> l : mouseListeners) {
						if ( evt.isConsumed() ) break;
						l.mouseDoubleClicked(evt);
					}
				}
			}
			
			private void mouseMoved(MouseEvent<org.eclipse.swt.widgets.Event> evt) {
				synchronized (mouseMotionListeners) {
					for (MouseMotionListener<org.eclipse.swt.widgets.Event> l : mouseMotionListeners) {
						if ( evt.isConsumed() ) break;
						l.mouseMoved(evt);
					}
				}
			}
			
			private void mouseDragged(MouseEvent<org.eclipse.swt.widgets.Event> evt) {
				synchronized (mouseMotionListeners) {
					for (MouseMotionListener<org.eclipse.swt.widgets.Event> l : mouseMotionListeners) {
						if ( evt.isConsumed() ) break;
						l.mouseDragged(evt);
					}
				}
			}
		};
	}

	
	private void createMouseWheelListener() {
		mouseWheelListener = new org.eclipse.swt.widgets.Listener() {
			public void handleEvent(org.eclipse.swt.widgets.Event e) {
				MouseWheelEvent<org.eclipse.swt.widgets.Event> evt = createMouseWheelEvent(e);

				if ( e.type == SWT.MouseWheel) {
					mouseWheelMoved(evt);
				}
			}
			
			private void mouseWheelMoved(MouseWheelEvent<org.eclipse.swt.widgets.Event> evt) {
				synchronized (mouseWheelListeners) {
					for (MouseWheelListener<org.eclipse.swt.widgets.Event> l : mouseWheelListeners) {
						if ( evt.isConsumed() ) break;
						l.mouseWheelMoved(evt);
					}
				}
			}
		};
	}
	
	
	public static <E extends org.eclipse.swt.widgets.Event> KeyEvent<E> createKeyEvent(E event) {
		int modifiers = getModifiers(event);
		int extendedModifiers = getExtendedModifiers(event);
		
		char keyChar = event.character;  // returns the key char
		KeyEvent<E> e = new KeyEvent<E>(event, event.widget, 
				event.time&0xFFFFFFFFL, modifiers, extendedModifiers,
				keyChar);

		return e;
	}
	
	
	private void createKeyListener() {
		keyListener = new org.eclipse.swt.widgets.Listener() {
			public void handleEvent(org.eclipse.swt.widgets.Event e) {
				KeyEvent<org.eclipse.swt.widgets.Event> evt = createKeyEvent(e);

				if ( e.type == SWT.KeyDown) {
					keyPressed(evt);
				} else if ( e.type == SWT.KeyUp) {
					keyReleased(evt);
					keyTyped(evt);
				}
			}
			
			private void keyPressed(KeyEvent<org.eclipse.swt.widgets.Event> evt) {
				synchronized (keyListeners) {
					for (KeyListener<org.eclipse.swt.widgets.Event> l : keyListeners) {
						if ( evt.isConsumed() ) break;
						l.keyPressed(evt);
					}
				}
			}

			private void keyReleased(KeyEvent<org.eclipse.swt.widgets.Event> evt) {
				synchronized (keyListeners) {
					for (KeyListener<org.eclipse.swt.widgets.Event> l : keyListeners) {
						if ( evt.isConsumed() ) break;
						l.keyReleased(evt);
					}
				}
			}

			private void keyTyped(KeyEvent<org.eclipse.swt.widgets.Event> evt) {
				synchronized (keyListeners) {
					for (KeyListener<org.eclipse.swt.widgets.Event> l : keyListeners) {
						if ( evt.isConsumed() ) break;
						l.keyTyped(evt);
					}
				}
			}
		};
	}
	
	
	public ScreenDevice(C comp) {
		this.comp = comp;
		comp.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if (curColor != null) curColor.dispose();
				if (curFont != null) curFont.dispose();
			}
		});
	}
	
	public C getUIComponent() {
		return comp;
	}
	
	public Color getBackground() {
		org.eclipse.swt.graphics.Color c = comp.getBackground();
		RGB rgb = c.getRGB();
		return new Color(rgb.red, rgb.green, rgb.blue);
	}
	public Rectangle getBounds() {
		org.eclipse.swt.graphics.Rectangle r = comp.getBounds();
		return new Rectangle(r.x, r.y, r.width, r.height);
	}
	public Font getFont() {
		org.eclipse.swt.graphics.Font f = comp.getFont();
		FontData fd = f.getFontData()[0];
		String name = fd.getName();
		int height = fd.getHeight();
		int style = fd.getStyle();
		int awtStyle = 0;
		if ( (style & SWT.NORMAL) != 0 ) awtStyle = java.awt.Font.PLAIN;
		if ( (style & SWT.BOLD) != 0 ) awtStyle |= java.awt.Font.BOLD;
		if ( (style & SWT.ITALIC) != 0 ) awtStyle |= java.awt.Font.ITALIC;
		
		return new Font(name, awtStyle, height);
	}
	public Color getForeground() {
		org.eclipse.swt.graphics.Color c = comp.getForeground();
		RGB rgb = c.getRGB();
		return new Color(rgb.red, rgb.green, rgb.blue);
	}
	public Dimension getSize() {
		Point p =  comp.getSize();
		return new Dimension(p.x, p.y);
	}
	public void repaint() {
		comp.redraw();
	}
	
	org.eclipse.swt.graphics.Color curColor;
	public void setBackground(Color c) {
		org.eclipse.swt.graphics.Color color = new org.eclipse.swt.graphics.Color(comp.getDisplay(), c.getRed(), c.getGreen(), c.getBlue());
		comp.setBackground(color);
		if (curColor != null) curColor.dispose();
		curColor = color;
	}
	org.eclipse.swt.graphics.Font curFont;
	public void setFont(Font font) {
		String name = font.getName();
		int awtStyle = font.getStyle();
		int height = font.getSize();
		int style = 0;
		if ( (awtStyle) == 0 ) style |= SWT.NORMAL;
		if ( (awtStyle & java.awt.Font.BOLD) != 0 ) style |= SWT.BOLD;
		if ( (awtStyle & java.awt.Font.ITALIC) != 0 ) style |= SWT.ITALIC;
	
		org.eclipse.swt.graphics.Font f = new org.eclipse.swt.graphics.Font(comp.getDisplay(), name, height, style);
		comp.setFont(f);
		if (curFont != null) curFont.dispose();
		curFont = f;
	}
	public void setForeground(Color c) {
		org.eclipse.swt.graphics.Color color = new org.eclipse.swt.graphics.Color(comp.getDisplay(), c.getRed(), c.getGreen(), c.getBlue());
		comp.setForeground(color);
		if (curColor != null) curColor.dispose();
		curColor = color;
	}


	public void addScreenDeviceListener(ScreenDeviceListener<C> l) {
		synchronized (screenDeviceListeners) {
			if ( screenDeviceListeners.isEmpty() ) {
				createControlListener();
				comp.addControlListener(controlListener);
			}
			screenDeviceListeners.add(l);
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.ScreenDevice#removeScreenDeviceListener(edu.uci.ics.jung.visualization.event.ScreenDeviceListener)
	 */
	public void removeScreenDeviceListener(ScreenDeviceListener l) {
		synchronized (screenDeviceListeners) {
			screenDeviceListeners.remove(l);
			if ( screenDeviceListeners.isEmpty() ) {
				comp.removeControlListener(controlListener);
				controlListener = null;
			}
		}
	}

	public ScreenDeviceListener[] getScreenDeviceListeners() {
		synchronized (screenDeviceListeners) {
			return screenDeviceListeners.toArray(new ScreenDeviceListener[screenDeviceListeners.size()]);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.ScreenDevice#addKeyListener(edu.uci.ics.jung.visualization.event.MouseListener)
	 */
	public void addKeyListener(KeyListener l) {
		synchronized (keyListeners) {
			if ( keyListeners.isEmpty() ) {
				createKeyListener();
				comp.addListener(SWT.KeyUp, keyListener);
				comp.addListener(SWT.KeyDown, keyListener);
			}
			keyListeners.add(l);
		}
	}

	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.ScreenDevice#removeKeyListener(edu.uci.ics.jung.visualization.event.MouseListener)
	 */
	public void removeKeyListener(KeyListener l) {
		synchronized (keyListeners) {
			keyListeners.remove(l);
			if ( keyListeners.isEmpty() ) {
				comp.removeListener(SWT.KeyUp, keyListener);
				comp.removeListener(SWT.KeyDown, keyListener);
				keyListener = null;
			}
		}
	}
	
	public KeyListener[] getKeyListeners() {
		synchronized (keyListeners) {
			return keyListeners.toArray(new KeyListener[keyListeners.size()]);
		}
	}
	
	private void addMouseListeners() {
		createMouseListener();
		comp.addListener(SWT.MenuDetect, mouseListener);
		comp.addListener(SWT.MouseEnter, mouseListener);
		comp.addListener(SWT.MouseExit, mouseListener);
		comp.addListener(SWT.MouseDown, mouseListener);
		comp.addListener(SWT.MouseUp, mouseListener);
		comp.addListener(SWT.MouseDoubleClick, mouseListener);
		comp.addListener(SWT.MouseMove, mouseListener);
	}
	
	private void removeMouseListeners() {
		comp.removeListener(SWT.MouseEnter, mouseListener);
		comp.removeListener(SWT.MouseExit, mouseListener);
		comp.removeListener(SWT.MouseDown, mouseListener);
		comp.removeListener(SWT.MouseUp, mouseListener);
		comp.removeListener(SWT.MouseDoubleClick, mouseListener);
		comp.removeListener(SWT.MouseMove, mouseListener);
		mouseListener = null;
	}
	
	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.ScreenDevice#addMouseListener(edu.uci.ics.jung.visualization.event.MouseListener)
	 */
	public void addMouseListener(MouseListener l) {
		synchronized (mouseListeners) {
			if ( mouseListeners.isEmpty() && mouseMotionListeners.isEmpty() ) {
				addMouseListeners();
			}
			mouseListeners.add(l);
		}
	}

	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.ScreenDevice#removeMouseListener(edu.uci.ics.jung.visualization.event.MouseListener)
	 */
	public void removeMouseListener(MouseListener l) {
		synchronized (mouseListeners) {
			mouseListeners.remove(l);
			if ( mouseListeners.isEmpty() && mouseMotionListeners.isEmpty()) {
				removeMouseListeners();
			}
		}
	}
	
	public MouseListener[] getMouseListeners() {
		synchronized (mouseListeners) {
			return mouseListeners.toArray(new MouseListener[mouseListeners.size()]);
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.ScreenDevice#addMouseMotionListener(edu.uci.ics.jung.visualization.event.MouseMotionListener)
	 */
	public void addMouseMotionListener(MouseMotionListener l) {
		synchronized (mouseMotionListeners) {
			if ( mouseListeners.isEmpty() && mouseMotionListeners.isEmpty() ) {
				createMouseListener();
			}
			mouseMotionListeners.add(l);
		}
	}

	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.ScreenDevice#removeMouseMotionListener(edu.uci.ics.jung.visualization.event.MouseMotionListener)
	 */
	public void removeMouseMotionListener(MouseMotionListener l) {
		synchronized (mouseMotionListeners) {
			mouseMotionListeners.remove(l);
			if ( mouseListeners.isEmpty() && mouseMotionListeners.isEmpty() ) {
				removeMouseListeners();
			}
		}
	}
	
	public MouseMotionListener[] getMouseMotionListeners() {
		synchronized (mouseMotionListeners) {
			return mouseMotionListeners.toArray(new MouseMotionListener[mouseMotionListeners.size()]);
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.ScreenDevice#addMouseWheelListener(edu.uci.ics.jung.visualization.event.MouseWheelListener)
	 */
	public void addMouseWheelListener(MouseWheelListener l) {
		synchronized (mouseWheelListeners) {
			if ( mouseWheelListeners.isEmpty() ) {
				createMouseWheelListener();
				comp.addListener(SWT.MouseWheel, mouseWheelListener);
			}
			mouseWheelListeners.add(l);
		}
	}

	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.ScreenDevice#removeMouseWheelListener(edu.uci.ics.jung.visualization.event.MouseWheelListener)
	 */
	public void removeMouseWheelListener(MouseWheelListener l) {
		synchronized (mouseWheelListeners) {
			mouseWheelListeners.remove(l);
			if ( mouseWheelListeners.isEmpty() ) {
				comp.removeListener(SWT.MouseWheel, mouseWheelListener);
				mouseWheelListener = null;
			}
		}
	}
	
	public MouseWheelListener[] getMouseWheelListeners() {
		synchronized (mouseWheelListeners) {
			return mouseWheelListeners.toArray(new MouseWheelListener[mouseWheelListeners.size()]);
		}
	}
}
