package edu.uci.ics.jung.algorithms.layout;

/**
 * interface for operating the relax iterations on a layout
 * 
 * @author Tom Nelson - tomnelson@dev.java.net
 *
 */
public interface Relaxer {
	
	/**
	 * execute a loop of steps in a new Thread
	 * firing an event after each step
	 *
	 */
	void relax();
	
	/**
	 * execute a loop of steps in the calling
	 * thread, firing no events
	 *
	 */
	void prerelax();
	
	/**
	 * make the relaxer thread wait
	 *
	 */
	void pause();
	
	/**
	 * make the relaxer thread resume
	 *
	 */
	void resume();
	
	/**
	 * set flags to stop the relaxer thread
	 *
	 */
	void stop();

	void setSleepTime(long i);
}
