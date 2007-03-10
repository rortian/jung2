package edu.uci.ics.jung.visualization.event;


public class KeyEvent extends InputEvent {
	char keyChar;
	
	public KeyEvent(KeyEvent event) {
		super(event);
		this.keyChar = event.keyChar;
	}
	
	public KeyEvent(Object uiEvent, Object source, 
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
