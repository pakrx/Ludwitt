package com.pakrx.platformgame.sound;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.pakrx.platformgame.graphics.Screen;

public class SoundFile implements LineListener {

	private boolean position = false;
	private boolean stopped = true;
	private boolean loop = false;
	
	// sound localization
	private int locationX = Screen.getWidth() / 2;
	private final static int SIDE_DISTANCE = 200;
	private final static int MAX_DISTANCE = Screen.getWidth() / 2;
	private final static int VOLUME_MULTIPLIER = 1; 
	private static int middleLocation = Screen.getWidth() / 2;
	private static int leftLocation = middleLocation - SIDE_DISTANCE;
	private static int rightLocation = middleLocation + SIDE_DISTANCE;
	private final static int bufferSize = 1024;
	
	private String filename;
	
	public SoundFile(String filename) {
		this.filename = filename;
	}
	
	public void play() {
		final AudioInputStream audioInputStream;
		final SourceDataLine sourceDataLine;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(
					new BufferedInputStream(new FileInputStream(new File(filename))));
			
			AudioFormat format = audioInputStream.getFormat();
			final int originalFormatChannels = format.getChannels();
			int channelsCount = originalFormatChannels;
			if (position) {
				channelsCount = 2;
			}
			format = new AudioFormat(format.getSampleRate(), format.getSampleSizeInBits(), channelsCount, true, false);
			
			sourceDataLine = AudioSystem.getSourceDataLine(format);
			sourceDataLine.addLineListener(this);
			sourceDataLine.open(format, bufferSize);
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					sourceDataLine.start();
					audioInputStream.mark(Integer.MAX_VALUE);
					int numBytesRead = -1;
					stopped = false;
					byte[] srcData = new byte[bufferSize];
					byte[] trgData = new byte[bufferSize];
					while (!stopped) {
						for (int i = 0; i < bufferSize; i++) {
							srcData[i] = 0;
						}
						try {
							numBytesRead = audioInputStream.read(srcData, 0, bufferSize);
							if (numBytesRead == -1) {
								if (loop && !stopped) {
									audioInputStream.reset();
								} else {
									break;
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						}

						playBuffer(srcData, trgData, numBytesRead, originalFormatChannels, sourceDataLine);
					}
					sourceDataLine.drain();
					sourceDataLine.stop();
					sourceDataLine.flush();
					sourceDataLine.close();
					try {
						audioInputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		} 
		
	}
	
	private void playBuffer(byte[] srcData, byte[] trgData, int numBytesRead, int originalFormatChannels, SourceDataLine sourceDataLine) {

		if (numBytesRead > 0) {
			if (originalFormatChannels == 1 && position) {
				for (int i = 0; i < bufferSize / 2; i += 2) {
					
					int distanceFromLeft =  Math.min(MAX_DISTANCE, Math.abs(locationX - leftLocation));
					int distanceFromRight =  Math.min(MAX_DISTANCE, Math.abs(locationX - rightLocation));
					final int leftPercantage = Math.min(100, VOLUME_MULTIPLIER * (100 * (MAX_DISTANCE - distanceFromLeft)) / MAX_DISTANCE);
					final int rightPercantage = Math.min(100, VOLUME_MULTIPLIER * (100 * (MAX_DISTANCE - distanceFromRight)) / MAX_DISTANCE);
					
					ByteBuffer byteBuffer = ByteBuffer.wrap(srcData, i, 2);
					byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
					
					short left = byteBuffer.getShort();
					short right = left; 
					
					left = (short)(leftPercantage * left / 100);
					right = (short)(rightPercantage * right / 100);
					
					byteBuffer.clear();
					byteBuffer.putShort(left); byteBuffer.rewind();
					trgData[i * 2] = byteBuffer.get(0);
					trgData[i * 2 + 1] = byteBuffer.get(1);
					

					byteBuffer.clear();
					byteBuffer.putShort(right); byteBuffer.rewind();
					trgData[i * 2 + 2] = byteBuffer.get(0); byteBuffer.rewind();
					trgData[i * 2 + 3] = byteBuffer.get(1);
				}
				sourceDataLine.write(trgData, 0, bufferSize);
				for (int i = bufferSize / 2; i < bufferSize - 1; i += 2) {
					
					int distanceFromLeft =  Math.min(MAX_DISTANCE, Math.abs(locationX - leftLocation));
					int distanceFromRight =  Math.min(MAX_DISTANCE, Math.abs(locationX - rightLocation));
					final int leftPercantage = Math.min(100, VOLUME_MULTIPLIER * (100 * (MAX_DISTANCE - distanceFromLeft)) / MAX_DISTANCE);
					final int rightPercantage = Math.min(100, VOLUME_MULTIPLIER * (100 * (MAX_DISTANCE - distanceFromRight)) / MAX_DISTANCE);
					
					ByteBuffer byteBuffer = ByteBuffer.wrap(srcData, i, 2);
					byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
					
					short left = byteBuffer.getShort();
					short right = left; 
					
					left = (short)(leftPercantage * left / 100);
					right = (short)(rightPercantage * right / 100);
					
					byteBuffer.clear();
					byteBuffer.putShort(left); byteBuffer.rewind();
					trgData[i * 2 - bufferSize] = byteBuffer.get(0);
					trgData[i * 2 + 1 - bufferSize] = byteBuffer.get(1);
					

					byteBuffer.clear();
					byteBuffer.putShort(right); byteBuffer.rewind();
					trgData[i * 2 + 2 - bufferSize] = byteBuffer.get(0);
					trgData[i * 2 + 3 - bufferSize] = byteBuffer.get(1);
				}
				sourceDataLine.write(trgData, 0, bufferSize);
				
			} else {
				sourceDataLine.write(srcData, 0, bufferSize);
			}
		}
	}
	
	public void setLocationX(int x) {
		position = true;
		locationX = x;
	}
	
	public boolean isPlaying() {
		return !stopped;
	}
	
	public void stop() {
		stopped = true;
	}
	
	public void loop() {
		this.loop = true;
		play();
	}
	
	public void setLooping(boolean loop) {
		this.loop = loop;
	}
	
	@Override
	public void update(LineEvent event) {
		
	}

}
 