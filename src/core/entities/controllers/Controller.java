package core.entities.controllers;

import core.entities.Entity;
import core.entities.components.EntityComponent;
import core.entities.events.ControllerEvent;
import core.ui.event.KeybindListener;

public abstract class Controller extends EntityComponent implements KeybindListener {
	
	public Controller(Entity entity) {
		super(entity);
	}

	public abstract void movement(ControllerEvent e);
	
}
