package core.physics.world;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import core.entities.Entity;
import core.entities.bodies.Body;
import core.entities.bodies.RigidBody;
import core.entities.events.BodyEvent;
import core.physics.colliders.BoxCollider;
import core.physics.colliders.Collider;

public class World {
	
	float gravityMag; //magnitude of gravity
	float dragMag; //magnitude of air resistance
	BodyEvent gravity; //gravity forceEvent
	
	ArrayList<RigidBody> bodies;
	
	public World() {
		gravityMag = 0;
		dragMag = 0;
		gravity = new BodyEvent(BodyEvent.FORCE, new Vector3f(0, gravityMag, 0)); 
		bodies = new ArrayList<RigidBody>();
	}
	

	
	public RigidBody addBody(Entity entity, Vector3f position, Vector3f size) {
		RigidBody body = new RigidBody(entity, position, new BoxCollider(position, size));
		bodies.add(body);
		return body;
	}
	
	public void update() {
		bodies.stream()
			.filter(Body -> Body.gravityEnabled())
			.forEach(Body -> Body.force(gravity));
		//checkCollisions();
		bodies.stream().forEach(RigidBody::update);
	}

	public void setGravity(float magnitude) {
		gravityMag = magnitude;
		gravity = new BodyEvent(BodyEvent.FORCE, new Vector3f(0, gravityMag, 0)); 
	}
	
	public void setDrag(float magnitude) {
		dragMag = magnitude;
	}
	
	private BodyEvent airResistance(RigidBody body) {
		return new BodyEvent(BodyEvent.FORCE, (Vector3f) new Vector3f(body.getVelocity()).scale(dragMag) );
	}
	
	private void checkCollisions() {
		for(int i=0; i < bodies.size(); i++) {
			for(int j=0; j < bodies.size(); j++) {
				if( hasCollision(bodies.get(i).getCollider(), bodies.get(j).getCollider()) ) {
					bodies.get(i).impulse(collision(bodies.get(i).getCollider(), bodies.get(j).getCollider()));
				}
			}
		}
	}
	
	private BodyEvent collision(Collider bodyOne, Collider bodyTwo) {
		
		return bodyOne.buildCollision(bodyTwo);
	}
	
	private boolean hasCollision(Collider bodyOne, Collider bodyTwo) {
		
		return false;
	}
}
