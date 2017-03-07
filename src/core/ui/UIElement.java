package core.ui;

import java.awt.geom.Rectangle2D;

import org.lwjgl.util.vector.Vector3f;

import core.Theater;
import core.event.AvoEvent;
import core.render.DrawUtils;
import core.render.textured.UIFrame;
import core.setups.utils.UIContainer;
import core.ui.event.CompleteScriptEvent;
import core.ui.event.CompleteScriptListener;
import core.ui.event.KeybindEvent;
import core.ui.event.KeybindListener;
import core.ui.event.MouseEvent;
import core.ui.event.MouseListener;
import core.ui.event.MouseMotionListener;
import core.ui.event.StateChangeEvent;
import core.ui.event.TimeEvent;
import core.ui.event.TimeListener;
import core.ui.event.WindowEvent;
import core.ui.event.WindowListener;
import core.ui.utils.HorizontalAlign;
import core.ui.utils.UIBounds;
import core.ui.utils.VerticalAlign;
import core.utilities.ValueSupplier;

public abstract class UIElement {

	public static final int NORMAL = 0;
	public static final int KILL_FLAG = 1;
	
	protected final UIBounds uiBounds = new UIBounds();
	
	protected ValueSupplier<Double> x;
	protected ValueSupplier<Double> y;
	protected ValueSupplier<Double> width;
	protected ValueSupplier<Double> height;
	
	protected UIFrame frame;
	
	protected boolean enabled = true;
	
	private UIContainer container;
	protected Panel parentPanel;
	private int state = NORMAL;
	
	/** For managing keyboard mapped menus. 0 = up, 1 = down, 2 = right, 3 = left */
	protected UIElement[] surroundings = new UIElement[4];
		
	protected MouseListener mouseListener;
	protected MouseMotionListener mouseMotionListener;
	protected KeybindListener keybindListener;
	protected TimeListener timeListener;
	protected WindowListener windowListener;
	protected CompleteScriptListener completeScriptListener;
	
	public void draw() {
		if(frame != null) {
			frame.draw(getBoundsAsRect());
		}
		
		if(Theater.debug) {
			DrawUtils.setColor(new Vector3f(1, 0, 0));
			DrawUtils.drawRect(getBounds());
		}
	}
	
	public void drawBounds() {
		DrawUtils.setColor(new Vector3f(1, 0, 0));
		DrawUtils.drawRect(getBounds());
	}

	public UIBounds getBounds() {
		return uiBounds;
	}
	
	/**
	 * @return The exact values stored in <code>UIElement.bounds</code></br>Could be NaN values as well as width/height without borders applied.
	 * Use with caution.
	 */
	public Rectangle2D getBoundsAsRect() {
		return uiBounds.toRectangle();
	}
	
	public void setBounds(double x, double y, double width, double height) {
		setBounds(() -> x, () -> y, () -> width, () -> height);
	}
	
	public void setBounds(ValueSupplier<Double> x, ValueSupplier<Double> y, ValueSupplier<Double> width, ValueSupplier<Double> height) {
		uiBounds.setFrame(x, y, width, height);
	}
	
	public void setPosition(double x, double y) {
		setPosition(() -> x, () -> y);
	}
	
	public void setPosition(ValueSupplier<Double> x, ValueSupplier<Double> y) {
		uiBounds.setX(x);
		uiBounds.setY(y);
	}

	public void setSize(double width, double height) {
		setSize(() -> width, () -> height);
	}
	
	public void setSize(ValueSupplier<Double> width, ValueSupplier<Double> height) {
		uiBounds.setWidth(width);
		uiBounds.setHeight(height);
	}
	
	public void setAlignments(VerticalAlign verticalAlign, HorizontalAlign horizontalAlign) {
		setVerticalAlign(verticalAlign);
		setHorizontalAlign(horizontalAlign);
	}

	public void setVerticalAlign(VerticalAlign alignment) {
		uiBounds.setVerticalAlign(alignment);
	}
	
	public void setHorizontalAlign(HorizontalAlign alignment) {
		uiBounds.setHorizontalAlign(alignment);
	}
	
	public void setFrame(String image) {
		if(image != null) {
			this.frame = new UIFrame(image);
		}
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 *  0 = up, 1 = right, 2 = left, 3 = down 
	 * @return surroundings
	 */
	public UIElement[] getSurroundings() {
		return surroundings;
	}
	
	/**
	 * @param index 0 = up, 1 = right, 2 = left, 3 = down
	 * @param surround Element at <code>index</code>
	 */
	public void setSurrounding(int index, UIElement surround) {
		this.surroundings[index] = surround;
		if(surround.getSurroundings()[Math.abs(index - 3)] == null) {
			surround.setSurrounding(Math.abs(index - 3), this);
		}
	}

	public UIContainer getContainer() {
		return container;
	}
	
	public void setContainer(UIContainer container) {
		this.container = container;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
		
		if(container != null) {
			container.fireEvent(new StateChangeEvent(this, this.state));
		}
	}

	public void removeMouseListener(MouseListener l) {
		if(l == null) {
			return;
		}
		this.mouseListener = null;
	}
	
	public void addMouseListener(MouseListener l) {
		this.mouseListener = l;
	}
	
	public void removeMouseMotionListener(MouseMotionListener l) {
		if(l == null) {
			return;
		}
		this.mouseMotionListener = null;
	}
	
	public void addMouseMotionListener(MouseMotionListener l) {
		this.mouseMotionListener = l;
	}

	public void removeKeybindListener(KeybindListener l) {
		if(l == null) {
			return;
		}
		keybindListener = null;
	}
	
	public void addKeybindListener(KeybindListener l) {
		keybindListener = l;
	}
	
	public void removeTimeListener(TimeListener l) {
		if(l == null) {
			return;
		}
		this.timeListener = null;
	}
	
	public void addTimeListener(TimeListener l) {
		this.timeListener = l;
	}
	
	public void removeCompleteScriptListener(CompleteScriptListener l) {
		if(l == null) {
			return;
		}
		this.completeScriptListener = null;
	}
	
	public void addCompleteScriptListener(CompleteScriptListener l) {
		this.completeScriptListener = l;
	}
	
	public void fireEvent(AvoEvent e) {
		if(e instanceof MouseEvent) {
			processMouseEvent((MouseEvent) e);
		} else if(e instanceof KeybindListener) {
			processKeybindEvent((KeybindEvent) e);
		} else if(e instanceof TimeEvent) {
			processTimeEvent((TimeEvent) e);
		} else if(e instanceof WindowEvent) {
			processWindowEvent((WindowEvent) e);
		} else if(e instanceof CompleteScriptEvent) {
			processCompleteScriptEvent((CompleteScriptEvent) e);
		}
	}
	
	protected void processMouseEvent(MouseEvent e) {
		if(mouseListener != null) {
			if(e.getEvent() == MouseEvent.MOVED) {
				if(getBounds().contains(e.getPosition()) && !getBounds().contains(e.getPrevPosition())) {
					mouseListener.mouseEntered(e);
					return;
				} else if(!getBounds().contains(e.getPosition()) && getBounds().contains(e.getPrevPosition())) {
					mouseListener.mouseExited(e);
					return;
				}
			}
			
			switch(e.getEvent()) {
			case MouseEvent.CLICKED:
				mouseListener.mouseClicked(e);
				e.consume();
				break;
			case MouseEvent.PRESSED:
				mouseListener.mousePressed(e);
				e.consume();
				break;
			case MouseEvent.RELEASED:
				mouseListener.mouseReleased(e);
				e.consume();
				break;
			}
		}
		
		if(mouseMotionListener != null) {
			switch(e.getEvent()) {
			case MouseEvent.MOVED:
				mouseMotionListener.mouseMoved(e);
				break;
			case MouseEvent.DRAGGED:
				mouseMotionListener.mouseDragged(e);
				break;
			}
		}
	}

	protected void processKeybindEvent(KeybindEvent e) {
		if(keybindListener != null) {
			keybindListener.keybindClicked(e);
		}
	}
	
	protected void processTimeEvent(TimeEvent e) {
		if(timeListener != null) {
			timeListener.timeStep(e);
		}
	}
	
	protected void processWindowEvent(WindowEvent e) {
		if(windowListener != null) {
			windowListener.windowResized(e);
		}
	}
	
	protected void processCompleteScriptEvent(CompleteScriptEvent e) {
		if(completeScriptListener != null) {
			completeScriptListener.onCompleteScript(e);
		}
	}

}
