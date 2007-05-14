package edu.uci.ics.jung.visualization.graphics;

import java.util.LinkedHashSet;
import java.util.Set;


/**
 * An icon that is made up of a collection of Icons.
 * They are rendered in layers starting with the first
 * Icon added (from the constructor).
 * 
 * @author Tom Nelson
 *
 */
public class LayeredImage extends Image implements ImageDrawDelegate {

	Set<Image> iconSet = new LinkedHashSet<Image>();
	Image base;
	
	public LayeredImage(Image image) {
	    this.base = image;
	}

	public Image getBaseImage() {
		return base;
	}
	
	public void draw(GraphicsContext g, int x, int y) {
		base.draw(g, x, y);
        int bw = base.getWidth();
        int bh = base.getHeight();
		for (Image img : iconSet) {
			int iw = img.getWidth();
			int ih = img.getHeight();
			int dx = (bw - iw)/2;
			int dy = (bh - ih)/2;
			img.draw(g, x+dx, y+dy);
		}
	}

	public void add(Image image) {
		iconSet.add(image);
	}

	public boolean remove(Image image) {
		return iconSet.remove(image);
	}

	
	public GraphicsContext getGraphicsContext() {
		return base.getGraphicsContext();
	}

	public int getHeight() {
		return base.getHeight();
	}

	public Image getScaledInstance(int width, int height, int hints) {
		return base.getScaledInstance(width, height, hints);
	}

	public int getWidth() {
		return base.getWidth();
	}
}