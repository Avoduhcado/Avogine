package core.setups.utils;

import java.util.ArrayList;

import core.entities.Entity;
import core.event.AvoEvent;

public interface EntityContainer {

	public void drawEntities();
	
	public void addEntity(Entity entity);
	public Entity getEntity(String name);
	public ArrayList<Entity> getEntities();
	public boolean removeEntity(Entity entity);
	
	public void fireEvent(AvoEvent e);
	
}
