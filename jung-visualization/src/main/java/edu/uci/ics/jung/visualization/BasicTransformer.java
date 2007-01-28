package edu.uci.ics.jung.visualization;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.uci.ics.jung.visualization.transform.LayoutTransformer;
import edu.uci.ics.jung.visualization.transform.MutableAffineTransformer;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import edu.uci.ics.jung.visualization.transform.BidirectionalTransformer;
import edu.uci.ics.jung.visualization.transform.ViewTransformer;
import edu.uci.ics.jung.visualization.util.ChangeEventSupport;
import edu.uci.ics.jung.visualization.util.DefaultChangeEventSupport;

public class BasicTransformer implements BidirectionalTransformer, LayoutTransformer, ViewTransformer,
	ChangeListener, ChangeEventSupport {

    protected ChangeEventSupport changeSupport =
        new DefaultChangeEventSupport(this);

    protected MutableTransformer viewTransformer = 
        new MutableAffineTransformer(new AffineTransform());
    
    protected MutableTransformer layoutTransformer =
        new MutableAffineTransformer(new AffineTransform());
    
    

    public BasicTransformer() {
		super();
		viewTransformer.addChangeListener(this);
		layoutTransformer.addChangeListener(this);
	}

	/* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#setViewTransformer(edu.uci.ics.jung.visualization.transform.MutableTransformer)
     */
    public void setViewTransformer(MutableTransformer transformer) {
        this.viewTransformer.removeChangeListener(this);
        this.viewTransformer = transformer;
        this.viewTransformer.addChangeListener(this);
//        renderContext.setViewTransformer(transformer);
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#setLayoutTransformer(edu.uci.ics.jung.visualization.transform.MutableTransformer)
     */
    public void setLayoutTransformer(MutableTransformer transformer) {
        this.layoutTransformer.removeChangeListener(this);
        this.layoutTransformer = transformer;
        this.layoutTransformer.addChangeListener(this);
    }


	/**
	 * @return the layoutTransformer
	 */
	public MutableTransformer getLayoutTransformer() {
		return layoutTransformer;
	}

	/**
	 * @return the viewTransformer
	 */
	public MutableTransformer getViewTransformer() {
		return viewTransformer;
	}

	/* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#inverseTransform(java.awt.geom.Point2D)
     */
	public Point2D inverseTransform(Point2D p) {
	    return layoutTransformer.inverseTransform(inverseViewTransform(p));
	}
	
	/* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#inverseViewTransform(java.awt.geom.Point2D)
     */
	public Point2D inverseViewTransform(Point2D p) {
	    return viewTransformer.inverseTransform(p);
	}

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#inverseLayoutTransform(java.awt.geom.Point2D)
     */
    public Point2D inverseLayoutTransform(Point2D p) {
        return layoutTransformer.inverseTransform(p);
    }

	/* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#transform(java.awt.geom.Point2D)
     */
	public Point2D transform(Point2D p) {
	    // transform with vv transform
	    return viewTransformer.transform(layoutTransform(p));
	}
    
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#viewTransform(java.awt.geom.Point2D)
     */
    public Point2D viewTransform(Point2D p) {
        return viewTransformer.transform(p);
    }
    
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#layoutTransform(java.awt.geom.Point2D)
     */
    public Point2D layoutTransform(Point2D p) {
        return layoutTransformer.transform(p);
    }
    
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#addChangeListener(javax.swing.event.ChangeListener)
     */
    public void addChangeListener(ChangeListener l) {
        changeSupport.addChangeListener(l);
    }
    
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#removeChangeListener(javax.swing.event.ChangeListener)
     */
    public void removeChangeListener(ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }
    
    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#getChangeListeners()
     */
    public ChangeListener[] getChangeListeners() {
        return changeSupport.getChangeListeners();
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#fireStateChanged()
     */
    public void fireStateChanged() {
        changeSupport.fireStateChanged();
    }   
    
	/* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationServer#stateChanged(javax.swing.event.ChangeEvent)
     */
	public void stateChanged(ChangeEvent e) {
	    fireStateChanged();
	}
}
