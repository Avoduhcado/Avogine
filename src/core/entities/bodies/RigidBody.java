package core.entities.bodies;

import org.lwjgl.util.vector.Vector3f;

import core.Theater;
import core.entities.Entity;
import core.entities.events.BodyEvent;

public class RigidBody extends Body {
	private Vector3f position;
	private Vector3f velocity;
	private Vector3f acceleration;
	private float width, height;
	private float mass;
	
	public RigidBody(Entity entity, float width, float height) {
		super(entity);
		this.position = new Vector3f();
		this.velocity = new Vector3f();
		this.acceleration = new Vector3f();
		setWidth(width);
		setHeight(height);
	}
	
	public RigidBody(Entity entity, Vector3f vec, float width, float height) {
		super(entity);
		this.position = vec;
		this.velocity = new Vector3f();
		this.acceleration = new Vector3f();
		setWidth(width);
		setHeight(height);
	}
	
	public void update() {
		float timestep = Theater.getDeltaSpeed(0.025f);
		Vector3f.add(velocity, (Vector3f) new Vector3f(acceleration).scale(timestep), velocity);
		Vector3f.add(position, (Vector3f) new Vector3f(velocity).scale(timestep), position);
	}

	@Override
	public Vector3f getPosition() {
		return position;
	}
	
	public Vector3f getVelocity() {
		return velocity;
	}
	
	public Vector3f getAcceleration() {
		return acceleration;
	}
	
	@Override
	public Vector3f getCenter() {
		return Vector3f.add(position, new Vector3f(width * 0.5f, height * 0.5f, 0), null);
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	@Override
	public void move(BodyEvent e) {
		Vector3f.add(position, e.getMovement(), position);
	}

	@Override
	public void force(BodyEvent e) {
		float timestep = Theater.getDeltaSpeed(0.025f);
		Vector3f.add(acceleration, (Vector3f) new Vector3f(e.getMovement()).scale(timestep), acceleration);
	}

	@Override
	public void impulse(BodyEvent e) {
		Vector3f.add(velocity, e.getMovement(), velocity);
	}
}
