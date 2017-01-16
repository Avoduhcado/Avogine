package core.physics.colliders;

import org.lwjgl.util.vector.Vector3f;

import core.entities.events.BodyEvent;

public class WallCollider extends Collider {
	
	float width, height;
	
	public WallCollider(Vector3f position, float width, float height) {
		super(position);
		this.width = width;
		this.height = height;
	}
	
	@Override
	public Vector3f getCenter() {
		// TODO Auto-generated method stub
		return Vector3f.add(position, new Vector3f(width * 0.5f, height * 0.5f, 0), null);
	}

	@Override
	public double getWidth() {
		// TODO Auto-generated method stub
		return width;
	}

	@Override
	public double getHeight() {
		// TODO Auto-generated method stub
		return height;
	}

	@Override
	public BodyEvent buildCollision(Collider bodyTwo) {
		
		return null;
	}
	
	
}
