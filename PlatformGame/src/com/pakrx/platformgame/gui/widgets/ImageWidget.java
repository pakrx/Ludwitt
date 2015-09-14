package com.pakrx.platformgame.gui.widgets;

import java.awt.Graphics2D;
import java.awt.Image;

public class ImageWidget extends Widget {

	private Image image;
	
	public void setImage(Image image) {
		this.image = image;
	}
	
	public Image getImage() {
		return image;
	}
	
	@Override
	protected void updateSelf(long interval) {

	}

	@Override
	protected void drawSelf(Graphics2D g) {
		if (image != null) {
			int x = getX() + (getWidth() - image.getWidth(null)) / 2;
			int y = getY() + (getHeight() - image.getHeight(null)) / 2;
			g.drawImage(image, x, y, null);
		}
	}

}
