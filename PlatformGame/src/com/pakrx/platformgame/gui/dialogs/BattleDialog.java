package com.pakrx.platformgame.gui.dialogs;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;

import com.pakrx.platformgame.Game;
import com.pakrx.platformgame.gameplay.actions.CharacterAction;
import com.pakrx.platformgame.gameplay.characters.EnemyCharacter;
import com.pakrx.platformgame.gameplay.characters.PlayerCharacter;
import com.pakrx.platformgame.graphics.Screen;
import com.pakrx.platformgame.gui.widgets.BattleLogListWidget;
import com.pakrx.platformgame.gui.widgets.Button;
import com.pakrx.platformgame.gui.widgets.CharacterActionListWidget;
import com.pakrx.platformgame.gui.widgets.CharacterPortraitWidget;
import com.pakrx.platformgame.gui.widgets.EnergyBarWidget;
import com.pakrx.platformgame.gui.widgets.ListWidget.ListWidgetListener;
import com.pakrx.platformgame.gui.widgets.TextWidget;
import com.pakrx.platformgame.gui.widgets.Widget;
import com.pakrx.platformgame.input.InputHandler;
import com.pakrx.platformgame.resources.ResourceManager;

public class BattleDialog extends Dialog {
	
	public static final int ACTION_PLAYER_ACTION_SELECTED = InputHandler.getNextAction();
	public static final int ACTION_ENEMY_ACTION_SELECTED = InputHandler.getNextAction();
	public static final int ACTION_ACTION = InputHandler.getNextAction();
	public static final int ACTION_ACTION_SPACE = InputHandler.getNextAction();
	public static final int ACTION_ENEMY_BEATEN = InputHandler.getNextAction();
	public static final int ACTION_1 = InputHandler.getNextAction();
	public static final int ACTION_2 = InputHandler.getNextAction();
	public static final int ACTION_3 = InputHandler.getNextAction();
	public static final int ACTION_4 = InputHandler.getNextAction();
	public static final int ACTION_5 = InputHandler.getNextAction();
	public static final int ACTION_6 = InputHandler.getNextAction();
	public static final int ACTION_LEFT = InputHandler.getNextAction();
	public static final int ACTION_RIGHT = InputHandler.getNextAction();
	public static final int ACTION_UP = InputHandler.getNextAction();
	public static final int ACTION_DOWN = InputHandler.getNextAction();
	
	private Image bgImage;
	
	private TextWidget playerCharacterNameWidget;
	private TextWidget enemyCharacterNameWidget;
	private CharacterPortraitWidget playerCharacterPortraitWidget;
	private CharacterPortraitWidget enemyCharacterPortraitWidget;
	private PlayerCharacter playerCharacter;
	private EnemyCharacter enemyCharacter;
	private TextWidget infoTextWidget;
	private Button actionButtonWidget;
	private EnergyBarWidget playerCharacterEnergyBarWidget;
	private EnergyBarWidget enemyCharacterEnergyBarWidget;
	private CharacterActionListWidget playerCharacterActionListWidget;
	private CharacterActionListWidget enemyCharacterActionListWidget;
	private BattleLogListWidget battleLogListWidget;
	
	
	public BattleDialog() {
		pausesGame = true;
		dismissable = false;
		singleton = true;
		draggable = true;
		flamed = false;
		topbar = true;
		
		title = "BATTLE";
		
		bgImage = ResourceManager.readImage("gui/battlebg.png");
		
		int width = 800;
		int height = 600;
		int x = (Screen.getWidth() - width) / 2;
		int y = (Screen.getHeight() - height) / 2;
		setBounds(x, y, width, height);
		
		width = 250;
		height = 50;
		x = 0;
		y = 0;
		playerCharacterNameWidget = new TextWidget();
		playerCharacterNameWidget.setVerticalCentering(true);
		playerCharacterNameWidget.setHorizontalCentering(true);
		playerCharacterNameWidget.setString("");
		playerCharacterNameWidget.setBounds(x, y, width, height);
		addChild(playerCharacterNameWidget);

		width = 250;
		height = 50;
		x = 550;
		y = 0;
		enemyCharacterNameWidget = new TextWidget();
		enemyCharacterNameWidget.setVerticalCentering(true);
		enemyCharacterNameWidget.setHorizontalCentering(true);
		enemyCharacterNameWidget.setString("");
		enemyCharacterNameWidget.setBounds(x, y, width, height);
		addChild(enemyCharacterNameWidget);

		width = 100;
		height = 200;
		x = 150;
		y = 50;
		playerCharacterPortraitWidget = new CharacterPortraitWidget();
		playerCharacterPortraitWidget.setBounds(x, y, width, height);
		addChild(playerCharacterPortraitWidget);

		width = 100;
		height = 200;
		x = 550;
		y = 50;
		enemyCharacterPortraitWidget = new CharacterPortraitWidget();
		enemyCharacterPortraitWidget.setBounds(x, y, width, height);
		addChild(enemyCharacterPortraitWidget);
		
		width = 300;
		height = 250;
		x = 250;
		y = 0;
		battleLogListWidget = new BattleLogListWidget();
		battleLogListWidget.setBounds(x, y, width, height);
		addChild(battleLogListWidget);
		
		width = 300;
		height = 200;
		x = 250;
		y = 250;
		infoTextWidget = new TextWidget();
		infoTextWidget.setBounds(x, y, width, height);
		infoTextWidget.setString("Choose an action using mouse, 1-6 keys, or arrow keys, then click the [Attack!] button, press [Enter] key, or spacebar. Good luck!");
		infoTextWidget.setVerticalCentering(true);
		infoTextWidget.setHorizontalCentering(true);
		addChild(infoTextWidget);
		
		width = 150;
		height = 50;
		x = 250 + (300 - 150)/2;
		y = 500;
		actionButtonWidget = new Button();
		actionButtonWidget.setBounds(x, y, width, height);
		actionButtonWidget.setString("Action!");
		actionButtonWidget.setVerticalCentering(true);
		actionButtonWidget.setHorizontalCentering(true);
		actionButtonWidget.hide();
		actionButtonWidget.setClickListener(new WidgetClickListener() {
			@Override
			public void onWidgetClick(Widget guiElement) {
				InputHandler.activateAction(ACTION_ACTION);
			}
		});
		addChild(actionButtonWidget);
		
		width = 250;
		height = 50;
		x = 0;
		y = 250;
		playerCharacterEnergyBarWidget = new EnergyBarWidget();
		playerCharacterEnergyBarWidget.setBounds(x, y, width, height);
		addChild(playerCharacterEnergyBarWidget);
		
		width = 250;
		height = 50;
		x = 550;
		y = 250;
		enemyCharacterEnergyBarWidget = new EnergyBarWidget();
		enemyCharacterEnergyBarWidget.setBounds(x, y, width, height);
		enemyCharacterEnergyBarWidget.setFromRightToLeft();
		addChild(enemyCharacterEnergyBarWidget);
		
		playerCharacterActionListWidget = new CharacterActionListWidget();
		playerCharacterActionListWidget.setBounds(0, 300, 250, 300);
		playerCharacterActionListWidget.setListener(new ListWidgetListener() {
			@Override
			public void onListElementSelected(int index) {
				InputHandler.activateAction(ACTION_PLAYER_ACTION_SELECTED);
			}
		});
		addChild(playerCharacterActionListWidget);
		
		enemyCharacterActionListWidget = new CharacterActionListWidget();
		enemyCharacterActionListWidget.setBounds(550, 300, 250, 300);
		enemyCharacterActionListWidget.setListener(new ListWidgetListener() {
			@Override
			public void onListElementSelected(int index) {
				InputHandler.activateAction(ACTION_ENEMY_ACTION_SELECTED);
			}
		});
		addChild(enemyCharacterActionListWidget);
	}
	
	@Override
	public void update(long interval) {
		super.update(interval);
		
		if (InputHandler.popAction(ACTION_ACTION_SPACE) && actionButtonWidget != null) {
			actionButtonWidget.getClickListener().onWidgetClick(actionButtonWidget);
		}
		
		CharacterActionListWidget currentActionListWidget;
		CharacterActionListWidget anotherActionListWidget;
		int selectedIndex;
		if (playerCharacterActionListWidget.getSelectedActionListEntry() != null) {
			currentActionListWidget = playerCharacterActionListWidget;
			anotherActionListWidget = enemyCharacterActionListWidget;
			selectedIndex = playerCharacterActionListWidget.getSelectedActionListEntry().getIndex();
		} else if (enemyCharacterActionListWidget.getSelectedActionListEntry() != null) {
			currentActionListWidget = enemyCharacterActionListWidget;
			anotherActionListWidget = playerCharacterActionListWidget;
			selectedIndex = enemyCharacterActionListWidget.getSelectedActionListEntry().getIndex();
		} else { 
			currentActionListWidget = playerCharacterActionListWidget;
			anotherActionListWidget = enemyCharacterActionListWidget;
			playerCharacterActionListWidget.deselectAll();
			enemyCharacterActionListWidget.deselectAll();;
			selectedIndex = -1;
		}
		
		if ((InputHandler.popAction(ACTION_LEFT) || InputHandler.popAction(ACTION_RIGHT)) && selectedIndex >= 0) {
			currentActionListWidget.deselectAll();
			anotherActionListWidget.select(0);
		} else if (InputHandler.popAction(ACTION_UP)) {
			if (selectedIndex > 0) {
				currentActionListWidget.select(selectedIndex - 1); 
			} else {
				currentActionListWidget.select(currentActionListWidget.getSize() - 1);
			}
		} else if (InputHandler.popAction(ACTION_DOWN)) {
			if (selectedIndex < currentActionListWidget.getSize() - 1) {
				currentActionListWidget.select(selectedIndex + 1); 
			} else {
				currentActionListWidget.select(0);
			}
		}
		
		if (InputHandler.popAction(ACTION_1)) {
			playerCharacterActionListWidget.select(0);
		} else if (InputHandler.popAction(ACTION_2)) {
			playerCharacterActionListWidget.select(1);
		} else if (InputHandler.popAction(ACTION_3)) {
			playerCharacterActionListWidget.select(2);
		} else if (InputHandler.popAction(ACTION_4)) {
			playerCharacterActionListWidget.select(3);
		} else if (InputHandler.popAction(ACTION_5)) {
			playerCharacterActionListWidget.select(4);
		} else if (InputHandler.popAction(ACTION_6)) {
			playerCharacterActionListWidget.select(5);
		}
		
		if (InputHandler.popAction(ACTION_PLAYER_ACTION_SELECTED)) {
			enemyCharacterActionListWidget.deselectAll();
			CharacterAction action = playerCharacterActionListWidget.getSelectedActionListEntry().getCharacterAction();
			infoTextWidget.setString(action.getDescription());
			if (!InputHandler.isActionActivated(ACTION_ENEMY_BEATEN)) {
				if (action.getAmount() > 0) {
					actionButtonWidget.setString("Attack!");
					actionButtonWidget.show();
				} else {
					actionButtonWidget.hide();
				}
			}
		}
		
		if (InputHandler.popAction(ACTION_ENEMY_ACTION_SELECTED)) {
			playerCharacterActionListWidget.deselectAll();
			infoTextWidget.setString(enemyCharacterActionListWidget.getSelectedActionListEntry().getCharacterAction().getDescription());
			if (!InputHandler.isActionActivated(ACTION_ENEMY_BEATEN)) {
				actionButtonWidget.hide();
			}
		}
		
		if (InputHandler.isActionActivated(ACTION_ENEMY_BEATEN)) {
			return;
		}
		
		if (enemyCharacter.getEnergy() <= 0) {
			InputHandler.activateAction(ACTION_ENEMY_BEATEN);
			actionButtonWidget.setString("Win the battle!");
			actionButtonWidget.show();
			actionButtonWidget.setClickListener(new WidgetClickListener() {
				@Override
				public void onWidgetClick(Widget guiElement) {
					InputHandler.activateAction(Game.ACTION_WIN_BATTLE);
				}
			});
			return;
		}
		
		if (InputHandler.popAction(ACTION_ACTION)) {
			if (playerCharacterActionListWidget.getSelectedActionListEntry() != null) {
				actionButtonWidget.onMouseClickedInside(null, null);
				CharacterAction playerAction = playerCharacterActionListWidget.getSelectedActionListEntry().getCharacterAction();
				playerAction.affectPlayers(playerCharacter, enemyCharacter);
				battleLogListWidget.addEntry(String.format(playerAction.getFormat(), playerCharacter.getName(), enemyCharacter.getName()));

				if (enemyCharacter.getEnergy() > 0) {
					CharacterAction enemyAction = enemyCharacter.getNextAction();
					enemyAction.affectPlayers(enemyCharacter, playerCharacter);
					battleLogListWidget.addEntry(String.format(enemyAction.getFormat(), enemyCharacter.getName(), playerCharacter.getName()));
				}
				
				if (playerAction.getAmount() == 0) {
					actionButtonWidget.hide();
				} else {
					actionButtonWidget.show();
				}
			}
		}
	}
	
	public void setParticipants(PlayerCharacter playerCharacter, EnemyCharacter enemyCharacter) {
		this.playerCharacter = playerCharacter;
		this.enemyCharacter = enemyCharacter;
		playerCharacterNameWidget.setString(this.playerCharacter.getName());
		enemyCharacterNameWidget.setString(this.enemyCharacter.getName());
		playerCharacterPortraitWidget.setGameCharacter(this.playerCharacter);
		enemyCharacterPortraitWidget.setGameCharacter(this.enemyCharacter);
		playerCharacterEnergyBarWidget.setGameCharacter(playerCharacter);
		enemyCharacterEnergyBarWidget.setGameCharacter(enemyCharacter);
		playerCharacterActionListWidget.setGameCharacter(playerCharacter);
		enemyCharacterActionListWidget.setGameCharacter(enemyCharacter);
	}
	
	@Override
	protected void bindActions() {
		super.bindActions();
		bindActionToKey(ACTION_ACTION_SPACE, KeyEvent.VK_SPACE);
		bindActionToKey(ACTION_ACTION_SPACE, KeyEvent.VK_ENTER);
		bindActionToKey(ACTION_1, KeyEvent.VK_1);
		bindActionToKey(ACTION_2, KeyEvent.VK_2);
		bindActionToKey(ACTION_3, KeyEvent.VK_3);
		bindActionToKey(ACTION_4, KeyEvent.VK_4);
		bindActionToKey(ACTION_5, KeyEvent.VK_5);
		bindActionToKey(ACTION_6, KeyEvent.VK_6);
		bindActionToKey(ACTION_LEFT, KeyEvent.VK_LEFT);
		bindActionToKey(ACTION_RIGHT, KeyEvent.VK_RIGHT);
		bindActionToKey(ACTION_UP, KeyEvent.VK_UP);
		bindActionToKey(ACTION_DOWN, KeyEvent.VK_DOWN);
	}
	
	@Override
	protected void unbindActions() {
		super.unbindActions();
		unbindAction(ACTION_ACTION_SPACE, KeyEvent.VK_SPACE);
		unbindAction(ACTION_ACTION_SPACE, KeyEvent.VK_ENTER);
		unbindAction(ACTION_1, KeyEvent.VK_1);
		unbindAction(ACTION_2, KeyEvent.VK_2);
		unbindAction(ACTION_3, KeyEvent.VK_3);
		unbindAction(ACTION_4, KeyEvent.VK_4);
		unbindAction(ACTION_5, KeyEvent.VK_5);
		unbindAction(ACTION_6, KeyEvent.VK_6);
		unbindAction(ACTION_LEFT, KeyEvent.VK_LEFT);
		unbindAction(ACTION_RIGHT, KeyEvent.VK_RIGHT);
		unbindAction(ACTION_UP, KeyEvent.VK_UP);
		unbindAction(ACTION_DOWN, KeyEvent.VK_DOWN);
	}
	
	@Override
	protected void updateSelf(long interval) {
		Screen.setDefaultCursor();
	}

	@Override
	protected void drawSelf(Graphics2D g) {
		g.drawImage(bgImage, getX(), getY(), null);
	}

}
