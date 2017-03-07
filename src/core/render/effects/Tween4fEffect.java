package core.render.effects;

import org.lwjgl.util.vector.Vector4f;

public abstract class Tween4fEffect extends TweenEffect<Vector4f> {

	public Tween4fEffect(Vector4f start, Vector4f end, float duration, boolean moveTo, Tween tween) {
		super(start, end, duration, moveTo, tween);
	}

	@Override
	protected Vector4f handleMoveTo(Vector4f start, Vector4f end) {
		return Vector4f.sub(end, start, null);
	}
	
	@Override
	protected Vector4f calculateTween() {
		Vector4f value = new Vector4f();
		value.x = tween.getTweenedValue(currentTime, startingValue.x, endingValue.x, duration);
		value.y = tween.getTweenedValue(currentTime, startingValue.y, endingValue.y, duration);
		value.z = tween.getTweenedValue(currentTime, startingValue.z, endingValue.z, duration);
		value.w = tween.getTweenedValue(currentTime, startingValue.w, endingValue.w, duration);
		
		return value;
	}
	
}
