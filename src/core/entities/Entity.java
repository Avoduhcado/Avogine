package core.entities;

import java.io.Serializable;
import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;

import core.Theater;
import core.entities.bodies.Body;
import core.entities.bodies.Box2DBody;
import core.entities.components.EntityComponent;
import core.entities.components.interactions.Interaction;
import core.entities.controllers.Controller;
import core.entities.events.BodyEvent;
import core.entities.events.ControllerEvent;
import core.entities.events.InteractEvent;
import core.entities.renders.Render;
import core.event.AvoEvent;
import core.render.DrawUtils;
import core.setups.utils.EntityContainer;
import core.utilities.ComponentBased;

public class Entity implements ComponentBased<EntityComponent>, Comparable<Entity>, Serializable {
	private static final long serialVersionUID = 1L;
	
	private String name;
	private EntityContainer container;
		
	private HashMap<Class<?>, EntityComponent> components = new HashMap<Class<?>, EntityComponent>();

	public Entity(EntityContainer container) {
		this.container = container;
	}
	
	/**
	 * Draw the entity's render on the screen defined by the body's position.
	 */
	public void draw() {
		if(hasRender()) {
			getRender().draw();
		}
		
		if(Theater.debug) {
			if(hasBody() && getBody() instanceof Box2DBody) {
				DrawUtils.setColor(new Vector3f((float) Math.random(),(float)  Math.random(), (float) Math.random()));
				Box2DBody b2dbody = (Box2DBody) getBody();
				DrawUtils.drawBox2DShape(b2dbody.getBody(), b2dbody.getBody().getFixtureList().getShape());
			}
		}
	}

	@Override
	public int compareTo(Entity other) {
		if(hasBody() && other.hasBody()) {
			return (int) (getBody().getBottom().getY() - other.getBody().getBottom().getY());
		}
		return 0;
	}

	public EntityContainer getContainer() {
		return container;
	}
	
	public void fireEvent(AvoEvent event) {
		if(event instanceof ControllerEvent) {
			processControllerEvent((ControllerEvent) event);
		} else if(event instanceof BodyEvent) {
			processBodyEvent((BodyEvent) event);
		} else if(event instanceof InteractEvent) {
			processInteractEvent((InteractEvent) event);
		}
	}
	
	protected void processControllerEvent(ControllerEvent e) {
		if(!hasController()) {
			return;
		}
		
		switch(e.getType()) {
		case ControllerEvent.MOVE_RIGHT | ControllerEvent.MOVE_LEFT | ControllerEvent.MOVE_UP | ControllerEvent.MOVE_DOWN:
			getController().movement(e);
			break;
		}
	}
	
	protected void processBodyEvent(BodyEvent e) {
		if(!hasBody()) {
			return;
		}
		
		switch(e.getType()) {
		case BodyEvent.MOVE:
			getBody().move(e);
			break;
		}
	}
	
	protected void processInteractEvent(InteractEvent event) {
		if(!hasComponent(Interaction.class)) {
			return;
		}
		
		getComponent(Interaction.class).interact(event);
	}

	public String getName() {
		return name;
	}
	
	public boolean hasBody() {
		return hasComponent(Body.class);
	}
	
	public Body getBody() {
		return getComponent(Body.class);
	}
	
	public Body addBody(Body body) {
		return addComponent(body);
	}
	
	public boolean hasRender() {
		return hasComponent(Render.class);
	}
	
	public Render getRender() {
		return getComponent(Render.class);
	}
	
	public Render addRender(Render render) {
		return addComponent(render);
	}
	
	public boolean hasController() {
		return hasComponent(Controller.class);
	}
	
	public Controller getController() {
		return getComponent(Controller.class);
	}
	
	public Controller addController(Controller controller) {
		return addComponent(controller);
	}

	@Override
	public HashMap<Class<?>, EntityComponent> getComponents() {
		return components;
	}

	@Override
	public void setComponents(HashMap<Class<?>, EntityComponent> components) {
		this.components = components;
	}
	
	@Override
	public <T extends EntityComponent> T getComponent(Class<T> clazz) {
		if(components.containsKey(clazz)) {
			return clazz.cast(components.get(clazz));
		}
		return null;
	}

	@Override
	public boolean hasComponent(Class<? extends EntityComponent> clazz) {
		return components.containsKey(clazz);
	}

	@Override
	public <T extends EntityComponent> T addComponent(T component) {
		Class<?> clazz = component.getClass();
		while(clazz.getSuperclass() != null && !clazz.getSuperclass().equals(EntityComponent.class)) {
			clazz = clazz.getSuperclass();
		}
		components.put(clazz, component);
		return component;
	}

	@Override
	public <T extends EntityComponent> T removeComponent(Class<T> clazz) {
		return clazz.cast(components.remove(clazz));
	}

	@Override
	public String toString() {
		return name;
	}

}
