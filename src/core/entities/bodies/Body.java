package core.entities.bodies;

import org.lwjgl.util.vector.Vector3f;

import core.entities.Entity;
import core.entities.components.EntityComponent;
import core.entities.listeners.BodyEventListener;

public abstract class Body extends EntityComponent implements BodyEventListener {
	
	public Body(Entity entity) {
		super(entity);
	}

	public abstract Vector3f getPosition();
	public abstract Vector3f getSize();
	
	public Vector3f getCenter() {
		return Vector3f.add(getPosition(), (Vector3f) new Vector3f().set(getSize()).scale(0.5f), null);
	}
	
	public Vector3f getBottom() {
		return Vector3f.add(getPosition(), new Vector3f(0, getSize().getY(), 0), null);
	}
	
}
