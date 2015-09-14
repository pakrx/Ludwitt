package com.pakrx.platformgame.gameplay.objects;

public class MovingJumpPad extends JumpPad {

	private float initialX = -1;
	private float initialY = -1;
	
	@Override
	public synchronized void update(long interval) {
		if (initialX == -1 && initialY == -1) {
			initialX = getX();
			initialY = getY();
		}
	}
	
}
