package core.entities.bodies;

import org.lwjgl.util.vector.Vector3f;

import core.entities.Entity;
import core.entities.events.BodyCollisionEvent;
import core.entities.events.BodyEvent;

public class PlainBody extends Body {

	protected Vector3f position;
	protected Vector3f size;
	
	public PlainBody(Entity entity, Vector3f vec, Vector3f size) {
		super(entity);
		this.position = vec;
		this.size = size;
	}

	@Override
	public Vector3f getPosition() {
		return position;
	}

	@Override
	public void setPosition(Vector3f position) {
		this.position.set(position);
	}

	@Override
	public Vector3f getSize() {
		return size;
	}

	@Override
	public void setSize(Vector3f size) {
		this.size.set(size);
	}

	@Override
	public void move(BodyEvent e) {
		Vector3f.add(position, e.getMovement(), position);
		
		entity.getContainer().fireEvent(new BodyCollisionEvent(this));
	}

}
