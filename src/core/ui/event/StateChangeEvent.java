package core.ui.event;

import core.event.AvoEvent;
import core.ui.UIElement;

public class StateChangeEvent extends AvoEvent {

	private UIElement element;
	private int state;
	
	public StateChangeEvent(UIElement element, int state) {
		setElement(element);
		setState(state);
	}

	public UIElement getElement() {
		return element;
	}

	public void setElement(UIElement element) {
		this.element = element;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
}
