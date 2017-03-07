package core.render.effects;

import org.lwjgl.util.vector.Vector3f;

public abstract class Tween3fEffect extends TweenEffect<Vector3f> {

	public Tween3fEffect(Vector3f start, Vector3f end, float duration, boolean moveTo, Tween tween) {
		super(start, end, duration, moveTo, tween);
	}
	
	@Override
	protected Vector3f handleMoveTo(Vector3f start, Vector3f end) {
		return Vector3f.sub(end, start, null);
	}
	
	@Override
	protected Vector3f calculateTween() {
		Vector3f value = new Vector3f();
		value.x = tween.getTweenedValue(currentTime, startingValue.x, endingValue.x, duration);
		value.y = tween.getTweenedValue(currentTime, startingValue.y, endingValue.y, duration);
		value.z = tween.getTweenedValue(currentTime, startingValue.z, endingValue.z, duration);
		
		return value;
	}

}
