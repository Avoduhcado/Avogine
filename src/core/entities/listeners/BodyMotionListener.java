package core.entities.listeners;

import core.entities.events.BodyCollisionEvent;

// TODO
public interface BodyMotionListener {

	public void onApproach(BodyCollisionEvent e);
	public void onTouch(BodyCollisionEvent e);
	
}
