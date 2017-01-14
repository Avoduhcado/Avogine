package core.physics.world;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import core.entities.bodies.Body;
import core.entities.bodies.RigidBody;
import core.entities.events.BodyEvent;

public class World {
	
	float gravityMag; //magnitude of gravity
	float dragMag; //magnitude of air resistance
	BodyEvent gravity; //gravity forceEvent
	
	ArrayList<Body> bodies;
	
	public World() {
		gravityMag = 0;
		dragMag = 0;
		gravity = new BodyEvent(BodyEvent.FORCE, new Vector3f(0, gravityMag, 0)); 
		bodies = new ArrayList<Body>();
	}
	
	public World(ArrayList<Body> bodies) {
		gravityMag = 0;
		dragMag = 0;
		gravity = new BodyEvent(BodyEvent.FORCE, new Vector3f(0, gravityMag, 0));
		this.bodies = bodies;
	}
	
	public void addBody(Body body) {
		bodies.add(body);
	}
	
	public void update() {
		bodies.stream().forEach(Body -> Body.force(gravity));
		//bodies.stream().forEach(Body::update);
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
}
