package core.ui;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import core.Camera;
import core.render.textured.UIFrame;
import core.ui.utils.ActionListener;
import core.ui.utils.Align;
import core.ui.utils.MouseEvent;
import core.ui.utils.MouseListener;
import core.ui.utils.UIAction;
import core.ui.utils.UIEvent;
import core.utilities.mouse.MouseInput;

public abstract class UIElement {

	protected Rectangle2D bounds;
	protected UIFrame frame;
	protected String background;
	protected float xBorder;
	protected float yBorder;
	protected Align alignment = Align.RIGHT;
	
	protected boolean enabled = true;
	protected boolean selected;
	protected boolean still;
	protected boolean dead;
	
	/** For managing keyboard mapped menus. 0 = up, 1 = down, 2 = right, 3 = left */
	protected UIElement[] surroundings = new UIElement[4];
	
	protected ArrayList<UIAction> events = new ArrayList<UIAction>();
	private ArrayList<ActionListener> listeners = new ArrayList<ActionListener>();
	
	private MouseListener mouseListener;
		
	// TODO Get rid of this function and sort into multiple separate action listener functions
	public void update() {
		if(enabled) {
			for(UIAction e : events) {
				e.actionPerformed();
			}
		}
	}
	
	public void draw() {
		if(frame != null) {
			frame.draw(bounds);
		}
		
		/*if(background != null) {
			SpriteList.get(background).setStill(still);
			SpriteList.get(background).setFixedSize((float) bounds.getWidth(), (float) bounds.getHeight());
			SpriteList.get(background).draw((float) bounds.getX(), (float) bounds.getY());
		}*/
	}

	public void addEvent(UIAction event) {
		this.events.add(event);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void setStill(boolean still) {
		this.still = still;
	}

	public boolean isDead() {
		return dead;
	}
	
	public void setDead(boolean dead) {
		this.dead = dead;
	}
	
	public Rectangle2D getBounds() {
		return bounds;
	}

	// TODO Introduce border offsets when drawing elements
	public void setBounds(float x, float y, float width, float height) {
		bounds = new Rectangle2D.Double((Float.isNaN(x) ? Camera.get().getDisplayWidth(0.5f) : x) - xBorder,
				(Float.isNaN(y) ? Camera.get().getDisplayHeight(0.5f) : y) - yBorder, width + (xBorder * 2), height + (yBorder * 2));
	}
	
	public void setBounds(double x, double y, double width, double height) {
		bounds = new Rectangle2D.Double((Double.isNaN(x) ? Camera.get().getDisplayWidth(0.5f) : x) - xBorder,
				(Double.isNaN(y) ? Camera.get().getDisplayHeight(0.5f) : y) - yBorder, width + (xBorder * 2), height + (yBorder * 2));
	}
	
	public void setPosition(double x, double y) {
		bounds = new Rectangle2D.Double((Double.isNaN(x) ? Camera.get().getDisplayWidth(0.5f) : x) - xBorder,
				(Double.isNaN(y) ? Camera.get().getDisplayHeight(0.5f) : y) - yBorder, bounds.getWidth(), bounds.getHeight());
	}
	
	public float getXBorder() {
		return xBorder;
	}
	
	public void setXBorder(float xBorder) {
		this.xBorder = xBorder;
		setBounds((float) bounds.getX(), (float) bounds.getY(), (float) bounds.getWidth(), (float) bounds.getHeight());
	}
	
	public float getYBorder() {
		return yBorder;
	}
	
	public void setYBorder(float yBorder) {
		this.yBorder = yBorder;
		setBounds((float) bounds.getX(), (float) bounds.getY(), (float) bounds.getWidth(), (float) bounds.getHeight());
	}
	
	public void setAlign(Align alignment) {
		if(this.alignment != alignment) {
			this.alignment = alignment;
			
			switch(alignment) {
			case RIGHT:
				setBounds((float) bounds.getMaxX(), (float) bounds.getY(), (float) bounds.getWidth(), (float) bounds.getHeight());
				break;
			case LEFT:
				setBounds((float) (bounds.getX() - bounds.getWidth()), (float) bounds.getY(), (float) bounds.getWidth(), (float) bounds.getHeight());
				break;
			case CENTER:
				// TODO Borders
				bounds.setFrameFromCenter(bounds.getX(), bounds.getY(), 
						bounds.getX() - (bounds.getWidth() / 2f), bounds.getY() - (bounds.getHeight() / 2f));
				break;
			default:
				break;
			}
		}
	}
	
	public void setFrame(String image) {
		if(image != null) {
			this.frame = new UIFrame(image);
		}
	}
	
	public void setBackground(String background) {
		this.background = background;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	/**
	 *  0 = up, 1 = right, 2 = left, 3 = down 
	 * @return surroundings
	 */
	public UIElement[] getSurroundings() {
		return surroundings;
	}
	
	/** 0 = up, 1 = right, 2 = left, 3 = down */
	public void setSurrounding(int index, UIElement surround) {
		this.surroundings[index] = surround;
		if(surround.getSurroundings()[Math.abs(index - 3)] == null) {
			surround.setSurrounding(Math.abs(index - 3), this);
		}
	}
	
	public boolean isClicked() {
		//return (bounds.contains(MouseInput.getMouse()) && Input.mouseClicked()) || (selected && Keybinds.CONFIRM.clicked());
		return false;
	}
	
	public boolean isHovering() {
		return bounds.contains(MouseInput.getMouse()) || selected;
	}
	
	public boolean isValueChanged() {
		return false;
	}

	public void removeMouseListener(MouseListener l) {
		if(l == null) {
			return;
		}
		this.mouseListener = l;
	}
	
	public void addMouseListener(MouseListener l) {
		this.mouseListener = l;
	}
	
	public void fireEvent(UIEvent e) {
		if(e instanceof MouseEvent) {
			processMouseEvent((MouseEvent) e);
		}
	}
	
	protected void processMouseEvent(MouseEvent e) {
		if(mouseListener != null) {
			System.out.println("fsdfsdfsdfsdfsdf");
			switch(e.getEvent()) {
			case MouseEvent.CLICKED:
				mouseListener.mouseClicked(e);
				break;
			case MouseEvent.MOVED:
				break;
			}
		}
	}

}
