package edu.uci.ics.jung.visualization.event;

/**
 * An abstract mouse wheel event from the screen device.
 * @author Jason A Wrang
 *
 */
public class MouseWheelEvent<E> extends MouseEvent<E> {
	/**
	 * Indicated what sort of scrolling should take place in response to this
	 * event, based on platform settings.  Legal values are:
	 * <ul>
	 * <li> Event.WHEEL_UNIT_SCROLL
	 * <li> Event.WHEEL_BLOCK_SCROLL
	 * </u>
	 */
	int scrollType;
	
	/**
	 * Only valid for scrollType Event.WHEEL_UNIT_SCROLL.
	 * Indicates number of units that should be scrolled per
	 * click of mouse wheel rotation, based on platform settings.
	 */
	int scrollAmount;
	
	/**
	 * Indicated how far the mouse wheel was rotated.
	 */
	int wheelRotation;
	
	public MouseWheelEvent(MouseWheelEvent<E> event) {
		super(event);
		this.scrollType = event.scrollType;
		this.scrollAmount = event.scrollAmount;
		this.wheelRotation = event.wheelRotation;
	}
	
	public MouseWheelEvent(E uiEvent, Object source, 
			long time, int modifiers, int extendedModifiers,
			int button, int x, int y, boolean popupTrigger, int clickCount,
			int scrollType, int scrollAmount, int wheelRotation) {
		super(uiEvent, source, 
				time, modifiers, extendedModifiers,
				button, x, y, popupTrigger, clickCount);
		this.scrollType = scrollType;
		this.scrollAmount = scrollAmount;
		this.wheelRotation = wheelRotation;
	}

	/**
	 * @return the scrollAmount
	 */
	public int getScrollAmount() {
		return scrollAmount;
	}

	/**
	 * @return the scrollType
	 */
	public int getScrollType() {
		return scrollType;
	}

	/**
	 * @return the wheelRotation
	 */
	public int getWheelRotation() {
		return wheelRotation;
	}
	
	
}
