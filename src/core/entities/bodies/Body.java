package core.entities.bodies;

import org.lwjgl.util.vector.Vector3f;

import core.entities.Entity;
import core.entities.components.EntityComponent;
import core.entities.events.BodyEvent;

public abstract class Body extends EntityComponent {
	
	public Body(Entity entity) {
		super(entity);
	}

	public abstract void update();
	public abstract Vector3f getPosition();
	public abstract Vector3f getCenter();
	public abstract void processEvent(BodyEvent event);

}
