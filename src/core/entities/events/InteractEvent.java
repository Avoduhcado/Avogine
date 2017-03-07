package core.entities.events;

import core.entities.Entity;
import core.event.AvoEvent;

public class InteractEvent extends AvoEvent {

	public static final int INITIATE = 0;
	public static final int INTERACT = 1;
	public static final int INTERRUPT = 2;
	
	protected final int interactType;
	protected Entity interactor;
	
	public InteractEvent(int interactType, Entity interactor) {
		this.interactType = interactType;
		this.interactor = interactor;
	}
	
	public int getInteractType() {
		return interactType;
	}
	
	public Entity getInteractor() {
		return interactor;
	}
	
}
