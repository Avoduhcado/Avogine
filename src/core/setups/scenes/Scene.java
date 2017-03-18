package core.setups.scenes;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import core.Camera;
import core.entities.Entity;
import core.entities.bodies.PlainBody;
import core.entities.components.interactions.ActivateInteraction;
import core.entities.components.interactions.Interaction;
import core.entities.components.interactions.TouchInteraction;
import core.entities.controllers.PlayerController;
import core.entities.renders.PlainRender;
import core.entities.renders.Render;
import core.event.AvoEvent;
import core.render.effects.Tween;
import core.render.effects.Tween3fEffect;
import core.render.effects.Tween4fEffect;
import core.render.effects.TweenEffect;
import core.scripts.Script;
import core.setups.scenes.components.SceneComponent;
import core.setups.utils.EntityContainer;
import core.utilities.ComponentBased;

public class Scene implements ComponentBased<SceneComponent>, EntityContainer {

	private ArrayList<Entity> entities = new ArrayList<Entity>();
	
	public Scene() {
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
		
		ent = new Entity(this);
		ent.addRender(new PlainRender(ent, "AGDG Logo"));
		ent.addBody(new PlainBody(ent, new Vector3f(0f, 0f, 0f), new Vector3f(32f, 32f, 0f)));
		ent.addController(new PlayerController(ent));
		addEntity(ent);
		
		Camera.get().setFocus(ent.getBody());
	}
	
	public void draw() {
		drawEntities();
	}
	
	@Override
	public void drawEntities() {
		entities.stream()
			.sorted()
			.forEach(Entity::draw);
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
