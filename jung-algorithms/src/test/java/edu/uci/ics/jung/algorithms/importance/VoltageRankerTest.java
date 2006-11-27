/*
 * Created on Aug 11, 2004
 *
 */
package edu.uci.ics.jung.algorithms.importance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.graph.SimpleUndirectedSparseGraph;

/**
 * 
 *  
 * @author Joshua O'Madadhain, adapted to jung2 by Tom Nelson
 */
public class VoltageRankerTest extends TestCase
{
    protected Graph<Number,Number> g;
    
    public void setUp() {
        g = new SimpleUndirectedSparseGraph<Number,Number>();
        for (int i = 0; i < 7; i++) {
        	g.addVertex(i);
        }

        int j = 0;
        g.addEdge(j++,0,1);
        g.addEdge(j++,0,2);
        g.addEdge(j++,1,3);
        g.addEdge(j++,2,3);
        g.addEdge(j++,3,4);
        g.addEdge(j++,3,5);
        g.addEdge(j++,4,6);
        g.addEdge(j++,5,6);
    }
    
    public final void testCalculateVoltagesSourceTarget() {
        Map<Number,Number> vv = new HashMap<Number,Number>();
        VoltageRanker<Number,Number> vr = new VoltageRanker<Number,Number>(vv, 100, 0.001);
        double[] voltages = {1.0, 0.75, 0.75, 0.5, 0.25, 0.25, 0};
        
        vr.calculateVoltages(g, 0, 6);
        for (int i = 0; i < 7; i++) {
            assertEquals(vv.get(i).doubleValue(), voltages[i], 0.01);
        }
    }
    
    public final void testCalculateVoltagesSourcesTargets()
    {
        Map<Number,Number> vv = new HashMap<Number,Number>();
        VoltageRanker<Number,Number> vr = new VoltageRanker<Number,Number>(vv, 100, 0.001);
        double[] voltages = {1.0, 0.5, 0.66, 0.33, 0.16, 0, 0};
        
        Map<Number,Number> sources = new HashMap<Number,Number>();
        sources.put(0, new Double(1.0));
        sources.put(1, new Double(0.5));
        Set<Number> sinks = new HashSet<Number>();
        sinks.add(6);
        sinks.add(5);
        
        vr.calculateVoltages(g, sources, sinks);
        for (int i = 0; i < 7; i++) {
            assertEquals(vv.get(i).doubleValue(), voltages[i], 0.01);
        }
    }
    
//    protected static class VertexVoltages<V> implements Map<V,Number>
//    {
////        protected final static String VOLTAGE_KEY = "VoltageRankerTest.KEY";
//    	private Map<V,Number> map = new HashMap<V,Number>();
//
//        public Number get(V v) {
//            return map.get(v);
//        }
//
//        public void setNumber(V v, Number n) {
//            map.put(v,n);
//        }
//    }
}
