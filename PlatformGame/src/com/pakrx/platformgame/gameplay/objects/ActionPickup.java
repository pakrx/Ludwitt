package com.pakrx.platformgame.gameplay.objects;

import java.util.List;

import com.pakrx.platformgame.gameplay.actions.CharacterAction;
import com.pakrx.platformgame.gameplay.characters.GameCharacter;

public class ActionPickup extends Pickup {

	private CharacterAction action;
	
	
	@Override
	public boolean affectCharacter(GameCharacter character) {
		if (action != null) {
			character.addAction(action);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected int readFromLines(List<String> lines, int i) {
		String line = lines.get(i);
		if (line.startsWith("ACTION:")) {
			action = CharacterAction.createActionFromString(line.substring("ACTION:".length()));
		}
		return (i + 1);
	}

}
