package edu.uci.ics.jung.visualization.event;
/**
 * An Event interface that defined constant settings for all events
 * @author Jason A Wrang
 *
 */
public interface Event {
	/*
	 * The following are used for the modifiers of input event
	 */
	int SHIFT_MASK	= 1;
	int CTRL_MASK 	= 1<<1;
	int ALT_MASK 	= 1<<2;
	int META_MASK 	= 1<<3;
	
	/**
	 * Set if button 1 is involved in the event, press, release, click
	 */
	int BUTTON1_MASK		= 1<<4;
	/**
	 * Set if button 2 is involved in the event, press, release, click
	 */
	int BUTTON2_MASK		= 1<<5;
	/**
	 * Set if button 3 is involved in the event, press, release, click
	 */
	int BUTTON3_MASK		= 1<<6;

	
	/*
	 * The following are used for the extended modifiers of input event
	 */
	/**
	 * Set if button 1 pressed during the event
	 */
	int BUTTON1_DOWN_MASK	= 1<<7;
	/**
	 * Set if button 2 pressed during the event
	 */
	int BUTTON2_DOWN_MASK	= 1<<8;
	/**
	 * Set if button 3 pressed during the event
	 */
	int BUTTON3_DOWN_MASK	= 1<<9;
	
	/*
	 * The following describe the scroll type of the mouse wheel
	 */
	int WHEEL_UNIT_SCROLL = 0;
	int WHEEL_BLOCK_SCROLL = 1;
	
}
