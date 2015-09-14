package com.pakrx.platformgame.gui.widgets;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;

import com.pakrx.platformgame.gameplay.Physics;
import com.pakrx.platformgame.resources.ResourceManager;

public class BattleLogListWidget extends ListWidget {

	private Image darkenImage;
	private Font font = new Font(Font.SANS_SERIF, Font.BOLD, 12);
	private Color oddColor = new Color(0, 0, 0, 0);
	private Color evenColor = new Color(255, 255, 255, 32);
	private boolean m = false;
	
	public BattleLogListWidget() {
		darkenImage = ResourceManager.readImage("gui/darkenbattlelog.png");
	}
	
	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		if (!hidden) {
			g.drawImage(darkenImage, getX(), getY(), null);
		}
	}
	
	@Override
	public void addEntry(String string) {

		if (children.size() == 5) {
			removeChild(4);
		}
		
		for (int i = 0; i < children.size(); i++) {
			Widget child = children.get(i);
			child.setBounds(0, (i + 1) * child.getHeight(), getWidth(), child.getHeight());
		}
		
		ListEntry entry = new ListEntry();
		if (m) {
			entry.setBackgroundColor(oddColor);
			entry.setToRight();
			entry.setFadingBackground((long)(0.3 * Physics.INTERVAL_DIVIDER), Color.red, oddColor);
		} else {
			entry.setBackgroundColor(evenColor);
			entry.setToLeft();
			entry.setFadingBackground((long)(0.3 * Physics.INTERVAL_DIVIDER), Color.red, evenColor);
		}
		m = !m;
		entry.setString(string);
		entry.setSelectable(false);
		entry.setBounds(0, 0, getWidth(), 50);
		entry.setPadding(0);
		entry.setFont(font);
		entry.setIndex(children.size());
		entry.setParentOffset(new Point(getX(), getY()));
		children.add(0, entry);
	}
}
