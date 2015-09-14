package com.pakrx.platformgame.gameplay.effects;

import com.pakrx.platformgame.gameplay.characters.GameCharacter;

public class BattleEffect {

	private String name = "";
	private int usesCount = 0;
	
	
	public BattleEffect() {
		
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void affectPlayer(GameCharacter gameCharacter) {
		usesCount--;
	}
	
	public int getUsesCount() {
		return usesCount;
	}
	
}
