package com.pakrx.platformgame.gameplay.objects;

import java.util.List;

import com.pakrx.platformgame.gameplay.characters.GameCharacter;
import com.pakrx.platformgame.resources.ResourceManager;
import com.pakrx.platformgame.sound.SoundFile;

public class EnergyPickup extends Pickup {

	private int amount = 10;
	
	public EnergyPickup(int amount) {
		this.amount = amount;
	}
	
	public EnergyPickup() {
		
	}

	@Override
	public boolean affectCharacter(GameCharacter character) {
		if (character.getEnergy() < character.getMaxEnergy()) {
			character.increaseEnergy(amount);
			SoundFile pickSound =  ResourceManager.readSoundFile("blub.wav");
			pickSound.setLocationX(screenX);
			pickSound.play();
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected int readFromLines(List<String> lines, int i) {
		String line = lines.get(i);
		if (line.startsWith("AMOUNT:")) {
			amount = Integer.parseInt(line.trim().split(ResourceManager.SEPARATOR)[1]);
		}
		return (i + 1);
	}
	
}
