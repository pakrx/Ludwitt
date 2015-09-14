package com.pakrx.platformgame.gameplay;

import java.awt.Graphics2D;
import java.awt.Point;

import com.pakrx.platformgame.Game;
import com.pakrx.platformgame.gameplay.Physics.TileCollision;
import com.pakrx.platformgame.gameplay.characters.EnemyCharacter;
import com.pakrx.platformgame.gameplay.characters.PlayerCharacter;
import com.pakrx.platformgame.gameplay.objects.GameObject;
import com.pakrx.platformgame.graphics.Screen;
import com.pakrx.platformgame.graphics.TileMap;
import com.pakrx.platformgame.input.InputHandler;

/**
 * Game logic corresponding to platform jumping walking world mode.
 */
public class PlatformWorld {
	
	protected PlayerCharacter playerCharacter;
	protected EnemyCharacter interactingEnemyCharacter;
	protected GameObject interactingGameObject;
	protected Level level;
	protected boolean paused = false;
	protected Point offset = new Point(0, 0);
	protected Point playerStartLocation = new Point(300, 300);
	
	public PlatformWorld() {

	}

	public boolean isPaused() {
		return paused;
	}
	
	public void update(long interval) {
		if (!paused) {
			playerCharacter.update(interval);
			level.update(interval);
			
			EnemyCharacter collidingEnemy = null;
			for (EnemyCharacter enemy : level.getEnemyCharacters()) {
				if (enemy.isOnScreen()) {
					enemy.update(interval);
					if (collidingEnemy == null && Physics.gameObjectsCollide(enemy, playerCharacter)) {
						collidingEnemy = enemy;
					}
				}
			}
			
			GameObject collidingObject = null;
			for (GameObject gameObject : level.getGameObjects()) {
				if (gameObject.isOnScreen()) {
					gameObject.update(interval);
					if (collidingObject == null && Physics.gameObjectsCollide(gameObject, playerCharacter)) {
						collidingObject = gameObject;
					}
				}
			}
			
			if (collidingEnemy != null) {
				onPlayerEnemyCharacterCollision(collidingEnemy);
			} else	if (collidingObject != null && collidingEnemy == null) {
				onPlayerGameObjectCollision(collidingObject);
			}
			
			int xdiv = 4;
			int xOffsetBefore = offset.x;
			int xOffsetMiddle = (int)playerCharacter.getMapX() - (Screen.getWidth() - playerCharacter.getWidth()) / 2;
			int xOffsetMaxLeft = (int)playerCharacter.getMapX() - Screen.getWidth() / xdiv;
			int xOffsetRightBorder = level.getTileMap().getWidthTiles() * TileMap.TILE_WIDTH - Screen.getWidth();
			int xOffsetLeftBorder = 0;
			int prevxof = offset.x;
			offset.x = Math.max(xOffsetBefore, xOffsetMiddle);
			offset.x = Math.min(offset.x, xOffsetMaxLeft);
			offset.x = Math.min(Math.max(offset.x, xOffsetLeftBorder), xOffsetRightBorder);
			offset.x = (prevxof + offset.x) / 2;
			
			int ydiv = 4;
			offset.y = level.getTileMap().getHeightPixels() - Screen.getHeight();
			int yOffsetTop = 0;
			int yOffsetBottom = level.getTileMap().getHeightPixels() - Screen.getHeight();
			int yOffsetMaxDown = (int)playerCharacter.getMapY() - (ydiv - 1) * Screen.getHeight() / ydiv - playerCharacter.getHeight();
			int yOffsetMaxUp = (int)playerCharacter.getMapY() - Screen.getHeight() / ydiv;
			offset.y = Math.max(yOffsetMaxDown, yOffsetMaxUp);
			offset.y = Math.max(offset.y, yOffsetTop);
			offset.y = Math.min(offset.y, yOffsetBottom);
			
			level.setOffset(offset);
			playerCharacter.setOffset(offset);
		}
		
		playerCharacter.updateAnimation(interval);
		for (EnemyCharacter enemy : level.getEnemyCharacters()) {
			if (enemy.isOnScreen()) {
				enemy.updateAnimation(interval);
			}
		}
		for (GameObject gameObject : level.getGameObjects()) {
			if (gameObject.isOnScreen()) {
				gameObject.updateAnimation(interval);
			}
		}
		
	}

	public synchronized void draw(Graphics2D g) {
		level.draw(g);
		playerCharacter.draw(g);
	}
	
	private void onPlayerGameObjectCollision(final GameObject collidingObject) {
		this.interactingGameObject = collidingObject;
		if (collidingObject.isBlocking()) {
			if (playerCharacter.getVelocityX() >= 0) {
				float x = collidingObject.getX() - playerCharacter.getWidth() + offset.x - 1;
				float y = playerCharacter.getY() + offset.y;
				playerCharacter.setLocation(x, y);
				playerCharacter.setVelocity(0, playerCharacter.getVelocityY());
			} else if (playerCharacter.getVelocityX() < 0){
				float x = collidingObject.getX() + collidingObject.getWidth() + offset.x + 1;
				float y = playerCharacter.getY() + offset.y;
				playerCharacter.setLocation(x, y);
				playerCharacter.setVelocity(0, playerCharacter.getVelocityY());
			}
		}
		InputHandler.activateAction(Game.ACTION_INTERACT_WITH_OBJECT);
	}

	private void onPlayerEnemyCharacterCollision(final EnemyCharacter collidingEnemy) {
		this.interactingEnemyCharacter = collidingEnemy;
		InputHandler.activateAction(Game.ACTION_START_BATTLE);
	}
	
	public void setPlayerCharacter(PlayerCharacter playerCharacter) {
		this.playerCharacter = playerCharacter;
		playerCharacter.setLocation(playerStartLocation);
		playerCharacter.setWorld(this);
	}
	
	public PlayerCharacter getPlayerCharacter() {
		return playerCharacter;
	}
	
	public Level getLevel() {
		return level;
	}
	
	public synchronized void setLevel(Level level) {
		this.level = level;
		level.setGameWorld(this);
		if (playerCharacter != null) {
			playerCharacter.setLocation(playerStartLocation);
			playerCharacter.setVelocity(0, 0);
		}
	}
	
	public TileCollision getObjectTileMapCollision(GameObject gameObject, float x, float y) {
		return Physics.checkObjectTileMapCollisions(getLevel().getTileMap(), gameObject, x, y);
	}
	
	public void setOffset(Point offset) {
		this.offset.x = offset.x;
		this.offset.y = offset.y;
	}
	
	public synchronized void setPaused(boolean paused) {
		if (this.paused != paused) {
			InputHandler.releaseAll();
		}
		this.paused = paused;
		level.setPaused(paused);
		playerCharacter.setPaused(paused);
	}
	
	public EnemyCharacter getInteractingEnemy() {
		return interactingEnemyCharacter;
	}
	
	public GameObject getInteractingGameObject() {
		return interactingGameObject;
	}
	
}

