package core.entities.events;

import org.lwjgl.util.vector.Vector3f;

import core.event.AvoEvent;

public class BodyEvent extends AvoEvent {
	
	public static final int MOVE = 1;
	
	private final int type;
	private Vector3f movement;

	public BodyEvent(int type, Vector3f movement) {
		this.type = type;
		this.movement = movement;
	}

	public int getType() {
		return type;
	}

	public Vector3f getMovement() {
		return movement;
	}

	public void setMovement(Vector3f movement) {
		this.movement = movement;
	}
}
