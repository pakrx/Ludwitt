package com.pakrx.platformgame.gameplay.objects;

import java.util.List;

import com.pakrx.platformgame.gameplay.characters.GameCharacter;
import com.pakrx.platformgame.resources.ResourceManager;
import com.pakrx.platformgame.sound.SoundFile;

public class DamagePickup extends Pickup {

	private int amount = 10;
	
	@Override
	public boolean affectCharacter(GameCharacter character) {
		character.decreaseEnergy(amount);

		SoundFile pickupSound =  ResourceManager.readSoundFile("shsh.wav");
		pickupSound.setLocationX(screenX);
		pickupSound.play();
		
		return true;
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
