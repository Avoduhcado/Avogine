package core.setups.scenes;

import java.util.ArrayList;
import java.util.HashMap;

import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import core.Camera;
import core.entities.Entity;
import core.entities.bodies.Box2DBody;
import core.entities.bodies.PlainBody;
import core.entities.bodies.TileBody;
import core.entities.components.interactions.ActivateInteraction;
import core.entities.components.interactions.Interaction;
import core.entities.components.interactions.TouchInteraction;
import core.entities.controllers.PlayerController;
import core.entities.renders.PlainRender;
import core.entities.renders.Render;
import core.event.AvoEvent;
import core.generation.box2d.BodyBuilder;
import core.generation.box2d.WorldGeneratorBox2D;
import core.render.DrawUtils;
import core.render.effects.Tween;
import core.render.effects.Tween3fEffect;
import core.render.effects.Tween4fEffect;
import core.render.effects.TweenEffect;
import core.scripts.Script;
import core.setups.scenes.components.SceneComponent;
import core.setups.utils.EntityContainer;
import core.utilities.ComponentBased;

public class Scene implements ComponentBased<SceneComponent>, EntityContainer {

	private World world;
	private ArrayList<Body> walls = new ArrayList<Body>();
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	
	public Scene() {
		world = new World(new Vec2(0, 0));
		
		Entity ent = new Entity(this);
		Render render = ent.addRender(new PlainRender(ent, "AGDG Logo"));
		TweenEffect<?> e = new Tween3fEffect(new Vector3f(render.getTransform().getRotation(), 0, 0), new Vector3f(36f, 0, 0), 2f, false, Tween.IN_OUT) {
			@Override
			protected void applyEffect(Vector3f value) {
				render.getTransform().setCenterRotate(true);
				render.getTransform().setRotation(value.x);
			}
			
			@Override
			protected void processLoop() {
				startingValue = new Vector3f(render.getTransform().getRotation(), 0, 0);
				super.processLoop();
			}
		};
		e.setLoop(true);
		e.setReverse(true);
		render.addTweenEffect(e);
		
		e = new Tween4fEffect(render.getTransform().getColor(), new Vector4f(0f, 0, 0, 0f), 10f, true, Tween.IN_OUT) {
			@Override
			protected void applyEffect(Vector4f value) {
				render.getTransform().setColor(value);
			}
			
			@Override
			protected void processLoop() {
				startingValue = render.getTransform().getColor();
				super.processLoop();
			}
		};
		e.setLoop(true);
		e.setReverse(true);
		render.addTweenEffect(e);
		ent.addBody(new PlainBody(ent, new Vector3f(-16f, -16f, 0f), new Vector3f(256f, 256f, 0f)));
		ent.addComponent(new ActivateInteraction(ent, new Script(ent, Script.testScript)));
		addEntity(ent);
		
		System.out.println(ent.getComponent(Interaction.class));
		System.out.println(ent.getComponent(TouchInteraction.class));
		System.out.println(ent.getComponent(ActivateInteraction.class));
		
		Entity player = new Entity(this);
		player.addRender(new PlainRender(player, "AGDG Logo"));
		player.addBody(new Box2DBody(player, world, new Vec2(), 16));
		player.addController(new PlayerController(player, 40));
		addEntity(player);
		
		Camera.get().setFocus(player.getBody());
	}
	
	public void draw() {
		drawEntities();
	}
	
	@Override
	public void drawEntities() {
		walls.stream().forEach(e -> {
			DrawUtils.setColor(new Vector3f(0, 1, 0));
			EdgeShape edge = (EdgeShape) e.getFixtureList().getShape();
			DrawUtils.drawBox2DShape(e, edge);
		});
		
		entities.stream()
			.sorted()
			.forEach(Entity::draw);
	}
	
	public void setGrid(boolean[][] gridArray) {
		if(!walls.isEmpty()) {
			walls.stream().forEach(e -> world.destroyBody(e));
			walls.clear();
		}
		
		Entity tile = null;
		
		for(int x = 0; x<gridArray.length; x++) {
			for(int y = 0; y<gridArray[0].length; y++) {
				if(gridArray[x][y]) {
					tile = new Entity(this);
					tile.addBody(new TileBody(tile, new Vector3f(x * WorldGeneratorBox2D.SCALE_FACTOR, y * WorldGeneratorBox2D.SCALE_FACTOR, 0),
							new Vector3f(WorldGeneratorBox2D.SCALE_FACTOR, WorldGeneratorBox2D.SCALE_FACTOR, 0)));
					tile.addRender(new PlainRender(tile, "Sandstone"));
					addEntity(tile);
					
					if(x == 0 || (x > 0 && !gridArray[x - 1][y])) {
						walls.add(BodyBuilder.createEdge(world, new Vec2(x * WorldGeneratorBox2D.SCALE_FACTOR, y * WorldGeneratorBox2D.SCALE_FACTOR),
								new Vec2(0, WorldGeneratorBox2D.SCALE_FACTOR)));
					}
					if(x == gridArray.length - 1 || (x < gridArray.length - 1 && !gridArray[x + 1][y])) {
						walls.add(BodyBuilder.createEdge(world, new Vec2((x + 1) * WorldGeneratorBox2D.SCALE_FACTOR, y * WorldGeneratorBox2D.SCALE_FACTOR),
								new Vec2(0, WorldGeneratorBox2D.SCALE_FACTOR)));
					}
					if(y == 0 || (y > 0 && !gridArray[x][y - 1])) {
						walls.add(BodyBuilder.createEdge(world, new Vec2(x * WorldGeneratorBox2D.SCALE_FACTOR, y * WorldGeneratorBox2D.SCALE_FACTOR),
								new Vec2(WorldGeneratorBox2D.SCALE_FACTOR, 0)));
					}
					if(y == gridArray[0].length - 1 || (y < gridArray[0].length - 1 && !gridArray[x][y + 1])) {
						walls.add(BodyBuilder.createEdge(world, new Vec2(x * WorldGeneratorBox2D.SCALE_FACTOR, (y + 1) * WorldGeneratorBox2D.SCALE_FACTOR),
								new Vec2(WorldGeneratorBox2D.SCALE_FACTOR, 0)));
					}
				} else {
					tile = new Entity(this);
					tile.addBody(new TileBody(tile, new Vector3f(x * WorldGeneratorBox2D.SCALE_FACTOR, y * WorldGeneratorBox2D.SCALE_FACTOR, 0),
							new Vector3f(WorldGeneratorBox2D.SCALE_FACTOR, WorldGeneratorBox2D.SCALE_FACTOR, 0)));
					tile.addRender(new PlainRender(tile, "Sand"));
					addEntity(tile);
				}
			}
		}
	}
	
	public Vector3f getOriginRoomCoords(boolean[][] gridArray) {
		for(int x = 0; x < gridArray.length; x++) {
			for(int y = gridArray[0].length - 1; y > 0; y--) {
				if(gridArray[x][y]) {
					return new Vector3f(x * WorldGeneratorBox2D.SCALE_FACTOR, y * WorldGeneratorBox2D.SCALE_FACTOR, 0);
				}
			}
		}
		
		return new Vector3f();
	}
	
	public World getWorld() {
		return world;
	}
	
	@Override
	public void addEntity(Entity entity) {
		entities.add(entity);
	}

	@Override
	public Entity getEntity(String name) {
		return entities.stream()
				.filter(e -> e.getName().equals(name))
				.findFirst()
				.get();
	}

	@Override
	public ArrayList<Entity> getEntities() {
		return entities;
	}

	@Override
	public boolean removeEntity(Entity entity) {
		return entities.remove(entity);
	}

	@Override
	public void fireEvent(AvoEvent e) {
		entities.stream()
			.forEach(ent -> ent.fireEvent(e));
	}

	@Override
	public HashMap<Class<?>, SceneComponent> getComponents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setComponents(HashMap<Class<?>, SceneComponent> components) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <U extends SceneComponent> U getComponent(Class<U> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasComponent(Class<? extends SceneComponent> clazz) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T extends SceneComponent> T addComponent(T component) {
		// TODO Auto-generated method stub
		return component;
	}

	@Override
	public <U extends SceneComponent> U removeComponent(Class<U> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

}
