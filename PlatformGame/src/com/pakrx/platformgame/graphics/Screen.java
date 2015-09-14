package com.pakrx.platformgame.graphics;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.RepaintManager;

public class Screen {
	
	private static Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
	private static Cursor invisibleCursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(""), new Point(0, 0), "invisible");
	
	public static class UnsupportedResolutionException extends Exception {

		private static final long serialVersionUID = 5714694853392805953L;
		
	}

	private static JFrame frame;
	private static int width;
	private static int height;
	
	public static void initScreen(int width, int height) throws UnsupportedResolutionException {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setResizable(false);
		frame.setIgnoreRepaint(true);
		frame.setBackground(Color.darkGray);
		
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
		graphicsDevice.setFullScreenWindow(frame);
						
		frame.createBufferStrategy(2);
		frame.setVisible(true);
		
		Screen.width = width;
		Screen.height = height;

		RepaintManager.setCurrentManager(new NoRepaintManager());
	}
	
	public static int getDefaultWidth() {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		return gd.getDisplayMode().getWidth();
	}
	
	public static int getDefaultHeight() {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		return gd.getDisplayMode().getHeight();
	}
	
	public static void setInvisibleCursor() {
		frame.setCursor(invisibleCursor);
	}
	
	public static void setDefaultCursor() {
		frame.setCursor(defaultCursor);
	}
	
	public static int getWidth() {
		return width;
	}
	
	public static  int getHeight() {
		return height;
	}
	
	public static Graphics2D getGraphics() {
		return (Graphics2D)frame.getBufferStrategy().getDrawGraphics();
	}
	
	public static JFrame getFrame() {
		return frame;
	}
	
	public static void drawBackground() {
		Graphics2D g = getGraphics();
		g.clearRect(0, 0, width, height);	
		g.setColor(Color.darkGray);
		g.fillRect(0, 0, width, height);
	}
	
	public static void finishDrawing() {
		Graphics2D g = getGraphics();
		JFrame frame = getFrame();

		if (frame != null) {
			BufferStrategy strategy = frame.getBufferStrategy();
			if (!strategy.contentsLost()) {
				strategy.show();
			}
		}		
		g.dispose();
	}
}
