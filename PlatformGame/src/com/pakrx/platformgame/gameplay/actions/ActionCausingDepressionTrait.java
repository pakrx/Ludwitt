package com.pakrx.platformgame.gameplay.actions;

import com.pakrx.platformgame.gameplay.characters.GameCharacter;
import com.pakrx.platformgame.gameplay.traits.CharacterTrait;

public class ActionCausingDepressionTrait extends CharacterAction {


	public ActionCausingDepressionTrait(String name, String format, String description, int amount) {
		super(name, format, description, amount);
	}
	
	@Override
	protected void act(GameCharacter sourceCharacter, GameCharacter targetCharacter) {
		targetCharacter.addTrait(CharacterTrait.DEPRESSION);
	}
	
}
