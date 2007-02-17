/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 */
package samples.graph;

import javax.media.j3d.BoundingSphere;
import javax.vecmath.Point3d;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.algorithms.IterativeContext;
import edu.uci.ics.jung.algorithms.layout.util.VisRunner;
import edu.uci.ics.jung.graph.util.TestGraphs;
import edu.uci.ics.jung3d.algorithms.layout.Layout;
import edu.uci.ics.jung3d.algorithms.layout.SpringLayout;


/**
 * Demonstrates the use of <code>GraphZoomScrollPane</code>.
 * This class shows the <code>VisualizationViewer</code> zooming
 * and panning capabilities, using horizontal and
 * vertical scrollbars.
 *
 * <p>This demo also shows ToolTips on graph vertices and edges,
 * and a key listener to change graph mouse modes.</p>
 * 
 * @author Tom Nelson
 * 
 */
public class SpringLayout3DTest {

    /**
     * the graph
     */
    Graph<String, Number> graph;
    
    /**
     * create an instance of a simple graph with controls to
     * demo the zoom features.
     * 
     */
    public SpringLayout3DTest() {
        
        // create a simple graph for the demo
        graph = TestGraphs.getOneComponentGraph();

        Layout<String,Number> layout = new SpringLayout<String,Number>(graph);
        layout.initialize();
        layout.setSize(new BoundingSphere(new Point3d(), 100));
        VisRunner runner = new VisRunner((IterativeContext)layout);
        runner.relax();
        
    }
    
    /**
     * a driver for this demo
     */
    public static void main(String[] args) 
    {
        new SpringLayout3DTest();
    }
}
