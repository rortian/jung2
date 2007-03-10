/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Jul 6, 2005
 */

package edu.uci.ics.jung.visualization.control;

import edu.uci.ics.jung.visualization.event.MouseEvent;
import edu.uci.ics.jung.visualization.event.MouseListener;

/**
 * Simple extension of MouseAdapter that supplies modifier
 * checking
 * 
 * @author Tom Nelson 
 *
 */
public class GraphMouseAdapter implements MouseListener {

    protected int modifiers;
    
    public GraphMouseAdapter(int modifiers) {
        this.modifiers = modifiers;
    }
    
    public int getModifiers() {
        return modifiers;
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }
    
    protected boolean checkModifiers(MouseEvent e) {
        return e.getModifiers() == modifiers;
    }
    
    

	public void mouseClicked(MouseEvent mouseEvent) {}

	public void mouseDoubleClicked(MouseEvent mouseEvent) {}

	public void mouseEntered(MouseEvent mouseEvent) {}

	public void mouseExited(MouseEvent mouseEvent) {}

	public void mousePressed(MouseEvent mouseEvent) {}

	public void mouseReleased(MouseEvent mouseEvent) {}
}
