package edu.uci.ics.jung.visualization.awt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CursorUtils {
	private static final Map<Integer, Cursor> customCursorMap = new HashMap<Integer, Cursor>();
	private static final Cursor[] cursors;

	static {
		cursors = new Cursor[edu.uci.ics.jung.visualization.cursor.Cursor.MAX_TYPE+1];
	}
	
	
	private static synchronized Cursor getRotateCursor() {
		Cursor cursor = customCursorMap.get(edu.uci.ics.jung.visualization.cursor.Cursor.ROTATE_CURSOR);
		if (cursor != null) return cursor;
		
		Dimension cd = Toolkit.getDefaultToolkit().getBestCursorSize(16,16);
        BufferedImage cursorImage = 
        		new BufferedImage(cd.width,cd.height,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = cursorImage.createGraphics();
        g.addRenderingHints(Collections.singletonMap(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        g.setColor(new Color(0,0,0,0));
        g.fillRect(0,0,16,16);

        int left = 0;
        int top = 0;
        int right = 15;
        int bottom = 15;
        
        g.setColor(Color.white);
        g.setStroke(new BasicStroke(3));
        // top bent line
        g.drawLine(left+2,top+6,right/2+1,top);
        g.drawLine(right/2+1,top,right-2,top+5);
        // bottom bent line
        g.drawLine(left+2,bottom-6,right/2,bottom);
        g.drawLine(right/2,bottom,right-2,bottom-6);
        // top arrow
        g.drawLine(left+2,top+6,left+5,top+6);
        g.drawLine(left+2,top+6,left+2,top+3);
        // bottom arrow
        g.drawLine(right-2,bottom-6,right-6,bottom-6);
        g.drawLine(right-2, bottom-6,right-2,bottom-3);

        
        g.setColor(Color.black);
        g.setStroke(new BasicStroke(1));
        // top bent line
        g.drawLine(left+2,top+6,right/2+1,top);
        g.drawLine(right/2+1,top,right-2,top+5);
        // bottom bent line
        g.drawLine(left+2,bottom-6,right/2,bottom);
        g.drawLine(right/2,bottom,right-2,bottom-6);
        // top arrow
        g.drawLine(left+2,top+6,left+5,top+6);
        g.drawLine(left+2,top+6,left+2,top+3);
        // bottom arrow
        g.drawLine(right-2,bottom-6,right-6,bottom-6);
        g.drawLine(right-2, bottom-6,right-2,bottom-3);

        g.dispose();
        
        cursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(), "RotateCursor");
        customCursorMap.put(edu.uci.ics.jung.visualization.cursor.Cursor.ROTATE_CURSOR, cursor);
        return cursor;
	}
	
	private static synchronized Cursor getShearCursor() {
		Cursor cursor = customCursorMap.get(edu.uci.ics.jung.visualization.cursor.Cursor.SHEAR_CURSOR);
		if (cursor != null) return cursor;
		Dimension cd = Toolkit.getDefaultToolkit().getBestCursorSize(16,16);
		BufferedImage cursorImage = 
			new BufferedImage(cd.width,cd.height,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = cursorImage.createGraphics();
		g.addRenderingHints(Collections.singletonMap(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
		g.setColor(new Color(0,0,0,0));
		g.fillRect(0,0,16,16);

		int left = 0;
		int top = 0;
		int right = 15;
		int bottom = 15;

		g.setColor(Color.white);
		g.setStroke(new BasicStroke(3));
		g.drawLine(left+2,top+5,right-2,top+5);
		g.drawLine(left+2,bottom-5,right-2,bottom-5);
		g.drawLine(left+2,top+5,left+4,top+3);
		g.drawLine(left+2,top+5,left+4,top+7);
		g.drawLine(right-2,bottom-5,right-4,bottom-3);
		g.drawLine(right-2,bottom-5,right-4,bottom-7);

		g.setColor(Color.black);
		g.setStroke(new BasicStroke(1));
		g.drawLine(left+2,top+5,right-2,top+5);
		g.drawLine(left+2,bottom-5,right-2,bottom-5);
		g.drawLine(left+2,top+5,left+4,top+3);
		g.drawLine(left+2,top+5,left+4,top+7);
		g.drawLine(right-2,bottom-5,right-4,bottom-3);
		g.drawLine(right-2,bottom-5,right-4,bottom-7);
		g.dispose();
		cursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(), "RotateCursor");
		customCursorMap.put(edu.uci.ics.jung.visualization.cursor.Cursor.SHEAR_CURSOR, cursor);
		return cursor;
	}
	
	public static Cursor getCursor(edu.uci.ics.jung.visualization.cursor.Cursor cursor) {
		int type = cursor.getType();
		if (type < 0 || type >= cursors.length) {
			Cursor c = customCursorMap.get(type);
			if (c == null) c = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
			return c;
		} else {
			Cursor c = cursors[type];
			if (c == null) c = getCursor(type);
			return c;
		}
	}
	
	protected static Cursor getCursor(int type) {
		if (type == edu.uci.ics.jung.visualization.cursor.Cursor.DEFAULT_CURSOR) {
			cursors[type] = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		} else if (type == edu.uci.ics.jung.visualization.cursor.Cursor.CROSSHAIR_CURSOR) {
			cursors[type] = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		} else if (type == edu.uci.ics.jung.visualization.cursor.Cursor.TEXT_CURSOR) {
			cursors[type] = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);
		} else if (type == edu.uci.ics.jung.visualization.cursor.Cursor.WAIT_CURSOR) {
			cursors[type] = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
		} else if (type == edu.uci.ics.jung.visualization.cursor.Cursor.SW_RESIZE_CURSOR) {
			cursors[type] = Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
		} else if (type == edu.uci.ics.jung.visualization.cursor.Cursor.SE_RESIZE_CURSOR) {
			cursors[type] = Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
		} else if (type == edu.uci.ics.jung.visualization.cursor.Cursor.NW_RESIZE_CURSOR) {
			cursors[type] = Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
		} else if (type == edu.uci.ics.jung.visualization.cursor.Cursor.NE_RESIZE_CURSOR) {
			cursors[type] = Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
		} else if (type == edu.uci.ics.jung.visualization.cursor.Cursor.N_RESIZE_CURSOR) {
			cursors[type] = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
		} else if (type == edu.uci.ics.jung.visualization.cursor.Cursor.S_RESIZE_CURSOR) {
			cursors[type] = Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
		} else if (type == edu.uci.ics.jung.visualization.cursor.Cursor.W_RESIZE_CURSOR) {
			cursors[type] = Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
		} else if (type == edu.uci.ics.jung.visualization.cursor.Cursor.E_RESIZE_CURSOR) {
			cursors[type] = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
		} else if (type == edu.uci.ics.jung.visualization.cursor.Cursor.HAND_CURSOR) {
			cursors[type] = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
		} else if (type == edu.uci.ics.jung.visualization.cursor.Cursor.MOVE_CURSOR) {
			cursors[type] = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
		} else if (type == edu.uci.ics.jung.visualization.cursor.Cursor.ROTATE_CURSOR) {
			cursors[type] = getRotateCursor();
		} else if (type == edu.uci.ics.jung.visualization.cursor.Cursor.SHEAR_CURSOR) {
			cursors[type] = getShearCursor();
		} else {
			// shouldn't be here
			System.err.println("Could not find cursor type: " + type);
			cursors[type] = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		}
		
		return cursors[type];
	}
}
