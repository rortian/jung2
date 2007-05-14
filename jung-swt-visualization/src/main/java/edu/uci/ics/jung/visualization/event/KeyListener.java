package edu.uci.ics.jung.visualization.event;


public interface KeyListener<E> {
	void keyPressed(KeyEvent<E> keyEvent);
	
	void keyReleased(KeyEvent<E> keyEvent);
	
	void keyTyped(KeyEvent<E> keyEvent);
}
