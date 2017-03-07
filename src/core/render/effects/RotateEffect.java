package core.render.effects;

import org.lwjgl.util.vector.Vector3f;

import core.Camera;

public class RotateEffect extends Tween3fEffect {

	public RotateEffect(Vector3f end, float duration, boolean moveTo, Tween tween) {
		super(Camera.get().getRotation(), end, duration, moveTo, tween);
	}

	@Override
	protected void applyEffect(Vector3f value) {
		Camera.get().setRotation(value);
	}
	
	@Override
	protected void processLoop() {
		startingValue = Camera.get().getRotation();
		super.processLoop();
	}
}
