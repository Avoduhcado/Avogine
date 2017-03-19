package core.entities.bodies;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.lwjgl.util.vector.Vector3f;

import core.entities.Entity;
import core.entities.events.BodyCollisionEvent;
import core.entities.events.BodyEvent;
import core.generation.box2d.BodyBuilder;
import core.generation.box2d.WorldGeneratorBox2D;

public class Box2DBody extends core.entities.bodies.Body {

	private Body body;
	
	public Box2DBody(Entity entity, World world, Vec2 position, float radius) {
		super(entity);
		body = BodyBuilder.createCircle(world, position, radius);
	}

	@Override
	public Vector3f getPosition() {
		return new Vector3f((body.getPosition().x - body.getFixtureList().getShape().m_radius) * WorldGeneratorBox2D.SCALE_FACTOR,
				(body.getPosition().y - body.getFixtureList().getShape().m_radius) * WorldGeneratorBox2D.SCALE_FACTOR, 0);
	}

	@Override
	public void setPosition(Vector3f position) {
		body.setTransform(new Vec2(position.x / WorldGeneratorBox2D.SCALE_FACTOR, position.y / WorldGeneratorBox2D.SCALE_FACTOR),
				body.getAngle());
	}

	@Override
	public Vector3f getSize() {
		return new Vector3f(body.getFixtureList().getShape().m_radius * WorldGeneratorBox2D.SCALE_FACTOR * 2,
				body.getFixtureList().getShape().m_radius * WorldGeneratorBox2D.SCALE_FACTOR * 2, 0);
	}

	@Override
	public void setSize(Vector3f size) {
		body.getFixtureList().getShape().setRadius(size.x);
	}
	
	@Override
	public Vector3f getCenter() {
		return new Vector3f((body.getPosition().x + body.getFixtureList().getShape().m_radius) * WorldGeneratorBox2D.SCALE_FACTOR,
				(body.getPosition().y + body.getFixtureList().getShape().m_radius) * WorldGeneratorBox2D.SCALE_FACTOR, 0);
	}
	
	public Body getBody() {
		return body;
	}

	@Override
	public void move(BodyEvent e) {
		body.applyForceToCenter(new Vec2(e.getMovement().x, e.getMovement().y));

		entity.getContainer().fireEvent(new BodyCollisionEvent(this));
	}

}
