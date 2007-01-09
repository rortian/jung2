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
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.TransformerUtils;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.SimpleSparseGraph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.EditingPopupGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

/**
 * Shows how easy it is to create a graph editor with JUNG.
 * Mouse modes and actions are explained in the help text.
 * The application version of GraphEditorDemo provides a
 * File menu with an option to save the visible graph as
 * a jpeg file.
 * 
 * @author Tom Nelson
 * 
 */
public class GraphEditorDemo extends JApplet implements Printable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2023243689258876709L;

	/**
     * the graph
     */
    SimpleSparseGraph<Number,Number> graph;
    
    AbstractLayout<Number,Number> layout;

    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer<Number,Number> vv;
    
    Map<Number,Point2D> vertexLocations = new HashMap<Number,Point2D>();
    
    String instructions =
        "<html>"+
        "<h3>All Modes:</h3>"+
        "<ul>"+
        "<li>Right-click an empty area for <b>Create Vertex</b> popup"+
        "<li>Right-click on a Vertex for <b>Delete Vertex</b> popup"+
        "<li>Right-click on a Vertex for <b>Add Edges</b> menus <br>(if there are selected Vertices)"+
        "<li>Right-click on an Edges for <b>Delete Edges</b> popup"+
        "<li>Mousewheel scales with a crossover value of 1.0.<p>"+
        "     - scales the graph layout when the combined scale is greater than 1<p>"+
        "     - scales the graph view when the combined scale is less than 1"+

        "</ul>"+
        "<h3>Editing Mode:</h3>"+
        "<ul>"+
        "<li>Left-click an empty area to create a new Vertex"+
        "<li>Left-click on a Vertex and drag to another Vertex to create an Undirected Edges"+
        "<li>Shift+Left-click on a Vertex and drag to another Vertex to create a Directed Edges"+
        "</ul>"+
        "<h3>Picking Mode:</h3>"+
        "<ul>"+
        "<li>Mouse1 on a Vertex selects the vertex"+
        "<li>Mouse1 elsewhere unselects all Vertices"+
        "<li>Mouse1+Shift on a Vertex adds/removes Vertex selection"+
        "<li>Mouse1+drag on a Vertex moves all selected Vertices"+
        "<li>Mouse1+drag elsewhere selects Vertices in a region"+
        "<li>Mouse1+Shift+drag adds selection of Vertices in a new region"+
        "<li>Mouse1+CTRL on a Vertex selects the vertex and centers the display on it"+
        "</ul>"+
        "<h3>Transforming Mode:</h3>"+
        "<ul>"+
        "<li>Mouse1+drag pans the graph"+
        "<li>Mouse1+Shift+drag rotates the graph"+
        "<li>Mouse1+CTRL(or Command)+drag shears the graph"+
        "</ul>"+
        "</html>";
    
    /**
     * create an instance of a simple graph with popup controls to
     * create a graph.
     * 
     */
    public GraphEditorDemo() {
        
        // allows the precise setting of initial vertex locations
//        vertexLocations = new DefaultSettableVertexLocationFunction<Number>();
        
        // create a simple graph for the demo
        graph = new SimpleSparseGraph<Number,Number>();

        this.layout = new StaticLayout<Number,Number>(graph, 
        	TransformerUtils.mapTransformer(vertexLocations),
        	new Dimension(600,600));
        
        vv =  new VisualizationViewer<Number,Number>(layout);
        vv.setBackground(Color.white);

        vv.getRenderContext().setVertexStringer(new Transformer<Number, String>() {

            public String transform(Number v) {
                return v.toString();
            }});

        vv.setVertexToolTipTransformer(new ToStringLabeller());

        Container content = getContentPane();
        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        content.add(panel);
        Factory<Number> vertexFactory = new VertexFactory(graph);
        Factory<Number> edgeFactory = new EdgeFactory(graph);
        
        final EditingModalGraphMouse<Number,Number> graphMouse = 
        	new EditingModalGraphMouse<Number,Number>(vertexFactory, edgeFactory);
        
        // the EditingGraphMouse will pass mouse event coordinates to the
        // vertexLocations function to set the locations of the vertices as
        // they are created
        graphMouse.setVertexLocations(vertexLocations);
        vv.setGraphMouse(graphMouse);
        vv.addKeyListener(graphMouse.getModeKeyListener());

        graphMouse.add(new EditingPopupGraphMousePlugin<Number,Number>(vertexLocations, vertexFactory, edgeFactory));
        graphMouse.setMode(ModalGraphMouse.Mode.EDITING);
        
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
        
        JButton help = new JButton("Help");
        help.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(vv, instructions);
            }});

        JPanel controls = new JPanel();
        controls.add(plus);
        controls.add(minus);
        JComboBox modeBox = graphMouse.getModeComboBox();
        controls.add(modeBox);
        controls.add(help);
        content.add(controls, BorderLayout.SOUTH);
    }
    
    /**
     * copy the visible part of the graph to a file as a jpeg image
     * @param file
     */
    public void writeJPEGImage(File file) {
        int width = vv.getWidth();
        int height = vv.getHeight();

        BufferedImage bi = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bi.createGraphics();
        vv.paint(graphics);
        graphics.dispose();
        
        try {
            ImageIO.write(bi, "jpeg", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public int print(java.awt.Graphics graphics,
            java.awt.print.PageFormat pageFormat, int pageIndex)
            throws java.awt.print.PrinterException {
        if (pageIndex > 0) {
            return (Printable.NO_SUCH_PAGE);
        } else {
            java.awt.Graphics2D g2d = (java.awt.Graphics2D) graphics;
            vv.setDoubleBuffered(false);
            g2d.translate(pageFormat.getImageableX(), pageFormat
                    .getImageableY());

            vv.paint(g2d);
            vv.setDoubleBuffered(true);

            return (Printable.PAGE_EXISTS);
        }
    }
    
    class VertexFactory implements Factory<Number> {

    	Graph<Number,Number> graph;
    	
    	public VertexFactory(Graph<Number,Number> graph) {
    		this.graph = graph;
    	}
		public Number create() {
			return graph.getVertices().size();
		}
    }
    
    class EdgeFactory implements Factory<Number> {

    	Graph<Number,Number> graph;
    	
    	public EdgeFactory(Graph<Number,Number> graph) {
    		this.graph = graph;
    	}
		public Number create() {
			return graph.getEdges().size();
		}
    }

    /**
     * a driver for this demo
     */
    @SuppressWarnings("serial")
	public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final GraphEditorDemo demo = new GraphEditorDemo();
        
        JMenu menu = new JMenu("File");
        menu.add(new AbstractAction("Make Image") {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser  = new JFileChooser();
                int option = chooser.showSaveDialog(demo);
                if(option == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    demo.writeJPEGImage(file);
                }
            }});
        menu.add(new AbstractAction("Print") {
            public void actionPerformed(ActionEvent e) {
                    PrinterJob printJob = PrinterJob.getPrinterJob();
                    printJob.setPrintable(demo);
                    if (printJob.printDialog()) {
                        try {
                            printJob.print();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
            }});
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(demo);
        frame.pack();
        frame.setVisible(true);
    }
}

