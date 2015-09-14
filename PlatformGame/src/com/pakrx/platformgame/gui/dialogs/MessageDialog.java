package com.pakrx.platformgame.gui.dialogs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import com.pakrx.platformgame.graphics.Screen;
import com.pakrx.platformgame.gui.widgets.Button;
import com.pakrx.platformgame.gui.widgets.TextWidget;
import com.pakrx.platformgame.gui.widgets.Widget;
import com.pakrx.platformgame.input.InputHandler;

public class MessageDialog extends Dialog {
	
	private final static int ACTION_MESSAGE_DIALOG_OK = InputHandler.getNextAction();
	
	private Button okButton;
	private TextWidget messageTextWidget;

	
	public MessageDialog() {
		
		dismissable = true;
		pausesGame = true;
		singleton = true;
		topbar = true;
		draggable = true;

		int width = 300;
		int height = 200;
		int x = (Screen.getWidth() - width) / 2;
		int y = (Screen.getHeight() - height) / 2;
		setBounds(x, y, width, height);
		
		width = 150;
		height = 50;
		x = (getWidth() - width) / 2;
		y = 130;
		okButton = new Button();
		okButton.setString("OK");
		okButton.setBounds(x, y, width, height);
		addChild(okButton);
		
		width = 280;
		height = 130;
		x = (getWidth() - width) / 2;
		y = 10;
		messageTextWidget = new TextWidget();
		messageTextWidget.setBounds(x, y, width, height);
		addChild(messageTextWidget);
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setOkClickListener(Widget.WidgetClickListener listener) {
		okButton.setClickListener(listener);
	}
	
	public void setString(String string) {
		messageTextWidget.setString(string);
	}
	
	@Override
	protected void bindActions() {
		bindActionToKey(ACTION_MESSAGE_DIALOG_OK, KeyEvent.VK_SPACE);
		bindActionToKey(ACTION_MESSAGE_DIALOG_OK, KeyEvent.VK_ENTER);
	}
	
	@Override
	protected void unbindActions() {
		unbindAction(ACTION_MESSAGE_DIALOG_OK, KeyEvent.VK_SPACE);
		unbindAction(ACTION_MESSAGE_DIALOG_OK, KeyEvent.VK_ENTER);
	}
	
	@Override
	protected void updateSelf(long interval) {
		Screen.setDefaultCursor();
		if (InputHandler.popAction(ACTION_MESSAGE_DIALOG_OK)) {
			okButton.dismiss();
		}
	}

	@Override
	protected void drawSelf(Graphics2D g) {
		g.setColor(Color.black);
		g.fillRect(getX(), getY(), getWidth(), getHeight());
	}

}
