package edu.uci.ics.jung.visualization;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.uci.ics.jung.visualization.transform.BidirectionalTransformer;
import edu.uci.ics.jung.visualization.transform.LayoutTransformer;
import edu.uci.ics.jung.visualization.transform.MutableAffineTransformer;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import edu.uci.ics.jung.visualization.transform.ViewTransformer;
import edu.uci.ics.jung.visualization.transform.shape.ShapeTransformer;
import edu.uci.ics.jung.visualization.util.ChangeEventSupport;
import edu.uci.ics.jung.visualization.util.DefaultChangeEventSupport;

public class BasicTransformer implements BidirectionalTransformer, LayoutTransformer, ViewTransformer,
	ShapeTransformer, ChangeListener, ChangeEventSupport {

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
     */
	public Point2D inverseTransform(Point2D p) {
	    return inverseLayoutTransform(inverseViewTransform(p));
	}
	
	/* (non-Javadoc)
     */
	public Point2D inverseViewTransform(Point2D p) {
	    return viewTransformer.inverseTransform(p);
	}

    /* (non-Javadoc)
     */
    public Point2D inverseLayoutTransform(Point2D p) {
        return layoutTransformer.inverseTransform(p);
    }

	/* (non-Javadoc)
     */
	public Point2D transform(Point2D p) {
	    return viewTransform(layoutTransform(p));
	}
    
    /* (non-Javadoc)
     */
    public Point2D viewTransform(Point2D p) {
        return viewTransformer.transform(p);
    }
    
    /* (non-Javadoc)
     */
    public Point2D layoutTransform(Point2D p) {
        return layoutTransformer.transform(p);
    }
    
	/* (non-Javadoc)
     */
	public Shape inverseTransform(Shape shape) {
	    return inverseLayoutTransform(inverseViewTransform(shape));
	}
	
	/* (non-Javadoc)
     */
	public Shape inverseViewTransform(Shape shape) {
	    return viewTransformer.inverseTransform(shape);
	}

    /* (non-Javadoc)
     */
    public Shape inverseLayoutTransform(Shape shape) {
        return layoutTransformer.inverseTransform(shape);
    }

	/* (non-Javadoc)
     */
	public Shape transform(Shape shape) {
	    return viewTransform(layoutTransform(shape));
	}
    
    /* (non-Javadoc)
     */
    public Shape viewTransform(Shape shape) {
        return viewTransformer.transform(shape);
    }
    
    /* (non-Javadoc)
     */
    public Shape layoutTransform(Shape shape) {
        return layoutTransformer.transform(shape);
    }
    
    public void setToIdentity() {
    	layoutTransformer.setToIdentity();
    	viewTransformer.setToIdentity();
    }
    
    /* (non-Javadoc)
     */
    public void addChangeListener(ChangeListener l) {
        changeSupport.addChangeListener(l);
    }
    
    /* (non-Javadoc)
     */
    public void removeChangeListener(ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }
    
    /* (non-Javadoc)
     */
    public ChangeListener[] getChangeListeners() {
        return changeSupport.getChangeListeners();
    }

    /* (non-Javadoc)
     */
    public void fireStateChanged() {
        changeSupport.fireStateChanged();
    }   
    
	/* (non-Javadoc)
     */
	public void stateChanged(ChangeEvent e) {
	    fireStateChanged();
	}
}
