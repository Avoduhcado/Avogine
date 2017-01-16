package core.physics.colliders;

import org.lwjgl.util.vector.Vector3f;

import core.entities.events.BodyEvent;

public abstract class Collider {
	Vector3f position;
	
	public Collider(Vector3f position) {
		this.position = position;
	}
	
	public abstract BodyEvent buildCollision(Collider bodyTwo);
	
	public abstract Vector3f getCenter();
	public abstract double getWidth();
	public abstract double getHeight();
	
	
}
