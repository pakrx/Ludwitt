package com.pakrx.platformgame.gui.widgets;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import com.pakrx.platformgame.gameplay.Physics;

public class Button extends TextWidget {

	private Color normalBgColor = new Color(32, 32, 32, 0);
	private Color hoverBgColor = new Color(64, 64, 64, 50);
	private Color pressBgColor = new Color(80, 80, 80, 100);
	private Color outlineColor = new Color(255, 255, 255, 30);
	private Color forcedColor = null;

	public Button() {
		setBackgroundColor(normalBgColor);
	}
	
	public void forcePressBackgroundColor() {
		forcedColor = pressBgColor;
		super.setBackgroundColor(forcedColor);
	}
	
	public void releasePressBackgroundColor() {
		forcedColor = null;
		super.setBackgroundColor(normalBgColor);
	}
	
	@Override
	public void setBackgroundColor(Color color) {
		if (forcedColor == null) {
			super.setBackgroundColor(color);
		}
	}
	
	@Override
	public void onMouseClickedInside(MouseEvent mousePressEvent,
			MouseEvent mouseReleaseEvent) {
		setFadingBackground((int)(0.3 * Physics.INTERVAL_DIVIDER), pressBgColor, normalBgColor);
	}
	
	@Override
	public void onMouseInside(MouseEvent mouseEvent) {
		setBackgroundColor(hoverBgColor);
	}
	
	@Override
	public void onMouseOutside() {
		setBackgroundColor(normalBgColor);
	}
	
	@Override
	public void onMousePressInside(MouseEvent mouseEvent) {
		setBackgroundColor(pressBgColor);
	}
	
	@Override
	public void onMouseReleaseInside(MouseEvent mouseEvent) {
		setBackgroundColor(hoverBgColor);
	}
	
	public void dismiss() {
		if (clickListener != null) {
			clickListener.onWidgetClick(this);
		}
	}
	
	@Override
	protected void drawSelf(Graphics2D g) {
		g.setColor(outlineColor);
		g.drawRect(getX(), getY(), getWidth(), getHeight());
		super.drawSelf(g);
	}
	
}
