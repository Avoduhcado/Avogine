package core.entities.bodies;

import org.lwjgl.util.vector.Vector3f;

import core.entities.Entity;
import core.entities.events.BodyEvent;

public class PlainBody extends Body {

	private Vector3f position;
	private float width, height;
	
	public PlainBody(Entity entity, Vector3f vec, float width, float height) {
		super(entity);
		this.position = vec;
		setWidth(width);
		setHeight(height);
	}

	@Override
	public void update() {
	}
	
	@Override
	public Vector3f getPosition() {
		return position;
	}

	@Override
	public Vector3f getCenter() {
		return Vector3f.add(position, new Vector3f(width * 0.5f, height * 0.5f, 0), null);
	}

	@Override
	public void move(BodyEvent e) {
		Vector3f.add(position, e.getMovement(), position);
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

}
