package core.ui.event;

import core.event.AvoEvent;
import core.ui.UIElement;

enum InteractType {
	KEYPRESS, PLAYER_TOUCH, ENTITY_TOUCH, AUTO, PARALLEL;
}

public abstract class ScriptEvent extends AvoEvent {

	//private Scriptable scriptHost;
	private InteractType type;
	
	public ScriptEvent(UIElement parent) {
		// TODO Auto-generated constructor stub
	}
	
	/*public ScriptEvent(UIElement parent, Scriptable scriptHost, InteractType type) {
		super(parent);
		this.scriptHost = scriptHost;
		this.type = type;
	}*/
	
	public abstract void read();
	
	public void actionPerformed() {
		switch(type) {
		case KEYPRESS:
			break;
		case PLAYER_TOUCH:
			read();
			break;
		default:
			break;
		}
	}

}
