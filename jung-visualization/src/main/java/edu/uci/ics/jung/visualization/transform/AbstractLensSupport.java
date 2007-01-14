/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Jul 21, 2005
 */

package edu.uci.ics.jung.visualization.transform;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.VisualizationServer.Paintable;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
/**
 * A class to make it easy to add an
 * examining lens to a jung graph application. See HyperbolicTransformerDemo,
 * ViewLensSupport and LayoutLensSupport
 * for examples of how to use it.
 * 
 * @author Tom Nelson
 *
 *
 */
public abstract class AbstractLensSupport<V,E> implements LensSupport {

    protected VisualizationViewer<V,E> vv;
    protected VisualizationViewer.GraphMouse graphMouse;
    protected LensTransformer lensTransformer;
    protected ModalGraphMouse lensGraphMouse;
    protected Lens lens;
    protected LensControls lensControls;
    protected String defaultToolTipText;

    protected static final String instructions = 
        "<html><center>Mouse-Drag the Lens center to move it<p>"+
        "Mouse-Drag the Lens edge to resize it<p>"+
        "Ctrl+MouseWheel to change magnification</center></html>";
    
    /**
     * create the base class, setting common members and creating
     * a custom GraphMouse
     * @param vv the VisualizationViewer to work on
     */
    public AbstractLensSupport(VisualizationViewer<V,E> vv, ModalGraphMouse lensGraphMouse) {
        this.vv = vv;
        this.graphMouse = vv.getGraphMouse();
        this.defaultToolTipText = vv.getToolTipText();

        this.lensGraphMouse = lensGraphMouse;//new ModalLensGraphMouse();
    }

    public void activate(boolean state) {
        if(state) activate();
        else deactivate();
    }
    
    public LensTransformer getLensTransformer() {
        return lensTransformer;
    }

    /**
     * @return Returns the hyperbolicGraphMouse.
     */
    public ModalGraphMouse getGraphMouse() {
        return lensGraphMouse;
    }

    /**
     * the background for the hyperbolic projection
     * @author Tom Nelson - RABA Technologies
     *
     *
     */
    public static class Lens implements Paintable {
        LensTransformer lensTransformer;
        Ellipse2D ellipse;
        
        public Lens(LensTransformer lensTransformer) {
            this.lensTransformer = lensTransformer;
            this.ellipse = lensTransformer.getEllipse();
        }
        
        /**
         * @return Returns the hyperbolicTransformer.
         */

        public void paint(Graphics g) {
            
            Graphics2D g2d = (Graphics2D)g;
            g.setColor(Color.decode("0xdddddd"));
            g2d.fill(ellipse);
        }

        public boolean useTransform() {
            return true;
        }
    }
    
    /**
     * the background for the hyperbolic projection
     * @author Tom Nelson - RABA Technologies
     *
     *
     */
    public static class LensControls  implements Paintable {
        LensTransformer lensTransformer;
        Ellipse2D ellipse;
        
        public LensControls(LensTransformer lensTransformer) {
            this.lensTransformer = lensTransformer;
            this.ellipse = lensTransformer.getEllipse();
        }
        
        /**
         * @return Returns the hyperbolicTransformer.
         */

        public void paint(Graphics g) {
            
            Graphics2D g2d = (Graphics2D)g;
            g.setColor(Color.gray);
            g2d.draw(ellipse);
            int centerX = (int)Math.round(ellipse.getCenterX());
            int centerY = (int)Math.round(ellipse.getCenterY());
//            g.setColor(Color.darkGray);
            g.drawOval(centerX-10, centerY-10, 20, 20);
//            g.setColor(Color.gray);
//            g.drawOval(centerX-9, centerY-9, 18, 18);
//            g.setColor(Color.white);
//            g.drawOval(centerX-8, centerY-8, 16, 16);
//            g.setColor(Color.gray);
//            g.drawOval(centerX-7, centerY-7, 14, 14);
//            g.setColor(Color.darkGray);
//            g.drawOval(centerX-6, centerY-6, 12, 12);
//            g.setColor(Color.gray);
//            g.fill3DRect(centerX-4, centerY-11, 8, 8, true);
        }

        public boolean useTransform() {
            return true;
        }
    }

}
