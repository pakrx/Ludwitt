package com.pakrx.platformgame.gui.widgets;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

public class ListWidget extends Widget {
	
	protected ListWidgetListener listener;
	
	public interface ListWidgetListener {
		public void onListElementSelected(int index);
	}
	
	protected class ListEntry extends TextWidget {

		private int index = 0;
		private boolean selectable = true;
		private boolean selected = false;
		private Color normalBgColor = new Color(0, 0, 0, 0);
		private Color hoverBgColor = new Color(100, 100, 100, 50);
		private Color selectedBgColor = new Color(200, 200, 200, 60);
		
		public ListEntry() {
			setBackgroundColor(normalBgColor);
		}
		
		public void setSelectable(boolean selectable) {
			this.selectable = selectable;
		}
		
		public void setIndex(int index) {
			this.index = index;
		}
	
		public int getIndex() {
			return index;
		}
		
		public void setSelected(boolean selected) {
			this.selected = selected;
			if (selected) {
				setBackgroundColor(selectedBgColor);
			} else if (mouseInside) {
				setBackgroundColor(hoverBgColor);
			} else {
				setBackgroundColor(normalBgColor);
			}
		}
		
		@Override
		protected void updateSelf(long interval) {
			super.updateSelf(interval);
		}

		@Override
		protected void drawSelf(Graphics2D g) {
			super.drawSelf(g);
		}
		
		@Override
		public void onMouseInside(MouseEvent mouseEvent) {
			if (!selected && selectable) {
				setBackgroundColor(hoverBgColor);
			}
		}
		
		@Override
		public void onMouseOutside() {
			if (!selected && selectable) {
				setBackgroundColor(normalBgColor);
			}
		}
		
		@Override
		public void onMousePressInside(MouseEvent mouseEvent) {
			if (selectable) {
				setBackgroundColor(selectedBgColor);
				setSelected(true);
				onEntrySelected(index);
			}
		}		
		
		@Override
		public void onMouseReleaseInside(MouseEvent mouseEvent) {
			if (!selected && selectable) {
				setBackgroundColor(hoverBgColor);
			}
		}
	}
	
	public ListWidget() {
		
	}
	
	public void addEntry(String string) {
		ListEntry entry = new ListEntry();
		entry.setString(string);
		entry.setBounds(0, children.size() * 50, getWidth(), 50);
		entry.setIndex(children.size());
		addChild(entry);
	}
	
	@Override
	protected void updateSelf(long interval) {
		
	}

	@Override
	protected void drawSelf(Graphics2D g) {

	}
	
	public int getSize() {
		return children.size();
	}
	
	public void onEntrySelected(int index) {
		for (int i = 0; i < children.size(); i++) {
			if (i != index) {
				((ListEntry)children.get(i)).setSelected(false);
			}
		}
		if (listener != null) {
			listener.onListElementSelected(index);
		}
	}
	
	public void setListener(ListWidgetListener listener) {
		this.listener = listener;
	}
	
	public void deselectAll() {
		for (int i = 0; i < children.size(); i++) {
			((ListEntry)children.get(i)).setSelected(false);
		}
	}
	
	public void select(int index) {
		if (index >= 0 && index < children.size()) {
			onEntrySelected(index);
		}
	}

	public void removeChild(int index) {
		children.remove(index);
	}
}
