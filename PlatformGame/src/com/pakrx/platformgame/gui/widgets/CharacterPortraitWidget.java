package com.pakrx.platformgame.gui.widgets;

import java.awt.Graphics2D;

import com.pakrx.platformgame.gameplay.characters.GameCharacter;

public class CharacterPortraitWidget extends Widget {

	private GameCharacter gameCharacter;
	
	
	
	@Override
	protected void updateSelf(long interval) {
		
	}

	@Override
	protected void drawSelf(Graphics2D g) {
		int x = getX() + (getWidth() - gameCharacter.getPortrait().getWidth(null)) / 2;
		int y = getY() + (getHeight() - gameCharacter.getPortrait().getHeight(null)) / 2;
		g.drawImage(gameCharacter.getPortrait(), x, y, null);
	}

	public void setGameCharacter(GameCharacter gameCharacter) {
		this.gameCharacter = gameCharacter;
	}
	
}
