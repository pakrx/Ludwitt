package com.pakrx.platformgame.gameplay;

import java.awt.Point;
import java.awt.Rectangle;

import com.pakrx.platformgame.gameplay.objects.GameObject;
import com.pakrx.platformgame.graphics.Tile;
import com.pakrx.platformgame.graphics.TileMap;


public class Physics {

	public final static int DOWN = 1;
	public final static int UP = 2;
	public final static int LEFT = 4;
	public final static int RIGHT = 8;
	public final static int WITHIN = 16;
	public final static int NO_COLLISION = 0;
	
	public final static float GRAVITY = 30.0f;
	public final static float INTERVAL_MULTIPLIER = 0.00000001f;
	public final static long INTERVAL_DIVIDER = 1000000000;
	
	private static TileCollision tileCollision = new TileCollision();
	private static Rectangle rectangleA = new Rectangle();
	private static Rectangle rectangleB = new Rectangle();
	
	public static class TileCollision extends Point {
		
		private static final long serialVersionUID = -7353150713180117800L;

		public TileCollision() {
			super();
		}
		
		public TileCollision(Point point) {
			x = point.x;
			y = point.y;
		}
		
		public Tile tile = null;
	}
	
	public static boolean gameObjectsCollide(GameObject gameObjectA, GameObject gameObjectB) {
		
		if (!gameObjectA.isActive() || !gameObjectB.isActive()) {
			return false;
		}
		
		rectangleA.x = Math.round(gameObjectA.getMapX());
		rectangleA.y = Math.round(gameObjectA.getMapY());
		rectangleA.width = gameObjectA.getWidth();
		rectangleA.height = gameObjectA.getHeight();
		rectangleB.x = Math.round(gameObjectB.getMapX());
		rectangleB.y = Math.round(gameObjectB.getMapY());
		rectangleB.width = gameObjectB.getWidth();
		rectangleB.height = gameObjectB.getHeight();
		
		return rectangleA.intersects(rectangleB);
	}
	
	public static TileCollision checkObjectTileMapCollisions(TileMap tilemap, GameObject object, float testX, float testY) {

		// get the tile locations
		int fromTileX = Math.round(testX) / TileMap.TILE_WIDTH;
		int fromTileY = Math.round(testY) / TileMap.TILE_HEIGHT;
		int toTileX = Math.round(testX + object.getWidth()) / TileMap.TILE_WIDTH;
		int toTileY = Math.round(testY + object.getHeight()) / TileMap.TILE_HEIGHT;

		// check each tile for a collision
		for (int x = fromTileX; x <= toTileX; x++) {
			for (int y = fromTileY; y <= toTileY; y++) {
				if (x <= 0 || x >= tilemap.getWidthTiles() || (tilemap.getTile(x, y) != null && tilemap.getTile(x, y).collides())) {
					tileCollision.x = x;
					tileCollision.y = y;
					tileCollision.tile = tilemap.getTile(x, y);
					return tileCollision;
				}
			}
		}

		// no collision found
		return null;
	}
	
}
