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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JRootPane;

import edu.uci.ics.graph.Graph;
import edu.uci.ics.graph.Graphs;
import edu.uci.ics.jung.graph.SimpleDirectedSparseGraph;
import edu.uci.ics.jung.visualization.Renderer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.FRLayout;
import edu.uci.ics.jung.visualization.layout.Layout;
import edu.uci.ics.jung.visualization.layout.Relaxer;
import edu.uci.ics.jung.visualization.layout.SpringLayout;
import edu.uci.ics.jung.visualization.layout.SpringLayout.LengthFunction;

/*
 * Created on May 10, 2004
 */

/**
 * Thanks to Brad Allen for an original inspiration for this.
 * 
 * @author danyelf
 */
public class AddNodeDemo extends javax.swing.JApplet {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5345319851341875800L;

	private Graph<Number,Number> g = null;

    private VisualizationViewer<Number,Number> vv = null;

    private Layout<Number,Number> layout = null;

    Timer timer;
    
    boolean done;

    protected JButton switchLayout;

    public static final LengthFunction<Number> UNITLENGTHFUNCTION = new SpringLayout.UnitLengthFunction<Number>(
            100);

    public void init() {

        //create a graph
        g = Graphs.<Number,Number>synchronizedDirectedGraph(new SimpleDirectedSparseGraph<Number,Number>());

        //create a graphdraw
        layout = new FRLayout<Number,Number>(g);
        
        vv = new VisualizationViewer<Number,Number>(layout, new Dimension(600,600));

        JRootPane rp = this.getRootPane();
        rp.putClientProperty("defeatSystemEventQueueCheck", Boolean.TRUE);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(java.awt.Color.lightGray);
        getContentPane().setFont(new Font("Serif", Font.PLAIN, 12));

        vv.getModel().getRelaxer().setSleepTime(500);
        vv.setGraphMouse(new DefaultModalGraphMouse());
        
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        vv.getRenderContext().setVertexStringer(new ToStringLabeller<Number>());
        vv.setForeground(Color.white);
        getContentPane().add(vv);
        switchLayout = new JButton("Switch to SpringLayout");
        switchLayout.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
            	Dimension d = new Dimension(600,600);
                if (switchLayout.getText().indexOf("Spring") > 0) {
                    switchLayout.setText("Switch to FRLayout");
                    layout = new SpringLayout<Number,Number>(g, UNITLENGTHFUNCTION);
                    layout.setSize(d);
                    vv.getModel().setGraphLayout(layout, d);
                } else {
                    switchLayout.setText("Switch to SpringLayout");
                    layout = new FRLayout<Number,Number>(g, d);
                    vv.getModel().setGraphLayout(layout, d);
                }
            }
        });

        getContentPane().add(switchLayout, BorderLayout.SOUTH);

        timer = new Timer();
    }

    public void start() {
        validate();
        //set timer so applet will change
        timer.schedule(new RemindTask(), 1000, 1000); //subsequent rate
        vv.repaint();
    }

    Integer v_prev = null;

    public void process() {

        try {

            if (g.getVertices().size() < 100) {

                //add a vertex
                Integer v1 = new Integer(g.getVertices().size());

                Relaxer relaxer = vv.getModel().getRelaxer();
                relaxer.pause();
                g.addVertex(v1);
                System.err.println("added node " + v1);

                // wire it to some edges
                if (v_prev != null) {
                    g.addEdge(g.getEdges().size(), v_prev, v1);
                    // let's connect to a random vertex, too!
                    int rand = (int) (Math.random() * g.getVertices().size());
                    g.addEdge(g.getEdges().size(), v1, rand);
                }

                v_prev = v1;

                layout.reset();
                relaxer.resume();

            } else {
            	done = true;
            }

        } catch (Exception e) {
            System.out.println(e);

        }
    }

    class RemindTask extends TimerTask {

        public void run() {
            process();
            if(done) cancel();

        }
    }
    
    public static void main(String[] args) {
    	AddNodeDemo and = new AddNodeDemo();
    	JFrame frame = new JFrame();
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.getContentPane().add(and);

    	and.init();
    	and.start();
    	frame.pack();
    	frame.setVisible(true);
    }
}