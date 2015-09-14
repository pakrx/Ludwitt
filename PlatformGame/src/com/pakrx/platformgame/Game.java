package com.pakrx.platformgame;
import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

import javax.imageio.ImageIO;

import com.pakrx.platformgame.gameplay.Level;
import com.pakrx.platformgame.gameplay.PlatformWorld;
import com.pakrx.platformgame.gameplay.characters.EnemyCharacter;
import com.pakrx.platformgame.gameplay.characters.GameCharacter;
import com.pakrx.platformgame.gameplay.characters.PlayerCharacter;
import com.pakrx.platformgame.gameplay.objects.Door;
import com.pakrx.platformgame.gameplay.objects.GameObject;
import com.pakrx.platformgame.graphics.Screen;
import com.pakrx.platformgame.graphics.Screen.UnsupportedResolutionException;
import com.pakrx.platformgame.gui.dialogs.BattleDialog;
import com.pakrx.platformgame.gui.dialogs.Dialog;
import com.pakrx.platformgame.gui.dialogs.EnergyBarDialog;
import com.pakrx.platformgame.gui.dialogs.MainMenu;
import com.pakrx.platformgame.gui.dialogs.MessageDialog;
import com.pakrx.platformgame.gui.widgets.Widget;
import com.pakrx.platformgame.input.InputHandler;
import com.pakrx.platformgame.resources.ResourceManager;


public class Game implements WindowFocusListener {
	
	public final static int ACTION_EXIT = InputHandler.getNextAction();
	public final static int ACTION_SHOW_DEBUG_WINDOW = InputHandler.getNextAction();
	public final static int ACTION_START_BATTLE = InputHandler.getNextAction();
	public final static int ACTION_INTERACT_WITH_OBJECT = InputHandler.getNextAction();
	public final static int ACTION_NEXT_LEVEL = InputHandler.getNextAction();
	public final static int ACTION_GAME_OVER = InputHandler.getNextAction();
	public final static int ACTION_GAME_OVER_OK = InputHandler.getNextAction();
	public final static int ACTION_WIN_BATTLE = InputHandler.getNextAction();
	public final static int ACTION_WIN_BATTLE_OK = InputHandler.getNextAction();
	public final static int ACTION_TAKE_GARBAGE = InputHandler.getNextAction();
	public final static int ACTION_PRINTSCREEN = InputHandler.getNextAction();
	public final static int ACTION_SAVE_GAME = InputHandler.getNextAction();
	public final static int ACTION_LOAD_GAME = InputHandler.getNextAction();
	public final static int ACTION_NEW_GAME = InputHandler.getNextAction();
	public final static int ACTION_MENU = InputHandler.getNextAction();
	public final static int ACTION_MENU_CLOSE = InputHandler.getNextAction();
	
	private PlatformWorld platformWorld;
	private String module;
	private Stack<Dialog> overlayWindows = new Stack<>();
	private EnergyBarDialog energyBarDialog;

	public static void main(String[] args) {
		Game game = new Game();
		game.init();
		game.loop();
	}
	
	public Game() {
		
	}
	
	private void init() {
		
		int x = Screen.getDefaultWidth();
		int y = Screen.getDefaultHeight();
		try {
			Screen.initScreen(x, y);
		} catch (UnsupportedResolutionException ex) {
			log("Unsupported Screen Resolution " + x + ":" + y);
			stop();
		}
		
		Screen.setInvisibleCursor();

		module = "main";
		ResourceManager.setModule(module);
		ResourceManager.readStrings();
		
		platformWorld = new PlatformWorld();
		Level level = new Level();
		platformWorld.setLevel(level);
		ResourceManager.readLevel(level);

		PlayerCharacter playerCharacter = ResourceManager.readPlayerCharacter();
		platformWorld.setPlayerCharacter(playerCharacter);
		
		energyBarDialog = new EnergyBarDialog();
		energyBarDialog.setGameCharacter(platformWorld.getPlayerCharacter());
		putDialog(energyBarDialog);
		
		InputHandler.bindActionToKey(ACTION_MENU, KeyEvent.VK_ESCAPE);
		InputHandler.bindActionToKey(ACTION_SHOW_DEBUG_WINDOW, KeyEvent.VK_BACK_SPACE);
		InputHandler.bindActionToKey(ACTION_TAKE_GARBAGE, KeyEvent.VK_G);
		InputHandler.bindActionToKey(ACTION_PRINTSCREEN, KeyEvent.VK_BACK_SPACE);
		InputHandler.bindActionToKey(ACTION_SAVE_GAME, KeyEvent.VK_F5);
		InputHandler.bindActionToKey(ACTION_LOAD_GAME, KeyEvent.VK_F9);
		
		Screen.getFrame().addKeyListener(InputHandler.getKeyListener());
		Screen.getFrame().addMouseListener(InputHandler.getMouseInputListener());
		Screen.getFrame().addMouseMotionListener(InputHandler.getMouseInputListener());
		Screen.getFrame().addWindowFocusListener(this);
		
		load();
	}
	
	public void loop() {
		long previousloopstarttime = currentTime();
		long loopstarttime = currentTime();
		while (true) {
			previousloopstarttime = loopstarttime;
			loopstarttime = currentTime();
			long interval = loopstarttime - previousloopstarttime;
			
			update(interval);
			draw();
		}
	}
	
	public void load() {
		boolean gameLoaded = ResourceManager.loadGame(this);
		if (!gameLoaded) {
			startNew();
		}
	}
	
	public void startNew() {
		Level level = new Level();
		platformWorld.setLevel(level);
		ResourceManager.setLevel(ResourceManager.DEFAULT_LEVEL);
		ResourceManager.readLevel(level);
		PlayerCharacter playerCharacter = ResourceManager.readPlayerCharacter();
		platformWorld.setPlayerCharacter(playerCharacter);
		energyBarDialog.setGameCharacter(playerCharacter);
		getPlatformWorld().getPlayerCharacter().setLocation(100.0f, 100.0f);
		save();
	}
	
	public void nextLevel() {
		Level level = new Level();
		platformWorld.setLevel(level);
		Door door = (Door)platformWorld.getInteractingGameObject();
		ResourceManager.setLevel(door.getLevelName());
		ResourceManager.readLevel(level);
		getPlatformWorld().getPlayerCharacter().setLocation(100.0f, 100.0f);
		save();
	}
	
	public void save() {
		ResourceManager.saveGame(this);
	}
	
	public PlatformWorld getPlatformWorld() {
		return platformWorld;
	}
	
	public void update(long interval) {

		if (InputHandler.popAction(ACTION_EXIT)) {
			if (overlayWindows.isEmpty() || !overlayWindows.peek().escapeCloses()) {
				stop();
			} else {
				removeDialog();
			}
		}
		
		if (InputHandler.popAction(ACTION_SHOW_DEBUG_WINDOW)) {
			// TODO
		}
		
		boolean shouldpause = false;
		for (Dialog overlayWindow : overlayWindows) {
			if (overlayWindow.pausesGame()) {
				shouldpause = true;
				break;
			}
		}
		platformWorld.setPaused(shouldpause);
		
		if (InputHandler.popAction(ACTION_GAME_OVER_OK)) {
			load();
			removeDialog();
		}
		
		if (InputHandler.popAction(ACTION_NEW_GAME)) {
			startNew();
		}
		
		if (InputHandler.popAction(ACTION_GAME_OVER)) {
			MessageDialog gameOverDialog = new MessageDialog();
			gameOverDialog.setOkClickListener(new Widget.WidgetClickListener() {
				@Override
				public void onWidgetClick(Widget guiElement) {
					InputHandler.activateAction(ACTION_GAME_OVER_OK);
				}
			});
			gameOverDialog.setTitle("GAME OVER");
			gameOverDialog.setString("You have lost the game. Try again.");
			putDialog(gameOverDialog);
		}

		if (platformWorld.getPlayerCharacter().getEnergy() <= 0) {
			platformWorld.getPlayerCharacter().setState(GameCharacter.STATE_DEAD);
			platformWorld.getPlayerCharacter().die();
			InputHandler.activateAction(ACTION_GAME_OVER);
		}
		
		if (InputHandler.popAction(ACTION_START_BATTLE)) {
			BattleDialog battleDialog = new BattleDialog();
			battleDialog.setParticipants(platformWorld.getPlayerCharacter(), platformWorld.getInteractingEnemy());
			putDialog(battleDialog);
		}
		

		if (InputHandler.popAction(ACTION_MENU)) {
			MainMenu mainMenu = new MainMenu();
			putDialog(mainMenu);
		}
		
		if (InputHandler.popAction(ACTION_MENU_CLOSE)) {
			removeDialog();
		}
		
		if (InputHandler.popAction(ACTION_WIN_BATTLE)) {
			EnemyCharacter enemyCharacter = platformWorld.getInteractingEnemy();
			platformWorld.getLevel().removeEnemyCharacter(enemyCharacter);
			for (GameObject loot : enemyCharacter.getLoot()) {
				platformWorld.getLevel().addGameObject(loot);
			}
			platformWorld.getPlayerCharacter().resetActionsAmounts();
			removeDialog();
			MessageDialog battleVictoryDialog = new MessageDialog();
			battleVictoryDialog.setOkClickListener(new Widget.WidgetClickListener() {
				@Override
				public void onWidgetClick(Widget guiElement) {
					InputHandler.activateAction(ACTION_WIN_BATTLE_OK);
				}
			});
			battleVictoryDialog.setTitle("CONGRATULATIONS");
			battleVictoryDialog.setString("You have defeated " + platformWorld.getInteractingEnemy().getName() + ", and won the battle!");
			putDialog(battleVictoryDialog);
		}
		
		if (InputHandler.popAction(ACTION_WIN_BATTLE_OK)) {
			removeDialog();
		}
		
		if (InputHandler.popAction(ACTION_INTERACT_WITH_OBJECT)) {
			GameObject interactingObject = platformWorld.getInteractingGameObject();
			if (interactingObject.getState() != GameObject.STATE_DISABLED) {
				boolean used = interactingObject.affectCharacter(platformWorld.getPlayerCharacter());
				if (used) {
					if (interactingObject.isPickable()) {
						platformWorld.getLevel().removeGameObject(interactingObject);
					}
					if (interactingObject.isOneTimeUseOnly()) {
						interactingObject.setState(GameObject.STATE_DISABLED);
					}
				}
			}
		}
		
		for (Dialog overlayWindow : overlayWindows) {
			overlayWindow.update(interval);
		}
		platformWorld.update(interval);
		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (InputHandler.popAction(ACTION_TAKE_GARBAGE)) {
			System.gc();
		}
		
		if (InputHandler.popAction(ACTION_PRINTSCREEN)) {
			Robot robot = null;
			try {
				robot = new Robot();
			} catch (AWTException e) {
				e.printStackTrace();
			}
			BufferedImage image = robot.createScreenCapture(new Rectangle(0, 0, Screen.getWidth(), Screen.getHeight()));
			File file = new File("screen.png");
			try {
				ImageIO.write(image, "png", file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (InputHandler.popAction(ACTION_SAVE_GAME) && platformWorld.getPlayerCharacter().getEnergy() > 0) {
			save();
		}
		
		if (InputHandler.popAction(ACTION_LOAD_GAME)) {
			load();
		}
		
		
		if (InputHandler.popAction(ACTION_NEXT_LEVEL)) {
			nextLevel();
		}
		
	}
	
	public void putDialog(Dialog overlayWindow) {
		if (overlayWindow.isSingleton() && overlayWindows.contains(overlayWindow)) {
			return;
		}
		if (!overlayWindows.isEmpty()) {
			overlayWindows.peek().setEnabled(false);
		}
		overlayWindows.push(overlayWindow);
		overlayWindow.setEnabled(true);
		InputHandler.releaseAll();
	}
	
	public void removeDialog() {
		if (!overlayWindows.isEmpty()) {
			overlayWindows.pop().setEnabled(false);
		}
		if (!overlayWindows.isEmpty()) {
			overlayWindows.peek().setEnabled(true);
		}
		InputHandler.releaseAll();
	}

	public synchronized void draw() {
		Screen.drawBackground();
		Graphics2D g = Screen.getGraphics();
		
		platformWorld.draw(g);
		for (Dialog overlayWindow : overlayWindows) {
			overlayWindow.draw(g);
		}
		
		Screen.finishDrawing();
	}
	
	public static void log(String message) {
		System.out.println(message);
	}
	
	public void stop() {
		System.exit(0);
	}
	
	
	public long currentTime() {
		return System.nanoTime();
	}

	@Override
	public void windowGainedFocus(WindowEvent e) {
		
	}

	@Override
	public void windowLostFocus(WindowEvent e) {
		
	}
	
}
