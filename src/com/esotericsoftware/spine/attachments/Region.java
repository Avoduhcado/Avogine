package com.esotericsoftware.spine.attachments;

import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.lwjgl.util.vector.Vector4f;

import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.MathUtils;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.Slot;

@Deprecated
public class Region extends Attachment {

	private String path;
	private float x, y;
	private float scaleX = 1f, scaleY = 1f;
	private float rotation;
	private float width, height;
	private float offsetX, offsetY;
	private float worldX, worldY;
	private Vector4f color = new Vector4f(1, 1, 1, 1);
	
	private Object userData;

	public Region(String name) {
		super(name);
	}

	public void updateOffset () {
		float width = getWidth();
		float height = getHeight();
		float localX2 = width / 2;
		float localY2 = height / 2;
		float localX = -localX2;
		//float localY = -localY2;
		float scaleX = getScaleX();
		float scaleY = getScaleY();
		localX *= scaleX;
		//localY *= scaleY;
		localX2 *= scaleX;
		localY2 *= scaleY;
		float rotation = getRotation();
		float cos = MathUtils.cosDeg(rotation);
		float sin = MathUtils.sinDeg(rotation);
		float x = getX();
		float y = getY();
		float localXCos = localX * cos + x;
		float localXSin = localX * sin;
		/*float localYCos = localY * cos + y;
		float localYSin = localY * sin;
		float localX2Cos = localX2 * cos + x;
		float localX2Sin = localX2 * sin;*/
		float localY2Cos = localY2 * cos + y;
		float localY2Sin = localY2 * sin;
		//float[] offset = this.offset;
		setOffsetX(localXCos - localY2Sin);
		setOffsetY(localY2Cos + localXSin);
	}
	
	public void updateWorldVertices (Slot slot) {
		Skeleton skeleton = slot.getSkeleton();
				
		Bone bone = slot.getBone();
		float x = skeleton.getX() + bone.getWorldX();
		float y = skeleton.getY() + bone.getWorldY();
		float m00 = slot.getBone().getM00();
		float m01 = slot.getBone().getM01();
		float m10 = slot.getBone().getM10();
		float m11 = slot.getBone().getM11();

		worldX = (offsetX * m00) + (offsetY * m01) + x; // ul
		worldY = (offsetX * m10) + (offsetY * m11) + y;
	}
	
	public float[] getVertices(Slot slot) {
		Polygon poly = getRotatedBox(slot, null);
		float[] vertices = new float[8];
		for(int n = 0; n<poly.npoints; n += 2) {
			vertices[n] = poly.xpoints[n];
			vertices[n + 1] = poly.ypoints[n];
		}
		
		return vertices;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
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

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(float offsetX) {
		this.offsetX = offsetX;
	}

	public float getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(float offsetY) {
		this.offsetY = offsetY;
	}

	public float getWorldX() {
		return worldX;
	}

	public void setWorldX(float worldX) {
		this.worldX = worldX;
	}

	public float getWorldY() {
		return worldY;
	}

	public void setWorldY(float worldY) {
		this.worldY = worldY;
	}

	public Vector4f getColor() {
		return color;
	}

	public void setColor(Vector4f color) {
		this.color = color;
	}
	
	public Rectangle2D getBox() {
		return new Rectangle2D.Double(0, 0, width, height);
	}
	
	public Rectangle2D getSubBox(Rectangle2D sub, boolean flipped) {
		if(flipped) {
			return new Rectangle2D.Double(width * (1 - (sub.getX() + sub.getWidth())), height * sub.getY(),
					width * sub.getWidth(), height * sub.getHeight());
		}
		
		return new Rectangle2D.Double(width * sub.getX(), height * sub.getY(), width * sub.getWidth(), height * sub.getHeight());
	}
	
	public Polygon getRotatedBox(Slot slot, Rectangle2D sub) {
		Skeleton skeleton = slot.getSkeleton();
		Bone bone = slot.getBone();
		
		AffineTransform at;
		if(skeleton.getFlipX()) {
			at = AffineTransform.getRotateInstance(Math.toRadians(bone.getWorldRotation() + rotation), 0, 0);
			at.translate(-width, 0);
		} else {
			at = AffineTransform.getRotateInstance(Math.toRadians(-bone.getWorldRotation() - rotation), 0, 0);
		}

		ArrayList<double[]> polyPoints = new ArrayList<double[]>();
		double[] coords = new double[3];

		//for(PathIterator pi = at.createTransformedShape(sub == null ? getBox() : getSubBox(sub)).getPathIterator(null);
		for(PathIterator pi = (sub == null ? getBox().getPathIterator(at) :
				getSubBox(sub, skeleton.getFlipX()).getPathIterator(at)); !pi.isDone(); pi.next()) {
			// The type will be SEG_LINETO, SEG_MOVETO, or SEG_CLOSE
			// Because the Area is composed of straight lines
			int type = pi.currentSegment(coords);
			// We record a double array of {segment type, x coord, y coord}
			double[] pathIteratorCoords = {type, coords[0], coords[1]};
			polyPoints.add(pathIteratorCoords);
		}

		Polygon poly = new Polygon();
		for(double[] c : polyPoints) {
			poly.addPoint((int) c[1], (int) c[2]);
		}

		return poly;
	}
	
	public AffineTransform getTransform(Slot slot, Rectangle2D sub) {
		Skeleton skeleton = slot.getSkeleton();
		Bone bone = slot.getBone();
		
		AffineTransform at;
		if(skeleton.getFlipX()) {
			at = AffineTransform.getRotateInstance(Math.toRadians(bone.getWorldRotation() + rotation), 0, 0);
			at.translate(-width, 0);
			if(sub != null) {
				sub = new Rectangle2D.Double(sub.getX() - (1 - sub.getWidth()), sub.getY(), sub.getWidth(), sub.getHeight());
			}
		} else {
			at = AffineTransform.getRotateInstance(Math.toRadians(-bone.getWorldRotation() - rotation), 0, 0);
		}
		
		return at;
	}

	public Object getUserData() {
		return userData;
	}

	public void setUserData(Object userData) {
		this.userData = userData;
	}
	
}
