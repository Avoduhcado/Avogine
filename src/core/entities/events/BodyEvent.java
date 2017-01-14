package core.entities.events;

import org.lwjgl.util.vector.Vector3f;

public class BodyEvent extends EntityEvent {
	
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
