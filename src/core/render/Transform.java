package core.render;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Transform {

	private Vector3f position = new Vector3f();
	private Vector2f size = new Vector2f();
	private float rotation;
	private Vector3f scale = new Vector3f(1f, 1f, 1f);
	private boolean flipX;
	private Vector4f color = new Vector4f(1f,1f,1f,1f);
	private boolean centerRotate;
	private Vector4f textureOffsets;
	
	public Transform() {
		
	}
	
	public void clear() {
		setPosition(new Vector3f());
		setSize(new Vector2f());
		setRotation(0);
		setScale(new Vector3f(1f, 1f, 1f));
		setFlipX(false);
		setColor(new Vector4f(1f,1f,1f,1f));
		setCenterRotate(false);
		setTextureOffsets(null);
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public void setPosition(double x, double y) {
		this.position.set((float) x, (float) y);
	}
	
	public Vector2f getSize() {
		return size;
	}

	public void setSize(Vector2f size) {
		this.size = size;
	}
	
	public void setSize(double width, double height) {
		this.size.set((float) width, (float) height);
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public Vector3f getScale() {
		return scale;
	}

	public void setScale(Vector3f scale) {
		this.scale = scale;
	}

	public void setScale(double scaleX, double scaleY) {
		this.scale.set((float) scaleX, (float) scaleY);
	}
	
	public boolean isFlipX() {
		return flipX;
	}

	public void setFlipX(boolean flipX) {
		this.flipX = flipX;
	}

	public Vector4f getColor() {
		return color;
	}

	public void setColor(Vector4f color) {
		this.color = (color != null ? color : new Vector4f(1f,1f,1f,1f));
	}
	
	public boolean isCenterRotate() {
		return centerRotate;
	}

	public void setCenterRotate(boolean centerRotate) {
		this.centerRotate = centerRotate;
	}

	public Vector4f getTextureOffsets() {
		return textureOffsets;
	}

	public void setTextureOffsets(Vector4f textureOffsets) {
		this.textureOffsets = textureOffsets;
	}

}
