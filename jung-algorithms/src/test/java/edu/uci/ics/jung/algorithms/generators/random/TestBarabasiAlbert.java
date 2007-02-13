package edu.uci.ics.jung.algorithms.generators.random;

/**
 * @author W. Giordano, Scott White
 */

import java.util.HashSet;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.algorithms.generators.random.BarabasiAlbertGenerator;
import edu.uci.ics.jung.graph.SparseGraph;


public class TestBarabasiAlbert extends TestCase {
	public static Test suite() {
		return new TestSuite(TestBarabasiAlbert.class);
	}

	protected void setUp() {
	}

	public void test() 
    {
        int init_vertices = 1;
        int edges_to_add_per_timestep = 1;
        int random_seed = 0;
        int num_tests = 10;
        int num_timesteps = 10;
        
        Factory<Graph<Integer,Number>> graphFactory =
        	new Factory<Graph<Integer,Number>>() {
        	public Graph<Integer,Number> create() {
        		return new SparseGraph<Integer,Number>();
        	}
        };
    	Factory<Integer> vertexFactory = 
    		new Factory<Integer>() {
    			int count;
				public Integer create() {
					return count++;
				}};
		Factory<Number> edgeFactory = 
		    new Factory<Number>() {
			    int count;
				public Number create() {
					return count++;
				}};

	    BarabasiAlbertGenerator<Integer,Number> generator = 
            new BarabasiAlbertGenerator<Integer,Number>(graphFactory, vertexFactory, edgeFactory,
            		init_vertices,edges_to_add_per_timestep,random_seed, new HashSet<Integer>());
	    for (int i = 1; i <= num_tests; i++) {
	        
	        generator.evolveGraph(num_timesteps);
	        Graph graph = (Graph) generator.generateGraph();
	        assertEquals(graph.getVertexCount(), (i*num_timesteps) + init_vertices);
	        assertEquals(graph.getEdgeCount(), edges_to_add_per_timestep * (i*num_timesteps));
	    }
	}
}
