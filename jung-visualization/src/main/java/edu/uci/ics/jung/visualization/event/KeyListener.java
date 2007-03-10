package edu.uci.ics.jung.visualization.event;


public interface KeyListener {
	void keyPressed(KeyEvent keyEvent);
	
	void keyReleased(KeyEvent keyEvent);
	
	void keyTyped(KeyEvent keyEvent);
}
