package com.pakrx.platformgame.gameplay.objects;

import java.util.List;

public class Decoration extends GameObject {
	@Override
	protected int readFromLines(List<String> lines, int i) {
		return i;
	}

}
