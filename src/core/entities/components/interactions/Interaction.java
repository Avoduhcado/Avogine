package core.entities.components.interactions;

import core.entities.Entity;
import core.entities.components.EntityComponent;
import core.entities.events.InteractEvent;
import core.scripts.Script;

public abstract class Interaction extends EntityComponent {

	/** The actual container of the events for the interaction */
	protected Script script;
	
	public Interaction(Entity entity, Script script) {
		super(entity);
		this.script = script;
	}
	
	public abstract void interact(InteractEvent e);
	public abstract void interupt(InteractEvent e);
	
	/*public void interact(InteractEvent e) {
		if(!script.isBusyReading()) {
			script.startReading(e.getInteractor());
		} else {
			script.read();
		} 
	}
	
	public void interrupt(InteractEvent e) {
		if(script.isBusyReading()) {
			script.interrupt();
		} 
	}*/
	
}
