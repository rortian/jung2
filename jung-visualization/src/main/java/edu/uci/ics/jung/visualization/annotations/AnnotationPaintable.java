package edu.uci.ics.jung.visualization.annotations;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationServer.Paintable;
import edu.uci.ics.jung.visualization.transform.AffineTransformer;
import edu.uci.ics.jung.visualization.transform.LensTransformer;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;

public class AnnotationPaintable implements Paintable {
	
	protected Set<Annotation> annotations = new HashSet<Annotation>();
    protected AnnotationRenderer annotationRenderer = new AnnotationRenderer();

	RenderContext rc;
	AffineTransformer transformer;
	
	public AnnotationPaintable(RenderContext rc) {
		this.rc = rc;
		MutableTransformer mt = rc.getMultiLayerTransformer().getTransformer(Layer.LAYOUT);
		if(mt instanceof AffineTransformer) {
			transformer = (AffineTransformer)mt;
		} else if(mt instanceof LensTransformer) {
			transformer = (AffineTransformer)((LensTransformer)mt).getDelegate();
		}
	}
	
	public void add(Annotation annotation) {
		annotations.add(annotation);
	}
	
    public void paint(Graphics g) {
    	Graphics2D g2d = (Graphics2D)g;
        Color oldColor = g.getColor();
        for(Annotation annotation : annotations) {
        	Object ann = annotation.getAnnotation();
        	if(ann instanceof Shape) {
            	Shape shape = (Shape)ann;
            	Paint paint = annotation.getPaint();
            	Shape s = transformer.transform(shape);
            	g2d.setPaint(paint);
            	g2d.draw(s);
        	} else if(ann instanceof String) {
            	Point2D p = annotation.getLocation();
            	String label = (String)ann;
                Component component = prepareRenderer(rc, annotationRenderer, label);
                component.setForeground((Color)annotation.getPaint());
                Dimension d = component.getPreferredSize();
                AffineTransform old = g2d.getTransform();
                AffineTransform base = new AffineTransform(old);
                AffineTransform xform = transformer.getTransform();

                double rotation = transformer.getRotation();
                // unrotate the annotation
                AffineTransform unrotate = 
                	AffineTransform.getRotateInstance(-rotation, p.getX(), p.getY());
                base.concatenate(xform);
                base.concatenate(unrotate);
                g2d.setTransform(base);
                rc.getRendererPane().paintComponent(g, component, rc.getScreenDevice(), 
                        (int)p.getX(), (int)p.getY(),
                        d.width, d.height, true);
                g2d.setTransform(old);
        	}
        }
        g.setColor(oldColor);
    }
    
	public Component prepareRenderer(RenderContext rc, AnnotationRenderer annotationRenderer, Object value) {
		return annotationRenderer.getAnnotationRendererComponent(rc.getScreenDevice(), value);
	}

    public boolean useTransform() {
        return true;
    }
}
