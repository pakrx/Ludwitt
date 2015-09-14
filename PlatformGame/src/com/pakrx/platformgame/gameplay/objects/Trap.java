package com.pakrx.platformgame.gameplay.objects;

import java.util.List;

import com.pakrx.platformgame.gameplay.characters.GameCharacter;
import com.pakrx.platformgame.graphics.Animation;
import com.pakrx.platformgame.resources.ResourceManager;

public class Trap extends GameObject {

	private boolean active = true;
	private int damage = 10;
	private Animation activeAnimation = new Animation();
	
	@Override
	public boolean affectCharacter(GameCharacter character) {
		if (active) {
			active = false;
			pushedAnimation = activeAnimation;
			character.decreaseEnergy(damage);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected int readFromLines(List<String> lines, int i) {
		String line = lines.get(i);
		if (line.startsWith("DAMAGE:")) {
			damage = Integer.parseInt(line.trim().split(ResourceManager.SEPARATOR)[1]);
		}
		if (line.startsWith("ACTIVE ANIMATION:")) {
			Animation animation = new Animation();
			i = animation.readFromLines(lines, i + 1);
			activeAnimation = animation;
		}
		return (i + 1);
	}

}
