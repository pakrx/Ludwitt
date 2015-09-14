package com.pakrx.platformgame.gameplay.traits;

public class CharacterTrait {

	public final static CharacterTrait DEPRESSION = new CharacterTrait("Depression");
	
	
	public CharacterTrait(String name) {
		setName(name);
	}
	
	private String name = "";
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
