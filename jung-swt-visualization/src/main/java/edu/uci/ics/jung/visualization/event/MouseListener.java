package edu.uci.ics.jung.visualization.event;


public interface MouseListener<E> {
	void mousePressed(MouseEvent<E> mouseEvent);
	
	void mouseReleased(MouseEvent<E> mouseEvent);
	
	void mouseClicked(MouseEvent<E> mouseEvent);
	
	void mouseDoubleClicked(MouseEvent<E> mouseEvent);
	
	void mouseEntered(MouseEvent<E> mouseEvent);
	
	void mouseExited(MouseEvent<E> mouseEvent);
}
