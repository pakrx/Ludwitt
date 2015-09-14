package com.pakrx.platformgame.gameplay.characters;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.pakrx.platformgame.gameplay.actions.CharacterAction;
import com.pakrx.platformgame.gameplay.objects.BodyPart;
import com.pakrx.platformgame.gameplay.objects.GameObject;
import com.pakrx.platformgame.gameplay.traits.CharacterTrait;
import com.pakrx.platformgame.graphics.Animation;
import com.pakrx.platformgame.resources.ResourceManager;

public class GameCharacter extends GameObject {

	public final static int STATE_DEAD = STATE_DISABLED;
	public final static int STATE_ALIVE = STATE_GROUND;
	public final static int STATE_JUMPING = 4;
	public final static int STATE_WALKING = 5;
	
	protected String name = "";
	protected int energy = 100;
	protected int maxEnergy = 100;
	protected ArrayList<CharacterAction> actions = new ArrayList<CharacterAction>();
	protected ArrayList<CharacterTrait> traits = new ArrayList<CharacterTrait>();
	
	protected GameObject head;
	protected GameObject legs;
	protected Image portrait;
	
	
	public GameCharacter() {
		moving = true;
		physical = true;
	}
	
	@Override
	public synchronized void setImage(Image image) {
		this.image = image;
		if (width == -1 || height == -1) {
			width = image.getWidth(null);
			height = image.getHeight(null);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public synchronized void increaseEnergy(int amount, boolean overflow) {
		energy += amount;
		if (!overflow) {
			energy = Math.min(maxEnergy, energy);
		}
	}
	
	public Image getPortrait() {
		if (portrait != null) {
			return portrait;
		} else {
			portrait = getImage();
			return portrait;
		}
	}
	
	public int getMaxEnergy() {
		return maxEnergy;
	}
	
	public synchronized void increaseEnergy(int amount) {
		increaseEnergy(amount, false);
	}
	
	public synchronized void decreaseEnergy(int amount) {
		energy = Math.max(0, energy - amount);
	}
	
	public int getEnergy() {
		return energy;
	}
	
	public void setEnergy(int energy) {
		this.energy = energy;
	}
	
	public synchronized void randomizeName() {
		Random random = new Random();
		String[] consonants = new String[]{"b", "c", "d", "f", "j", "k", "l", "m", "n", "kh", "sh", "ch", "z", "r", "mn"};
		String[] vowels = new String[]{"a", "e", "i", "o", "u", "ou", "ai"};
		int wordLength = 3 + random.nextInt(7);
		StringBuffer nameBuffer = new StringBuffer();
		for (int i = 0; i < wordLength; i++) {
			if (i % 2 == 0) {
				int x = random.nextInt(consonants.length);
				nameBuffer.append(consonants[x]);
			} else {
				int x = random.nextInt(vowels.length);
				nameBuffer.append(vowels[x]);
			}
		}
		nameBuffer.setCharAt(0, java.lang.Character.toUpperCase(nameBuffer.charAt(0)));
		setName(nameBuffer.toString());
	}
	
	public List<CharacterAction> getActions() {
		return actions;
	}
	
	public synchronized void addAction(CharacterAction action) {
		int indexOfAction = actions.indexOf(action);
		if (indexOfAction >= 0) {
			actions.get(indexOfAction).increaseAmount(action.getAmount());
		} else {
			actions.add(action);
		}
	}
	
	public synchronized CharacterAction getRandomAction() {
		boolean nonZeroActionExists = false;
		for (CharacterAction action : actions) {
			if (action.getAmount() > 0) {
				nonZeroActionExists = true;
				break;
			}
		}
		if (nonZeroActionExists) {
			CharacterAction action = actions.get(new Random().nextInt(actions.size()));
			while (action.getAmount() == 0) {
				action = actions.get(new Random().nextInt(actions.size()));
			}
			return action;
		} else {
			return null;
		}
	}
	
	public synchronized List<CharacterAction> getAvailableActions() {
		ArrayList<CharacterAction> availableActions = new ArrayList<>();
		for (CharacterAction action : getActions()) {
			if (action.getAmount() > 0) {
				availableActions.add(action);
			}
		}
		return availableActions;
	}
	
	
	public synchronized void resetActionsAmounts() {
		for (CharacterAction action : actions) {
			action.resetAmount();
		}
	}
	
	public synchronized List<CharacterTrait> getTraits() {
		return traits;
	}
	
	public synchronized void addTrait(CharacterTrait trait) {
		if (!traits.contains(trait)) {
			traits.add(trait);
		}
	}
	
	public List<GameObject> getLoot() {
		return new ArrayList<>();
	}
	
	public int getJumpingSpeed() {
		return jumpingSpeed;
	}

	public void die() {
		
	}
	
	@Override
	public synchronized void draw(Graphics2D g) {
		int y = 0;
		if (head != null) {
			g.drawImage(head.getImage(), screenX, screenY, null);
			y += head.getHeight();
		}
		g.drawImage(getImage(), screenX, screenY + y, null);
		y += getImage().getHeight(null);
		if (legs != null) {
			g.drawImage(legs.getImage(), screenX, screenY + y, null);
		}
	}
		
	@Override
	public void updateAnimation(long interval) {
		super.updateAnimation(interval);
		if (head != null) {
			head.updateAnimation(interval);
		}
		if (legs != null) {
			legs.updateAnimation(interval);
		}
		width = animation.getCurrentImage().getWidth(null);
		height = animation.getCurrentImage().getHeight(null);
		if (head != null) {
			width = Math.max(width, head.getAnimation().getCurrentImage().getWidth(null));
			height += head.getAnimation().getCurrentImage().getHeight(null);
		}
		if (legs != null) {
			width = Math.max(width,legs.getAnimation().getCurrentImage().getWidth(null));
			height += legs.getAnimation().getCurrentImage().getHeight(null);
		}
	}
	
	@Override
	public synchronized void setState(int state) {
		super.setState(state);
		if (head != null) {
			head.setState(state);
		}
		if (legs != null) {
			legs.setState(state);
		}
	}
	
	@Override
	public void setDirection(int direction) {
		super.setDirection(direction);
		if (head != null) {
			head.setDirection(direction);
		}
		if (legs != null) {
			legs.setDirection(direction);
		}
	}
	
	@Override
	protected int readFromLines(List<String> lines, int i) {
		String[] splitLine;
		String line = lines.get(i);
		if (line.startsWith("PORTRAIT:")) {
			splitLine = line.trim().split(ResourceManager.SEPARATOR);
			portrait = ResourceManager.readImage(splitLine[1]);
		}
		if (line.startsWith("ACTIONS:")) {
			CharacterAction action;
			while (line.trim().length() > 0 && i < lines.size() - 1) {
				line = lines.get(++i);
				action = CharacterAction.createActionFromString(line);
				addAction(action);
			}
		}
		if (line.startsWith("HEAD-ANIMATION:")) {
			Animation animation = new Animation();
			animation.readFromLines(lines, i);
			if (head == null) {
				head = new BodyPart();
			}
			head.addAnimation(animation);
		}
		if (line.startsWith("LEGS-ANIMATION:")) {
			Animation animation = new Animation();
			animation.readFromLines(lines, i);
			if (legs == null) {
				legs = new BodyPart();
			}
			legs.addAnimation(animation);
		}
		return (i + 1);
	}
}
