package core.entities;

import java.io.Serializable;
import java.util.HashMap;

import core.entities.bodies.Body;
import core.entities.components.EntityComponent;
import core.entities.components.interactions.Interaction;
import core.entities.controllers.Controller;
import core.entities.events.BodyEvent;
import core.entities.events.ControllerEvent;
import core.entities.events.InteractEvent;
import core.entities.renders.Render;
import core.event.AvoEvent;
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
		
		/*switch(event.getInteractType()) {
		case InteractEvent.AUTORUN:
			if(components.containsKey(AutorunInteraction.class)) {
				((Interaction) components.get(AutorunInteraction.class)).interact(event);
			}
			break;
		case InteractEvent.ON_TOUCH:
			if(components.containsKey(TouchInteraction.class)) {
				((Interaction) components.get(TouchInteraction.class)).interact(event);
			}
			break;
		case InteractEvent.ON_ACTIVATE:
		case InteractEvent.INTERRUPT:
			if(components.containsKey(ActivateInteraction.class)) {
				//getRender().lookAt(event.getInteractor());
				((Interaction) components.get(ActivateInteraction.class)).interact(event);
			}
			break;
		}*/
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
	
	public void setBody(Body body) {
		this.addComponent(body);
	}
	
	public boolean hasRender() {
		return hasComponent(Render.class);
	}
	
	public Render getRender() {
		return getComponent(Render.class);
	}
	
	public void setRender(Render render) {
		this.addComponent(render);
	}
	
	public boolean hasController() {
		return hasComponent(Controller.class);
	}
	
	public Controller getController() {
		return getComponent(Controller.class);
	}
	
	public void setController(Controller controller) {
		this.addComponent(controller);
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
	public void addComponent(EntityComponent component) {
		Class<?> clazz = component.getClass();
		while(clazz.getSuperclass() != null && !clazz.getSuperclass().equals(EntityComponent.class)) {
			clazz = clazz.getSuperclass();
		}
		components.put(clazz, component);
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
