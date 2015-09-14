package com.pakrx.platformgame.gui.dialogs;

import java.awt.Color;
import java.awt.Graphics2D;

import com.pakrx.platformgame.gameplay.characters.GameCharacter;
import com.pakrx.platformgame.graphics.Screen;
import com.pakrx.platformgame.gui.widgets.EnergyBarWidget;

public class EnergyBarDialog extends Dialog {

	private EnergyBarWidget energyBarWidget;
	private GameCharacter gameCharacter;
	
	public EnergyBarDialog() {
		super();
		pausesGame = false;
		dismissable = false;
		singleton = true;
		draggable = true;
		topbar = true;
		
		title = "Energy";
		
		int width = 300;
		int height = 32;
		int x = Screen.getWidth() - width - 50;
		int y = 50;
		setBounds(x, y, width, height);
		
		energyBarWidget = new EnergyBarWidget();
		energyBarWidget.setBounds(0, 0, width, height);
		addChild(energyBarWidget);
	}
	
	public void setGameCharacter(GameCharacter gameCharacter) {
		this.gameCharacter = gameCharacter;
	}
	
	@Override
	protected void updateSelf(long interval) {
		energyBarWidget.setValue(gameCharacter.getEnergy());
	}

	@Override
	protected void drawSelf(Graphics2D g) {
		g.setColor(Color.black);
		g.drawRect(getX(), getY(), getWidth(), getHeight());
	}

}
