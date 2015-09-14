package com.pakrx.platformgame.gui.widgets;

import java.awt.Color;
import java.awt.Graphics2D;

import com.pakrx.platformgame.gameplay.characters.GameCharacter;

public class EnergyBarWidget extends Widget {

	private int value = 50;
	private GameCharacter gameCharacter;
	private int energyWidth;
	private boolean mirrored = false;
	
	@Override
	protected void updateSelf(long interval) {
		if (gameCharacter != null) {
			setValue(gameCharacter.getEnergy());
		}
	}

	public void setFromRightToLeft() {
		mirrored = true;
	}
	
	public void setGameCharacter(GameCharacter character) {
		this.gameCharacter = character;
	}
	
	@Override
	protected void drawSelf(Graphics2D g) {
		g.setColor(Color.black);
		g.fillRect(getX(), getY(), getWidth(), getHeight());
		g.setColor(Color.red);
		if (mirrored) {
			g.fillRect(getX() + getWidth() - energyWidth, getY(), energyWidth, getHeight());
		} else {
			g.fillRect(getX(), getY(), energyWidth, getHeight());
		}
	}

	public void setValue(int value) {
		this.value = value;
		energyWidth = value * getWidth() / 100;
	}
	
	public int getValue() {
		if (gameCharacter != null) {
			value = gameCharacter.getEnergy();
		}
		return value;
	}
	
}
