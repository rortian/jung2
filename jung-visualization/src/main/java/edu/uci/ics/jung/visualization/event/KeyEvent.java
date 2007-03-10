package edu.uci.ics.jung.visualization.event;


public class KeyEvent<E> extends InputEvent<E> {
	char keyChar;
	
	public KeyEvent(KeyEvent<E> event) {
		super(event);
		this.keyChar = event.keyChar;
	}
	
	public KeyEvent(E uiEvent, Object source, 
			long time, int modifiers, int extendedModifiers,
			char keyChar) {
		super(uiEvent, source, 
				time, modifiers, extendedModifiers);
		this.keyChar = keyChar;
	}

	/**
	 * @return the keyChar
	 */
	public char getKeyChar() {
		return keyChar;
	}
}
