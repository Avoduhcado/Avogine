package core.scripts;

public class InterpretedEvent {
	private InterpretedAction action;
	private InterpretedAction closeAction;
	private InterpretedAction autoCompleteAction;

	public InterpretedEvent(InterpretedAction action, InterpretedAction closeAction, InterpretedAction autoCompleteAction) {
		this.action = action;
		this.closeAction = closeAction;
		this.autoCompleteAction = autoCompleteAction;
	}
	
	public InterpretedEvent(InterpretedAction action, InterpretedAction closeAction) {
		this(action, closeAction, null);
	}
	
	public InterpretedEvent(InterpretedAction action) {
		this(action, null, null);
	}
	
	public InterpretedAction getAction() {
		return action;
	}

	public void setAction(InterpretedAction action) {
		this.action = action;
	}

	public InterpretedAction getCloseAction() {
		return closeAction;
	}

	public void setCloseAction(InterpretedAction closeAction) {
		this.closeAction = closeAction;
	}

	public InterpretedAction getAutoCompleteAction() {
		return autoCompleteAction;
	}

	public void setAutoCompleteAction(InterpretedAction autoCompleteAction) {
		this.autoCompleteAction = autoCompleteAction;
	}
}
