package core.entities;

import java.io.Serializable;
import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;

import core.entities.bodies.Body;
import core.entities.components.EntityComponent;
import core.entities.components.interactions.ActivateInteraction;
import core.entities.components.interactions.AutorunInteraction;
import core.entities.components.interactions.Interaction;
import core.entities.components.interactions.TouchInteraction;
import core.entities.controllers.Controller;
import core.entities.events.BodyEvent;
import core.entities.events.EntityEvent;
import core.entities.events.InteractEvent;
import core.entities.renders.Render;

public class Entity implements Comparable<Entity>, Serializable {
	private static final long serialVersionUID = 1L;
	
	private String name;
	
	private Body body;
	private Render render;
	private Controller controller;
	
	private HashMap<Class<? extends EntityComponent>, EntityComponent> components = new HashMap<Class<? extends EntityComponent>, EntityComponent>();

	/**
	 * Poll entity's controller for updates. <br>
	 * Process any changes on entity's body.
	 */
	public void update() {
		if(controllable()) {
			controller.control();
		}
	}
	
	/**
	 * Draw the entity's render on the screen defined by the body's position.
	 */
	public void draw() {
		if(renderable()) {
			render.draw(getBodyPosition());
		}
	}

	@Override
	public int compareTo(Entity other) {
		if(hasBody() && other.hasBody()) {
			if(getBodyPosition().getY() >= other.getBodyPosition().getY()) {
				return 1;
			} else {
				return -1;
			}
		}
		return 0;
	}

	public void fireEvent(EntityEvent event) {
		if(event instanceof BodyEvent) {
			processBodyEvent((BodyEvent) event);
		} else if(event instanceof InteractEvent) {
			processInteractEvent((InteractEvent) event);
		}
	}
	
	protected void processBodyEvent(BodyEvent e) {
		if(!hasBody()) {
			return;
		}
		
		switch(e.getType()) {
		case BodyEvent.MOVE:
			body.move(e);
			break;
		}
	}
	
	protected void processInteractEvent(InteractEvent event) {
		switch(event.getInteractType()) {
		case AUTORUN:
			if(components.containsKey(AutorunInteraction.class)) {
				((Interaction) components.get(AutorunInteraction.class)).interact(event);
			}
			break;
		case ON_TOUCH:
			if(components.containsKey(TouchInteraction.class)) {
				((Interaction) components.get(TouchInteraction.class)).interact(event);
			}
			break;
		case ON_ACTIVATE:
		case INTERRUPT:
			if(components.containsKey(ActivateInteraction.class)) {
				//getRender().lookAt(event.getInteractor());
				((Interaction) components.get(ActivateInteraction.class)).interact(event);
			}
			break;
		}
	}
	
	public boolean hasBody() {
		return body != null;
	}
	
	public Body getBody() {
		return body;
	}
	
	public void setBody(Body body) {
		this.body = body;
	}
	
	private Vector3f getBodyPosition() {
		if(hasBody()) {
			return body.getPosition();
		}
		return new Vector3f();
	}
	
	public boolean renderable() {
		return render != null;
	}
	
	public Render getRender() {
		return render;
	}
	
	public void setRender(Render render) {
		this.render = render;
	}
	
	public boolean controllable() {
		return controller != null;
	}
	
	public Controller getController() {
		return controller;
	}
	
	public void setController(Controller controller) {
		this.controller = controller;
	}

	public HashMap<Class<? extends EntityComponent>, EntityComponent> getComponents() {
		return components;
	}

	public void setComponents(HashMap<Class<? extends EntityComponent>, EntityComponent> components) {
		this.components = components;
	}
	
	public EntityComponent getComponent(Class<? extends EntityComponent> clazz) {
		if(components.containsKey(clazz)) {
			return components.get(clazz);
		}
		return null;
	}
	
	public void addComponent(Class<? extends EntityComponent> clazz, EntityComponent component) {
		components.put(clazz, component);
	}
	
	public EntityComponent removeComponent(Class<? extends EntityComponent> clazz) {
		return components.remove(clazz);
	}

	@Override
	public String toString() {
		return name;
	}

}
