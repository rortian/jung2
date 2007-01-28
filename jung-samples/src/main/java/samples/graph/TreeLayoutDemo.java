/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 */
package samples.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.graph.DirectedGraph;
import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.SimpleSparseForest;
import edu.uci.ics.jung.graph.SimpleDirectedSparseGraph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ViewScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

/**
 * Demonstrates the use of images to represent graph vertices.
 * The images are supplied via the VertexShapeFunction so that
 * both the image and its shape can be utilized.
 * 
 * The images used in this demo (courtesy of slashdot.org) are
 * rectangular but with a transparent background. When vertices
 * are represented by these images, it looks better if the actual
 * shape of the opaque part of the image is computed so that the
 * edge arrowheads follow the visual shape of the image. This demo
 * uses the FourPassImageShaper class to compute the Shape from
 * an image with transparent background.
 * 
 * @author Tom Nelson
 * 
 */
public class TreeLayoutDemo extends JApplet {

    /**
     * the graph
     */
    Graph<String,Integer> graph;
    
    Factory<DirectedGraph<String,Integer>> graphFactory = 
    	new Factory<DirectedGraph<String,Integer>>() {

			public DirectedGraph<String, Integer> create() {
				return new SimpleDirectedSparseGraph<String,Integer>();
			}};
			
	Factory<Integer> edgeFactory = new Factory<Integer>() {
		int i=0;
		public Integer create() {
			return i++;
		}};
	
			
			
    
    Factory<String> vertexFactory = new Factory<String>() {
    	int i=0;
		public String create() {
			return "V"+i++;
		}};

    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer<String,Integer> vv;
    
    String root;

    public TreeLayoutDemo() {
        
        // create a simple graph for the demo
        graph = new SimpleSparseForest<String,Integer>(graphFactory, edgeFactory);

        createTree();
        
        Layout<String,Integer> layout = 
        	new TreeLayout<String,Integer>(graph, Arrays.asList("A0","V0","B0"));

        vv =  new VisualizationViewer<String,Integer>(layout, new Dimension(600,400));
        vv.setBackground(Color.white);
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
        
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        // add a listener for ToolTips
        vv.setVertexToolTipTransformer(new ToStringLabeller());
        
        Container content = getContentPane();
        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        content.add(panel);
        
        final PluggableGraphMouse graphMouse = new PluggableGraphMouse();
        graphMouse.add(new PickingGraphMousePlugin());
        graphMouse.add(new ScalingGraphMousePlugin(new ViewScalingControl(), InputEvent.CTRL_MASK));
        graphMouse.add(new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0));

        vv.setGraphMouse(graphMouse);
        
        final ScalingControl scaler = new CrossoverScalingControl();

        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1.1f, vv.getCenter());
            }
        });
        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1/1.1f, vv.getCenter());
            }
        });

        JPanel scaleGrid = new JPanel(new GridLayout(1,0));
        scaleGrid.setBorder(BorderFactory.createTitledBorder("Zoom"));

        JPanel controls = new JPanel();
        scaleGrid.add(plus);
        scaleGrid.add(minus);
        controls.add(scaleGrid);

        content.add(controls, BorderLayout.SOUTH);
    }
    
    /**
     * 
     */
    private void createTree() {
    	graph.addVertex("V0");
    	graph.addEdge(edgeFactory.create(), "V0", "V1");
    	graph.addEdge(edgeFactory.create(), "V0", "V2");
    	graph.addEdge(edgeFactory.create(), "V1", "V4");
    	graph.addEdge(edgeFactory.create(), "V2", "V3");
    	graph.addEdge(edgeFactory.create(), "V2", "V5");
    	graph.addEdge(edgeFactory.create(), "V4", "V6");
    	graph.addEdge(edgeFactory.create(), "V4", "V7");
    	graph.addEdge(edgeFactory.create(), "V3", "V8");
    	graph.addEdge(edgeFactory.create(), "V6", "V9");
    	graph.addEdge(edgeFactory.create(), "V4", "V10");
       	graph.addEdge(edgeFactory.create(), "V4", "V11");
       	graph.addEdge(edgeFactory.create(), "V4", "V12");
       	graph.addEdge(edgeFactory.create(), "V6", "V13");
       	graph.addEdge(edgeFactory.create(), "V10", "V14");
       	graph.addEdge(edgeFactory.create(), "V13", "V15");
       	graph.addEdge(edgeFactory.create(), "V13", "V16");
       	
       	graph.addVertex("A0");
       	
       	graph.addEdge(edgeFactory.create(), "A0", "A1");
       	graph.addEdge(edgeFactory.create(), "A0", "A2");
       	
       	graph.addVertex("B0");
    	graph.addEdge(edgeFactory.create(), "B0", "B1");
    	graph.addEdge(edgeFactory.create(), "B0", "B2");
    	graph.addEdge(edgeFactory.create(), "B1", "B4");
    	graph.addEdge(edgeFactory.create(), "B2", "B3");
    	graph.addEdge(edgeFactory.create(), "B2", "B5");
    	graph.addEdge(edgeFactory.create(), "B4", "B6");
    	graph.addEdge(edgeFactory.create(), "B4", "B7");
    	graph.addEdge(edgeFactory.create(), "B3", "B8");
    	graph.addEdge(edgeFactory.create(), "B6", "B9");
       	
//       	Thread t = new Thread() {
//       		public void run() {
//       			try {
//					Thread.sleep(5000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//       			graph.setRoot("V6");
//       			repaint();
//       		}
//       	};
//       	t.start();

    
    }


    /**
     * a driver for this demo
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Container content = frame.getContentPane();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        content.add(new TreeLayoutDemo());
        frame.pack();
        frame.setVisible(true);
    }
}
