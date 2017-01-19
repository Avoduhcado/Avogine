package core.render.textured;

import java.awt.geom.Rectangle2D;

import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.opengl.Texture;

import core.render.SpriteList;
import core.render.Transform;

public class UIFrame {
	
	/** TODO
	 * Not very elegant, but potentially better than just basing the size off the actual image?
	 * Include some option somewhere to set this value?
	 */
	private static final double frameSize = 60;

	private String frame;
	
	private float opacity = 0.8f;
	
	private Transform transform;
	
	public UIFrame(String ref) {
		this.frame = ref;
		
		this.transform = new Transform();
	}

	private void setTransform(int row, int col, Rectangle2D box) {
		Texture texture = SpriteList.get(frame).getTexture();
		transform.clear();
		transform.setTextureOffsets(new Vector4f());
		transform.setColor(new Vector4f(1f, 1f, 1f, opacity));
		
		switch(row) {
		case 0:
			transform.getPosition().setY((float) (box.getY() - (frameSize / 3)));
			transform.getSize().setY((float) (frameSize / 3));
			transform.getTextureOffsets().y = 0;
			transform.getTextureOffsets().w = (texture.getHeight() / 3f);
			break;
		case 1:
			transform.getPosition().setY((float) box.getY());
			transform.getSize().setY((float) box.getHeight());
			transform.getTextureOffsets().y = (texture.getHeight() / 3f);
			transform.getTextureOffsets().w = (texture.getHeight() * 0.667f);
			break;
		case 2:
			transform.getPosition().setY((float) box.getMaxY());
			transform.getSize().setY((float) (frameSize / 3));
			transform.getTextureOffsets().y = (texture.getHeight() * 0.667f);
			transform.getTextureOffsets().w = (texture.getHeight());
			break;
		}
		
		switch(col) {
		case 0:
			transform.getPosition().setX((float) (box.getX() - (frameSize / 3)));
			transform.getSize().setX((float) (frameSize / 3));
			transform.getTextureOffsets().x = 0;
			transform.getTextureOffsets().z = (texture.getWidth() / 3f);
			break;
		case 1:
			transform.getPosition().setX((float) box.getX());
			transform.getSize().setX((float) box.getWidth());
			transform.getTextureOffsets().x = (texture.getWidth() / 3f);
			transform.getTextureOffsets().z = (texture.getWidth() * 0.667f);
			break;
		case 2:
			transform.getPosition().setX((float) box.getMaxX());
			transform.getSize().setX((float) (frameSize / 3));
			transform.getTextureOffsets().x = (texture.getWidth() * 0.667f);
			transform.getTextureOffsets().z = (texture.getWidth());
			break;
		}
	}
	
	/**
	 * Draw frame around supplied <code>Rectangle2D</code> box
	 * @param box
	 */
	public void draw(Rectangle2D box) {
		for(int row = 0; row < 3; row++) {
			for(int col = 0; col < 3; col++) {
				setTransform(row, col, box);
								
				SpriteList.get(frame).draw(transform);
			}
		}
	}
	
	public float getOpacity() {
		return opacity;
	}
	
	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

}
