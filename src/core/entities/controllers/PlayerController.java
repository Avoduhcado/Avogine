package core.entities.controllers;

import org.lwjgl.util.vector.Vector3f;

import core.entities.Entity;
import core.entities.events.MoveEvent;
import core.utilities.keyboard.Keybind;

public class PlayerController extends Controller {
	
	protected float speed;
	protected float speedModifier = 1f;
	
	public PlayerController(Entity player, float speed) {
		super(player);
		this.speed = speed;
	}
	
	public PlayerController(Entity player) {
		this(player, 1f);
	}
	
	@Override
	public void control() {
		controlMovement();
	}
	
	protected void controlMovement() {
		// Reset movement speed
		speedModifier = 1f;
		// Basic "run" actions
		// Modify the entity's movement speed by 150%
		if(Keybind.RUN.held()) {
			speedModifier = 1.5f;
		}
		
		// Fire a movement event on the player if any movement key presses have occured
		if(Keybind.movement()) {
			entity.fireEvent(new MoveEvent((Vector3f) new Vector3f(Keybind.RIGHT.press() ? 1f : Keybind.LEFT.press() ? -1f : 0f,
						Keybind.UP.press() ? -1f : Keybind.DOWN.press() ? 1f : 0f, 0f).scale(speed * speedModifier)));
		}
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}

}
