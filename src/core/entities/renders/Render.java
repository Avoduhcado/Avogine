package core.entities.renders;

import core.entities.Entity;
import core.entities.components.EntityComponent;
import core.render.Transform;

public abstract class Render extends EntityComponent {
	
	public Render(Entity entity) {
		super(entity);
	}

	public abstract void draw(Transform transform);

}
