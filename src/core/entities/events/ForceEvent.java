package core.entities.events;

import org.lwjgl.util.vector.Vector3f;

public class ForceEvent extends BodyEvent{
	private Vector3f force;

	public ForceEvent(Vector3f force) {
		this.setForce(force);
	}

	public Vector3f getForce() {
		return force;
	}

	public void setForce(Vector3f force) {
		this.force = force;
	}
}
