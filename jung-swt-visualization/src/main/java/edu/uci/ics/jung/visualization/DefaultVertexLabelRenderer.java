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
 * DefaultVertexLabelRenderer is similar to the cell renderers
 * used by the JTable and JTree jfc classes.
 * 
 * @author Tom Nelson
 *
 * 
 */
@SuppressWarnings("serial")
public class DefaultVertexLabelRenderer implements
        VertexLabelRenderer, Serializable {

//     protected static Border noFocusBorder = new EmptyBorder(0,0,0,0); 
    
     protected Color pickedVertexLabelColor = Color.black;
     
    /**
     * Creates a default table cell renderer.
     */
    public DefaultVertexLabelRenderer(Color pickedVertexLabelColor) {
        this.pickedVertexLabelColor = pickedVertexLabelColor;
//        setOpaque(true);
//        setBorder(noFocusBorder);
    }

    
    /**
     *
     * Returns the default label renderer for a Vertex
     *
     * @param vv  the <code>VisualizationViewer</code> to render on
     * @param value  the value to assign to the label for
     *			<code>Vertex</code>
     * @param vertex  the <code>Vertex</code>
     * @return the default label renderer
     */
    public <V> Label getVertexLabelRendererComponent(ScreenDevice vv, Object value,
            Font font, boolean isSelected, V vertex) {
        
    	Label label = vv.getGraphicsContext().createLabel();
        
        label.setForeground(vv.getForeground());
        if(isSelected) label.setForeground(pickedVertexLabelColor);
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
    
//
//    /**
//     * Sets the <code>String</code> object for the cell being rendered to
//     * <code>value</code>.
//     * 
//     * @param value  the string value for this cell; if value is
//     *		<code>null</code> it sets the text value to an empty string
//     * @see JLabel#setText
//     * 
//     */
//    protected void setValue(Object value) {
//        setText((value == null) ? "" : value.toString());
//    }
}
