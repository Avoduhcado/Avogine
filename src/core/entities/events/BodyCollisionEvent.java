package core.entities.events;

import core.entities.bodies.Body;
import core.event.AvoEvent;

// TODO
public class BodyCollisionEvent extends AvoEvent {

	private final Body movingBody;
	
	public BodyCollisionEvent(Body movingBody) {
		this.movingBody = movingBody;
	}
	
	public Body getMovingBody() {
		return movingBody;
	}
	
}
