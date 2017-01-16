package core.physics.colliders;

import org.lwjgl.util.vector.Vector3f;

import core.entities.events.BodyEvent;

public class BoxCollider extends Collider {
	
	float width, height;
	
	public BoxCollider(Vector3f position, float width, float height) {
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
		if(bodyTwo instanceof WallCollider) {
			return buildWallCollision((BoxCollider) bodyTwo);
		}
		
		return null;
	}
	
	private BodyEvent buildWallCollision(BoxCollider bodyTwo) {
		return new BodyEvent(3, (Vector3f)new Vector3f(position).scale(-1));
	}
	
}
