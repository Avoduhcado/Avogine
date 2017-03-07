package core.render.effects;

import org.lwjgl.util.vector.Vector3f;

import core.Camera;

public class TranslateEffect extends Tween3fEffect {

	public TranslateEffect(Vector3f end, float duration, boolean moveTo, Tween tween) {
		super(Camera.get().getTranslation(), end, duration, moveTo, tween);
	}

	@Override
	protected void applyEffect(Vector3f value) {
		Camera.get().setTranslation(value);
	}

	@Override
	protected void processLoop() {
		startingValue = Camera.get().getTranslation();
		super.processLoop();
	}
}
