package com.pakrx.platformgame.gameplay.actions;

import com.pakrx.platformgame.gameplay.characters.GameCharacter;
import com.pakrx.platformgame.resources.ResourceManager;

public class DamageAction extends CharacterAction {

	protected int damage = 10;
	
	public DamageAction() {
		super();
	}
	
	public DamageAction(String name, String format, String description, int amount, int damage) {
		super (name, format, description, amount);
		this.damage = damage;
	}
	
	public DamageAction clone() {
		return new DamageAction(name, format, description, totalAmount, damage);
	}
	
	@Override
	protected void act(GameCharacter sourceCharacter, GameCharacter targetCharacter) {
		targetCharacter.decreaseEnergy(damage);
	}
	
	@Override
	public void readFromString(String line) {
		super.readFromString(line);
		damage = Integer.parseInt(line.trim().split(ResourceManager.SEPARATOR)[5]);
	}
}
