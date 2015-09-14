package com.pakrx.platformgame.graphics;

import java.awt.Image;
import java.util.LinkedList;
import java.util.List;

import com.pakrx.platformgame.gameplay.objects.GameObject;
import com.pakrx.platformgame.resources.ResourceManager;

public class Animation {
	
	private int direction = GameObject.DIRECTION_RIGHT;
	private int state = GameObject.STATE_GROUND;
	private long offset = 0;
	private long currentTime = 0;
	private long totalTime = 0;
	private boolean onetime = false;
	
	private LinkedList<AnimationFrame> frames = new LinkedList<>();
	
	public static class AnimationFrame {
		Image image;
		long duration;
		
		public AnimationFrame(Image image, long durationMilliseconds) {
			this.image = image;
			this.duration = durationMilliseconds;
		}
		
		@Override
		protected void finalize() throws Throwable {
			this.image = null;
		}
	}
	
	public int getFramesCount() {
		return frames.size();
	}
	
	public Animation(long offset) {
		this.offset = offset;
	}
	
	public Animation() {
		this.offset = 0;
	}
	
	public void setOneTime(boolean onetime) {
		this.onetime = onetime;
	}
	
	public boolean isOneTime() {
		return onetime;
	}
	
	public boolean isDone() {
		if (!onetime) {
			return false;
		} else {
			return currentTime >= totalTime;
		}
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public int getDirection() {
		return direction;
	}
	
	public void setState(int state) {
		this.state = state;
	}
	
	public int getState() {
		return state;
	}
	
	public synchronized void addFrame(AnimationFrame frame) {
		frames.add(frame);
		totalTime += frame.duration;
	}
	
	public synchronized void addFrame(String imageFileName, long milliseconds) {
		Image image = ResourceManager.readImage(imageFileName);
		AnimationFrame frame = new AnimationFrame(image, milliseconds);
		addFrame(frame);
	}
	
	public void setOffset(long offset) {
		this.offset = offset;
	}
	
	public long getOffset() {
		return offset;
	}
	
	public long getTotalTime() {
		return totalTime;
	}
	
	public synchronized void clear() {
		for (AnimationFrame frame : frames) {
			frame.image = null;
		}
		frames.clear();
	}
	
	public void reset() {
		currentTime = 0;
	}
	
	public synchronized Image getCurrentImage() {
		long time = 0;
		long targetTime;
		if (onetime && isDone()) {
			return frames.getLast().image;
		} else {
			targetTime = (currentTime + offset) % totalTime;
		}
		if (frames.size() > 0) {
			for (AnimationFrame frame : frames) {
				time += frame.duration;
				if (time > targetTime) {
					return frame.image;  
				}
			}
			return frames.get(0).image;
		}
		return null;
	}
	
	public synchronized void update(long timeMilliseconds) {
		if (onetime) {
			this.currentTime += timeMilliseconds;
		} else {
			this.currentTime = (this.currentTime + timeMilliseconds) % totalTime;
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		frames.clear();
		frames = null;
	}
	
	
	public int readFromLines(List<String> lines, int start) {
		int i = start;
		String frameImageName;
		long frameDuration;
		String[] splitLine;
		for (; i < lines.size() && lines.get(i).trim().length() > 0; i++) {
			splitLine = lines.get(i).trim().split(ResourceManager.SEPARATOR);
			if (lines.get(i).startsWith("direction:")) {
				try {
					setDirection(GameObject.class.getDeclaredField(splitLine[1]).getInt(null));
					
				} catch (IllegalArgumentException
						| IllegalAccessException | NoSuchFieldException
						| SecurityException e) {
					e.printStackTrace();
				}
			}
			if (lines.get(i).startsWith("state:")) {
				try {
					setState(GameObject.class.getDeclaredField(splitLine[1]).getInt(null));
				} catch (IllegalArgumentException
						| IllegalAccessException | NoSuchFieldException
						| SecurityException e) {
					e.printStackTrace();
				}
			}
			if (lines.get(i).startsWith("frame:")){
				frameImageName = splitLine[1];
				frameDuration = Long.parseLong(splitLine[2]);
				addFrame(frameImageName, frameDuration);
			}
		}
		return i;
	}
}
