package core.generation.box2d;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.lwjgl.util.vector.Vector3f;

import core.Theater;
import core.generation.WorldGenerator;
import core.render.DrawUtils;
import core.utilities.text.Text;
import core.utilities.text.TextModifier;
import core.utilities.text.TextModifier.TextModValue;

public class RoomBox2D extends Rectangle {
	private static final long serialVersionUID = 1L;
	
	private static int roomCount = 0;
	
	private final int ID;
	private Body body;
	private boolean active;
	private Map<RoomBox2D, Boolean> edges = new HashMap<>();
	
	public RoomBox2D(Vec2 position, Vec2 size) {
		if(size.x == WorldGenerator.TILE_SIZE && size.y == WorldGenerator.TILE_SIZE) {
			ID = -1;
		} else {
			ID = roomCount++;
		}
		setFrameFromCenter(position.x, position.y, position.x - (size.x / 2), position.y - (size.y / 2));
	}
	
	public RoomBox2D(World world, Vec2 position, Vec2 size) {
		this(position, size);
		
		this.body = BodyBuilder.createPolygon(world, position, size);
	}
	
	public void draw() {
		if(WorldGenerator.hideInactiveRooms && !isActive()) {
			return;
		}
		
		if(body != null) {
			Vec2 size = getBodySize();
			Vec2 position = getBodyPosition(size);

			DrawUtils.setColor(new Vector3f(0, 0, 1f));
			DrawUtils.drawRect(new Rectangle2D.Double(position.x, position.y, size.x, size.y));
		}
		
		DrawUtils.setColor(active ? new Vector3f(1f, 0, 0) : new Vector3f(0, 0, 1f));
		DrawUtils.drawRect(new Rectangle2D.Double(x, y, width, height));
		
		if(WorldGenerator.showRoomNumber && ID != -1) {
			Text.getFont("DEBUG").drawString("" + ID, getX(), getY(), TextModifier.compile(TextModValue.SIZE + "=1"));
		}
		
		if(WorldGenerator.hideConnections) {
			return;
		}
		
		for(RoomBox2D room : edges.keySet()) {
			if(!edges.get(room)) {
				if(Theater.debug) {
					DrawUtils.setColor(new Vector3f(0.5f, 1f, 0.5f));
					DrawUtils.drawLine(getCenterX(), getCenterY(), room.getCenterX(), room.getCenterY());
				}
				continue;
			}
			DrawUtils.setColor(new Vector3f(0, 1f, 0));
			DrawUtils.drawLine(getCenterX(), getCenterY(), room.getCenterX(), room.getCenterY());
		}
	}

	public void updateRoomToGrid() {
		Vec2 size = getBodySize();
		Vec2 position = getBodyPosition(size);
		
		setBounds(WorldGenerator.roundM(position.x, WorldGenerator.TILE_SIZE), WorldGenerator.roundM(position.y, WorldGenerator.TILE_SIZE),
				width, height);
	}
	
	public void destroyBody() {
		body.getWorld().destroyBody(body);
		body = null;
	}

	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public Point getCenter() {
		return new Point((int) getCenterX(), (int) getCenterY());
	}
	
	private Vec2 getBodySize() {
		if(body == null) {
			return new Vec2((float) getWidth(), (float) getHeight());
		}
		
		PolygonShape bodyShape = (PolygonShape) body.getFixtureList().getShape();
		return new Vec2(bodyShape.getVertex(2).x - bodyShape.getVertex(0).x, bodyShape.getVertex(2).y - bodyShape.getVertex(0).y)
				.mul(WorldGeneratorBox2D.SCALE_FACTOR);
	}
	
	private Vec2 getBodyPosition(Vec2 size) {
		if(body == null) {
			return new Vec2((float) getX(), (float) getY());
		}
		
		return body.getPosition().mul(WorldGeneratorBox2D.SCALE_FACTOR).sub(size.mul(0.5f));
	}
	
	public Body getBody() {
		return body;
	}

	public void clearEdges() {
		edges.clear();
	}
	
	public void disableEdges() {
		for(RoomBox2D room : edges.keySet()) {
			edges.replace(room, false);
		}
	}
	
	public void setEnabledEdge(RoomBox2D edge, boolean enabled) {
		edges.put(edge, enabled);
	}
	
	public Set<RoomBox2D> getEdges() {
		return edges.keySet();
	}
	
	public List<RoomBox2D> getEdgesAsList() {
		return Arrays.asList(edges.keySet().toArray(new RoomBox2D[0]));
	}
	
	public List<RoomBox2D> getEnabledEdges() {
		List<RoomBox2D> enabledEdges = new ArrayList<RoomBox2D>();
		edges.keySet().stream().filter(e -> edges.get(e)).forEach(e -> enabledEdges.add(e));
		return enabledEdges;
	}
	
	public void makeEdge(RoomBox2D room) {
		edges.put(room, true);
	}
	
	public static void resetRoomCount() {
		roomCount = 0;
	}
	
}
