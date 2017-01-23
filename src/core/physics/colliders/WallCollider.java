package core.physics.colliders;

import org.lwjgl.util.vector.Vector3f;

import core.entities.events.BodyEvent;

public class WallCollider extends Collider {
	
	Vector3f size;
	
	public WallCollider(Vector3f position, Vector3f size) {
		super(position);
		this.size = size;
	}
	
	@Override
	public Vector3f getCenter() {
		// TODO Auto-generated method stub
		return Vector3f.add(position, new Vector3f(size.x * 0.5f, size.y * 0.5f, 0), null);
	}


	@Override
	public BodyEvent buildCollision(Collider bodyTwo) {
		
		return null;
	}

	@Override
	public Vector3f getSize() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
