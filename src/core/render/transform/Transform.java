package core.render.transform;

import org.lwjgl.util.vector.Vector4f;

public class Transform {

	public double x, y;
	public double width, height;
	public float rotation;
	public float scaleX, scaleY;
	public boolean flipX;
	public Vector4f color = new Vector4f();
	public boolean centerRotate;
	public Vector4f textureOffsets;
	
	public Transform() {
		clear();
	}
	
	public Transform(float x, float y, float rotation, float scaleX, float scaleY,
			boolean flipX, Vector4f color, boolean centerRotate, Vector4f textureOffsets) {
		setX(x);
		setY(y);
		setRotation(rotation);
		setScaleX(scaleX);
		setScaleY(scaleY);
		setFlipX(flipX);
		setColor(color != null ? color : new Vector4f(1f,1f,1f,1f));
		setCenterRotate(centerRotate);
		setTextureOffsets(textureOffsets);
	}
	
	public void clear() {
		setX(0);
		setY(0);
		setRotation(0);
		setScaleX(1);
		setScaleY(1);
		setFlipX(false);
		setColor(new Vector4f(1f,1f,1f,1f));
		setCenterRotate(false);
		setTextureOffsets(null);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setHeight(double height) {
		this.height = height;
	}
	
	public void setWidth(double width) {
		this.width = width;
	}
	
	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public float getScaleX() {
		return scaleX;
	}

	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
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
