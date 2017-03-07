package core.ui.event;

import core.event.AvoEvent;

public class TimeEvent extends AvoEvent {

	private float delta;
	
	public TimeEvent(float delta) {
		setDelta(delta);
	}

	public float getDelta() {
		return delta;
	}

	public void setDelta(float delta) {
		this.delta = delta;
	}

}
