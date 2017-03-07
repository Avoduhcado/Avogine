package core.ui.event;

import core.event.AvoEvent;

public class MouseWheelEvent extends AvoEvent {

	private int scroll;
	
	public MouseWheelEvent(int scroll) {
		this.setScroll(scroll);
	}

	public int getScroll() {
		return scroll;
	}

	public void setScroll(int scroll) {
		this.scroll = scroll;
	}
	
}
