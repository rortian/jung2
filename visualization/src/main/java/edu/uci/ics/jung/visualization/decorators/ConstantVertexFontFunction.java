/*
 * Created on Aug 29, 2004
 *
 * Copyright (c) 2004, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.visualization.decorators;

import java.awt.Font;

/**
 * 
 * @author Joshua O'Madadhain
 */
public class ConstantVertexFontFunction<V> implements VertexFontFunction<V>
{
    protected Font font;
    
    public ConstantVertexFontFunction(Font f)
    {
        this.font = f;
    }
    
    /**
     * @see edu.uci.ics.jung.graph.decorators.VertexFontFunction#getFont(edu.uci.ics.jung.graph.Vertex)
     */
    public Font getFont(V v)
    {
        return font;
    }

}
