package com.pakrx.platformgame.gameplay.objects;

import java.util.List;
import java.util.Random;

import com.pakrx.platformgame.gameplay.Physics;
import com.pakrx.platformgame.gameplay.characters.GameCharacter;
import com.pakrx.platformgame.resources.ResourceManager;

public class Crusher extends GameObject {

	private boolean instakill = false;
	private int damage = 10;
	private long topDuration = 1 * Physics.INTERVAL_DIVIDER;
	private long upDuration = 1 * Physics.INTERVAL_DIVIDER;
	private long downDuration = 1 * Physics.INTERVAL_DIVIDER;
	private long bottomDuration = 1 * Physics.INTERVAL_DIVIDER;
	private int distance = 32;
	private boolean used = false;
	private boolean damaging = false;
	private long time;
	
	public Crusher() {
		oneTimeUseOnly = false;
		pickable = false;
		physical = false;
		moving = true;
		blocking = true;
	}
	
	@Override
	public synchronized void update(long interval) {
		if (initialMapX == -1 && initialMapY == -1) {
			initialMapX = getX();
			initialMapY = getY();
		}
		time += interval;
		long maxTime = (bottomDuration + upDuration + topDuration + downDuration);
		if (time > maxTime) {
			time -= maxTime;
		}
		if (time <= bottomDuration) {
			// bottom
			setLocation(initialMapX, initialMapY);
			damaging = false;
			used = false;
			blocking = true;
		} else if (time >= bottomDuration && time <= bottomDuration + upDuration) {
			// going up
			long inMovementTime = time - bottomDuration;
			float dy = inMovementTime * distance / upDuration;
			setLocation(initialMapX, initialMapY - dy);
			damaging = false;
			blocking = true;
		} else if (time >= bottomDuration + upDuration && time <= bottomDuration + upDuration + topDuration) {
			// top
			setLocation(initialMapX, initialMapY - distance);
			damaging = false;
			blocking = true;
		} else {
			// going down
			long inMovementTime = time - (bottomDuration + upDuration + topDuration);
			float dy = inMovementTime * distance / downDuration;
			setLocation(initialMapX, initialMapY - distance + dy);
			damaging = true;
			blocking = false;
		}
	}
		
	@Override
	public boolean affectCharacter(GameCharacter character) {
		if (damaging && !used) {
			if (instakill) {
				character.decreaseEnergy(character.getEnergy());
			} else {
				character.decreaseEnergy(damage);
			}
			used = true;
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected int readFromLines(List<String> lines, int i) {
		String line = lines.get(i);
		if (line.startsWith("INSTAKILL:")) {
			instakill = Boolean.parseBoolean(line.trim().split(ResourceManager.SEPARATOR)[1]);
		}
		if (line.startsWith("DAMAGE:")) {
			damage = Integer.parseInt(line.trim().split(ResourceManager.SEPARATOR)[1]);
		}
		if (line.startsWith("DISTANCE:")) {
			distance = Integer.parseInt(line.trim().split(ResourceManager.SEPARATOR)[1]);
		}
		if (line.startsWith("TOP DURATION:")) {
			topDuration = (long)(Float.parseFloat(line.trim().split(ResourceManager.SEPARATOR)[1]) * Physics.INTERVAL_DIVIDER);
		}
		if (line.startsWith("UP DURATION:")) {
			upDuration = (long)(Float.parseFloat(line.trim().split(ResourceManager.SEPARATOR)[1]) * Physics.INTERVAL_DIVIDER);
		}
		if (line.startsWith("BOTTOM DURATION:")) {
			bottomDuration = (long)(Float.parseFloat(line.trim().split(ResourceManager.SEPARATOR)[1]) * Physics.INTERVAL_DIVIDER);
		}
		if (line.startsWith("DOWN DURATION:")) {
			downDuration = (long)(Float.parseFloat(line.trim().split(ResourceManager.SEPARATOR)[1]) * Physics.INTERVAL_DIVIDER);
		}
		
		long maxTime = (bottomDuration + upDuration + topDuration + downDuration) / 3;
		time = new Random().nextLong() % maxTime;
		
		return (i + 1);
	}

}
