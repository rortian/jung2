package edu.uci.ics.jung.visualization.event;


public interface MouseListener {
	void mousePressed(MouseEvent mouseEvent);
	
	void mouseReleased(MouseEvent mouseEvent);
	
	void mouseClicked(MouseEvent mouseEvent);
	
	void mouseDoubleClicked(MouseEvent mouseEvent);
	
	void mouseEntered(MouseEvent mouseEvent);
	
	void mouseExited(MouseEvent mouseEvent);
}
