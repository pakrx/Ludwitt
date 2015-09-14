package com.pakrx.platformgame.gameplay.characters;

import java.awt.event.KeyEvent;

import com.pakrx.platformgame.graphics.Screen;
import com.pakrx.platformgame.input.InputHandler;
import com.pakrx.platformgame.resources.ResourceManager;
import com.pakrx.platformgame.sound.SoundFile;

public class PlayerCharacter extends GameCharacter {

	
	public final static int TEST_SMALL = 1;
	public final static int TEST_NORMAL = 2;
	public final static int TEST_BIG = 3;
	public final static int NO_TEST = 0;
	
	public final static int ACTION_UP = InputHandler.getNextAction();
	public final static int ACTION_LEFT = InputHandler.getNextAction();
	public final static int ACTION_RIGHT = InputHandler.getNextAction();
	
	private SoundFile walkingSound;
	
	public PlayerCharacter() {
		super();
		
		energy = 50;
		
		InputHandler.bindActionToKey(ACTION_UP, KeyEvent.VK_UP);
		InputHandler.bindActionToKey(ACTION_UP, KeyEvent.VK_SPACE);
		InputHandler.bindActionToKey(ACTION_LEFT, KeyEvent.VK_LEFT);
		InputHandler.bindActionToKey(ACTION_RIGHT, KeyEvent.VK_RIGHT);
	}
	
	@Override
	public void update(long interval) {
		super.update(interval);
		if (collidingTile != null && collidingTile.deadly) {
			energy = 0;
		}
		if (InputHandler.isActionActivated(ACTION_LEFT) || InputHandler.isActionActivated(ACTION_RIGHT) || InputHandler.isActionActivated(ACTION_UP)) {
			Screen.setInvisibleCursor();
		}
		if (InputHandler.isActionActivated(ACTION_LEFT)) {
			velocityX = -movementSpeed;
			if (state == STATE_GROUND) {
				setState(STATE_WALKING);
			}
		} else if (InputHandler.isActionActivated(ACTION_RIGHT)) {
			velocityX = movementSpeed;
			if (state == STATE_GROUND) {
				setState(STATE_WALKING);
			}
		} else {
			velocityX = 0;
			if (state == STATE_WALKING) {
				setState(STATE_GROUND);
			}
		}
		
		if (state == STATE_WALKING && Math.abs(velocityX) > 0 && (walkingSound == null || !walkingSound.isPlaying())) {
			walkingSound = ResourceManager.readSoundFile("tumptump.wav");
			walkingSound.setLocationX(screenX);
			walkingSound.loop();
		} else if (walkingSound != null && (state != STATE_WALKING || Math.abs(velocityX) == 0) || world.isPaused()) {
			walkingSound.stop();
		}
		if (walkingSound != null) {
			walkingSound.setLocationX(screenX);
		}
		
		if (state != STATE_INERT) {
			if (InputHandler.isActionActivated(ACTION_UP)) {
				if (state == STATE_GROUND || state == STATE_WALKING) {
					velocityY = -jumpingSpeed;
					setState(STATE_JUMPING);
				}
			} else {
				if (velocityY < 0) {
					velocityY = 0;
				}
			}
		}
	}
	
	@Override
	public void die() {
		super.die();
		if (walkingSound != null) {
			walkingSound.stop();
		}
	}
	
	@Override
	protected synchronized void finalize() throws Throwable {
		walkingSound.stop();
		walkingSound = null;
	}
	
	@Override
	public void setPaused(boolean paused) {
		super.setPaused(paused);
		if (paused && walkingSound != null) {
			walkingSound.stop();
		}
	}
	

}
