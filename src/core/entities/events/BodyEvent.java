package core.entities.events;

import org.lwjgl.util.vector.Vector3f;

public class BodyEvent extends EntityEvent {
	
	public static final int MOVE = 1;
	public static final int FORCE = 2;
	public static final int IMPULSE = 3;
	
	private final int type;
	private Vector3f vector;

	public BodyEvent(int type, Vector3f movement) {
		this.type = type;
		this.vector = movement;
	}

	public int getType() {
		return type;
	}

	public Vector3f getMovement() {
		return vector;
	}

	public void setMovement(Vector3f movement) {
		this.vector = movement;
	}
}
