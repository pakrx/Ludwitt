package com.pakrx.platformgame.gameplay.actions;

import com.pakrx.platformgame.gameplay.characters.GameCharacter;
import com.pakrx.platformgame.resources.ResourceManager;

public class CharacterAction {

	public final static String UNLIMITED_AMOUNT_STRING = "∞";
	public final static int UNLIMITED_AMOUNT = Integer.MAX_VALUE;
	
	protected String name = "Nothing";
	protected String format = "%s does absolutely nothing to %s.";
	protected String description = "Accomplishes absolutely nothing.";
	protected int unusedAmount = 1;
	protected int totalAmount = 1;
	
	protected int hashCode = 0;
	
	public CharacterAction() {
		
	}
	
	public CharacterAction(String name, String format, String description, int amount) {
		this.name = name;
		this.format = format;
		this.unusedAmount = amount;
		this.totalAmount = amount;
		this.description = description;
		this.hashCode = this.name.hashCode();
	}
	
	public CharacterAction clone() {
		return new CharacterAction(name, format, description, totalAmount);
	}
	
	public synchronized void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public int getHashCode() {
		return hashCode;
	}
	
	public synchronized void setFormat(String format) {
		this.format = format;
	}
	
	public String getFormat() {
		return format;
	}
	
	public String getDescription() {
		return description;
	}
	
	public synchronized void setAmount(int amount) {
		this.unusedAmount = amount;
	}
	
	public synchronized void increaseAmount(int number) {
		if (this.totalAmount != UNLIMITED_AMOUNT) { 
			this.totalAmount += number;
		}
		if (this.unusedAmount != UNLIMITED_AMOUNT) {
			this.unusedAmount += number;
		}
	}
	
	public int getAmount() {
		return unusedAmount;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(name);
		sb.append(" (");
		if (unusedAmount == UNLIMITED_AMOUNT) {
			sb.append(UNLIMITED_AMOUNT_STRING);
		} else {
			sb.append(unusedAmount);
		}
		sb.append(") ");
		return sb.toString();
	}
	
	public void resetAmount() {
		unusedAmount = totalAmount;
	}
	
	@Override
	public synchronized boolean equals(Object obj) {
		if (obj instanceof CharacterAction) {
			CharacterAction another = (CharacterAction)obj;
			return (this.hashCode == another.hashCode);
		}
		return false;
	}

	protected void act(GameCharacter sourceCharacter, GameCharacter targetCharacter) {
		//
	}
	
	public void affectPlayers(GameCharacter sourceCharacter, GameCharacter targetCharacter) {
		if (getAmount() > 0) {
			// action
			act(sourceCharacter, targetCharacter);
			if (getAmount() != CharacterAction.UNLIMITED_AMOUNT) {
				setAmount(getAmount() - 1);
			}
		}
	}

	public void readFromString(String line) {
		String[] splitLine = line.trim().split(ResourceManager.SEPARATOR);
		String name = ResourceManager.getString(splitLine[1]);
		String format = ResourceManager.getString(splitLine[2]);
		String desc = ResourceManager.getString(splitLine[3]);
		int amount;
		if ("unlimited".equals(splitLine[4])) {
			amount = UNLIMITED_AMOUNT;
		} else {
			amount = Integer.parseInt(splitLine[4]);
		}
		this.name = name;
		this.format = format;
		this.unusedAmount = amount;
		this.totalAmount = amount;
		this.description = desc;
		this.hashCode = this.name.hashCode();
	}

	public static CharacterAction createActionFromString(String line) {
		String[] splitLine = line.trim().split(ResourceManager.SEPARATOR);
		String actionClassName = splitLine[0];
		try {
			Class<?> actionClass = Class.forName(String.format("com.pakrx.platformgame.gameplay.actions.%s", actionClassName));
			CharacterAction characterAction = (CharacterAction)actionClass.newInstance();
			characterAction.readFromString(line);
			return characterAction;							
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException e) {
			e.printStackTrace();
			return null;
		}
	}
}
