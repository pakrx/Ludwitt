package com.pakrx.platformgame.gameplay;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import com.pakrx.platformgame.gameplay.characters.EnemyCharacter;
import com.pakrx.platformgame.gameplay.objects.GameObject;
import com.pakrx.platformgame.graphics.TileMap;

/**
 * Data structure for information about game levels.
 * Reading level from files.
 */
public class Level {

	protected String name;
	protected PlatformWorld gameWorld;
	protected TileMap tileMap;
	protected LinkedList<GameObject> gameObjects = new LinkedList<>();
	protected LinkedList<EnemyCharacter> enemyCharacters = new LinkedList<>();
	protected Point offset = new Point(0, 0);
	
	public Level(String name) {
		this.name = name;
		tileMap = new TileMap();
	}
	
	public Level() {
		tileMap = new TileMap();
	}

	public void readFromImage() {
		tileMap.readFromImage();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public synchronized void update(long interval) {
		
	}
	
	public synchronized void draw(Graphics2D g) {
		tileMap.draw(g);
		for (GameObject gameObject : gameObjects) {
			if (gameObject.isOnScreen()) {
				gameObject.draw(g);
			}
		}
		for (EnemyCharacter enemy : enemyCharacters) {
			if (enemy.isOnScreen()) {
				enemy.draw(g);
			}
		}
	}
	
	public TileMap getTileMap() {
		return tileMap;
	}
	
	public synchronized void setTileMap(TileMap tileMap) {
		this.tileMap = tileMap;
	}
	
	public synchronized void setGameWorld(PlatformWorld gameWorld) {
		this.gameWorld = gameWorld;
	}
	
	public PlatformWorld getGameWorld() {
		return gameWorld;
	}
	
	public synchronized void addGameObject(GameObject gameObject) {
		gameObject.setWorld(gameWorld);
		gameObjects.add(gameObject);
	}
	
	public List<GameObject> getGameObjects() {
		return gameObjects;
	}
	
	public List<EnemyCharacter> getEnemyCharacters() {
		return enemyCharacters;
	}
	
	public synchronized void removeGameObject(GameObject gameObject) {
		gameObject.setWorld(null);
		gameObjects.remove(gameObject);
	}
	
	public synchronized void removeEnemyCharacter(EnemyCharacter enemyCharacter) {
		enemyCharacter.setWorld(null);
		enemyCharacters.remove(enemyCharacter);
	}
	
	public synchronized void addEnemyCharacter(EnemyCharacter enemyCharacter) {
		enemyCharacter.setWorld(gameWorld);
		enemyCharacters.add(enemyCharacter);
	}
	
	public synchronized void setOffset(Point offset) {
		this.offset.x = offset.x;
		this.offset.y = offset.y;
		tileMap.setOffset(offset);
		for (EnemyCharacter enemy : enemyCharacters) {
			enemy.setOffset(offset);
		}
		for (GameObject gameObject : gameObjects) {
			gameObject.setOffset(offset);
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		gameWorld = null;
		tileMap = null;
		gameObjects.clear();
		enemyCharacters.clear();
	}

	public void setPaused(boolean paused) {
		for (EnemyCharacter enemyCharacter : enemyCharacters) {
			enemyCharacter.setPaused(paused);
		}
		for (GameObject gameObject : gameObjects) {
			gameObject.setPaused(paused);
		}
	}
	
	public String getName() {
		return name;
	}
}
