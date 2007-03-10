package edu.uci.ics.jung.visualization.event;

import java.awt.Point;

public class MouseEvent<E> extends InputEvent<E> {
	int button;
	int x;
	int y;
	boolean popupTrigger;
	int clickCount;
	
	public MouseEvent(MouseEvent<E> event) {
		super(event);
		this.button = event.button;
		this.x = event.x;
		this.y = event.y;
		this.popupTrigger = event.popupTrigger;
		this.clickCount = event.clickCount;
	}
	
	public MouseEvent(E uiEvent, Object source, 
			long time, int modifiers, int extendedModifiers,
			int button, int x, int y, boolean popupTrigger, int clickCount) {
		super(uiEvent, source, 
				time, modifiers, extendedModifiers);
		this.button = button;
		this.x = x;
		this.y = y;
		this.popupTrigger = popupTrigger;
		this.clickCount = clickCount;
	}

	/**
	 * @return the button
	 */
	public int getButton() {
		return button;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}
	
	public Point getPoint() {
		return new Point(x, y);
	}
	
	public boolean isPopupTrigger() {
		return popupTrigger;
	}
	
	public int getClickCount() {
		return clickCount;
	}
}
