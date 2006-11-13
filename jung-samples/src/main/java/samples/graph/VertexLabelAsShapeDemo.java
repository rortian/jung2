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

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ChainedTransformer;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.graph.TestGraphs;
import edu.uci.ics.jung.visualization.BasicRenderer;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Renderer;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.DefaultToolTipFunction;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.FRLayout;
import edu.uci.ics.jung.visualization.layout.Layout;
import edu.uci.ics.jung.visualization.renderers.VertexLabelAsShapeRenderer;


/**
 * 
 * @author Tom Nelson - RABA Technologies
 * 
 */
public class VertexLabelAsShapeDemo extends JApplet {

    /**
     * the graph
     */
    Graph<String,Number> graph;

    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer<String,Number> vv;
    
    Layout<String,Number> layout;
    
    /**
     * create an instance of a simple graph with controls to
     * demo the zoomand hyperbolic features.
     * 
     */
    public VertexLabelAsShapeDemo() {
        
        // create a simple graph for the demo
        graph = 
        TestGraphs.getOneComponentGraph();
        
        BasicRenderer<String,Number> pr = new BasicRenderer<String,Number>();
        layout = new FRLayout<String,Number>(graph);

        Dimension preferredSize = new Dimension(400,400);
        final VisualizationModel<String,Number> visualizationModel = 
            new DefaultVisualizationModel<String,Number>(layout, preferredSize);
        vv =  new VisualizationViewer<String,Number>(visualizationModel, preferredSize);
        
        VertexLabelAsShapeRenderer<String,Number> vlasr = new VertexLabelAsShapeRenderer<String,Number>();
        vv.getRenderContext().setVertexStringer(
        		new ChainedTransformer<String,String>(new Transformer[]{
        		new ToStringLabeller<String>(),
        		new Transformer<String,String>() {

					public String transform(String input) {
						return "<html><center>Vertex<p>"+input;
					}}}));

        vv.getRenderContext().setVertexShapeFunction(vlasr);
        pr.setVertexRenderer(new Renderer.Vertex.NOOP());
        pr.setVertexLabelRenderer(vlasr);
        vv.setRenderer(pr);

        vv.setBackground(Color.white);
        
        // add a listener for ToolTips
        vv.setToolTipFunction(new DefaultToolTipFunction());
        
        /**
         * the regular graph mouse for the normal view
         */
        final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();

        vv.setGraphMouse(graphMouse);
        
        Container content = getContentPane();
        GraphZoomScrollPane gzsp = new GraphZoomScrollPane(vv);
        content.add(gzsp);
        
        JComboBox modeBox = graphMouse.getModeComboBox();
        modeBox.addItemListener(graphMouse.getModeListener());
        graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
        
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
        
        JPanel controls = new JPanel();
        JPanel zoomControls = new JPanel(new GridLayout(2,1));
        zoomControls.setBorder(BorderFactory.createTitledBorder("Zoom"));
        zoomControls.add(plus);
        zoomControls.add(minus);
        controls.add(zoomControls);
        controls.add(modeBox);
        content.add(controls, BorderLayout.SOUTH);
    }
    
    /**
     * a driver for this demo
     */
    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new VertexLabelAsShapeDemo());
        f.pack();
        f.setVisible(true);
    }
}


