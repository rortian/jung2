package edu.uci.ics.jung.visualization;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import edu.uci.ics.jung.visualization.VisualizationServer.Paintable;
import edu.uci.ics.jung.visualization.transform.AffineTransformer;
import edu.uci.ics.jung.visualization.transform.LensTransformer;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;

public class AnnotationPaintable implements Paintable {
	
	protected Map<Shape,Paint> shapes = new HashMap<Shape,Paint>();
	protected Map<Point2D,String> notes = new HashMap<Point2D,String>();
    protected AnnotationRenderer annotationRenderer = new AnnotationRenderer(Color.blue);

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
	
	public void add(Shape shape,Paint paint) {
		shapes.put(shape,paint);
	}
	
	public void add(Point2D point, String annotation) {
		notes.put(point, annotation);
	}
	
    public void paint(Graphics g) {
    	Graphics2D g2d = (Graphics2D)g;
        Color oldColor = g.getColor();
        for(Map.Entry<Shape,Paint> entry : shapes.entrySet()) {
        	Shape shape = entry.getKey();
        	Paint paint = entry.getValue();
        	Shape s = transformer.transform(shape);
        	g2d.setPaint(paint);
        	g2d.draw(s);
        }
        for(Map.Entry<Point2D,String> note : notes.entrySet()) {
        	Point2D p = note.getKey();
        	String label = note.getValue();
            Component component = prepareRenderer(rc, annotationRenderer, label);
            
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
        g.setColor(oldColor);
    }
    
	public Component prepareRenderer(RenderContext rc, AnnotationRenderer annotationRenderer, Object value) {
		return annotationRenderer.getAnnotationRendererComponent(rc.getScreenDevice(), value);
	}


    public boolean useTransform() {
        return true;
    }
}
