package edu.uci.ics.jung.visualization.annotations;

import java.awt.Paint;
import java.awt.geom.Point2D;

public class Annotation<T> {
	
	protected T annotation;
	protected Paint paint;
	protected Point2D location;
	protected Layer layer;
	protected boolean fill;
	public static enum Layer { LOWER, UPPER }
	
	
	
	public Annotation(T annotation, Layer layer, Paint paint, 
			boolean fill, Point2D location) {
		this.annotation = annotation;
		this.layer = layer;
		this.paint = paint;
		this.fill = fill;
		this.location = location;
	}
	/**
	 * @return the annotation
	 */
	public T getAnnotation() {
		return annotation;
	}
	/**
	 * @param annotation the annotation to set
	 */
	public void setAnnotation(T annotation) {
		this.annotation = annotation;
	}
	/**
	 * @return the location
	 */
	public Point2D getLocation() {
		return location;
	}
	/**
	 * @return the layer
	 */
	public Layer getLayer() {
		return layer;
	}
	/**
	 * @param layer the layer to set
	 */
	public void setLayer(Layer layer) {
		this.layer = layer;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(Point2D location) {
		this.location = location;
	}
	/**
	 * @return the paint
	 */
	public Paint getPaint() {
		return paint;
	}
	/**
	 * @param paint the paint to set
	 */
	public void setPaint(Paint paint) {
		this.paint = paint;
	}
	/**
	 * @return the fill
	 */
	public boolean isFill() {
		return fill;
	}
	/**
	 * @param fill the fill to set
	 */
	public void setFill(boolean fill) {
		this.fill = fill;
	}
	

}
