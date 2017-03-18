package core.utilities;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

public class MathUtils {

	/**
	 * Clamp a value between two ints
	 * @param value
	 * @param min
	 * @param max
	 * @return
	 */
	public static int clamp(int value, int min, int max) {
		if(value < min)
			return min;
		if(value > max)
			return max;
		return value;
	}

	/**
	 * Clamp a value between two floats
	 * @param value
	 * @param min
	 * @param max
	 * @return
	 */
	public static float clamp(float value, float min, float max) {
		if(value < min)
			return min;
		if(value > max)
			return max;
		return value;
	}
	
	/**
	 * Clamp a value between two doubles
	 * @param value
	 * @param min
	 * @param max
	 * @return
	 */
	public static double clamp(double value, double min, double max) {
		if(value < min)
			return min;
		if(value > max)
			return max;
		return value;
	}
	
	/**
	 * Change over time at linear rate
	 */
	public static float linearTween(float time, float begin, float change, float duration) {
		return change * time / duration + begin;
	}
	
	/**
	 * Start slow then move quickly
	 */
	public static float easeIn(float t, float b, float c, float d) {
		return c*(t/=d)*t + b;
	}
	
	/**
	 * Start fast then slow down
	 */
	public static float easeOut(float t, float b, float c, float d) {
		return -c *(t/=d)*(t-2) + b;
	}
	
	public static float easeInOut(float t, float b, float c, float d) {
		if ((t/=d/2) < 1) return c/2*t*t + b;
		return -c/2 * ((--t)*(t-2) - 1) + b;
	}

	public static double lerp(double startValue, double endValue, double time) {
		 return (1 - time) * startValue + time * endValue;
	}

	public static float lerp(float startValue, float endValue, float time) {
		return (1 - time) * startValue + time * endValue;
	}

	public static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}
	
	public static Vector2f limitVector(Vector2f srcVector, float limit) {
		double lengthSquared = srcVector.x * srcVector.x + srcVector.y * srcVector.y;

		Vector2f limVector = new Vector2f(srcVector);
		if((lengthSquared > limit * limit) && (lengthSquared > 0)) {
			double ratio = (limit / Math.sqrt(lengthSquared));
			limVector.x *= ratio;
			limVector.y *= ratio;
		}
		
		return limVector;
	}
	
	public static Vector2f rotateVector(Vector2f vector, double degrees){
		return new Vector2f((float) ((vector.x * Math.cos(degrees)) - (vector.y * Math.sin(degrees))),
				(float) ((vector.x * Math.sin(degrees)) + (vector.y * Math.cos(degrees))));
	}

	public static Vector4f valueOfHex(String hex) {
		int r = Integer.valueOf(hex.substring(0, 2), 16);
		int g = Integer.valueOf(hex.substring(2, 4), 16);
		int b = Integer.valueOf(hex.substring(4, 6), 16);
		int a = hex.length() != 8 ? 255 : Integer.valueOf(hex.substring(6, 8), 16);
		return new Vector4f(r / 255f, g / 255f, b / 255f, a / 255f);
	}

}
