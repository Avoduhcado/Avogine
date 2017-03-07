package core.event;

public abstract class AvoEvent {
	
	private boolean consumed;
	
	public boolean isConsumed() {
		return consumed;
	}
	
	public void consume() {
		this.consumed = true;
	}
	
}
