package edu.uci.ics.jung.visualization.event;


public interface MouseMotionListener<E> {
	void mouseDragged(MouseEvent<E> mouseEvent);
	
	void mouseMoved(MouseEvent<E> mouseEvent);
}
