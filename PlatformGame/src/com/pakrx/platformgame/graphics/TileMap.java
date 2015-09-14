package com.pakrx.platformgame.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.pakrx.platformgame.resources.ResourceManager;

public class TileMap {
	
	public final static int TILE_WIDTH = 32;
	public final static int TILE_HEIGHT = 32;
	public final static int DEFAULT_WIDTH = 1920 / TILE_WIDTH + 1;
	public final static int DEFAULT_HEIGHT = 1080 / TILE_HEIGHT + 1;
	
	private Tile tile;
	
	protected Tile[][] tiles;
	
	//protected Image backgroundImage1 = GameImage.createImage("images/background/background1.png");
	//protected Image backgroundImage2 = GameImage.createImage("images/background/background2.png");
	//protected Image backgroundImage3 = GameImage.createImage("images/background/background3.png");
	
	protected int widthTiles = DEFAULT_WIDTH;
	protected int heightTiles = DEFAULT_HEIGHT;
	protected int offsetPixelsX = 0;
	protected int offsetPixelsY = 0;

	protected int screenWidthPixels = -1;
	protected int screenHeightPixels = -1;
	
	public TileMap() {
		
	}
	
	
	public void generateFlatFloor(int level) {
		for (int x = 0; x < widthTiles; x++) {
			tiles[x][level] = tile;
		}
	}
	
	public void setSize(int width, int height) {
		this.widthTiles = width;
		this.heightTiles = height;
		tiles = new Tile[widthTiles][heightTiles];
	}
	
	public int getWidthTiles() {
		return widthTiles;
	}
	
	public int getHeightTiles() {
		return heightTiles;
	}
	
	public Tile getTile(int x, int y) {
		if (x >= 0 && y >= 0 && x < widthTiles && y < heightTiles) {
			return tiles[x][y];
		} else {
			return null;
		}
	}
	
	public void generateSimpleWall(int pos) {
		for (int y = 0; y < heightTiles; y++) {
			tiles[pos][y] = tile;
		}
	}
	
	public void generateRandomFloor(int level, int holesPercentage) {
		Random r = new Random();
		for (int x = 0; x < widthTiles; x++) {
			int pick = r.nextInt(100) + 1;
			int y = level;
			if (pick <= holesPercentage) {
				tiles[x][y] = tile;
			}
		}
	}
	
	public int getWidthPixels() {
		return widthTiles * TILE_WIDTH;
	}
	
	public int getHeightPixels() {
		return heightTiles * TILE_HEIGHT;
	}
	
	public void randomize() {
		int i;
		for (i = 1; i < 8; i++) {
			generateFlatFloor(heightTiles - i);
		}
		generateFlatFloor(0);
		generateRandomFloor(heightTiles - i++, 50);
		generateRandomFloor(heightTiles - i++, 50);
		generateRandomFloor(heightTiles - i, 50);
		generateSimpleWall(0);
		generateSimpleWall(widthTiles - 1);
	}
	
	public void drawTiles(Graphics2D g) {
		screenWidthPixels = g.getDeviceConfiguration().getBounds().width;
		screenHeightPixels = g.getDeviceConfiguration().getBounds().height;
		
		int startMapPosTilesX = offsetPixelsX / TILE_WIDTH;
		int startMapPosTilesY = offsetPixelsY / TILE_HEIGHT;
		int endMapPosTilesX = (screenWidthPixels + offsetPixelsX) / TILE_WIDTH + 1;
		int endMapPosTilesY = (screenHeightPixels + offsetPixelsY) / TILE_HEIGHT + 1;
		
		Tile tile;
		
		int currentScreenPosPixelsX = TILE_WIDTH - offsetPixelsX % TILE_WIDTH - TILE_WIDTH;
		int currentScreenPosPixelsY = TILE_HEIGHT - offsetPixelsY % TILE_HEIGHT - TILE_HEIGHT;
		
		Image[] partialImages = new Image[9];
		Tile[] neighbours = new Tile[8];
		
		for (int currentMapPosTilesY = startMapPosTilesY; 
				currentMapPosTilesY <= endMapPosTilesY; 
				currentMapPosTilesY++) {
			
			if (currentMapPosTilesY < heightTiles && currentMapPosTilesY >= 0) {
				for (int currentMapPosTilesX = startMapPosTilesX; 
						currentMapPosTilesX <= endMapPosTilesX; 
						currentMapPosTilesX++) {
					
					if (currentMapPosTilesX < widthTiles && currentMapPosTilesX >= 0) {
						tile = tiles[currentMapPosTilesX][currentMapPosTilesY];
						if (tile != null) {
							if (tile.getImage() != null) {
								g.drawImage(tile.getImage(), currentScreenPosPixelsX, currentScreenPosPixelsY, null);
							} else {
								neighbours[Tile.N] = currentMapPosTilesY - 1 > 0 ? tiles[currentMapPosTilesX][currentMapPosTilesY - 1] : tile;
								neighbours[Tile.W] = currentMapPosTilesX - 1 > 0 ? tiles[currentMapPosTilesX - 1][currentMapPosTilesY] : tile;
								neighbours[Tile.S] = currentMapPosTilesY + 1 < heightTiles ? tiles[currentMapPosTilesX][currentMapPosTilesY + 1] : tile;
								neighbours[Tile.E] = currentMapPosTilesX + 1 < widthTiles ? tiles[currentMapPosTilesX + 1][currentMapPosTilesY] : tile;
								neighbours[Tile.NW] = currentMapPosTilesX - 1 > 0 && currentMapPosTilesY - 1 > 0 ? tiles[currentMapPosTilesX - 1][currentMapPosTilesY - 1] : tile;
								neighbours[Tile.NE] = currentMapPosTilesX + 1 < widthTiles && currentMapPosTilesY - 1 > 0 ? tiles[currentMapPosTilesX + 1][currentMapPosTilesY - 1] : tile;
								neighbours[Tile.SW] = currentMapPosTilesX - 1 > 0 && currentMapPosTilesY + 1 < heightTiles ? tiles[currentMapPosTilesX - 1][currentMapPosTilesY + 1] : tile;
								neighbours[Tile.SE] = currentMapPosTilesX + 1 < widthTiles && currentMapPosTilesY + 1 < heightTiles ? tiles[currentMapPosTilesX + 1][currentMapPosTilesY + 1] : tile;
								long posSeed = currentMapPosTilesX + currentMapPosTilesY * widthTiles;
								tile.getImages(partialImages, neighbours, posSeed);
								g.drawImage(partialImages[Tile.NW], currentScreenPosPixelsX, currentScreenPosPixelsY, null);
								g.drawImage(partialImages[Tile.N], currentScreenPosPixelsX + 9, currentScreenPosPixelsY, null);
								g.drawImage(partialImages[Tile.NE], currentScreenPosPixelsX + 23, currentScreenPosPixelsY, null);
								g.drawImage(partialImages[Tile.E], currentScreenPosPixelsX + 23, currentScreenPosPixelsY + 9, null);
								g.drawImage(partialImages[Tile.SE], currentScreenPosPixelsX + 23, currentScreenPosPixelsY + 23, null);
								g.drawImage(partialImages[Tile.S], currentScreenPosPixelsX + 9, currentScreenPosPixelsY + 23, null);
								g.drawImage(partialImages[Tile.SW], currentScreenPosPixelsX, currentScreenPosPixelsY + 23, null);
								g.drawImage(partialImages[Tile.W], currentScreenPosPixelsX, currentScreenPosPixelsY + 9, null);
								g.drawImage(partialImages[Tile.C], currentScreenPosPixelsX + 9, currentScreenPosPixelsY + 9, null);
							}
						}
					}
					currentScreenPosPixelsX += TILE_WIDTH;
				}
			}
			currentScreenPosPixelsX = TILE_WIDTH - offsetPixelsX % TILE_WIDTH - TILE_WIDTH;
			currentScreenPosPixelsY += TILE_HEIGHT;
		}
		
	}
	
	public void draw(Graphics2D g) {
		
		//g.drawImage(backgroundImage1, 0, 0, null);
		//g.drawImage(backgroundImage2, -offsetPixelsX / 10, 0, null);
		//g.drawImage(backgroundImage3, -offsetPixelsX / 5, 0, null);
		
		drawTiles(g);
	}
	
	public synchronized int getOffsetX() {
		return offsetPixelsX;
	}
	
	public synchronized int getOffsetY() {
		return offsetPixelsY;
	}
	
	public synchronized void setOffset(int x, int y) {
		offsetPixelsX = x;
		offsetPixelsY = y;
	}
	
	public synchronized void setOffset(Point point) {
		setOffset(point.x, point.y);
	}
	
	public Tile[][] getTiles() {
		return tiles;
	}
	
	public void readFromImage() {
		BufferedImage img = ResourceManager.readTileMapLayoutImage();
		widthTiles = img.getWidth();
		heightTiles = img.getHeight();
		tiles = new Tile[widthTiles][heightTiles];
		for (int x = 0; x < widthTiles; x++) {
			for (int y = 0; y < heightTiles; y++) {
				if (img.getRGB(x, y) == Color.black.getRGB()) {
					tiles[x][y] = tile;
				} else {
					tiles[x][y] = null;
				}
			}
		}
	}
	
}
