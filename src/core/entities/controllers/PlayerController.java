package core.entities.controllers;

import org.lwjgl.util.vector.Vector3f;

import core.entities.Entity;
import core.entities.events.BodyEvent;
import core.entities.events.ControllerEvent;
import core.entities.events.InteractEvent;
import core.ui.event.KeybindEvent;
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
	public void movement(ControllerEvent e) {
		// Reset movement speed
		speedModifier = 1f;
		// Basic "run" actions
		// Modify the entity's movement speed by 150%
		if(Keybind.RUN.held()) {
			speedModifier = 1.5f;
		}

		// Fire a movement event on the player for whichever movement key presses have occurred
		entity.fireEvent(new BodyEvent(BodyEvent.MOVE, (Vector3f) new Vector3f(
				e.getType() == ControllerEvent.MOVE_RIGHT ? 1f : e.getType() == ControllerEvent.MOVE_LEFT ? -1f : 0f,
				e.getType() == ControllerEvent.MOVE_UP ? -1f : e.getType() == ControllerEvent.MOVE_DOWN ? 1f : 0f, 0f)
					.scale(speed * speedModifier)));
	}

	public float getSpeed() {
		return speed;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	@Override
	public void keybindClicked(KeybindEvent e) {
		switch(e.getKeybind()) {
		case RIGHT:
			movement(new ControllerEvent(ControllerEvent.MOVE_RIGHT));
			e.consume();
			break;
		case LEFT:
			movement(new ControllerEvent(ControllerEvent.MOVE_LEFT));
			e.consume();
			break;
		case UP:
			movement(new ControllerEvent(ControllerEvent.MOVE_UP));
			e.consume();
			break;
		case DOWN:
			movement(new ControllerEvent(ControllerEvent.MOVE_DOWN));
			e.consume();
			break;
		case CONFIRM:
			if(e.getKeybind().clicked()) {
				entity.getContainer().fireEvent(new InteractEvent(InteractEvent.INTERACT, entity));
				e.consume();
			}
			break;
		default:
			break;
		}
	}

}
