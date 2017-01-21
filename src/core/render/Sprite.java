package core.render;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Sprite {

	private Texture texture;
	private float texXOffset, texYOffset, texWidth, texHeight;
	
	public Sprite(String texture) {
		try {
			this.texture = load(texture);
		} catch (IOException e) {
			loadError();
		}
	}
	
	protected Texture load(String ref) throws IOException {
		if(ref.endsWith(".tga")) {
			return TextureLoader.getTexture("TGA",
					ResourceLoader.getResourceAsStream(System.getProperty("resources") + "/sprites/" + ref + ".tga"));
		}
		return TextureLoader.getTexture("PNG",
				ResourceLoader.getResourceAsStream(System.getProperty("resources") + "/sprites/" + ref + ".png"));
	}
	
	private void loadError() {
		try {
			this.texture = load("error");
		} catch (IOException e2) {
			e2.printStackTrace();
			System.err.println("Resources folder may be missing.");
		}
	}
	
	public void draw(Transform transform) {
		texture.bind();
		updateTextureOffsets(transform);
		
		GL11.glPushMatrix();

		GL11.glTranslated(transform.getPosition().x, transform.getPosition().y, 0f);
		GL11.glScalef(transform.getScale().x, transform.getScale().y, 0f);
		
		if(transform.isFlipX()) {
			GL11.glRotatef(180f, 0, 1, 0);
		}
		
		if(transform.isCenterRotate()) {
			GL11.glTranslatef(texture.getImageWidth() / 2f, texture.getImageHeight() / 2f, 0);
			// Use Math.toDegrees for dope cool spinning effect
			GL11.glRotated(transform.getRotation(), 0, 0, 1);
			GL11.glTranslatef(-texture.getImageWidth() / 2f, -texture.getImageHeight() / 2f, 0);
		} else {
			GL11.glRotated(transform.getRotation(), 0, 0, 1);
		}
		
		GL11.glColor4f(transform.getColor().x, transform.getColor().y, transform.getColor().z, transform.getColor().w);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2f(texXOffset, texYOffset);
			GL11.glVertex2d(0, 0);
			GL11.glTexCoord2f(texWidth, texYOffset);
			GL11.glVertex2d(transform.getSize().x != 0 ? transform.getSize().x : getWidth(), 0);
			GL11.glTexCoord2f(texWidth, texHeight);
			GL11.glVertex2d(transform.getSize().x != 0 ? transform.getSize().x : getWidth(), transform.getSize().y != 0 ? transform.getSize().y : getHeight());
			GL11.glTexCoord2f(texXOffset, texHeight);
			GL11.glVertex2d(0, transform.getSize().y != 0 ? transform.getSize().y : getHeight());
		}
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	private void updateTextureOffsets(Transform transform) {
		Vector4f textureOffsets = transform.getTextureOffsets();
		if(textureOffsets == null) {
			texXOffset = 0;
			texYOffset = 0;
			texWidth = texture.getWidth();
			texHeight = texture.getHeight();
		} else {
			texXOffset = textureOffsets.x;
			texYOffset = textureOffsets.y;
			texWidth = textureOffsets.z;
			texHeight = textureOffsets.w;
		}
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public float getWidth() {
		return texture.getImageWidth();
	}
	
	public float getHeight() {
		return texture.getImageHeight();
	}
	
}
