package edu.uci.ics.jung.visualization.annotations;

import java.awt.Paint;
import java.awt.geom.Point2D;

public class Annotation<T> {
	
	T annotation;
	Paint paint;
	Point2D location;
	
	
	public Annotation(T annotation, Paint paint, Point2D location) {
		this.annotation = annotation;
		this.paint = paint;
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
	

}
