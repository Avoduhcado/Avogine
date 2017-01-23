package core.physics.colliders;

import org.lwjgl.util.vector.Vector3f;

import core.entities.events.BodyEvent;

public class BoxCollider extends Collider {
	
	Vector3f size;
	
	public BoxCollider(Vector3f position, Vector3f size) {
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
		if(bodyTwo instanceof WallCollider) {
			return buildWallCollision((BoxCollider) bodyTwo);
		}
		
		return null;
	}
	
	private BodyEvent buildWallCollision(BoxCollider bodyTwo) {
		return new BodyEvent(3, (Vector3f)new Vector3f(position).scale(-1));
	}

	@Override
	public Vector3f getSize() {
		// TODO Auto-generated method stub
		return size;
	}
	
}
