package com.pakrx.platformgame.gameplay.objects;

import java.util.List;

import com.pakrx.platformgame.Game;
import com.pakrx.platformgame.gameplay.characters.GameCharacter;
import com.pakrx.platformgame.input.InputHandler;
import com.pakrx.platformgame.resources.ResourceManager;

public class Door extends GameObject {
	
	private String levelName = null;
	
	public Door() {
		
		pickable = false;
		oneTimeUseOnly = false;
	}
	
	public String getLevelName() {
		return levelName;
	}
	
	@Override
	public boolean affectCharacter(GameCharacter character) {
		InputHandler.activateAction(Game.ACTION_NEXT_LEVEL);
		return true;
	}

	@Override
	protected int readFromLines(List<String> lines, int i) {
		String line = lines.get(i);
		if (line.startsWith("LEVEL:")) {
			levelName = line.trim().split(ResourceManager.SEPARATOR)[1];
		}
		return (i + 1);
	}
}
