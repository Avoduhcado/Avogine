package core.entities.bodies;

import org.lwjgl.util.vector.Vector3f;

import core.entities.Entity;
import core.entities.events.BodyEvent;

public class TileBody extends PlainBody {

	public TileBody(Entity entity, Vector3f position, Vector3f size) {
		super(entity, position, size);
	}

	@Override
	public void move(BodyEvent e) {}

	@Override
	public Vector3f getBottom() {
		return new Vector3f(0, Float.MIN_VALUE, 0);
	}

}
