package com.pakrx.platformgame.gui.widgets;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import com.pakrx.platformgame.input.InputHandler;
import com.pakrx.platformgame.resources.ResourceManager;

public class TopBarWidget extends Widget {

	
	public final static int ACTION_MINIMIZE_WINDOW = InputHandler.getNextAction();
	
	
	private Color normalBgColor = new Color(50, 50, 50, 50);
	private Color hoverBgColor = new Color(50, 50, 50, 100);
	private Color pressBgColor = new Color(30, 30, 30, 200);
	private Color bgColor = normalBgColor;
	
	private TextWidget titleTextWidget;
	private MinimizeButton minimizeButton;
	
	private int height = 20;
	
	
	
	
	
	private static class MinimizeButton extends ImageWidget {

		private Color bgColor;
		private Color normalBgColor = new Color(0, 0, 0, 0);
		private Color hoverBgColor = new Color(0, 0, 0, 30);
		private Color pressedBgColor = new Color(255, 255, 255, 50);
		
		public MinimizeButton() {
			setImage(ResourceManager.readImage("gui/minimize.png"));
		}
		
		@Override
		protected void drawSelf(Graphics2D g) {
			g.setColor(bgColor);
			g.fillRect(getX(), getY(), getWidth(), getHeight());
			super.drawSelf(g);
		}
		
		@Override
		public void onMouseClickedInside(MouseEvent mousePressEvent,
				MouseEvent mouseReleaseEvent) {
			InputHandler.activateAction(ACTION_MINIMIZE_WINDOW);
		}
		
		@Override
		public void onMouseInside(MouseEvent mouseEvent) {
			bgColor = hoverBgColor;
		}
		
		@Override
		public void onMouseOutside() {
			bgColor = normalBgColor;
		}
		
		@Override
		public void onMousePressInside(MouseEvent mouseEvent) {
			bgColor = pressedBgColor;
		}
		
		@Override
		public void onMouseReleaseInside(MouseEvent mouseEvent) {
			bgColor = hoverBgColor;
		}
	}
	
	
	
	
	
	
	
	
	public TopBarWidget() {
		setClickthrough(true);
		
		titleTextWidget = new TextWidget();
		titleTextWidget.setVerticalCentering(true);
		titleTextWidget.setHorizontalCentering(false);
		titleTextWidget.setFont(new Font(Font.SERIF, Font.PLAIN, 12));
		addChild(titleTextWidget);
		
		minimizeButton = new MinimizeButton();
		addChild(minimizeButton);
	}
	
	@Override
	protected void updateSelf(long interval) {
		
	}

	public void setTitle(String title) {
		titleTextWidget.setString(title);
	}
	
	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, -this.height, width, this.height);
		titleTextWidget.setBounds(0, 0, getWidth() - 50, getHeight());
		minimizeButton.setBounds(getWidth() - 20, 0, 20, getHeight());
	}
	
	@Override
	protected void drawSelf(Graphics2D g) {
		g.setColor(bgColor);
		g.fillRect(getX(), getY(), getWidth(), getHeight());
	}
	
	@Override
	public void onMouseInside(MouseEvent mouseEvent) {
		bgColor = hoverBgColor;
	}
	
	@Override
	public void onMouseOutside() {
		bgColor = normalBgColor;
	}
	
	@Override
	public void onMousePressInside(MouseEvent mouseEvent) {
		bgColor = pressBgColor;
	}
	
	@Override
	public void onMouseReleaseInside(MouseEvent mouseEvent) {
		bgColor = hoverBgColor;
	}
	
	@Override
	public void hide() {
		//
	}

}
