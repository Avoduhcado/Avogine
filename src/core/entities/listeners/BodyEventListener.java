package core.entities.listeners;

import core.entities.events.BodyEvent;

public interface BodyEventListener {

	public void move(BodyEvent e);
	public void force(BodyEvent e);
	public void impulse(BodyEvent e);
	
}
