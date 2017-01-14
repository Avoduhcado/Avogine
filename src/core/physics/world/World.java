package core.physics.world;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import core.entities.Entity;
import core.entities.bodies.Body;
import core.entities.bodies.RigidBody;
import core.entities.events.BodyEvent;
import core.entities.events.ForceEvent;

public class World {
	
	float gravityMag; //magnitude of gravity
	float dragMag; //magnitude of air resistance
	ForceEvent gravity; //gravity forceEvent
	
	ArrayList<Body> bodies;
	
	public World() {
		gravityMag = 0;
		dragMag = 0;
		gravity = new ForceEvent(new Vector3f(0, gravityMag, 0)); 
		bodies = new ArrayList<Body>();
	}
	
	public World(ArrayList<Body> bodies) {
		gravityMag = 0;
		dragMag = 0;
		gravity = new ForceEvent(new Vector3f(0, gravityMag, 0));
		this.bodies = bodies;
	}
	
	public void addBody(Body body) {
		bodies.add(body);
	}
	
	public void update() {
		bodies.stream().forEach(Body -> Body.processEvent(gravity));
		//bodies.stream().forEach(Body::update);
	}

	public void setGravity(float magnitude) {
		gravityMag = magnitude;
		gravity = new ForceEvent(new Vector3f(0, gravityMag, 0)); 
	}
	
	public void setDrag(float magnitude) {
		dragMag = magnitude;
	}
	
	private ForceEvent airResistance(RigidBody body) {
		return new ForceEvent( (Vector3f) new Vector3f(body.getVelocity()).scale(dragMag) );
	}
}
