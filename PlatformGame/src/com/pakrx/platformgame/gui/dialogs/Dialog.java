package com.pakrx.platformgame.gui.dialogs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import com.pakrx.platformgame.graphics.Screen;
import com.pakrx.platformgame.gui.widgets.TopBarWidget;
import com.pakrx.platformgame.gui.widgets.Widget;
import com.pakrx.platformgame.input.InputHandler;


public abstract class Dialog extends Widget {

	protected boolean pausesGame = false;
	protected boolean dismissable = true;
	protected boolean singleton = true;
	protected boolean topbar = false;
	protected boolean enabled = false;
	protected boolean draggable = false;
	protected boolean flamed = false;
	
	protected String title;
	
	private Point dragStartPosition;
	private MouseEvent dragStartEvent;
	private MouseEvent dragMoveEvent;
	private TopBarWidget topBarWidget;
	
	protected HashMap<Integer, Integer> previousBindings = new HashMap<Integer, Integer>();
	
	public Dialog() {
		
	}
	
	
	public boolean pausesGame() {
		return pausesGame;
	}
	
	public boolean escapeCloses() {
		return dismissable;
	}
	
	public boolean isSingleton() {
		return singleton;
	}
	
	public void setEnabled(boolean enabled) {
		if (this.enabled != enabled) {
			if (enabled) {
				bindActions();
			} else {
				unbindActions();
			}
			this.enabled = enabled;
		}
	}
	
	@Override
	public void update(long interval) {
		if (enabled && InputHandler.popAction(TopBarWidget.ACTION_MINIMIZE_WINDOW)) {
			if (hidden) {
				show();
			} else {
				hide();
			}
		}
		if (topbar && topBarWidget == null) {
			topBarWidget = new TopBarWidget();
			topBarWidget.setBounds(0, -20, getWidth(), 20);
			topBarWidget.setTitle(title);
			addChild(topBarWidget);
		}
		if (enabled) {
			MouseEvent lastMousePressEvent = InputHandler.getMousePressEvent();
			MouseEvent mouseMoveEvent = InputHandler.popMouseMoveEvent();
			MouseEvent mousePressEvent = InputHandler.popMousePressEvent();
			MouseEvent mouseReleaseEvent = InputHandler.popMouseReleaseEvent();
	
			if (mouseMoveEvent != null) {
				int x = mouseMoveEvent.getX();
				int y = mouseMoveEvent.getY();
				if (dragStartEvent != null && draggable) {
					dragMoveEvent = mouseMoveEvent;
					onDragged(dragStartEvent, dragMoveEvent);
				} else {
					for (Widget child : children) {
						if (x >= child.getX() && x < child.getX() + child.getWidth() &&
							y >= child.getY() && y < child.getY() + child.getHeight()) {
							child.handleMouseInside(mouseMoveEvent);
							break;
						}
					}
				}
				for (Widget child : children) {
					if (!(x >= child.getX() && x < child.getX() + child.getWidth() &&
						y >= child.getY() && y < child.getY() + child.getHeight()) &&
						child.isMouseInside()) {
						child.handleMouseOutside();
					}
				}
			}
			if (mousePressEvent != null) {
				int x = mousePressEvent.getX();
				int y = mousePressEvent.getY();
				boolean childPress = false;
				for (Widget child : children) {
					if (x >= child.getX() && x < child.getX() + child.getWidth() &&
						y >= child.getY() && y < child.getY() + child.getHeight()) {
						child.handleMousePressInside(mousePressEvent);
						if (!child.isClickthrough()) {
							childPress = true;
						}
						break;
					}
				}
				if (!childPress && dragStartEvent == null &&
						x >= getX() && x < getX() + getWidth() &&
						((!topbar && y >= getY()) || (topbar && topBarWidget != null && y >= getY() - topBarWidget.getHeight())) && 
						y < getY() + getHeight()) {
					dragStartEvent = mousePressEvent;
					dragStartPosition = new Point(getX(), getY());
				}
			}
			if (mouseReleaseEvent != null) {
				int x = mouseReleaseEvent.getX();
				int y = mouseReleaseEvent.getY();
				for (Widget child : children) {
					if (x >= child.getX() && x < child.getX() + child.getWidth() &&
						y >= child.getY() && y < child.getY() + child.getHeight()) {
						child.handleMouseReleaseInside(mouseReleaseEvent);
						break;
					}
				}
				dragStartEvent = null;
			}
			if (lastMousePressEvent != null && mouseReleaseEvent != null) {
				int xp = lastMousePressEvent.getX();
				int yp = lastMousePressEvent.getY();
				int xr = mouseReleaseEvent.getX();
				int yr = mouseReleaseEvent.getY();
				for (Widget child : children) {
					if (xp >= child.getX() && xp < child.getX() + child.getWidth() &&
						yp >= child.getY() && yp < child.getY() + child.getHeight() &&
						xr >= child.getX() && xr < child.getX() + child.getWidth() &&
						yr >= child.getY() && yr < child.getY() + child.getHeight()) {
						child.handleClickedInside(lastMousePressEvent, mouseReleaseEvent);
						break;
					}
				}
			}
		}
		super.update(interval);
	}
	
	protected void bindActions() {
		
	}
	
	protected void unbindActions() {
		
	}
	
	protected void bindActionToKey(int action, int key) {
		previousBindings.put(key, InputHandler.getActionForKey(key));
		InputHandler.bindActionToKey(action, key);
	}
	
	protected void unbindAction(int action, int key) {
		InputHandler.unbindAction(key);
		InputHandler.bindActionToKey(previousBindings.get(key), key);
	}
	
	@Override
	public void draw(Graphics2D g) {
		if (flamed) {
			Color color = g.getColor();
			g.setColor(new Color(255, 255, 255, 60));
			g.fillRect(0, 0, Screen.getWidth(), Screen.getHeight());
			g.setColor(color);
		}
		super.draw(g);
	}
	
	public void onDragged(MouseEvent dragStartEvent, MouseEvent dragMoveEvent) {
		int x = dragMoveEvent.getX() - dragStartEvent.getX();
		int y = dragMoveEvent.getY() - dragStartEvent.getY();
		bounds.x = dragStartPosition.x + x;
		bounds.x = Math.max(0, bounds.x);
		bounds.x = Math.min(bounds.x, Screen.getWidth() - getWidth());
		bounds.y = dragStartPosition.y + y;
		bounds.y = Math.max(0, bounds.y);
		bounds.y = Math.min(bounds.y, Screen.getHeight() - getHeight());
		for (Widget child : children) {
			child.setParentOffset(new Point(getX(), getY()));
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj.getClass().equals(getClass())) {
			if (isSingleton()) {
				return true;
			} else {
				return super.equals(obj);
			}
		} else {
			return false;
		}
	}
}
