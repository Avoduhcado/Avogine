package core.ui.event;

import core.event.AvoEvent;
import core.utilities.keyboard.Keybind;

public class KeybindEvent extends AvoEvent {

	private final Keybind keybind;
	
	public KeybindEvent(Keybind keybind) {
		this.keybind = keybind;
	}

	public Keybind getKeybind() {
		return keybind;
	}
	
}
