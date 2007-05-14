/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Apr 14, 2005
 */

package edu.uci.ics.jung.visualization;

import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;

import edu.uci.ics.jung.visualization.graphics.Label;

/**
 * DefaultEdgeLabelRenderer is similar to the cell renderers
 * used by the JTable and JTree jfc classes.
 * 
 * @author Tom Nelson 
 *
 * 
 */
@SuppressWarnings("serial")
public class DefaultEdgeLabelRenderer implements
        EdgeLabelRenderer, Serializable {

//     protected static Border noFocusBorder = new EmptyBorder(0,0,0,0); 
    
     protected Color pickedEdgeLabelColor = Color.black;
     protected boolean rotateEdgeLabels;
     
     public DefaultEdgeLabelRenderer(Color pickedEdgeLabelColor) {
         this(pickedEdgeLabelColor, true);
     }
     
    /**
     * Creates a default table cell renderer.
     */
    public DefaultEdgeLabelRenderer(Color pickedEdgeLabelColor, boolean rotateEdgeLabels) {
        super();
        this.pickedEdgeLabelColor = pickedEdgeLabelColor;
        this.rotateEdgeLabels = rotateEdgeLabels;
//        setOpaque(true);
//        setBorder(noFocusBorder);
    }

    /**
     * @return Returns the rotateEdgeLabels.
     */
    public boolean isRotateEdgeLabels() {
        return rotateEdgeLabels;
    }
    /**
     * @param rotateEdgeLabels The rotateEdgeLabels to set.
     */
    public void setRotateEdgeLabels(boolean rotateEdgeLabels) {
        this.rotateEdgeLabels = rotateEdgeLabels;
    }
 
    
    /**
    *
    * Returns the default label renderer for an Edge
    *
    * @param vv  the <code>VisualizationViewer</code> to render on
    * @param value  the value to assign to the label for
    *			<code>Edge</code>
    * @param edge  the <code>Edge</code>
    * @return the default label renderer
    */
    public <E> Label getEdgeLabelRendererComponent(ScreenDevice vv, Object value,
            Font font, boolean isSelected, E edge) {
        Label label = vv.getGraphicsContext().createLabel();
        
        label.setForeground(vv.getForeground());
        if(isSelected) label.setForeground(pickedEdgeLabelColor);
        label.setBackground(vv.getBackground());
        
        if(font != null) {
        	label.setFont(font);
        } else {
        	label.setFont(vv.getFont());
        }
        label.setImage(null);
//        setBorder(noFocusBorder);
        label.setText((value == null) ? "" : value.toString());
        return label;
    }
}
