package com.pakrx.platformgame.gameplay.objects;

import java.util.List;

import com.pakrx.platformgame.gameplay.characters.GameCharacter;

public class DeathTrap extends GameObject {

	public DeathTrap() {
		oneTimeUseOnly = false;
		pickable = false;
		physical = false;
		moving = false;
		blocking = false;
	}
	
	@Override
	protected int readFromLines(List<String> lines, int i) {
		return i;
	}
	
	@Override
	public boolean affectCharacter(GameCharacter character) {
		character.setEnergy(0);
		return true;
	}

}
