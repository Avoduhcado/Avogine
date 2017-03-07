package core.render.effects;

import org.lwjgl.util.vector.Vector4f;

import core.Camera;

public class TintEffect extends Tween4fEffect {

	public TintEffect(Vector4f end, float duration, boolean moveTo, Tween tween) {
		super(Camera.get().getTint(), end, duration, moveTo, tween);
	}

	@Override
	protected void applyEffect(Vector4f value) {
		Camera.get().setTint(value);
	}

	@Override
	protected void processLoop() {
		startingValue = Camera.get().getTint();
		super.processLoop();
	}
}
