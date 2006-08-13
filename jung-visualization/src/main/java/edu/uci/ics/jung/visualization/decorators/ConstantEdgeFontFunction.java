/*
 * Created on Oct 21, 2004
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
public class ConstantEdgeFontFunction<E> implements EdgeFontFunction<E>
{
    protected Font font;
    
    public ConstantEdgeFontFunction(Font f)
    {
        this.font = f;
    }
    
    public Font getFont(E e)
    {
        return this.font;
    }
}
