/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Aug 26, 2005
 */

package edu.uci.ics.jung.visualization.control;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.uci.ics.jung.visualization.VisualizationViewer.GraphMouse;

/**
 * Interface for a GraphMouse that supports modality.
 * 
 * @author Tom Nelson - RABA Technologies
 *
 */
public interface ModalGraphMouse extends GraphMouse {
    
    void setMode(Mode mode);
    
    /**
     * @return Returns the modeListener.
     */
    ItemListener getModeListener();
    
    /**
     *  The Mode class implements the typesafe enum pattern.
     *  This pattern is fully described in Joshua Bloch's book
     *  Effective Java Programming Language Guide, Item 21.
     *
     *  Created: Sun Aug 28 10:25:16 2005
     *
     *  @author Tom Nelson
     *  @version 1.0
     */
    enum Mode { TRANSFORMING, PICKING, EDITING }
    
}