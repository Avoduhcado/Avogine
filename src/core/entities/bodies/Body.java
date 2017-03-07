package core.entities.bodies;

import java.awt.geom.Rectangle2D;

import org.lwjgl.util.vector.Vector3f;

import core.entities.Entity;
import core.entities.components.EntityComponent;
import core.entities.listeners.BodyListener;
import core.render.Transform;

public abstract class Body extends EntityComponent implements BodyListener {
	
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
	
	public boolean intersects(Body body) {
		return new Rectangle2D.Double(getPosition().x, getPosition().y, getSize().x, getSize().y)
				.intersects(body.getPosition().x, body.getPosition().y, body.getSize().x, body.getSize().y);
	}
	
	@Override
	public void applyTransformEffect(Transform transform) {
		transform.setPosition(getPosition());
		transform.setSize(getSize().x, getSize().y);
	}
	
}
