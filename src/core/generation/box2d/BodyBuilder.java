package core.generation.box2d;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class BodyBuilder {

	public static Body createEdge(World world, Vec2 position, Vec2 size) {
		BodyDef bodyDef = buildBodyDef(position, BodyType.STATIC);

		EdgeShape bodyShape = new EdgeShape();
		bodyShape.set(new Vec2(), new Vec2(size.x / WorldGeneratorBox2D.SCALE_FACTOR, size.y / WorldGeneratorBox2D.SCALE_FACTOR));
		
		FixtureDef boxFixture = buildFixtureDef(1f, bodyShape);

		Body body = world.createBody(bodyDef);
		body.createFixture(boxFixture);
		body.setFixedRotation(true);
		body.setGravityScale(0f);
		
		return body;
	}
	
	public static Body createPolygon(World world, Vec2 position, Vec2 size) {
		BodyDef bodyDef = buildBodyDef(position, BodyType.DYNAMIC);

		PolygonShape bodyShape = new PolygonShape();
		bodyShape.setAsBox(size.x / WorldGeneratorBox2D.SCALE_FACTOR / 2f, size.y / WorldGeneratorBox2D.SCALE_FACTOR / 2f);

		FixtureDef boxFixture = buildFixtureDef(1f, bodyShape);

		Body body = world.createBody(bodyDef);
		body.createFixture(boxFixture);
		body.setFixedRotation(true);
		body.setGravityScale(0f);
		
		return body;
	}
	
	public static Body createCircle(World world, Vec2 position, float radius) {
		BodyDef bodyDef = buildBodyDef(position.addLocal(radius, radius), BodyType.DYNAMIC);

		CircleShape bodyShape = new CircleShape();
		bodyShape.setRadius(radius / WorldGeneratorBox2D.SCALE_FACTOR);

		FixtureDef boxFixture = buildFixtureDef(1f, bodyShape);

		Body body = world.createBody(bodyDef);
		body.createFixture(boxFixture);
		body.setFixedRotation(true);
		body.setGravityScale(0f);
		body.setLinearDamping(15f);
		
		return body;
	}
	
	private static BodyDef buildBodyDef(Vec2 position, BodyType type) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(position.x / WorldGeneratorBox2D.SCALE_FACTOR, position.y / WorldGeneratorBox2D.SCALE_FACTOR);
		bodyDef.type = type;
		
		return bodyDef;
	}
	
	private static FixtureDef buildFixtureDef(float density, Shape bodyShape) {
		FixtureDef boxFixture = new FixtureDef();
		boxFixture.density = density;
		boxFixture.shape = bodyShape;
		
		return boxFixture;
	}
	
}
