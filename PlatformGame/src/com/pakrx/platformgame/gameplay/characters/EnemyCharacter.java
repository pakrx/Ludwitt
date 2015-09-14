package com.pakrx.platformgame.gameplay.characters;

import java.util.ArrayList;
import java.util.List;

import com.pakrx.platformgame.gameplay.actions.CharacterAction;
import com.pakrx.platformgame.gameplay.objects.EnergyPickup;
import com.pakrx.platformgame.gameplay.objects.GameObject;

public class EnemyCharacter extends GameCharacter {

	public EnemyCharacter() {
		randomizeName();
		moving = true;
		physical = true;
	}
	
	public CharacterAction getNextAction() {
		return getRandomAction();
	}
	
	@Override
	public List<GameObject> getLoot() {
		ArrayList<GameObject> loot = new ArrayList<>();
		EnergyPickup lifeEnergyPickup = new EnergyPickup(10);
		lifeEnergyPickup.setLocation(
				getMapX() + (getWidth() - lifeEnergyPickup.getWidth()) / 2, 
				getMapY());
		loot.add(lifeEnergyPickup);
		return loot;
	}
	
	public void affectBeatenOpponent(GameCharacter character) {
		character.setState(STATE_DEAD);
	}
}
