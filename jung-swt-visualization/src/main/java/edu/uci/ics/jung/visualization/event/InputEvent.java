package edu.uci.ics.jung.visualization.event;

/**
 * A simplified event class for input events.
 * @author Jason A Wrang
 *
 */
public class InputEvent<E> extends EventObject<E> {
	/**
	 * The input event's Time stamp in UTC format.  The time stamp
	 * indicates when the input event was created.
	 */
	long time;
	
	/**
	 * Indicated whether or not the input event is consumed.
	 */
	boolean consumed = false;
	
	int modifiers;
	int extendedModifiers;
	
	public InputEvent(InputEvent<E> event) {
		super(event);
		this.time = event.time;
		this.modifiers = event.modifiers;
		this.extendedModifiers = event.extendedModifiers;
	}
	
	public InputEvent(E uiEvent, Object source, 
			long time, int modifers, int extendedModifiers) {
		super(uiEvent, source);
		this.time = time;
		this.modifiers = modifers;
		this.extendedModifiers = extendedModifiers;
	}

	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @return the consumed
	 */
	public boolean isConsumed() {
		return consumed;
	}

	/**
	 * @param consumed the consumed to set
	 */
	public void consume() {
		this.consumed = true;
	}
	
	
	/**
	 * @return the modifiers
	 */
	public int getModifiers() {
		return modifiers;
	}
	
	/**
	 * @return the extendedModifiers
	 */
	public int getExtendedModifiers() {
		return modifiers;
	}
	
	public boolean isShiftDown() {
		return (modifiers & Event.SHIFT_MASK) != 0;
	}
	
	public boolean isControlDown() {
		return (modifiers & Event.CTRL_MASK) != 0;
	}
	
	public boolean isAltDown() {
		return (modifiers & Event.ALT_MASK) != 0;
	}
}
