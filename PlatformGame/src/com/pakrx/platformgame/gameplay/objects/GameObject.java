package com.pakrx.platformgame.gameplay.objects;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.pakrx.platformgame.gameplay.Physics;
import com.pakrx.platformgame.gameplay.Physics.TileCollision;
import com.pakrx.platformgame.gameplay.PlatformWorld;
import com.pakrx.platformgame.gameplay.characters.GameCharacter;
import com.pakrx.platformgame.graphics.Animation;
import com.pakrx.platformgame.graphics.Screen;
import com.pakrx.platformgame.graphics.Tile;
import com.pakrx.platformgame.graphics.TileMap;

public abstract class GameObject {
	
	public final static int STATE_GROUND = 0;
	public final static int STATE_DISABLED = 1;
	public final static int STATE_INERT = 2;
	
	protected int state = STATE_GROUND;
	
	public final static int DIRECTION_LEFT = 0;
	public final static int DIRECTION_RIGHT = 1;
	
	protected int direction = DIRECTION_RIGHT;
	
	protected boolean physical = false;
	protected boolean moving = false;
	protected boolean pickable = false;
	protected boolean oneTimeUseOnly = true;
	protected boolean blocking = false;
	
	protected PlatformWorld world;
	protected Image image;
	protected float mapX;
	protected float mapY;
	protected int screenX;
	protected int screenY;
	protected int width = -1;
	protected int height = -1;
	protected int offsetPixelsX = 0;
	protected int offsetPixelsY = 0;
	protected float velocityX = 0.0f;
	protected float velocityY = 0.0f;
	protected float initialMapX;
	protected float initialMapY;
	protected Tile collidingTile = null;
	
	protected boolean paused = false;
	
	// animation played once disregarding state and direction
	protected Animation pushedAnimation;
	
	// animation for current state
	protected Animation animation;
	
	// animations for different states and directions (optional)
	protected LinkedList<Animation> animations = new LinkedList<>();

	protected int movementSpeed = 50;
	protected int jumpingSpeed = 60;

	private float lastCollisionFreeX;
	private float lastCollisionFreeY;
	
	private String typeFileName = null;
	
	
	public synchronized void setImage(Image image) {  
		this.image = image;
		width = image.getWidth(null);
		height = image.getHeight(null);
	}
	
	public Image getImage() {
		return image;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setInitialPosition(float x, float y) {
		initialMapX = x;
		initialMapY = y;
	}
	
	public int getTileWidth() {
		return width / TileMap.TILE_WIDTH;
	}
	
	public int getTileHeight() {
		return height / TileMap.TILE_HEIGHT;
	}
	
	public void randomizeAnimationOffset() {
		animation.setOffset(new Random().nextLong() % animation.getTotalTime());
	}
	
	public synchronized void setLocation(float mapX, float mapY) {
		this.mapX = mapX;
		this.mapY = mapY;
		screenX = Math.round(mapX - offsetPixelsX);
		screenY = Math.round(mapY - offsetPixelsY);
	}
	
	public synchronized void setLocation(Point point) {
		setLocation(point.x, point.y);
	}
	
	public synchronized void setScreenLocation(float x, float y) {
		setLocation(x - offsetPixelsX, y - offsetPixelsY);
	}
	
	public float getX() {
		return screenX;
	}
	
	public float getY() {
		return screenY;
	}
	
	public float getMapX() {
		return mapX;
	}
	
	public float getMapY() {
		return mapY;
	}
	
	public synchronized void draw(Graphics2D g) {
		g.drawImage(image, screenX, screenY, null);
	}
	
	public synchronized void setVelocity(float x, float y) {
		this.velocityX = x;
		this.velocityY = y;
	}
	
	public float getVelocityX() {
		return velocityX;
	}
	
	public float getVelocityY() {
		return velocityY;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public String getIdentificator() {
		return typeFileName;
	}
	
	public void setIdentificator(String identificator) {
		this.typeFileName = identificator;
	}
	
	public synchronized void update(long interval) {
		if (moving && !physical) {
			float dx = (interval * Physics.INTERVAL_MULTIPLIER * getVelocityX());
			float dy = (interval * Physics.INTERVAL_MULTIPLIER * getVelocityY()); 
	
			mapX += Math.round(dx);
			mapY += Math.round(dy);
			
			screenX = Math.round(mapX - offsetPixelsX);
			screenY = Math.round(mapY - offsetPixelsY);
			
			if (velocityX < 0) {
				setDirection(DIRECTION_LEFT);
			} else if (velocityX > 0) {
				setDirection(DIRECTION_RIGHT);
			}
		} else if (moving && physical) {
			float dx = (interval * Physics.INTERVAL_MULTIPLIER * velocityX);
			float dy = (interval * Physics.INTERVAL_MULTIPLIER * velocityY);

			float oldX = mapX;
			float newX = Math.round(mapX + dx);
			float newY = Math.round(mapY + dy);
			
			TileCollision verticalCollision = world.getObjectTileMapCollision(this, oldX, newY);
			if (verticalCollision != null) {
				collidingTile = verticalCollision.tile;
			} else {
				collidingTile = null;
			}
			if (verticalCollision != null) {
				if (velocityY > 0) {
					newY = Math.round(verticalCollision.y * TileMap.TILE_HEIGHT - getHeight()) - 1;
					state = STATE_GROUND;
				} else if (velocityY < 0) {
					newY = Math.round((verticalCollision.y + 1) * TileMap.TILE_HEIGHT) + 1; 
					state = STATE_INERT;
				}
				velocityY = 0;
			} else {
				velocityY += (interval * Physics.INTERVAL_MULTIPLIER * Physics.GRAVITY);
				lastCollisionFreeY = newY;
			}

			TileCollision horizontalCollision = world.getObjectTileMapCollision(this, newX, newY);
			if (collidingTile == null && horizontalCollision != null) {
				collidingTile = horizontalCollision.tile;
			}
			if (horizontalCollision != null) {
				if (velocityX > 0) {
					newX = horizontalCollision.x * TileMap.TILE_WIDTH - getWidth() - 1;
				} else if (velocityX < 0) {
					newX = (horizontalCollision.x + 1) * TileMap.TILE_WIDTH + 1; 
				}
			} else {
				lastCollisionFreeX = newX;
			}
			
			setLocation(newX, newY);
			horizontalCollision = world.getObjectTileMapCollision(this, newX, newY);
			verticalCollision = world.getObjectTileMapCollision(this, newX, newY);
			if (horizontalCollision != null || verticalCollision != null) {
				setLocation(lastCollisionFreeX, lastCollisionFreeY);
				velocityX = 0;
				velocityY = 0;
			}

			if (velocityX < 0) {
				setDirection(DIRECTION_LEFT);
			} else if (velocityX > 0) {
				setDirection(DIRECTION_RIGHT);
			}
		}
	}
	
	public void updateAnimation(long interval) {
		if (pushedAnimation != null && !pushedAnimation.isDone() && pushedAnimation.getTotalTime() > 0) {
			pushedAnimation.update((long) (interval * 0.000001f));
			setImage(pushedAnimation.getCurrentImage());
			if (pushedAnimation.isDone()) {
				pushedAnimation.reset();
				pushedAnimation = null;
			}
		} else {
			Animation foundAnimation = null;
			if (animation == null) {
				if (animations.size() > 0) {
					animation = animations.getFirst();
				} else {
					return;
				}
			}
			if (animation.getState() != getState()) {
				for (Animation a : animations) {
					if (a.getState() == getState() && a.getDirection() == getDirection()) {
						foundAnimation = a;
						break;
					}
				}
				if (foundAnimation == null) {
					for (Animation a : animations) {
						if (a.getState() == getState()) {
							foundAnimation = a;
							break;
						}
					}
				}
				if (foundAnimation == null) {
					for (Animation a : animations) {
						if (a.getState() == GameObject.STATE_GROUND && a.getDirection() == getDirection()) {
							foundAnimation = a;
							break;
						}
					}
				}
			} else if (animation.getDirection() != getDirection()) {
				for (Animation a : animations) {
					if (a.getState() == getState() && a.getDirection() == getDirection()) {
						foundAnimation = a;
						break;
					}
				}
			}
			if (foundAnimation != null) {
				foundAnimation.setOffset(animation.getOffset());
				animation = foundAnimation;
			}
			animation.update((long) (interval * 0.000001f));
			setImage(animation.getCurrentImage());
		}
	}
	
	public int getDirection() {
		return direction;
	}
	
	public synchronized void setOffset(int x, int y) {
		offsetPixelsX = x;
		offsetPixelsY = y;
		
		screenX = Math.round(this.mapX - offsetPixelsX);
		screenY = Math.round(this.mapY - offsetPixelsY);
	}
	
	public synchronized void setOffset(Point point) {
		setOffset(point.x, point.y);
	}
	
	public float getOffsetY() {
		return offsetPixelsY;
	}
	
	public float getOffsetX() {
		return offsetPixelsX;
	}
	
	public boolean isOnScreen() {
		return (mapX >= getOffsetX() - Screen.getWidth()  && mapX <= getOffsetX() + 2 * Screen.getWidth() &&
				mapY >= getOffsetY() - Screen.getHeight() && mapY <= getOffsetY() + 2 * Screen.getHeight());
	}
	
	public synchronized void setState(int satte) {
		this.state = satte;
	}
	
	public int getState() {
		return state;
	}
	
	public boolean isActive() {
		return (state != STATE_DISABLED);
	}

	public boolean affectCharacter(GameCharacter character) {
		// gain new action, kill, heal, etc. 
		return true;
	}
	
	public void setWorld(PlatformWorld world) {
		this.world = world;
	}
	
	@Override
	protected void finalize() throws Throwable {
		pushedAnimation = null;
		animation = null;
		animations.clear();
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public boolean isPickable() {
		return pickable;
	}

	public boolean isOneTimeUseOnly() {
		return oneTimeUseOnly;
	}
	
	public boolean isBlocking() {
		return blocking;
	}
	
	public void readFromSaveFile(String filename, int linenumber) {
		ArrayList<String> lines = null;
		try {
			lines = new ArrayList<String>(Files.readAllLines(Paths.get(filename), Charset.forName("UTF-8")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (lines == null) {
			return;
		}
		String[] line = lines.get(linenumber).split(": ");
		String idfile = line[2];
		float mapx = Float.valueOf(line[3]);
		float mapy = Float.valueOf(line[4]);
		
		readFromFile(idfile);
		setLocation(mapx, mapy);
		setInitialPosition(mapx, mapy);
		getAnimation().reset();
	}
	
	public void writeToSaveFile(PrintWriter writer) {
		writer.write("\n");
		writer.write("OBJECT: ");
		writer.write(getClass().getSimpleName());
		writer.write(": ");
		writer.write(typeFileName);
		writer.write(": ");
		writer.write(String.valueOf(initialMapX));
		writer.write(": ");
		writer.write(String.valueOf(initialMapY));
	}
	
	public void readFromFile(String filename) {
		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(filename), Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (lines != null) {
			typeFileName = filename;
			readFromStrings(lines);
		}
	}
	
	public void readFromStrings(List<String> strings) {
		String line;
		for (int i = 0; i < strings.size(); i++) {
			line = strings.get(i);
			if (line.trim().length() == 0) {
				continue;
			}
			if (line.startsWith("ANIMATION:")) {
				Animation animation = new Animation();
				i = animation.readFromLines(strings, i + 1);
				animations.add(animation);
				this.animation = animation;
			}
			readFromLines(strings, i);
		}
	}
	
	protected abstract int readFromLines(List<String> lines, int i);

	public Animation getAnimation() {
		return animation;
	}

	public void addAnimation(Animation animation) {
		animations.add(animation);
		if (this.animation == null) {
			this.animation = animation;
		}
	}
}
