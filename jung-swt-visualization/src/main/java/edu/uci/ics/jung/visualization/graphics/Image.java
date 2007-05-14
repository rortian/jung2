package edu.uci.ics.jung.visualization.graphics;


/**
 * An abstract wrapper image class using generics to wrap a UI
 * specific image. It is the responsibility of the particular
 * rendering engine to know how to draw the underlying image.
 * @author Jason A Wrang
 *
 * @param <I>
 */
public abstract class Image {
    /**
     * Use the default image-scaling algorithm.
     */
    public static final int SCALE_DEFAULT = 1;

    /**
     * Choose an image-scaling algorithm that gives higher priority
     * to scaling speed than smoothness of the scaled image.
     */
    public static final int SCALE_FAST = 2;

    /**
     * Choose an image-scaling algorithm that gives higher priority
     * to image smoothness than scaling speed.
     * @since JDK1.1
     */
    public static final int SCALE_SMOOTH = 4;

	
	public abstract int getWidth();
	
	public abstract int getHeight();
	
	public abstract GraphicsContext getGraphicsContext();
	
    /**
     * Creates a scaled version of this image.
     * A new <code>Image</code> object is returned which will render 
     * the image at the specified <code>width</code> and 
     * <code>height</code> by default.  The new <code>Image</code> object
     * may be loaded asynchronously even if the original source image
     * has already been loaded completely.  
     *
     * <p>
     * 
     * If either <code>width</code> 
     * or <code>height</code> is a negative number then a value is 
     * substituted to maintain the aspect ratio of the original image 
     * dimensions. If both <code>width</code> and <code>height</code>
     * are negative, then the original image dimensions are used.
     *
     * @param width the width to which to scale the image.
     * @param height the height to which to scale the image.
     * @param hints flags to indicate the type of algorithm to use
     * for image resampling.
     * @return     a scaled version of the image.
     * @exception IllegalArgumentException if <code>width</code>
     *             or <code>height</code> is zero.
     * @see        java.awt.Image#SCALE_DEFAULT
     * @see        java.awt.Image#SCALE_FAST 
     * @see        java.awt.Image#SCALE_SMOOTH
     * @see        java.awt.Image#SCALE_REPLICATE
     * @see        java.awt.Image#SCALE_AREA_AVERAGING
     * @since      JDK1.1
     */
    public abstract Image getScaledInstance(int width, int height, int hints);
    
    public void draw(GraphicsContext graphicsContext, int x, int y) {
    	graphicsContext.drawImage(this, x, y);
    }
}
