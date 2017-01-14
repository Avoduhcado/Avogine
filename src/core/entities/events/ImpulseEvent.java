package core.entities.events;

import org.lwjgl.util.vector.Vector3f;

public class ImpulseEvent extends BodyEvent {
	
	private Vector3f impulse;

	public ImpulseEvent(Vector3f impulse) {
		this.setImpulse(impulse);
	}

	public Vector3f getImpulse() {
		return impulse;
	}

	public void setImpulse(Vector3f impulse) {
		this.impulse = impulse;
	}
}
