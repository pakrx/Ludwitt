package com.pakrx.platformgame.gameplay.objects;

import java.util.List;

public class BodyPart extends GameObject {

	@Override
	public void readFromFile(String filename) {
		// nothing
	}

	@Override
	public synchronized void update(long interval) {
		// nothing
	}

	@Override
	protected int readFromLines(List<String> lines, int i) {
		return i;
	}
}
