/*
 * Created on Apr 21, 2007
 *
 * Copyright (c) 2007, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.graph;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.collections15.Factory;

public class HypergraphTest extends TestCase
{
    protected Factory<? extends Hypergraph<Integer,Double>> factory;
    protected Hypergraph<Integer,Double> h;
    
    public HypergraphTest(Factory<? extends Hypergraph<Integer,Double>> factory)
    {
        this.factory = factory;
    }
    
    public static Test suite()
    {
        TestSuite ts = new TestSuite("HypergraphTest");
        
        ts.addTest(new HypergraphTest(SetHypergraph.<Integer,Double>getFactory()));
        ts.addTest(new HypergraphTest(DirectedOrderedSparseMultigraph.<Integer,Double>getFactory()));
        ts.addTest(new HypergraphTest(DirectedSparseGraph.<Integer,Double>getFactory()));
        ts.addTest(new HypergraphTest(DirectedSparseMultigraph.<Integer,Double>getFactory()));
        ts.addTest(new HypergraphTest(OrderedSparseMultigraph.<Integer,Double>getFactory()));
        ts.addTest(new HypergraphTest(SortedSparseMultigraph.<Integer,Double>getFactory()));
        ts.addTest(new HypergraphTest(SparseGraph.<Integer,Double>getFactory()));
        ts.addTest(new HypergraphTest(SparseMultigraph.<Integer,Double>getFactory()));
        ts.addTest(new HypergraphTest(UndirectedOrderedSparseMultigraph.<Integer,Double>getFactory()));
        ts.addTest(new HypergraphTest(UndirectedSparseGraph.<Integer,Double>getFactory()));
        ts.addTest(new HypergraphTest(UndirectedSparseMultigraph.<Integer,Double>getFactory()));
//        ts.addTest(new HypergraphTest(.getFactory()));
        
        return ts;
    }
    
    public void setUp()
    {
        h = factory.create();
    }
    
    public void runTest() throws Exception {
        setUp();
        testAddVertex();
        tearDown();
    }

    /**
     * test for the following:
     * <ul>
     * <li/>count increases by 1 iff add is successful
     * <li/>null vertex argument actively rejected
     * <li/>vertex reported as present iff add is successful
     * </ul>
     */
    public void testAddVertex()
    {
        int count = h.getVertexCount();
        assertTrue(h.addVertex(new Integer(1)));
        assertEquals(count+1, h.getVertexCount());
        assertTrue(h.containsVertex(1));
        try
        {
            h.addVertex(null);
            fail("Implementation should disallow null vertices");
        }
        catch (IllegalArgumentException iae) {}
        catch (NullPointerException npe)
        {
            fail("Implementation should actively prevent null vertices");
        }
        assertFalse(h.addVertex(1));
        assertEquals(count+1, h.getVertexCount());
        assertFalse(h.containsVertex(2));
    }
}
