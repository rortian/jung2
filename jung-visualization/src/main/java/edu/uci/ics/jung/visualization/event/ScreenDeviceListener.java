package edu.uci.ics.jung.visualization.event;

import edu.uci.ics.jung.visualization.ScreenDevice;

public interface ScreenDeviceListener<C> {
	void screenResized(ScreenDevice<C> target);
}
