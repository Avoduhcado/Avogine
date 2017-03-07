package core.render.effects;

import org.lwjgl.util.vector.Vector3f;

import core.Camera;

public class ScaleEffect extends Tween3fEffect {

	public ScaleEffect(Vector3f value, float duration, boolean addValueNotMove, Tween tween) {
		super(Camera.get().getScale(), value, duration, addValueNotMove, tween);
	}

	@Override
	protected void applyEffect(Vector3f value) {
		Camera.get().setScale(value);
	}

	@Override
	protected void processLoop() {
		startingValue = Camera.get().getScale();
		super.processLoop();
	}
}
