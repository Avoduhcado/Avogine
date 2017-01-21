package core.entities.components;

import core.entities.Entity;
import core.render.Transform;

public abstract class EntityComponent {

	protected Entity entity;
	
	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public EntityComponent(Entity entity) {
		this.entity = entity;
	}
	
	// Optional overridable function to apply component specfic effects to the entity's transform
	public void applyTransformEffect(Transform transform) {
		
	}
	
}
