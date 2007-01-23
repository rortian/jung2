package edu.uci.ics.jung.graph.generators.random;

/**
 * @author W. Giordano, Scott White
 */

import org.apache.commons.collections15.Factory;

import edu.uci.ics.graph.Graph;

import junit.framework.*;


public class TestErdosRenyi extends TestCase {
	
	Factory<String> vertexFactory;
	Factory<Number> edgeFactory;

	public static Test suite() {
		return new TestSuite(TestErdosRenyi.class);
	}

	protected void setUp() {
		vertexFactory = new Factory<String>() {
			int count;
			public String create() {
				return Character.toString((char)('A'+count++));
			}
		};
		edgeFactory = 
			new Factory<Number>() {
			int count;
			public Number create() {
				return count++;
			}
		};
	}

	public void test() {

        int numVertices = 100;
        int total = 0;
		for (int i = 1; i <= 10; i++) {
			ErdosRenyiGenerator<String,Number> generator = 
				new ErdosRenyiGenerator<String,Number>(vertexFactory, edgeFactory,
					numVertices,0.1);
            generator.setSeed(0);

			Graph<String,Number> graph = generator.generateGraph();
			Assert.assertTrue(graph.getVertexCount() == numVertices);
            total += graph.getEdgeCount();
		}
        total /= 10.0;
        Assert.assertTrue(total > 495-50 && total < 495+50);

	}
	  
  
}
