package core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.opengl.PNGDecoder;
import org.newdawn.slick.util.ResourceLoader;

import core.entities.bodies.Body;
import core.render.DrawUtils;
import core.render.effects.TweenEffect;
import core.setups.GameSetup;
import core.utilities.text.Text;

public class Camera {
	/** Target FPS for application to run at */
	public static final int TARGET_FPS = 60;
	/** Window Icon */
	private final String icon = "AGDG Logo";
	/** Determine whether window should upscale or increase view distance on resize */
	private final boolean upscale = false;
	/** VSync status */
	private boolean vsync;
	
	/** Default Window width and current viewport width */
	private int viewWidth = 1280;
	/** Default Window height and current viewport height */
	private int viewHeight = 720;
	
	/** Current Window width */
	private int displayWidth = viewWidth;
	/** Current Window height */
	private int displayHeight = viewHeight;

	/** Target for the camera to "look at" and always be centered in the screen */
	private Body focus;
	
	private Vector3f translation = new Vector3f();
	private Vector3f scale = new Vector3f(1f, 1f, 1f);
	private Vector3f rotation = new Vector3f();
	
	private Vector4f clearColor = new Vector4f(0f, 0f, 0f, 1f);
	private Vector3f offset = new Vector3f();
	private Vector4f tint = new Vector4f(0f, 0f, 0f, 1f);
	
	private List<TweenEffect> screenEffects = new ArrayList<TweenEffect>();

	/** Screen singleton */
	private static Camera camera;
	
	/** Initialize Screen singleton */
	public static void init() {
		camera = new Camera();
	}
	
	/** Return Screen singleton */
	public static Camera get() {
		return camera;
	}
	
	public Camera() {
		try {
			Display.setDisplayMode(new DisplayMode(viewWidth, viewHeight));
			updateHeader();
			try {
				Display.setIcon(loadIcon(System.getProperty("resources") + "/sprites/" + icon + ".png"));
			} catch (IOException e) {
				System.out.println("Failed to load icon");
			}
			Display.setResizable(true);
			Display.create();
			
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, displayWidth, displayHeight, 0, -1, 1);
			GL11.glViewport(0, 0, displayWidth, displayHeight);
			GL11.glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
		} catch (LWJGLException e) {
			System.err.println("Could not create display.");
		}
	}
	
	public static ByteBuffer[] loadIcon(String ref) throws IOException {
        InputStream fis = ResourceLoader.getResourceAsStream(ref);
        try {
            PNGDecoder decoder = new PNGDecoder(fis);
            ByteBuffer bb = ByteBuffer.allocateDirect(decoder.getWidth()*decoder.getHeight()*4);
            decoder.decode(bb, decoder.getWidth()*4, PNGDecoder.RGBA);
            bb.flip();
            ByteBuffer[] buffer = new ByteBuffer[1];
            buffer[0] = bb;
            return buffer;
        } finally {
            fis.close();
        }
    }
	
	public void update() {
		Display.update();
		Display.sync(TARGET_FPS);
		
		if(resized()) {
			resize();
		}
	}
	
	public void updateHeader() {
		Display.setTitle(Theater.title + "  FPS: " + Theater.fps + " " + Theater.version);
	}
	
	public void draw(GameSetup setup) {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		
		processEffects();
		positionCamera();

		// Draw current game setup
		setup.draw();
		
		// Reload identity to draw UI
		GL11.glLoadIdentity();

		setup.drawUI();
		
		drawScreenTint();
		
		// Draw debug info
		if(Theater.debug) {
			int y = 15;
			Text.drawString("Current Setup: " + Theater.get().getSetup().getClass().getName(), 15, y, Text.DEBUG_TEXT);
			Text.drawString("Avogine v" + Theater.AVOGINE_VERSION, 15, y += 25, Text.DEBUG_TEXT);
			Text.drawString("Position: " + translation.toString(), 15, y += 40, Text.DEBUG_TEXT);
			Text.drawString("Scale: " + scale, 15, y += 30, Text.DEBUG_TEXT);
			Text.drawString("Rotation: " + rotation.toString(), 15, y += 30, Text.DEBUG_TEXT);
			Text.drawString("Mouse: " + Input.getMouseEventX() + " " + Input.getMouseEventY(), 15, y += 30, Text.DEBUG_TEXT);
			Text.drawString("Focus: " + setup.printFocusHierarchy(), 15, y += 30, Text.DEBUG_TEXT);
						
			Text.drawString("Mouse", Input.getMouseEventX(), Input.getMouseEventY());
		}
	}
	
	private void drawScreenTint() {
		DrawUtils.fillScreen(tint.x, tint.y, tint.z, tint.w);
	}
	
	public Matrix4f buildCameraTransform() {
		Matrix4f translate = new Matrix4f();
		Matrix4f rotate = new Matrix4f();
		Matrix4f scalem = new Matrix4f();
		
		Matrix4f.setIdentity(translate);
		translate.m03 = translation.x;
		translate.m13 = translation.y;
		translate.m23 = translation.z;
		
		Matrix4f.setIdentity(rotate);
		/*rotate.m11 = (float) Math.cos(Math.toRadians(rotation.x));
		rotate.m12 = (float) -Math.sin(Math.toRadians(rotation.x));
		rotate.m21 = (float) Math.sin(Math.toRadians(rotation.x));
		rotate.m22 = (float) Math.cos(Math.toRadians(rotation.x));
		
		Matrix4f.setIdentity(scalem);
		scalem.m00 = scale.x;
		scalem.m11 = scale.y;
		scalem.m22 = scale.z;*/
		
		Matrix4f world = new Matrix4f();
		world.translate(new Vector3f(translation.x, translation.y, translation.z));
		//world.rotate((float) Math.toRadians(rotation.x), new Vector3f(1f, 0, 0));
		//world.scale(scale);
		//Matrix4f world = Matrix4f.mul(translate, rotate, null);
		//Matrix4f.mul(world, scalem, world);
		//world.scale(scale);
		//System.out.println(world.toString());
		return world;
	}
	
	private void positionCamera() {
		Matrix4f world = buildCameraTransform();

		GL11.glTranslated(viewWidth * 0.5, viewHeight * 0.5, 0);
		GL11.glScalef(scale.x, scale.y, scale.z);
		GL11.glTranslated(-viewWidth * 0.5, -viewHeight * 0.5, 0);
		
		GL11.glTranslated(viewWidth * 0.5, viewHeight * 0.5, 0);
		GL11.glRotatef(rotation.x, 1, 0, 0);
		GL11.glRotatef(rotation.y, 0, 1, 0);
		GL11.glRotatef(rotation.z, 0, 0, 1);
		GL11.glTranslated(-viewWidth * 0.5, -viewHeight * 0.5, 0);
		
		Vector4f topLeft = new Vector4f();
		if(focus != null) {
			topLeft.set(-focus.getCenter().getX() + (viewWidth / 2), -focus.getCenter().getY() + (viewHeight / 2));
			//topLeft.set(focus.getCenter().x, focus.getCenter().y);
		}
		Matrix4f.transform(world, topLeft, topLeft);
		GL11.glTranslatef(topLeft.x, topLeft.y, 0);
		
		/*// Translation
		GL11.glTranslated(translation.x, translation.y, translation.z);

		// Perform scaling/rotation from the center of the screen to avoid weird offsets
		// Scale
		GL11.glTranslated(viewWidth * 0.5, viewHeight * 0.5, 0);
		GL11.glScalef(scale.x, scale.y, scale.z);
		GL11.glTranslated(-viewWidth * 0.5, -viewHeight * 0.5, 0);
		
		// Rotation
		GL11.glTranslated(viewWidth * 0.5, viewHeight * 0.5, 0);
		// I don't recommend rotating on the y or z axis in 2D space
		GL11.glRotatef(rotation.x, 1f, 0, 0);
		GL11.glRotatef(rotation.y, 0, 1f, 0);
		GL11.glRotatef(rotation.z, 0, 0, 1f);
		GL11.glTranslated(-viewWidth * 0.5, -viewHeight * 0.5, 0);

		// Place the focus target in the center of the screen
		if(focus != null) {
			GL11.glTranslated(-focus.getCenter().getX() + (viewWidth / 2), -focus.getCenter().getY() + (viewHeight / 2), 0);
		}*/
	}

	private void processEffects() {
		screenEffects.stream().forEach(TweenEffect::apply);
		screenEffects.removeIf(TweenEffect::isComplete);
	}
	
	public Body getFocus() {
		return focus;
	}
	
	public void setFocus(Body focus) {
		this.focus = focus;
	}

	public boolean getUpscale() {
		return upscale;
	}
	
	public boolean resized() {
		if(Display.getWidth() != displayWidth || Display.getHeight() != displayHeight) {
			return true;
		}
		
		return false;
	}
	
	public void resize() {
		displayWidth = Display.getWidth();
		displayHeight = Display.getHeight();
		GL11.glViewport(0, 0, displayWidth, displayHeight);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		
		if(!upscale) {
			viewWidth = displayWidth;
			viewHeight = displayHeight;
		}
		GL11.glOrtho(0, viewWidth, viewHeight, 0, -1, 1);
	}

	/**
	 * @return If Display is in fullscreen mode
	 */
	public boolean isFullscreen() {
		return Display.isFullscreen();
	}
	
	/**
	 * Attempt to toggle display's fullscreen mode
	 * @param fullscreen
	 * @return True if toggle succeeded
	 */
	public boolean setFullscreen(boolean fullscreen) {
		try {
			Display.setFullscreen(fullscreen);
			if(fullscreen) {
				Display.setDisplayMode(Display.getDesktopDisplayMode());
			} else if(!fullscreen && Display.isFullscreen()){
				Display.setDisplayMode(new DisplayMode(viewWidth, viewHeight));
			}
		} catch (LWJGLException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean isVSyncEnabled() {
		return vsync;
	}
	
	public void setVSync(boolean vsync) {
		this.vsync = vsync;
		Display.setVSyncEnabled(vsync);
	}
	
	/**
	 * Convert the supplied x value to ignore screen offsets
	 * @param x
	 * @return
	 */
	public double convertXCoordinateToScreenX(double x) {
		return x + (-focus.getCenter().getX() + (viewWidth / 2));
	}
	
	/**
	 * Convert the supplied y value to ignore screen offsets
	 * @param y
	 * @return
	 */
	public double convertYCoordinateToScreenY(double y) {
		return y + (-focus.getCenter().getY() + (viewHeight / 2));
	}
	
	public double getFrameXScale() {
		return (double) displayWidth / (double) viewWidth;
	}
	
	public double getFrameYScale() {
		return (double) displayHeight / (double) viewHeight;
	}
	
	public double getDisplayWidth() {
		return (double) displayWidth / getFrameXScale();
	}
	
	public double getDisplayHeight() {
		return (double) displayHeight / getFrameYScale();
	}
	
	public double getDisplayWidth(float mod) {
		return ((double) displayWidth * mod) / getFrameXScale();
	}
	
	public double getDisplayHeight(float mod) {
		return ((double) displayHeight * mod) / getFrameYScale();
	}
	
	public boolean isActive() {
		return Display.isActive();
	}
	
	public boolean toBeClosed() {
		if(Display.isCloseRequested()) {
			return true;
		}
		
		return false;
	}
	
	public void close() {
		Display.destroy();
	}
	
	public Vector3f getTranslation() {
		return translation;
	}
	
	public void setTranslation(Vector3f translation) {
		this.translation = translation;
	}

	public Vector3f getScale() {
		return scale;
	}
	
	public void setScale(Vector3f value) {
		this.scale = value;
	}
	
	public void setScale(float scale) {
		this.scale.set(scale, scale, scale);
	}

	public Vector3f getRotation() {
		return rotation;
	}
	
	public void setRotation(Vector3f rotation) {
		rotation.set(rotation.x >= 360 ? rotation.x - 360 : (rotation.x < 0 ? rotation.x + 360 : rotation.x),
				rotation.y >= 360 ? rotation.y - 360 : (rotation.y < 0 ? rotation.y + 360 : rotation.y),
				rotation.z >= 360 ? rotation.z - 360 : (rotation.z < 0 ? rotation.z + 360 : rotation.z));
		this.rotation = rotation;
	}

	public Vector4f getClear() {
		return clearColor;
	}
	
	public void setClear(Vector4f clear) {
		this.clearColor = clear;
		GL11.glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
	}
	
	public Vector4f getTint() {
		return tint;
	}
	
	public void setTint(Vector4f tint) {
		this.tint = tint;
	}
	
	public void addScreenEffect(TweenEffect effect) {
		screenEffects.add(effect);
	}

	public void cancelAllEffects() {
		screenEffects.clear();
	}
	
	public void cancelEffect(TweenEffect effect) {
		screenEffects.remove(effect);
	}
}
