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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractModalGraphMouse;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.GradientVertexRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.renderers.BasicVertexLabelRenderer.InsidePositioner;


/**
 * Shows a graph on a world map image
 * scaling of the graph also scales the image background
 * @author Tom Nelson
 * 
 */
public class WorldMapGraphDemo {

    /**
     * the graph
     */
    DirectedSparseGraph<String, Number> graph;

    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer<String, Number> vv;
    
    /**
     * create an instance of a simple graph with controls to
     * demo the zoom features.
     * 
     */
    public WorldMapGraphDemo() {
        
        // create a simple graph for the demo
        graph = new DirectedSparseGraph<String, Number>();
        String[] v = createVertices(10);
        createEdges(v);
        
        ImageIcon mapIcon = null;
        String imageLocation = "/images/political_world_map.jpg";
        try {
            mapIcon = 
            	    new ImageIcon(getClass().getResource(imageLocation));
        } catch(Exception ex) {
            System.err.println("Can't load \""+imageLocation+"\"");
        }
        final ImageIcon icon = mapIcon;

        Dimension layoutSize = new Dimension(2000,1000);
        Layout<String,Number> layout = new FRLayout(graph);
        layout.setSize(layoutSize);
        vv =  new VisualizationViewer<String,Number>(layout,
        		new Dimension(800,400));
        
        if(icon != null) {
            vv.addPreRenderPaintable(new VisualizationViewer.Paintable(){
                public void paint(Graphics g) {
                	Graphics2D g2d = (Graphics2D)g;
                	AffineTransform oldXform = g2d.getTransform();
                    Dimension d = vv.getSize();
                    AffineTransform lat = 
                    	vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).getTransform();
                    AffineTransform vat = 
                    	vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getTransform();
                    AffineTransform at = new AffineTransform();
                    at.concatenate(vat);
                    at.concatenate(lat);
                    g2d.setTransform(at);
                    g.drawImage(icon.getImage(),0,0,icon.getIconWidth(),icon.getIconHeight(),vv);
                    g2d.setTransform(oldXform);
                }
                public boolean useTransform() { return false; }
            });
        }

        vv.getRenderer().setVertexRenderer(
        		new GradientVertexRenderer<String,Number>(
        				Color.white, Color.red, 
        				Color.white, Color.blue,
        				vv.getPickedVertexState(),
        				false));
        
        // add my listeners for ToolTips
        vv.setVertexToolTipTransformer(new ToStringLabeller());
        vv.setEdgeToolTipTransformer(new Transformer<Number,String>() {
			public String transform(Number edge) {
				return "E"+graph.getEndpoints(edge).toString();
			}});
        
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderer().getVertexLabelRenderer().setPositioner(new InsidePositioner());
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.AUTO);
        vv.setForeground(Color.lightGray);
        
        // create a frome to hold the graph
        final JFrame frame = new JFrame();
        Container content = frame.getContentPane();
        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        content.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final AbstractModalGraphMouse graphMouse = new DefaultModalGraphMouse();
        vv.setGraphMouse(graphMouse);
        
        vv.addKeyListener(graphMouse.getModeKeyListener());
        vv.setToolTipText("<html><center>Type 'p' for Pick mode<p>Type 't' for Transform mode");
        
        final ScalingControl scaler = new CrossoverScalingControl();
        
        vv.scaleToLayout(scaler);


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

        JButton reset = new JButton("reset");
        reset.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).setToIdentity();
				vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).setToIdentity();
			}});

        JPanel controls = new JPanel();
        controls.add(plus);
        controls.add(minus);
        controls.add(reset);
        content.add(controls, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }
    
    /**
     * create some vertices
     * @param count how many to create
     * @return the Vertices in an array
     */
    private String[] createVertices(int count) {
        String[] v = new String[count];
        for (int i = 0; i < count; i++) {
        	v[i] = "V"+i;
            graph.addVertex(v[i]);
        }
        return v;
    }

    /**
     * create edges for this demo graph
     * @param v an array of Vertices to connect
     */
    void createEdges(String[] v) {
        graph.addEdge(new Double(Math.random()), v[0], v[1], EdgeType.DIRECTED);
        graph.addEdge(new Double(Math.random()), v[0], v[3], EdgeType.DIRECTED);
        graph.addEdge(new Double(Math.random()), v[0], v[4], EdgeType.DIRECTED);
        graph.addEdge(new Double(Math.random()), v[4], v[5], EdgeType.DIRECTED);
        graph.addEdge(new Double(Math.random()), v[3], v[5], EdgeType.DIRECTED);
        graph.addEdge(new Double(Math.random()), v[1], v[2], EdgeType.DIRECTED);
        graph.addEdge(new Double(Math.random()), v[1], v[4], EdgeType.DIRECTED);
        graph.addEdge(new Double(Math.random()), v[8], v[2], EdgeType.DIRECTED);
        graph.addEdge(new Double(Math.random()), v[3], v[8], EdgeType.DIRECTED);
        graph.addEdge(new Double(Math.random()), v[6], v[7], EdgeType.DIRECTED);
        graph.addEdge(new Double(Math.random()), v[7], v[5], EdgeType.DIRECTED);
        graph.addEdge(new Double(Math.random()), v[0], v[9], EdgeType.DIRECTED);
        graph.addEdge(new Double(Math.random()), v[9], v[8], EdgeType.DIRECTED);
        graph.addEdge(new Double(Math.random()), v[7], v[6], EdgeType.DIRECTED);
        graph.addEdge(new Double(Math.random()), v[6], v[5], EdgeType.DIRECTED);
        graph.addEdge(new Double(Math.random()), v[4], v[2], EdgeType.DIRECTED);
        graph.addEdge(new Double(Math.random()), v[5], v[4], EdgeType.DIRECTED);
    }

    /**
     * a driver for this demo
     */
    public static void main(String[] args) 
    {
        new WorldMapGraphDemo();
    }
}
