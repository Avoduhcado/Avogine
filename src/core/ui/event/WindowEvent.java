package core.ui.event;

import core.event.AvoEvent;

public class WindowEvent extends AvoEvent {

	private int newWindowWidth;
	private int newWindowHeight;
	
	public WindowEvent(int width, int height) {
		setNewWindowWidth(width);
		setNewWindowHeight(height);
	}

	public int getNewWindowWidth() {
		return newWindowWidth;
	}

	public void setNewWindowWidth(int newWindowWidth) {
		this.newWindowWidth = newWindowWidth;
	}

	public int getNewWindowHeight() {
		return newWindowHeight;
	}

	public void setNewWindowHeight(int newWindowHeight) {
		this.newWindowHeight = newWindowHeight;
	}
	
}
