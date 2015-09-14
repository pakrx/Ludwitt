package com.pakrx.platformgame.graphics;

import javax.swing.JComponent;
import javax.swing.RepaintManager;

public class NoRepaintManager extends RepaintManager {

	public NoRepaintManager() {
		super();
		setDoubleBufferingEnabled(false);
	}
	
	public void addInvalidComponent(JComponent c) {
		// do nothing
	}
	
	public void addDirtyRegion(JComponent c, int x, int y, int w, int h) {
		// do nothing
	}
	
	public void markCompetelyDirty(JComponent c) {
		// do nothing
	}
	
	public void paintDirtyRegions() {
		// do nothing
	}
	
}
