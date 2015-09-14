package com.pakrx.platformgame.gui.widgets;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public abstract class Widget {
	
	protected Rectangle bounds = new Rectangle();
	protected Point parentOffset = new Point(0, 0);
	protected ArrayList<Widget> children = new ArrayList<Widget>();
	protected boolean mouseInside = false;
	protected boolean mousePressed = false;
	private boolean clickthrough = false;
	protected boolean hidden = false;
	
	protected boolean fade = false;
	protected boolean duringBgFade = false;
	protected Color bgFadeStart;
	protected Color bgFadeEnd;
	protected Color bgFade;
	protected long fadeTime;
	protected long fadeTimeElapsed = 0;
	
	protected WidgetClickListener clickListener;
	
	public interface WidgetClickListener {
		public void onWidgetClick(Widget guiElement);
	}
	
	public Widget() {
		
	}
	
	public void addChild(Widget guiElement) {
		guiElement.setParentOffset(new Point(getX(), getY()));
		children.add(guiElement);
	}
	
	public void removeChild(Widget guiElement) {
		children.remove(guiElement);
	}
	
	public void removeAllChildren() {
		children = new ArrayList<Widget>();
	}
	
	protected abstract void updateSelf(long interval);
	
	protected abstract void drawSelf(Graphics2D g);
	
	public void setParentOffset(Point point) {
		parentOffset.x = point.x;
		parentOffset.y = point.y;
		for (Widget child : children) {
			child.setParentOffset(new Point(bounds.x + parentOffset.x, bounds.y + parentOffset.y));
		}
	}
	
	public int getX() {
		return bounds.x + parentOffset.x;
	}
	
	public int getY() {
		return bounds.y + parentOffset.y;
	}
	
	public int getWidth() {
		return bounds.width;
	}
	
	public int getHeight() {
		return bounds.height;
	}
	
	
	public void update(long interval) {		
		updateSelf(interval);
		for (Widget child : children) {
			child.update(interval);
		}
		if (fade && duringBgFade) {
			fadeTimeElapsed += interval;
			if (fadeTimeElapsed >= fadeTime) {
				duringBgFade = false;
				bgFade = bgFadeEnd;
			} else {
				int r = bgFadeStart.getRed() + (int)((fadeTimeElapsed * (bgFadeEnd.getRed() - bgFadeStart.getRed())) / fadeTime);
				int g = bgFadeStart.getGreen() + (int)((fadeTimeElapsed * (bgFadeEnd.getGreen() - bgFadeStart.getGreen())) / fadeTime);
				int b = bgFadeStart.getBlue() + (int)((fadeTimeElapsed * (bgFadeEnd.getBlue() - bgFadeStart.getBlue())) / fadeTime);
				int a = bgFadeStart.getAlpha() + (int)((fadeTimeElapsed * (bgFadeEnd.getAlpha() - bgFadeStart.getAlpha())) / fadeTime);
				bgFade = new Color(r, g, b, a);
			}
		}
	}
	
	public void setFadingBackground(long duration, Color startColor, Color endColor) {
		bgFade = startColor;
		bgFadeStart = startColor;
		bgFadeEnd = endColor;
		fadeTime = duration;
		fadeTimeElapsed = 0;
		fade = true;
		duringBgFade = true;
	}
	
	public void draw(Graphics2D g) {
		Color previousColor = g.getColor();
		Font previousFont = g.getFont();
		if (!hidden) {
			if (fade && bgFade != null) {
				g.setColor(bgFade);
				g.fillRect(getX(), getY(), getWidth(), getHeight());
			}
			drawSelf(g);
		}
		for (Widget child : children) {
			child.draw(g);
		}
		g.setColor(previousColor);
		g.setFont(previousFont);
	}
	
	public Rectangle getBounds() {
		return bounds;
	}
	
	public void setBounds(int x, int y, int width, int height) {
		bounds.x = x;
		bounds.y = y;
		bounds.width = width;
		bounds.height = height;
	}
	
	public void onMouseInside(MouseEvent mouseEvent) {

	}
	
	public void onMousePressInside(MouseEvent mouseEvent) {

	}
	
	public void onMouseReleaseInside(MouseEvent mouseEvent) {

	}
	
	public void onMouseClickedInside(MouseEvent mousePressEvent, MouseEvent mouseReleaseEvent) {

	}
	
	public void onMouseOutside() {
		
	}
	

	public void handleMouseInside(MouseEvent mouseMoveEvent) {
		mouseInside = true;
		int x = mouseMoveEvent.getX();
		int y = mouseMoveEvent.getY();
		for (Widget child : children) {
			if (x >= child.getX() && x < child.getX() + child.getWidth() &&
				y >= child.getY() && y < child.getY() + child.getHeight()) {
				child.handleMouseInside(mouseMoveEvent);
				break;
			}
		}
		for (Widget child : children) {
			if (!(x >= child.getX() && x < child.getX() + child.getWidth() &&
				y >= child.getY() && y < child.getY() + child.getHeight()) &&
				child.isMouseInside()) {
				child.handleMouseOutside();
			}
		}
		if (!hidden) {
			onMouseInside(mouseMoveEvent);
		}
	}

	public void handleMousePressInside(MouseEvent mousePressEvent) {
		int x = mousePressEvent.getX();
		int y = mousePressEvent.getY();
		for (Widget child : children) {
			if (x >= child.getX() && x < child.getX() + child.getWidth() &&
				y >= child.getY() && y < child.getY() + child.getHeight()) {
				child.handleMousePressInside(mousePressEvent);
				break;
			}
		}
		if (!hidden) {
			onMousePressInside(mousePressEvent);
		}
	}

	public void handleMouseReleaseInside(MouseEvent mouseReleaseEvent) {
		int x = mouseReleaseEvent.getX();
		int y = mouseReleaseEvent.getY();
		for (Widget child : children) {
			if (x >= child.getX() && x < child.getX() + child.getWidth() &&
				y >= child.getY() && y < child.getY() + child.getHeight()) {
				child.handleMousePressInside(mouseReleaseEvent);
				break;
			}
		}
		if (!hidden) {
			onMouseReleaseInside(mouseReleaseEvent);
		}
	}

	public void handleClickedInside(MouseEvent mousePressEvent, MouseEvent mouseReleaseEvent) {
		int xp = mousePressEvent.getX();
		int yp = mousePressEvent.getY();
		int xr = mouseReleaseEvent.getX();
		int yr = mouseReleaseEvent.getY();
		for (Widget child : children) {
			if (xp >= child.getX() && xp < child.getX() + child.getWidth() &&
				yp >= child.getY() && yp < child.getY() + child.getHeight() &&
				xr >= child.getX() && xr < child.getX() + child.getWidth() &&
				yr >= child.getY() && yr < child.getY() + child.getHeight()) {
				child.handleClickedInside(mousePressEvent, mouseReleaseEvent);
				break;
			}
		}
		if (!hidden) {
			onMouseClickedInside(mousePressEvent, mouseReleaseEvent);
			if (clickListener != null) {
				clickListener.onWidgetClick(this);
			}
		}
	}
	
	public void handleMouseOutside() {
		mouseInside = false;
		for (Widget child : children) {
			if (child.mouseInside) {
				child.handleMouseOutside();
			}
		}
		if (!hidden) {
			onMouseOutside();
		}
	}

	public boolean isMouseInside() {
		return mouseInside;
	}
	
	public void hide() {
		hidden = true;
		for (Widget child : children) {
			child.hide();
		}
	}
	
	public void show() {
		hidden = false;
		for (Widget child : children) {
			child.show();
		}
	}
	
	public void setClickListener(WidgetClickListener listener) {
		this.clickListener = listener;
	}
	
	public WidgetClickListener getClickListener() {
		return clickListener;
	}

	public boolean isClickthrough() {
		return clickthrough;
	}

	public void setClickthrough(boolean clickthrough) {
		this.clickthrough = clickthrough;
	}
}
