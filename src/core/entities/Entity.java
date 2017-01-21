package core.entities;

import java.io.Serializable;
import java.util.HashMap;

import core.entities.bodies.Body;
import core.entities.components.EntityComponent;
import core.entities.components.interactions.ActivateInteraction;
import core.entities.components.interactions.AutorunInteraction;
import core.entities.components.interactions.Interaction;
import core.entities.components.interactions.TouchInteraction;
import core.entities.controllers.Controller;
import core.entities.events.BodyEvent;
import core.entities.events.ControllerEvent;
import core.entities.events.EntityEvent;
import core.entities.events.InteractEvent;
import core.entities.renders.Render;
import core.render.Transform;

public class Entity implements Comparable<Entity>, Serializable {
	private static final long serialVersionUID = 1L;
	
	private String name;
	
	private Body body;
	private Render render;
	private Controller controller;
		
	private HashMap<Class<? extends EntityComponent>, EntityComponent> components = new HashMap<Class<? extends EntityComponent>, EntityComponent>();

	/**
	 * Draw the entity's render on the screen defined by the body's position.
	 */
	public void draw() {
		if(renderable()) {
			render.draw(constructTransform());
		}
	}

	@Override
	public int compareTo(Entity other) {
		if(hasBody() && other.hasBody()) {
			return (int) (body.getBottom().getY() - other.getBody().getBottom().getY());
		}
		return 0;
	}

	public void fireEvent(EntityEvent event) {
		if(event instanceof ControllerEvent) {
			processControllerEvent((ControllerEvent) event);
		} else if(event instanceof BodyEvent) {
			processBodyEvent((BodyEvent) event);
		} else if(event instanceof InteractEvent) {
			processInteractEvent((InteractEvent) event);
		}
	}
	
	protected void processControllerEvent(ControllerEvent e) {
		if(!controllable()) {
			return;
		}
		
		switch(e.getType()) {
		case ControllerEvent.MOVE_RIGHT | ControllerEvent.MOVE_LEFT | ControllerEvent.MOVE_UP | ControllerEvent.MOVE_DOWN:
			controller.movement(e);
			break;
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
	
	private Transform constructTransform() {
		Transform transform = new Transform();
		
		if(hasBody()) {
			transform.setPosition(body.getPosition());
			transform.setSize(body.getSize().x, body.getSize().y);
		}
		
		components.values().stream().forEach(e -> e.applyTransformEffect(transform));
		
		return transform;
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
	
	public <T extends EntityComponent> T getComponent(Class<T> clazz) {
		if(components.containsKey(clazz)) {
			return clazz.cast(components.get(clazz));
		}
		return null;
	}
	
	public boolean hasComponent(Class<? extends EntityComponent> clazz) {
		return components.containsKey(clazz);
	}
	
	public void addComponent(Class<? extends EntityComponent> clazz, EntityComponent component) {
		components.put(clazz, component);
	}
	
	public <T extends EntityComponent> T removeComponent(Class<T> clazz) {
		return clazz.cast(components.remove(clazz));
	}

	@Override
	public String toString() {
		return name;
	}

}
