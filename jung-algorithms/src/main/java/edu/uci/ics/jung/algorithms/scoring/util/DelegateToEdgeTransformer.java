/*
 * Created on Jul 11, 2008
 *
 * Copyright (c) 2008, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.algorithms.scoring.util;

import org.apache.commons.collections15.Transformer;

/**
 * 
 */
public class DelegateToEdgeTransformer<V,E,W> implements
        Transformer<VEPair<V,E>,W>
{
    protected Transformer<E,? extends W> delegate;
    
    public DelegateToEdgeTransformer(Transformer<E,? extends W> delegate)
    {
        this.delegate = delegate;
    }
    
    
    public W transform(VEPair<V,E> arg0)
    {
        return delegate.transform(arg0.getE());
    }

}
