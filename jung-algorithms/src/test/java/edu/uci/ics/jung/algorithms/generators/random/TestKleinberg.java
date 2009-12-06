package edu.uci.ics.jung.algorithms.generators.random;


import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.algorithms.generators.Lattice2DGenerator;
import edu.uci.ics.jung.algorithms.generators.TestLattice2D;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;


public class TestKleinberg extends TestLattice2D {
	
    protected Lattice2DGenerator<String, Number> generate(int i, int j, int k)
    {
        return new KleinbergSmallWorldGenerator<String,Number>(
                k == 0 ? undirectedGraphFactory : directedGraphFactory, 
                vertexFactory, edgeFactory,
                i, // rows
                i, // columns
                0.1, // clustering exponent
                j == 0 ? true : false); // toroidal?
    }
    
    protected void checkEdgeCount(int i, int j, int k, Graph<String, Number> graph) 
    {
        Assert.assertEquals(2*i*(i-j)*(k+1) + graph.getVertexCount(), graph.getEdgeCount());
    }
}
