package core.ui.event;

import java.awt.geom.Point2D;

import core.event.AvoEvent;

public class MouseEvent extends AvoEvent {

	public static final int CLICKED = 1;
	public static final int PRESSED = 2;
	public static final int RELEASED = 3;
	public static final int ENTERED = 4;
	public static final int EXITED = 5;
	
	public static final int MOVED = 6;
	public static final int DRAGGED = 7;

	private int event;
	private float x, y;
	private float dx, dy;

	/**
	 * 
	 * @param event
	 * @param x Autoscaled to match camera scaling
	 * @param y Autoscaled to match camera scaling
	 */
	public MouseEvent(int event, float x, float y) {
		this.event = event;
		this.x = x;// / ((float) Display.getWidth() / (float) Camera.get().WIDTH);
		this.y = y;// / ((float) Display.getHeight() / (float) Camera.get().HEIGHT);
	}
	
	public int getEvent() {
		return event;
	}

	public void setEvent(int event) {
		this.event = event;
	}
	
	public Point2D getPosition() {
		return new Point2D.Float(x, y);
	}
	
	public Point2D getPrevPosition() {
		return new Point2D.Float(x - dx, y - dy);
	}
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getDx() {
		return dx;
	}

	public void setDx(float dx) {
		this.dx = dx;
	}

	public float getDy() {
		return dy;
	}

	public void setDy(float dy) {
		this.dy = dy;
	}
	
}
