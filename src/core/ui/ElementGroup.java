package core.ui;

import java.util.ArrayList;
import java.util.Collection;

import org.lwjgl.util.vector.Vector2f;

import core.event.AvoEvent;
import core.setups.utils.UIContainer;
import core.ui.event.ActionEvent;
import core.ui.event.KeyEvent;
import core.ui.event.KeybindEvent;
import core.ui.event.KeybindListener;
import core.ui.event.MouseEvent;
import core.ui.event.MouseListener;
import core.ui.event.MouseMotionListener;
import core.ui.event.StateChangeEvent;
import core.ui.event.StateChangeListener;
import core.ui.event.TimeEvent;
import core.ui.event.TimeListener;
import core.ui.utils.Accessible;
import core.ui.utils.UIBounds;
import core.utilities.MathUtils;
import core.utilities.keyboard.Keybind;

public class ElementGroup extends UIElement implements UIContainer, Accessible {
	
	protected ArrayList<UIElement> uiElements = new ArrayList<UIElement>();
	protected UIElement focus;
	
	protected SelectionPointer pointer;
	protected KeybindListener keybindListener;
	
	private final StateChangeListener stateChangeListener = new StateChangeListener() {
		public void changeState(StateChangeEvent e) {
			switch(e.getState()) {
			case UIElement.KILL_FLAG:
				removeElement(e.getElement());
				e.consume();
				break;
			default:
				break;
			}
		}
	};
	
	public ElementGroup() {
		addMouseListener(new DefaultMouseAdapter());
		addMouseMotionListener(new DefaultMouseMotionAdapter());
	}

	public void draw() {
		super.draw();
		
		for(UIElement e : uiElements) {
			e.draw();
		}
		
		if(hasFocus()) {
			if(pointer != null) {
				pointer.draw();
			} else {
				//getFocus().drawBounds();
			}	
		}
	}
	
	public void setSelectionPointer(String pointerName) {
		this.pointer = new SelectionPointer(pointerName);
		
		if(getFocus() != null) {
			pointer.setPositionAt(getFocus());
		}
	}
	
	public void setKeyboardNavigable(boolean enabled, UIElement startIndex) {
		if(enabled) {
			setFocus(startIndex);
			addKeybindListener(new DefaultKeybindAdapter());
		} else {
			setFocus(null);
			removeKeybindListener(keybindListener);
		}
	}
	
	protected void changeSelection(int direction) {
		if(getFocus().getSurroundings()[direction] != null && getFocus().getSurroundings()[direction] instanceof Accessible) {
			setFocus(getFocus().getSurroundings()[direction]);
		}
	}
	
	// TODO just baed
	public void setBounds() {
		if(isEmpty()) {
			setBounds(0, 0, 1, 1);
		} else if(size() == 1) {
			UIBounds initBounds = getElement(0).getBounds();
			setBounds(initBounds.getXAsSupplier(), initBounds.getYAsSupplier(), initBounds.getWidthSupplier(), initBounds.getHeightSupplier());
		} else {
			for(UIElement e : uiElements) {
				UIBounds.merge(getBounds(), e.getBounds(), uiBounds);
			}
		}
	}

	@Override
	public void access(boolean accessed) {
		/*if(accessed) {
			if(getFocus() != null) {
				return;
			}
			for(UIElement e : uiElements) {
				if(e instanceof Accessible && !e.isEnabled()) {
					setFocus(e);
					break;
				}
			}
		} else {
			setFocus(null);
		}*/
	}
	
	@Override
	public boolean hasFocus() {
		return focus != null;
	}

	@Override
	public ArrayList<UIElement> getUI() {
		return uiElements;
	}

	@Override
	public UIElement getElement(int index) {
		return uiElements.get(index);
	}

	@Override
	public boolean removeElement(UIElement element) {
		boolean removed = uiElements.remove(element);
		if(removed) {
			setBounds();
		}
		
		return removed;
	}

	@Override
	public void addUI(UIElement element) {
		uiElements.add(element);
		setBounds();
	}
	
	@Override
	public void addUI(UIElement element, int index) {
		uiElements.set(index, element);
		setBounds();
	}

	public void addAll(Collection<UIElement> elements) {
		this.uiElements.addAll(elements);
		setBounds();
	}

	@Override
	public UIElement getFocus() {
		return focus;
	}

	@Override
	public void setFocus(UIElement focus) {
		if(!(focus instanceof Accessible)) {
			return;
		}
		// Unselect previous focus
		if(hasFocus()) {
			((Accessible) getFocus()).access(false);
		}
		this.focus = focus;
		((Accessible) getFocus()).access(true);
		
		if(pointer != null) {
			pointer.setPositionAt(getFocus());
		}
	}
	
	public int indexOf(UIElement element) {
		return uiElements.indexOf(element);
	}

	public int size() {
		return uiElements.size();
	}
	
	public boolean isEmpty() {
		return uiElements.isEmpty();
	}
	
	@Override
	public void drawUI() {	
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

	@Override
	public void fireEvent(AvoEvent e) {
		if(e instanceof StateChangeEvent) {
			processStateChangeEvent((StateChangeEvent) e);
		} else if(e instanceof MouseEvent) {
			processMouseEvent((MouseEvent) e);
		} else if(e instanceof TimeEvent) {
			processTimeEvent((TimeEvent) e);
		} else if(e instanceof KeyEvent) {
			processKeyEvent((KeyEvent) e);
		} else if(e instanceof KeybindEvent) {
			processKeybindEvent((KeybindEvent) e);
		}
	}
	
	protected void processStateChangeEvent(StateChangeEvent e) {
		if(stateChangeListener != null) {
			stateChangeListener.changeState(e);
		}
	}
	
	@Override
	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
		
		UIElement ui;
		for(int i = 0; i < size(); i++) {
			ui = getElement(i);
			if(!ui.isEnabled()) {
				continue;
			}
			if(ui.getBounds().contains(e.getPosition()) || ui.getBounds().contains(e.getPrevPosition())) {
				ui.fireEvent(e);
			}
		}
	}
	
	@Override
	protected void processTimeEvent(TimeEvent e) {
		super.processTimeEvent(e);
		
		UIElement ui;
		for(int i = 0; i < size(); i++) {
			ui = getElement(i);
			if(!ui.isEnabled()) {
				continue;
			}
			ui.fireEvent(e);
		}
	}
	
	protected void processKeyEvent(KeyEvent e) {
		if(e.isConsumed() || getFocus() == null) {
			return;
		}
		getFocus().fireEvent(e);
	}
	
	protected void processKeybindEvent(KeybindEvent e) {
		if(keybindListener != null) {
			keybindListener.keybindClicked(e);
		}
		
		if(e.isConsumed() || getFocus() == null) {
			return;
		}
		getFocus().fireEvent(e);
	}

	class DefaultMouseAdapter implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			for(UIElement ui : uiElements) {
				if(ui instanceof Accessible && ui.getBoundsAsRect().contains(e.getPosition())) {
					setFocus(ui);
					break;
				}
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
			if(getFocus() != null) {
				return;
			}
			for(UIElement ui : uiElements) {
				if(ui instanceof Accessible && ui.isEnabled()) {
					setFocus(ui);
					return;
				}
			}
		};

		@Override
		public void mouseExited(MouseEvent e) {
			
		}
	}
	
	class DefaultMouseMotionAdapter implements MouseMotionListener {
		@Override
		public void mouseMoved(MouseEvent e) {
			e.consume();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
		}
	}
	
	class DefaultKeybindAdapter implements KeybindListener {
		@Override
		public void keybindClicked(KeybindEvent e) {
			if(getFocus() == null || !e.getKeybind().matches(Keybind.UP, Keybind.RIGHT, Keybind.LEFT, Keybind.DOWN, Keybind.CONFIRM)) {
				return;
			}
			
			e.consume();
			if(!e.getKeybind().clicked()) {
				return;
			}
			
			switch(e.getKeybind()) {
			case UP:
				changeSelection(0);
				break;
			case RIGHT:
				changeSelection(1);
				break;
			case LEFT:
				changeSelection(2);
				break;
			case DOWN:
				changeSelection(3);
				break;
			case CONFIRM:
				((UIElement) getFocus()).fireEvent(new ActionEvent());
				break;
			default:
				break;
			}
		}
	}

}

class SelectionPointer extends Icon {
	
	private Vector2f offset = new Vector2f(0, 0);
	
	/** Tween variables */
	private float time;
	private float duration;
	private float distance;
	private boolean tweenOut;
	
	public SelectionPointer(String icon) {
		super(icon);
		
		this.time = 0f;
		this.duration = 1.5f;
		this.distance = 15f;
		this.tweenOut = true;
		
		addTimeListener(new DefaultPointerTimeAdapter());
	}
	
	public void setPositionAt(UIElement element) {
		setPosition(element.getBounds().getX() - (getBounds().getWidth() * 0.8f), 
				element.getBoundsAsRect().getCenterY() - (getBounds().getHeight() * 0.5f));
	}
	
	class DefaultPointerTimeAdapter implements TimeListener {

		@Override
		public void timeStep(TimeEvent e) {
			time = MathUtils.clamp(time + e.getDelta(), 0, duration);
			if(tweenOut) {
				offset.setX(MathUtils.easeOut(time, 0, -distance, duration));
			} else {
				offset.setX(MathUtils.easeOut(time, -distance, distance, duration));
			}
			if(time == duration) {
				time = 0;
				tweenOut = !tweenOut;
			}
		}
		
	}
	
}
