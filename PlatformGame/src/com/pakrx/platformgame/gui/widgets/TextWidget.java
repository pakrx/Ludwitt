package com.pakrx.platformgame.gui.widgets;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

public class TextWidget extends Widget {

	static class StringLine {
		String string;
		Rectangle bounds;
	}
	

	protected String string = "";
	protected ArrayList<StringLine> lines;
	protected boolean backgroundOpaque = false;
	protected Color backgroundColor = new Color(0, 0, 0, 0);
	protected Color foregroundColor = Color.white;
	protected int padding = 20;
	protected boolean centerHorizontally = true;
	protected boolean centerVertically = true;
	protected boolean toleft = false;
	protected boolean toright = false;
	protected Font font = new Font(Font.SANS_SERIF, Font.BOLD, 16);
	
	protected int lineHeight;
	
	@Override
	protected void updateSelf(long interval) {
		
	}

	@Override
	protected void drawSelf(Graphics2D g) {
		g.setFont(font);
		if (backgroundOpaque) {
			g.setColor(backgroundColor);
			g.fillRect(getX(), getY(), getWidth(), getHeight());
		}
		g.setColor(foregroundColor);
		if (lines == null) {
			setString(string, g);
		}
		int linex;
		int liney;
		if (centerVertically) {
			liney = getY() + 3 * lineHeight / 4 + (getHeight() - lines.size() * lineHeight) / 2;
		} else {
			liney = getY() + getHeight() - padding;
		}
		for (StringLine line : lines) {
			if (centerHorizontally) {
				linex = getX() + padding + (getWidth() - 2 * padding - line.bounds.width) / 2;
			} else if (toright) { 
				linex = getX() - padding + getWidth() - line.bounds.width;
			} else {
				linex = getX() + padding;
			}
			g.drawString(line.string, linex, liney);
			liney += lineHeight;
		}
	}

	public String getString() {
		return string;
	}
	
	public void setString(String string) {
		this.string = string;
		lines = null;
	}
	
	public void setFont(Font font) {
		this.font = font;
	}
	
	public void setToLeft() {
		toleft = true;
		toright = false;
		centerHorizontally = false;
	}
	
	public void setToRight() {
		toright = true;
		toleft = false;
		centerHorizontally = false;
	}
	
	public void setString(String string, Graphics2D g) {
		lines = new  ArrayList<StringLine>();
		Rectangle stringBounds = g.getFontMetrics().getStringBounds(string, g).getBounds();
		lineHeight = stringBounds.height;
		StringBuffer stringBuffer = new StringBuffer();
		int wordLength = 0;
		int stringLength = 0;
		for (String word : string.split(" ")) {
			wordLength = g.getFontMetrics().getStringBounds(word, g).getBounds().width;
			stringLength = g.getFontMetrics().getStringBounds(stringBuffer.toString(), g).getBounds().width;
			if (wordLength + stringLength > (getWidth() - 2 * padding) || stringLength > (getWidth() - 2 * padding)) {
				StringLine stringLine = new StringLine();
				stringLine.bounds = g.getFontMetrics().getStringBounds(stringBuffer.toString(), g).getBounds();
				stringLine.string = stringBuffer.toString();
				lines.add(stringLine);
				stringBuffer = new StringBuffer();
			}
			stringBuffer.append(' ');
			stringBuffer.append(word);
		}
		StringLine stringLine = new StringLine();
		stringLine.bounds = g.getFontMetrics().getStringBounds(stringBuffer.toString(), g).getBounds();
		stringLine.string = stringBuffer.toString();
		lines.add(stringLine);
	}
	
	public void setBackgroundColor(Color color) {
		if (color.getAlpha() > 0) {
			backgroundOpaque = true;
		}
		backgroundColor = color;
	}
	
	public void setForegroundColor(Color color) {
		foregroundColor = color;
	}
	
	public void setPadding(int padding) {
		this.padding = padding;
	}
	
	public void setHorizontalCentering(boolean center) {
		this.centerHorizontally = center;
	}
	
	public void setVerticalCentering(boolean center) {
		this.centerVertically = center;
	}
	
}
