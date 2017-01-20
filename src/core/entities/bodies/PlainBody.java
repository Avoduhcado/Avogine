package core.entities.bodies;

import org.lwjgl.util.vector.Vector3f;

import core.entities.Entity;
import core.entities.events.BodyEvent;

public class PlainBody extends Body {

	private Vector3f position;
	private Vector3f size;
	
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
	public Vector3f getSize() {
		return size;
	}

	@Override
	public void move(BodyEvent e) {
		Vector3f.add(position, e.getMovement(), position);
	}

}
