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
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.collections15.functors.ConstantTransformer;
import org.apache.commons.collections15.map.LazyMap;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.Tree;
import edu.uci.ics.graph.util.EdgeType;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.algorithms.shortestpath.PrimMinimumSpanningTree;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.SparseTree;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.TestGraphs;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.MultiPickedState;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;

/**
 * Demonstrates a single graph with 2 layouts in 2 views.
 * They share picking, transforms, and a pluggable renderer
 * 
 * @author Tom Nelson
 * 
 */
public class MinimumSpanningTreeDemo extends JApplet {

     /**
     * the graph
     */
    Graph<String,Number> graph;
    Tree<String,Number> tree;

    /**
     * the visual components and renderers for the graph
     */
    VisualizationViewer<String,Number> vv0;
    VisualizationViewer<String,Number> vv1;
    VisualizationViewer<String,Number> vv2;
    
    /**
     * the normal transformer
     */
    MutableTransformer layoutTransformer;
    
    Dimension preferredSize = new Dimension(300,300);
    
    /**
     * create an instance of a simple graph in two views with controls to
     * demo the zoom features.
     * 
     */
    public MinimumSpanningTreeDemo() {
        
        // create a simple graph for the demo
        // both models will share one graph
        graph = 
        	new UndirectedSparseGraph<String, Number>();
        String[] v = createVertices(10);
        createEdges(v);
        
        PrimMinimumSpanningTree<String,Number> prim = new PrimMinimumSpanningTree<String,Number>(graph, 
        		new SparseTree<String,Number>(new DirectedSparseGraph<String,Number>(), null),
        		"V0",
        		LazyMap.decorate(new HashMap<Number,Double>(), new ConstantTransformer(1.0)));
        
        tree = prim.getTree();
        
        // create two layouts for the one graph, one layout for each model
        Layout<String,Number> layout0 = new KKLayout<String,Number>(graph);
        Layout<String,Number> layout1 = new TreeLayout<String,Number>(tree);
        Layout<String,Number> layout2 = new StaticLayout<String,Number>(graph, layout1, preferredSize);

        // create the two models, each with a different layout
        VisualizationModel<String,Number> vm0 =
            new DefaultVisualizationModel<String,Number>(layout0, preferredSize);
        VisualizationModel<String,Number> vm1 =
            new DefaultVisualizationModel<String,Number>(layout1, preferredSize);
        VisualizationModel<String,Number> vm2 = 
            new DefaultVisualizationModel<String,Number>(layout2, preferredSize);

        // create the two views, one for each model
        // they share the same renderer
        vv0 = new VisualizationViewer<String,Number>(vm0, preferredSize);
        vv1 = new VisualizationViewer<String,Number>(vm1, preferredSize);
        vv2 = new VisualizationViewer<String,Number>(vm2, preferredSize);
        vv1.setRenderContext(vv2.getRenderContext());
        
        vv0.getRenderContext().setMultiLayerTransformer(vv1.getRenderContext().getMultiLayerTransformer());
        vv0.getRenderContext().getMultiLayerTransformer().addChangeListener(vv1);
        vv2.getRenderContext().setMultiLayerTransformer(vv1.getRenderContext().getMultiLayerTransformer());
        vv2.getRenderContext().getMultiLayerTransformer().addChangeListener(vv1);
        vv2.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
        
        vv0.addChangeListener(vv1);
        vv1.addChangeListener(vv2);
//        vv2.addChangeListener(vv0);
        
        vv0.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv2.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
//        vv1.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
        vv0.setBackground(Color.white);
        vv1.setBackground(Color.white);
        vv2.setBackground(Color.white);
        
        vv0.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        vv0.setForeground(Color.lightGray);
        vv1.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        vv1.setForeground(Color.lightGray);
        vv2.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        vv2.setForeground(Color.lightGray);


        
        // share one PickedState between the two views
        PickedState<String> ps = new MultiPickedState<String>();
        vv0.setPickedVertexState(ps);
        vv1.setPickedVertexState(ps);
        vv2.setPickedVertexState(ps);
        PickedState<Number> pes = new MultiPickedState<Number>();
        vv0.setPickedEdgeState(pes);
        vv1.setPickedEdgeState(pes);
        vv2.setPickedEdgeState(pes);
        
        // set an edge paint function that will show picking for edges
        vv0.getRenderContext().setEdgeDrawPaintTransformer(new PickableEdgePaintTransformer<String,Number>(vv0.getPickedEdgeState(), Color.black, Color.red));
        vv0.getRenderContext().setVertexFillPaintTransformer(new PickableVertexPaintTransformer<String>(vv0.getPickedVertexState(),
                Color.red, Color.yellow));
        vv1.getRenderContext().setEdgeDrawPaintTransformer(new PickableEdgePaintTransformer<String,Number>(vv1.getPickedEdgeState(), Color.black, Color.red));
        vv1.getRenderContext().setVertexFillPaintTransformer(new PickableVertexPaintTransformer<String>(vv1.getPickedVertexState(),
                Color.red, Color.yellow));
        // add default listeners for ToolTips
        vv0.setVertexToolTipTransformer(new ToStringLabeller());
        vv1.setVertexToolTipTransformer(new ToStringLabeller());
        vv2.setVertexToolTipTransformer(new ToStringLabeller());
        
        Container content = getContentPane();
        JPanel panel = new JPanel(new GridLayout(1,0));
        panel.add(new GraphZoomScrollPane(vv0));
        panel.add(new GraphZoomScrollPane(vv1));
        panel.add(new GraphZoomScrollPane(vv2));

        content.add(panel);
        
        // create a GraphMouse for each view
        DefaultModalGraphMouse gm0 = new DefaultModalGraphMouse();
        DefaultModalGraphMouse gm1 = new DefaultModalGraphMouse();
        DefaultModalGraphMouse gm2 = new DefaultModalGraphMouse();

        vv0.setGraphMouse(gm0);
        vv1.setGraphMouse(gm1);
        vv2.setGraphMouse(gm2);

        // create zoom buttons for scaling the transformer that is
        // shared between the two models.
        final ScalingControl scaler = new CrossoverScalingControl();

        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv1, 1.1f, vv1.getCenter());
            }
        });
        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv1, 1/1.1f, vv1.getCenter());
            }
        });
        
        JPanel zoomPanel = new JPanel(new GridLayout(1,2));
        zoomPanel.setBorder(BorderFactory.createTitledBorder("Zoom"));
        
        JPanel modePanel = new JPanel();
        modePanel.setBorder(BorderFactory.createTitledBorder("Mouse Mode"));
        gm1.getModeComboBox().addItemListener(gm2.getModeListener());
        gm1.getModeComboBox().addItemListener(gm0.getModeListener());
        modePanel.add(gm1.getModeComboBox());

        JPanel controls = new JPanel();
        zoomPanel.add(plus);
        zoomPanel.add(minus);
        controls.add(zoomPanel);
        controls.add(modePanel);
        content.add(controls, BorderLayout.SOUTH);
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
    	int i=0;
        graph.addEdge(i++, v[0], v[1]);
        graph.addEdge(i++, v[0], v[3]);
        graph.addEdge(i++, v[0], v[4]);
        graph.addEdge(i++, v[4], v[5]);
        graph.addEdge(i++, v[3], v[5]);
        graph.addEdge(i++, v[1], v[2]);
        graph.addEdge(i++, v[1], v[4]);
        graph.addEdge(i++, v[8], v[2]);
        graph.addEdge(i++, v[3], v[8]);
        graph.addEdge(i++, v[6], v[7]);
        graph.addEdge(i++, v[5], v[7]);
        graph.addEdge(i++, v[0], v[9]);
        graph.addEdge(i++, v[9], v[8]);
        graph.addEdge(i++, v[7], v[6]);
        graph.addEdge(i++, v[6], v[5]);
        graph.addEdge(i++, v[4], v[2]);
        graph.addEdge(i++, v[5], v[4]);
    }

    /**
     * a driver for this demo
     */
    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new MinimumSpanningTreeDemo());
        f.pack();
        f.setVisible(true);
    }
}
