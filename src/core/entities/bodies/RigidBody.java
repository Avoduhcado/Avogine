package core.entities.bodies;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.lwjgl.util.vector.Vector3f;

import core.Theater;
import core.entities.Entity;
import core.entities.events.BodyEvent;
import core.entities.listeners.RigidBodyEventListener;
import core.physics.colliders.Collider;

public class RigidBody extends Body implements RigidBodyEventListener{
	private Vector3f position;
	private Vector3f velocity;
	private Vector3f acceleration;
	private float mass;
	private Collider collider;
	private boolean gravityEnabled;
	

	
	public RigidBody(Entity entity, Vector3f position, Collider collider) {
		super(entity);
		this.position = position;
		this.velocity = new Vector3f();
		this.acceleration = new Vector3f();
		this.mass = 0;
		this.collider = collider;
		this.gravityEnabled = true;
		
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
		return collider.getCenter();
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
	
	public void disableGravity() {
		gravityEnabled = false;
	}
	public void enableGravity() {
		gravityEnabled = true;
	}
	
	public boolean gravityEnabled() {
		return gravityEnabled;
	}
	
	public Collider getCollider() {
		return collider;
	}

	@Override
	public Vector3f getSize() {
		// TODO Auto-generated method stub
		if(collider != null) {
			return collider.getSize();
		}
		return new Vector3f();
	}
}
