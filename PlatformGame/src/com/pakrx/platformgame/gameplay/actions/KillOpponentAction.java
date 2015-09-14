package com.pakrx.platformgame.gameplay.actions;

import com.pakrx.platformgame.gameplay.characters.GameCharacter;

public class KillOpponentAction extends CharacterAction {
	
	@Override
	protected void act(GameCharacter sourceCharacter, GameCharacter targetCharacter) {
		targetCharacter.setState(GameCharacter.STATE_DEAD);
	}
}
