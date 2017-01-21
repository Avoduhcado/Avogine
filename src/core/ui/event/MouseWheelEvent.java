package core.ui.event;

public class MouseWheelEvent extends UIEvent {

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
