package com.pakrx.platformgame.gui.widgets;

import java.awt.Font;
import java.util.ArrayList;

import com.pakrx.platformgame.gameplay.actions.CharacterAction;
import com.pakrx.platformgame.gameplay.characters.GameCharacter;


public class CharacterActionListWidget extends ListWidget {
	
	private ActionListEntry selectedActionListEntry;
	private GameCharacter gameCharacter;
	
	public class ActionListEntry extends ListWidget.ListEntry {
		
		private CharacterAction characterAction;
		private TextWidget indexTextWidget;
		private TextWidget numberTextWidget;
		private TextWidget nameTextWidget;
		
		public ActionListEntry() {
			super();
			
			indexTextWidget = new TextWidget();
			indexTextWidget.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
			indexTextWidget.setPadding(0);
			addChild(indexTextWidget);
			
			nameTextWidget = new TextWidget();
			nameTextWidget.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
			nameTextWidget.setPadding(0);
			addChild(nameTextWidget);
			
			numberTextWidget = new TextWidget();
			numberTextWidget.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
			numberTextWidget.setPadding(0);
			addChild(numberTextWidget);
		}
		
		public CharacterAction getCharacterAction() {
			return characterAction;
		}
		
		public void setCharacterAction(CharacterAction characterAction) {
			this.characterAction = characterAction;
			refresh();
		}
		
		@Override
		public void setIndex(int index) {
			super.setIndex(index);
			indexTextWidget.setString(String.format("%d", index + 1));
		}
		
		@Override
		public void setBounds(int x, int y, int width, int height) {
			super.setBounds(x, y, width, height);
			int numberwidth = 50;
			indexTextWidget.setBounds(0, 0, numberwidth, 50);
			nameTextWidget.setBounds(numberwidth, 0, getWidth() - 2 * numberwidth, 50);
			numberTextWidget.setBounds(getWidth() - numberwidth, 0, numberwidth, 50);
		}
		
		private void refresh() {
			if (characterAction != null) {
				nameTextWidget.setString(characterAction.getName());
				int amount = characterAction.getAmount();
				if (amount == CharacterAction.UNLIMITED_AMOUNT) {
					numberTextWidget.setString("∞");
				} else {
					numberTextWidget.setString(String.format("%d", amount));
				}
			}
		}
		
		@Override
		public void updateSelf(long interval) {
			refresh();
		}
	}
	
	@Override
	public void onEntrySelected(int index) {
		selectedActionListEntry = (ActionListEntry)children.get(index);
		super.onEntrySelected(index);
		selectedActionListEntry.setSelected(true);
	}
	
	public ActionListEntry getSelectedActionListEntry() {
		return selectedActionListEntry;
	}
	
	@Override
	protected void updateSelf(long interval) {
		if (gameCharacter == null) {
			return;
		}
		ArrayList<Widget> childrenToRemove = new ArrayList<Widget>();
		for (Widget child : children) {
			ActionListEntry entry = (ActionListEntry)child;
			if (entry.getCharacterAction().getAmount() <= 0 || !gameCharacter.getActions().contains(entry.getCharacterAction())) {
				if (selectedActionListEntry != null) {
					if (entry.getIndex() == selectedActionListEntry.getIndex()) {
						selectedActionListEntry = null;
					}
				}
				childrenToRemove.add(entry);
			}
		}
		for (Widget child : childrenToRemove) {
			removeChild(child);
		}
		for (CharacterAction action : gameCharacter.getActions()) {
			boolean actionShown = false;
			for (Widget child : children) {
				ActionListEntry entry = (ActionListEntry)child;
				if (entry.getCharacterAction().equals(action)) {
					actionShown = true;
					break;
				}
			}
			if (!actionShown) {
				addEntry(action);
			}
		}
	}
	
	@Override
	public void deselectAll() {
		super.deselectAll();
		selectedActionListEntry = null;
	}
	
	public void addEntry(CharacterAction characterAction) {
		if (characterAction.getAmount() > 0) {
			ActionListEntry entry = new ActionListEntry();
			entry.setCharacterAction(characterAction);
			entry.setBounds(0, children.size() * 50, getWidth(), 50);
			entry.setIndex(children.size());
			addChild(entry);
		}
	}
	
	public void setGameCharacter(GameCharacter gameCharacter) {
		removeAllChildren();
		this.gameCharacter = gameCharacter;
		for (CharacterAction characterAction : gameCharacter.getActions()) {
			addEntry(characterAction);
		}
	}
}
