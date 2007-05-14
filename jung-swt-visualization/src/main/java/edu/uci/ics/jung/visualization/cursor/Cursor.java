package edu.uci.ics.jung.visualization.cursor;

public class Cursor {
	/**
	 * The default cursor type
	 */
	public static final int DEFAULT_CURSOR = 0;
	/**
	 * The crosshair cursor type
	 */
	public static final int CROSSHAIR_CURSOR = 1;
	/**
	 * The text cursor type
	 */
	public static final int TEXT_CURSOR = 2;
	/**
	 * The wait cursor type
	 */
	public static final int WAIT_CURSOR = 3;
	/**
	 * The south-west-resize cursor type
	 */
	public static final int SW_RESIZE_CURSOR = 4;
	/**
	 * The south-east-resize cursor type
	 */
	public static final int SE_RESIZE_CURSOR = 5;
	/**
	 * The north-west-resize cursor type
	 */
	public static final int NW_RESIZE_CURSOR = 6;
	/**
	 * The north-east-resize cursor type
	 */
	public static final int NE_RESIZE_CURSOR = 7;
	/**
	 * The north-resize cursor type
	 */
	public static final int N_RESIZE_CURSOR = 8;
	/**
	 * The south-resize cursor type
	 */
	public static final int S_RESIZE_CURSOR = 9;
	/**
	 * The west-resize cursor type
	 */
	public static final int W_RESIZE_CURSOR = 10;
	/**
	 * The east-resize cursor type
	 */
	public static final int E_RESIZE_CURSOR = 11;
	/**
	 * The hand cursor type
	 */
	public static final int HAND_CURSOR = 12;
	/**
	 * The move cursor type
	 */
	public static final int MOVE_CURSOR = 13;
	/**
	 * The rotate cursor type
	 */
	public static final int ROTATE_CURSOR = 14;
	/**
	 * The rotate cursor type
	 */
	public static final int SHEAR_CURSOR = 15;

	public static final int MAX_TYPE = 15;
	
	private int type;

	public Cursor(int type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}
}
