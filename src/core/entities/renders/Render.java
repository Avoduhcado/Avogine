package core.entities.renders;

import org.lwjgl.util.vector.Vector3f;

import core.entities.Entity;
import core.entities.components.EntityComponent;

public abstract class Render extends EntityComponent {
	
	public Render(Entity entity) {
		super(entity);
	}

	public abstract void draw(Vector3f position, double width, double height);

}
