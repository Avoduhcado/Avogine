package core.render;

import java.awt.geom.Rectangle2D;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.Body;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import core.Camera;
import core.generation.box2d.WorldGeneratorBox2D;
import core.ui.utils.UIBounds;

public class DrawUtils {
	
	/** Color of object to be drawn */
	private static Vector3f color = new Vector3f(0f, 0f, 0f);
	private static float lineWidth = 1f;

	/**
	 * Set new drawing color.
	 * 
	 * @param color to draw with
	 */
	public static void setColor(Vector3f color) {
		DrawUtils.color = color;
	}
	
	/**
	 * Draw a line to the screen.
	 * 
	 * @param x1 of line
	 * @param y1 of line
	 * @param x2 of line
	 * @param y2 of line
	 */
	public static void drawLine(double x1, double y1, double x2, double y2) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glColor3f(color.x, color.y, color.z);
		GL11.glLineWidth(lineWidth);
		
		GL11.glBegin(GL11.GL_LINE_LOOP);
		{
			GL11.glVertex2d(x1, y1);
			GL11.glVertex2d(x2, y2);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// Reset color
		color.set(0f, 0f, 0f);
	}
	
	/**
	 * Draw a rectangle to the screen at the specified coordinates.
	 * 
	 * @param x of rectangle
	 * @param y of rectangle
	 * @param rect to be drawn
	 */
	public static void drawRectHere(double x, double y, Rectangle2D rect) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glColor3f(color.x, color.y, color.z);
		GL11.glLineWidth(lineWidth);
		
		GL11.glBegin(GL11.GL_LINE_LOOP);
		{
			GL11.glVertex2d(x, y);
			GL11.glVertex2d(x + rect.getWidth(), y);
			GL11.glVertex2d(x + rect.getWidth(), y + rect.getHeight());
			GL11.glVertex2d(x, y + rect.getHeight());
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// Reset color
		color.set(0f, 0f, 0f);
	}
	
	/**
	 * Draw a rectangle to the screen.
	 * 
	 * @param x of rectangle
	 * @param y of rectangle
	 * @param rect to be drawn
	 */
	public static void drawRect(Rectangle2D rect) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glColor3f(color.x, color.y, color.z);
		GL11.glLineWidth(lineWidth);
		
		GL11.glBegin(GL11.GL_LINE_LOOP);
		{
			GL11.glVertex2d(rect.getX(), rect.getY());
			GL11.glVertex2d(rect.getX() + rect.getWidth(), rect.getY());
			GL11.glVertex2d(rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight());
			GL11.glVertex2d(rect.getX(), rect.getY() + rect.getHeight());
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// Reset color
		color.set(0f, 0f, 0f);
	}

	public static void drawRect(UIBounds bounds) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glColor3f(color.x, color.y, color.z);
		GL11.glLineWidth(lineWidth);
		
		GL11.glBegin(GL11.GL_LINE_LOOP);
		{
			GL11.glVertex2d(bounds.getX(), bounds.getY());
			GL11.glVertex2d(bounds.getMaxX(), bounds.getY());
			GL11.glVertex2d(bounds.getMaxX(), bounds.getMaxY());
			GL11.glVertex2d(bounds.getX(), bounds.getMaxY());
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// Reset color
		color.set(0f, 0f, 0f);
	}
	
	/**
	 * Draws a single color over a specific rectangle.
	 * 
	 * @param r Red value of color
	 * @param g Green value of color
	 * @param b Blue value of color
	 * @param a Transparency
	 * @param rect The rectangle to be drawn
	 */
	public static void fillRect(float r, float g, float b, float a, Rectangle2D rect) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glColor4f(r, g, b, a);
		
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2d(rect.getX(), rect.getY());
			GL11.glVertex2d(rect.getMaxX(), rect.getY());
			GL11.glVertex2d(rect.getMaxX(), rect.getMaxY());
			GL11.glVertex2d(rect.getX(), rect.getMaxY());
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	/**
	 * Draws a single color over the entire screen.
	 * 
	 * @param r Red value of color
	 * @param g Green value of color
	 * @param b Blue value of color
	 * @param a Transparency
	 */
	public static void fillScreen(float r, float g, float b, float a) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glColor4f(r, g, b, a);
		
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2d(0, 0);
			GL11.glVertex2d(Camera.get().getDisplayWidth(1f), 0);
			GL11.glVertex2d(Camera.get().getDisplayWidth(1f), Camera.get().getDisplayHeight(1f));
			GL11.glVertex2d(0, Camera.get().getDisplayHeight(1f));
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public static void fillScreen(Vector4f glassColor) {
		fillScreen(glassColor.x, glassColor.y, glassColor.z, glassColor.w);
	}

	public static void drawBox2DShape(Body body, Shape shape, float scale) {
		switch(shape.getType()) {
		case POLYGON:
			drawBox2DPoly(body, (PolygonShape) shape, scale);
			break;
		case CIRCLE:
			drawBox2DCircle(body, (CircleShape) shape, scale);
			break;
		case EDGE:
			drawBox2DEdge(body, (EdgeShape) shape, scale);
			break;
		case CHAIN:
			break;
		}
	}
	
	public static void drawBox2DShape(Body body, Shape shape) {
		drawBox2DShape(body, shape, WorldGeneratorBox2D.SCALE_FACTOR);
	}
	
	public static void drawBox2DPoly(Body body, PolygonShape poly, float scale) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glTranslated(body.getPosition().x * scale, body.getPosition().y * scale, 0);
		GL11.glRotated(Math.toDegrees(body.getAngle()), 0, 0, 1f);
		
		GL11.glColor4f(color.x, color.y, color.z, 0.5f);
		GL11.glBegin(GL11.GL_QUADS);
		{
			for(int n = 0; n<poly.getVertexCount(); n++) {
				GL11.glVertex2f(poly.getVertex(n).x * scale, poly.getVertex(n).y * scale);
			}
		}
		GL11.glEnd();
		
		GL11.glColor3f(color.x, color.y, color.z);
		GL11.glBegin(GL11.GL_LINE_LOOP);
		{
			for(int n = 0; n<poly.getVertexCount(); n++) {
				GL11.glVertex2f(poly.getVertex(n).x * scale, poly.getVertex(n).y * scale);
			}
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// Reset color
		color.set(0f, 0f, 0f);
	}
	
	public static void drawBox2DEdge(Body body, EdgeShape edge, float scale) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glTranslated(body.getPosition().x * scale, body.getPosition().y * scale, 0);
		GL11.glColor3f(color.x, color.y, color.z);
		
		GL11.glBegin(GL11.GL_LINE_LOOP);
		{
			GL11.glVertex2f(edge.m_vertex1.x * scale, edge.m_vertex1.y * scale);
			GL11.glVertex2f(edge.m_vertex2.x * scale, edge.m_vertex2.y * scale);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// Reset color
		color.set(0f, 0f, 0f);
	}
	
	public static void drawBox2DCircle(Body body, CircleShape circle, float scale) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glTranslated(body.getPosition().x * scale,	body.getPosition().y * scale, 0);
		GL11.glColor4f(color.x, color.y, color.z, 0.5f);
		
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		{
			GL11.glVertex2f(0, 0);
			for(int i = 0; i<=360; i += 10) {
				GL11.glVertex2f((float) (Math.sin(Math.toRadians(i)) * (circle.m_radius * scale)),
						(float) Math.cos(Math.toRadians(i)) * (circle.m_radius * scale));
			}
		}
		GL11.glEnd();
		
		GL11.glColor3f(color.x, color.y, color.z);
		GL11.glBegin(GL11.GL_LINE_LOOP);
		{
			for(int i = 0; i<=360; i += 10) {
				GL11.glVertex2f((float) (Math.sin(Math.toRadians(i)) * (circle.m_radius * scale)),
						(float) Math.cos(Math.toRadians(i)) * (circle.m_radius * scale));
			}
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// Reset color
		color.set(0f, 0f, 0f);
	}
}
