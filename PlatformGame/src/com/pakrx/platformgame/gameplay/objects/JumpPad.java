package com.pakrx.platformgame.gameplay.objects;

import java.util.ArrayList;
import java.util.List;

import com.pakrx.platformgame.gameplay.characters.GameCharacter;
import com.pakrx.platformgame.gameplay.characters.PlayerCharacter;
import com.pakrx.platformgame.graphics.Animation;
import com.pakrx.platformgame.graphics.Animation.AnimationFrame;
import com.pakrx.platformgame.input.InputHandler;
import com.pakrx.platformgame.resources.ResourceManager;
import com.pakrx.platformgame.sound.SoundFile;

public class JumpPad extends GameObject  {

	private Animation activeAnimation = new Animation();
	private int speedMultipier = 3;
	private String soundFileName = "dang.wav";
	
	public JumpPad() {
		oneTimeUseOnly = false;
		activeAnimation.setOneTime(true);
	}
	
	public JumpPad(ArrayList<AnimationFrame> animationFrames, 
			ArrayList<AnimationFrame> activeAnimationFrames, 
			String soundFileName,
			int speedMultiplier) {
		for (AnimationFrame animationFrame : animationFrames) {
			animation.addFrame(animationFrame);
		}
		for (AnimationFrame animationFrame : activeAnimationFrames) {
			activeAnimation.addFrame(animationFrame);
		}
		this.speedMultipier = speedMultiplier;
		this.soundFileName = soundFileName;
	}
	
	@Override
	public boolean affectCharacter(GameCharacter character) {
		if (character.getVelocityY() > 10 && InputHandler.isActionActivated(PlayerCharacter.ACTION_UP)) {
			float vx = character.getVelocityX();
			float vy = - speedMultipier * character.getJumpingSpeed();
			character.setVelocity(vx, vy);
			character.setState(GameCharacter.STATE_INERT);
			pushedAnimation = activeAnimation;
			SoundFile interactSound =  ResourceManager.readSoundFile(soundFileName);
			interactSound.setLocationX(screenX);
			interactSound.play();
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected int readFromLines(List<String> lines, int i) {
		String line = lines.get(i);
		if (line.startsWith("ACTIVE ANIMATION:")) {
			Animation animation = new Animation();
			animation.setOneTime(true);
			i = animation.readFromLines(lines, i + 1);
			activeAnimation = animation;
		}
		if (line.startsWith("ACTIVE SOUND:")) {
			soundFileName = line.split(ResourceManager.SEPARATOR)[1];
		}
		if (line.startsWith("SPEED MULTIPLIER:")) {
			speedMultipier = Integer.parseInt(line.split(ResourceManager.SEPARATOR)[1]);
		}
		return (i + 1);
	}
	
	
}
