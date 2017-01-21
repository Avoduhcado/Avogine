package core.entities.events;

public class ControllerEvent extends EntityEvent {

	public static final int MOVE_RIGHT = 1;
	public static final int MOVE_LEFT = 2;
	public static final int MOVE_UP = 3;
	public static final int MOVE_DOWN = 4;
	
	protected final int type;
	
	public ControllerEvent(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
}
