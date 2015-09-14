package com.pakrx.platformgame.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputListener;

import com.pakrx.platformgame.graphics.Screen;

public class InputHandler {

	public final static int MAX_ACTIONS = 0xfff;
	public final static int MAX_KEYS = 0xfff;
	public final static int NO_ACTION = 0;
	
	private static boolean[] keys = new boolean[MAX_KEYS];
	private static boolean[] actions = new boolean[MAX_ACTIONS];
	private static int[] keysToActions = new int[MAX_KEYS];
	
	private static MouseEvent mouseMoveEvent;
	private static MouseEvent mousePressEvent;
	private static MouseEvent mousePressLastEvent;
	private static MouseEvent mouseReleaseEvent;
	
	private static int actionNumber = 1;
	
	
	
	public static int getNextAction() {
		return actionNumber++;
	}

	public static KeyListener getKeyListener() {
		return new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public synchronized void keyReleased(KeyEvent e) {
				InputHandler.keyReleased(e);
			}
			
			@Override
			public synchronized void keyPressed(KeyEvent e) {
				InputHandler.keyPressed(e);
			}
		};
	}
	
	public static MouseInputListener getMouseInputListener() {
		return new MouseInputListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Screen.setDefaultCursor();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				mousePressEvent = e;
				mousePressLastEvent = e;
				Screen.setDefaultCursor();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				mouseReleaseEvent = e;
				Screen.setDefaultCursor();
			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}

			@Override
			public void mouseDragged(MouseEvent e) {
				mouseMoveEvent = e;
				Screen.setDefaultCursor();
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				mouseMoveEvent = e;
				Screen.setDefaultCursor();
			}
		};
	}
	
	public static void bindActionToKey(int action, int key) {
		keysToActions[key] = action;
	}
	
	public static void unbindAction(int key) {
		keysToActions[key] = NO_ACTION;
	}

	public static void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (!keys[key]) {
			actions[keysToActions[key]] = true;
		}
		keys[key] = true; 
	}

	public static void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		keys[key] = false;
		actions[keysToActions[key]] = false; 
	}
	
	public static boolean isKeyPressed(int key) {
		return keys[key];
	}
	
	public static void activateAction(int action) {
		actions[action] = true;
	}
	
	public static void deactivateAction(int action) {
		actions[action] = false;
	}
	
	public static boolean isActionActivated(int action) {
		return actions[action];
	}
	
	public static boolean popAction(int action) {
		boolean active = actions[action];
		actions[action] = false;
		return active;
	}
	
	public static void releaseAll() {
		 for (int i = 0; i < MAX_KEYS; i++) {
			 keys[i] = false;
		 }
		 for (int i = 0; i < MAX_ACTIONS; i++) {			 
			 actions[i] = false;
		 }
	}
	
	public static int getActionsCount() {
		return actionNumber;
	}
	
	public static MouseEvent popMouseMoveEvent() {
		MouseEvent mouseEvent = mouseMoveEvent;
		mouseMoveEvent = null;
		return mouseEvent;
	}
	
	public static MouseEvent popMousePressEvent() {
		MouseEvent mouseEvent = mousePressEvent;
		mousePressEvent = null;
		return mouseEvent;
	}
	
	public static MouseEvent popMouseReleaseEvent() {
		MouseEvent mouseEvent = mouseReleaseEvent;
		mouseReleaseEvent = null;
		return mouseEvent;
	}
	
	public static MouseEvent getMousePressEvent() {
		return mousePressLastEvent;
	}
	
	public static int getActionForKey(int key) {
		return keysToActions[key];
	}
	
}
