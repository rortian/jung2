/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 */
package samples.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.io.InputStream;

import org.apache.commons.collections15.Transformer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.DefaultEdgeLabelRenderer;
import edu.uci.ics.jung.visualization.DefaultVertexLabelRenderer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.graphics.Image;
import edu.uci.ics.jung.visualization.swt.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.swt.VisualizationComposite;
import edu.uci.ics.jung.visualization.swt.graphics.SWTImageImpl;

/**
 * Demonstrates the use of images on graph edge labels.
 * 
 * @author Tom Nelson
 * 
 */
public class SWTImageEdgeLabelDemo extends Composite {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4332663871914930864L;
	
	private static final int VERTEX_COUNT=11;

	/**
     * the graph
     */
    DirectedSparseMultigraph<Number, Number> graph;

    /**
     * the visual component and renderer for the graph
     */
    VisualizationComposite<Number, Number> vv;
    
    public SWTImageEdgeLabelDemo(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(4, false));
        
        // create a simple graph for the demo
        graph = new DirectedSparseMultigraph<Number,Number>();
        createGraph(VERTEX_COUNT);
        
        FRLayout<Number, Number> layout = new FRLayout<Number, Number>(graph);
        layout.setMaxIterations(100);
        final GraphZoomScrollPane<Number,Number> panel = new GraphZoomScrollPane<Number,Number>(this, SWT.NONE, layout, new Dimension(400,400));
		GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.horizontalSpan = 4;
        panel.setLayoutData(gridData);
		
		vv =  panel.vv;
        
        vv.getRenderContext().setEdgeDrawPaintTransformer(new PickableEdgePaintTransformer<Number, Number>(vv.getServer().getPickedEdgeState(), Color.black, Color.cyan));

        vv.setBackground(Color.white);
        
        vv.getRenderContext().setVertexLabelRenderer(new DefaultVertexLabelRenderer(Color.cyan));
        vv.getRenderContext().setEdgeLabelRenderer(new DefaultEdgeLabelRenderer(Color.cyan));
        
        ImageLoader loader = new ImageLoader();
        InputStream is = getClass().getResourceAsStream("/images/lightning-s.gif");
        ImageData[] idataa = loader.load(is);
        ImageData idata = idataa[0];
        org.eclipse.swt.graphics.Image _image = new org.eclipse.swt.graphics.Image(getDisplay(), idata);
        final SWTImageImpl image = new SWTImageImpl(_image);
        vv.getRenderContext().setEdgeLabelImageTransformer(new Transformer<Number,Image>() {
			public Image transform(Number input) {
				return image;
			}});
        
        // add a listener for ToolTips
        vv.setVertexToolTipTransformer(new ToStringLabeller<Number>());
        vv.setEdgeToolTipTransformer(new ToStringLabeller<Number>());

        
        final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();
        vv.setGraphMouse(graphMouse);
        vv.addKeyListener(graphMouse.getModeKeyListener());
        final ScalingControl scaler = new CrossoverScalingControl();

        GridData fillerLeftGD = new GridData();
        fillerLeftGD.grabExcessHorizontalSpace = true;
        Label fillerLeft = new Label(this, SWT.NONE);
        fillerLeft.setLayoutData(fillerLeftGD);
        
        SWTUtils.createHorizontalZoomControls(this, scaler, vv);

        SWTUtils.createSimpleMouseControl(this, graphMouse, vv);
        
        GridData fillerRightGD = new GridData();
        fillerRightGD.grabExcessHorizontalSpace = true;
        Label fillerRight = new Label(this, SWT.NONE);
        fillerRight.setLayoutData(fillerRightGD);
    }
    
    /**
     * create some vertices
     * @param count how many to create
     * @return the Vertices in an array
     */
    private void createGraph(int vertexCount) {
        for (int i = 0; i < vertexCount; i++) {
            graph.addVertex(i);
        }
    	int j=0;
        graph.addEdge(j++, 0, 1, EdgeType.DIRECTED);
        graph.addEdge(j++, 3, 0, EdgeType.DIRECTED);
        graph.addEdge(j++, 0, 4, EdgeType.DIRECTED);
        graph.addEdge(j++, 4, 5, EdgeType.DIRECTED);
        graph.addEdge(j++, 5, 3, EdgeType.DIRECTED);
        graph.addEdge(j++, 2, 1, EdgeType.DIRECTED);
        graph.addEdge(j++, 4, 1, EdgeType.DIRECTED);
        graph.addEdge(j++, 8, 2, EdgeType.DIRECTED);
        graph.addEdge(j++, 3, 8, EdgeType.DIRECTED);
        graph.addEdge(j++, 6, 7, EdgeType.DIRECTED);
        graph.addEdge(j++, 7, 5, EdgeType.DIRECTED);
        graph.addEdge(j++, 0, 9, EdgeType.DIRECTED);
        graph.addEdge(j++, 9, 8, EdgeType.DIRECTED);
        graph.addEdge(j++, 7, 6, EdgeType.DIRECTED);
        graph.addEdge(j++, 6, 5, EdgeType.DIRECTED);
        graph.addEdge(j++, 4, 2, EdgeType.DIRECTED);
        graph.addEdge(j++, 5, 4, EdgeType.DIRECTED);
        graph.addEdge(j++, 4, 10, EdgeType.DIRECTED);
        graph.addEdge(j++, 10, 4, EdgeType.DIRECTED);
    }

    /**
	 * a driver for this demo
	 */
    public static void main(String[] args) {
    	Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("SWT ImageEdgeLabelDemo");
		shell.setLayout(new FillLayout());

        new SWTImageEdgeLabelDemo(shell, SWT.NONE);

        shell.open ();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
    }
}
