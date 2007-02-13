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
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.graph.DirectedGraph;
import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.algorithms.layout.BalloonLayout;
import edu.uci.ics.jung.algorithms.layout.PolarPoint;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.SparseForest;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
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
public class BalloonLayoutDemo extends JApplet {

    /**
     * the graph
     */
    Graph<String,Integer> graph;
    
    Factory<DirectedGraph<String,Integer>> graphFactory = 
    	new Factory<DirectedGraph<String,Integer>>() {

			public DirectedGraph<String, Integer> create() {
				return new DirectedSparseGraph<String,Integer>();
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
    
    VisualizationServer.Paintable rings;
    
    String root;
    
    TreeLayout<String,Integer> layout;
    
    BalloonLayout<String,Integer> radialLayout;

    public BalloonLayoutDemo() {
        
        // create a simple graph for the demo
        graph = new SparseForest<String,Integer>(graphFactory, edgeFactory);

        createTree();
        
        layout = new TreeLayout<String,Integer>(graph);
        layout.setSize(new Dimension(900,900));
        radialLayout = new BalloonLayout<String,Integer>(graph);
        radialLayout.setSize(new Dimension(900,900));
        vv =  new VisualizationViewer<String,Integer>(layout, new Dimension(600,600));
        vv.setBackground(Color.white);
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        // add a listener for ToolTips
        vv.setVertexToolTipTransformer(new ToStringLabeller());
        vv.getRenderContext().setArrowFillPaintTransformer(new ConstantTransformer(Color.lightGray));
        rings = new Rings(radialLayout);

        Container content = getContentPane();
        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        content.add(panel);
        
        final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();

        vv.setGraphMouse(graphMouse);
        
        JComboBox modeBox = graphMouse.getModeComboBox();
        modeBox.addItemListener(graphMouse.getModeListener());
        graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);

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
        
        JToggleButton radial = new JToggleButton("Balloon");
        radial.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
//					layout.setRadial(true);
					vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
					vv.setGraphLayout(radialLayout);
					vv.addPreRenderPaintable(rings);
				} else {
//					layout.setRadial(false);
					vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
					vv.setGraphLayout(layout);
					vv.removePreRenderPaintable(rings);
				}
				vv.repaint();
			}});

        JPanel scaleGrid = new JPanel(new GridLayout(1,0));
        scaleGrid.setBorder(BorderFactory.createTitledBorder("Zoom"));

        JPanel controls = new JPanel();
        scaleGrid.add(plus);
        scaleGrid.add(minus);
        controls.add(radial);
        controls.add(scaleGrid);
        controls.add(modeBox);

        content.add(controls, BorderLayout.SOUTH);
    }
    
    
    
    class Rings implements VisualizationServer.Paintable {
    	
    	BalloonLayout<String,Integer> layout;
    	
    	public Rings(BalloonLayout<String,Integer> layout) {
    		this.layout = layout;
    	}
    	
		public void paint(Graphics g) {
			g.setColor(Color.lightGray);
		
			Graphics2D g2d = (Graphics2D)g;

			Ellipse2D ellipse = new Ellipse2D.Double();
			for(String v : layout.getGraph().getVertices()) {
				Double radius = layout.getRadii().get(v);
				if(radius == null) continue;
				Point2D p = layout.transform(v);
				ellipse.setFrameFromDiagonal(p.getX()-radius, p.getY()-radius, 
						p.getX()+radius, p.getY()+radius);
				Shape shape = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).transform(ellipse);
				g2d.draw(shape);
			}
		}

		public boolean useTransform() {
			return true;
		}
    }
    
    /**
     * 
     */
    private void createTree() {
    	
       	graph.addVertex("A0");
       	graph.addEdge(edgeFactory.create(), "A0", "B0");
       	graph.addEdge(edgeFactory.create(), "A0", "B1");
       	graph.addEdge(edgeFactory.create(), "A0", "B2");
//       	graph.addEdge(edgeFactory.create(), "A0", "B3");
//       	graph.addEdge(edgeFactory.create(), "A0", "B4");
//       	graph.addEdge(edgeFactory.create(), "A0", "B5");
//       	graph.addEdge(edgeFactory.create(), "A0", "B6");
//       	graph.addEdge(edgeFactory.create(), "A0", "B7");
//       	graph.addEdge(edgeFactory.create(), "A0", "B8");
//       	graph.addEdge(edgeFactory.create(), "A0", "B9");
//       	graph.addEdge(edgeFactory.create(), "A0", "B10");
//       	graph.addEdge(edgeFactory.create(), "A0", "B11");
       	
       	graph.addEdge(edgeFactory.create(), "B0", "C0");
       	graph.addEdge(edgeFactory.create(), "B0", "C1");
       	graph.addEdge(edgeFactory.create(), "B0", "C2");
       	graph.addEdge(edgeFactory.create(), "B0", "C3");

       	graph.addEdge(edgeFactory.create(), "C2", "H0");
       	graph.addEdge(edgeFactory.create(), "C2", "H1");
//       	
       	graph.addEdge(edgeFactory.create(), "B1", "D0");
       	graph.addEdge(edgeFactory.create(), "B1", "D1");
       	graph.addEdge(edgeFactory.create(), "B1", "D2");

       	graph.addEdge(edgeFactory.create(), "B2", "E0");
       	graph.addEdge(edgeFactory.create(), "B2", "E1");
       	graph.addEdge(edgeFactory.create(), "B2", "E2");
//
       	graph.addEdge(edgeFactory.create(), "D0", "F0");
       	graph.addEdge(edgeFactory.create(), "D0", "F1");
       	graph.addEdge(edgeFactory.create(), "D0", "F2");
       	
       	graph.addEdge(edgeFactory.create(), "D1", "G0");
       	graph.addEdge(edgeFactory.create(), "D1", "G1");
       	graph.addEdge(edgeFactory.create(), "D1", "G2");
       	graph.addEdge(edgeFactory.create(), "D1", "G3");
       	graph.addEdge(edgeFactory.create(), "D1", "G4");
       	graph.addEdge(edgeFactory.create(), "D1", "G5");
       	graph.addEdge(edgeFactory.create(), "D1", "G6");
       	graph.addEdge(edgeFactory.create(), "D1", "G7");
       	
       	
       	graph.addVertex("K0");
       	graph.addEdge(edgeFactory.create(), "K0", "K1");
       	graph.addEdge(edgeFactory.create(), "K0", "K2");
       	graph.addEdge(edgeFactory.create(), "K0", "K3");
       	
       	graph.addVertex("J0");
    	graph.addEdge(edgeFactory.create(), "J0", "J1");
    	graph.addEdge(edgeFactory.create(), "J0", "J2");
    	graph.addEdge(edgeFactory.create(), "J1", "J4");
    	graph.addEdge(edgeFactory.create(), "J2", "J3");
    	graph.addEdge(edgeFactory.create(), "J2", "J5");
    	graph.addEdge(edgeFactory.create(), "J4", "J6");
    	graph.addEdge(edgeFactory.create(), "J4", "J7");
    	graph.addEdge(edgeFactory.create(), "J3", "J8");
    	graph.addEdge(edgeFactory.create(), "J6", "B9");

       	
    }


    /**
     * a driver for this demo
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Container content = frame.getContentPane();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        content.add(new BalloonLayoutDemo());
        frame.pack();
        frame.setVisible(true);
    }
}
