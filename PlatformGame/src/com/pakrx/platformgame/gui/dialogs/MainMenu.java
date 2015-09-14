package com.pakrx.platformgame.gui.dialogs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import com.pakrx.platformgame.Game;
import com.pakrx.platformgame.graphics.Screen;
import com.pakrx.platformgame.gui.widgets.Button;
import com.pakrx.platformgame.gui.widgets.Widget;
import com.pakrx.platformgame.input.InputHandler;

public class MainMenu extends Dialog {

	public final static int ACTION_DOWN = InputHandler.getNextAction();
	public final static int ACTION_UP = InputHandler.getNextAction();
	public final static int ACTION_SELECT = InputHandler.getNextAction();
	
	private int selectedButton = 0;
	private int buttonsCount = 5;
	
	public MainMenu() {
		pausesGame = true;
		dismissable = false;
		singleton = true;
		draggable = true;
		flamed = true;
		topbar = true;
		
		title = "MENU";
		
		
		int width = 400;
		int height = 600;
		int x = (Screen.getWidth() - width) / 2;
		int y = (Screen.getHeight() - height) / 2;
		setBounds(x, y, width, height);
		
		height = height / buttonsCount;
		x = 0;
		y = 0;
		Button continueButton = new Button();
		continueButton.setString("CONTINUE");
		continueButton.setBounds(x, y, width, height);
		continueButton.setClickListener(new WidgetClickListener() {
			@Override
			public void onWidgetClick(Widget guiElement) {
				InputHandler.activateAction(Game.ACTION_MENU_CLOSE);
			}
		});
		continueButton.forcePressBackgroundColor();
		addChild(continueButton);
		
		y += height;
		Button saveButton = new Button();
		saveButton.setString("SAVE");
		saveButton.setBounds(x, y, width, height);
		saveButton.setClickListener(new WidgetClickListener() {
			@Override
			public void onWidgetClick(Widget guiElement) {
				InputHandler.activateAction(Game.ACTION_SAVE_GAME);
				InputHandler.activateAction(Game.ACTION_MENU_CLOSE);
			}
		});
		addChild(saveButton);
		
		y += height;
		Button loadButton = new Button();
		loadButton.setString("LOAD");
		loadButton.setBounds(x, y, width, height);
		loadButton.setClickListener(new WidgetClickListener() {
			@Override
			public void onWidgetClick(Widget guiElement) {
				InputHandler.activateAction(Game.ACTION_LOAD_GAME);
				InputHandler.activateAction(Game.ACTION_MENU_CLOSE);
			}
		});
		addChild(loadButton);
		
		y += height;
		Button startButton = new Button();
		startButton.setString("START NEW GAME");
		startButton.setBounds(x, y, width, height);
		startButton.setClickListener(new WidgetClickListener() {
			@Override
			public void onWidgetClick(Widget guiElement) {
				InputHandler.activateAction(Game.ACTION_NEW_GAME);
				InputHandler.activateAction(Game.ACTION_MENU_CLOSE);
			}
		});
		addChild(startButton);
		
		y += height;
		Button exitButton = new Button();
		exitButton.setString("EXIT");
		exitButton.setBounds(x, y, width, height);
		exitButton.setClickListener(new WidgetClickListener() {
			@Override
			public void onWidgetClick(Widget guiElement) {
				InputHandler.activateAction(Game.ACTION_EXIT);
			}
		});
		addChild(exitButton);
	}
	
	@Override
	protected void updateSelf(long interval) {
		
		if (InputHandler.popAction(ACTION_UP)) {
			if (selectedButton > 0) {
				((Button)(children.get(selectedButton))).releasePressBackgroundColor();
				selectedButton--;
				((Button)(children.get(selectedButton))).forcePressBackgroundColor();
			}
		}
		
		if (InputHandler.popAction(ACTION_DOWN)) {
			if (selectedButton < buttonsCount - 1) {
				((Button)(children.get(selectedButton))).releasePressBackgroundColor();
				selectedButton++;
				((Button)(children.get(selectedButton))).forcePressBackgroundColor();
			}
		}
		
		if (InputHandler.popAction(ACTION_SELECT)) {
			children.get(selectedButton).getClickListener().onWidgetClick(null);
		}
		
	}

	@Override
	protected void drawSelf(Graphics2D g) {
		g.setColor(Color.black);
		g.fillRect(getX(), getY(), getWidth(), getHeight());
	}

	@Override
	protected void bindActions() {
		bindActionToKey(Game.ACTION_MENU_CLOSE, KeyEvent.VK_ESCAPE);
		bindActionToKey(ACTION_UP, KeyEvent.VK_UP);
		bindActionToKey(ACTION_DOWN, KeyEvent.VK_DOWN);
		bindActionToKey(ACTION_SELECT, KeyEvent.VK_ENTER);
		bindActionToKey(ACTION_SELECT, KeyEvent.VK_SPACE);
	}
	
	@Override
	protected void unbindActions() {
		unbindAction(Game.ACTION_MENU_CLOSE, KeyEvent.VK_ESCAPE);
		unbindAction(ACTION_UP, KeyEvent.VK_UP);
		unbindAction(ACTION_DOWN, KeyEvent.VK_DOWN);
		unbindAction(ACTION_SELECT, KeyEvent.VK_ENTER);
		unbindAction(ACTION_SELECT, KeyEvent.VK_SPACE);
	}
}
