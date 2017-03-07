package core.ui.event;

import core.event.AvoEvent;

public class KeyEvent extends AvoEvent {

	private int key;
	private char keyChar;
	private String keyName;
	
	public KeyEvent(int key, char keyChar, String keyName) {
		this.setKey(key);
		this.setKeyChar(keyChar);
		this.setKeyName(keyName);
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public char getKeyChar() {
		return keyChar;
	}

	public void setKeyChar(char keyChar) {
		this.keyChar = keyChar;
	}

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
	
}
