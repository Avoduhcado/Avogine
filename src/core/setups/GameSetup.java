package core.setups;

import java.util.ArrayList;

import core.ui.UIElement;
import core.ui.event.KeyEvent;
import core.ui.event.KeybindEvent;
import core.ui.event.MouseAdapter;
import core.ui.event.MouseEvent;
import core.ui.event.MouseListener;
import core.ui.event.MouseWheelEvent;
import core.ui.event.StateChangeEvent;
import core.ui.event.StateChangeListener;
import core.ui.event.TimeEvent;
import core.ui.event.UIEvent;
import core.ui.utils.Accessible;
import core.ui.utils.UIContainer;

public abstract class GameSetup implements UIContainer {

	private ArrayList<UIElement> uiElements = new ArrayList<UIElement>();
	protected UIElement focus;
	
	/** Handler for all contained UI state changes */
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
	
	private final MouseListener mouseListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent me) {
			UIElement tempFocus = null;
			for(UIElement ue : uiElements) {
				if(ue instanceof Accessible && ue.getBounds().contains(me.getPosition())) {
					tempFocus = ue;
					break;
				}
			}
			setFocus(tempFocus);
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
	};
	
	/** Update the current game state */
	public abstract void update();
	/** Draw the current game state */
	public abstract void draw();
	
	/** Draw the setup's UI */
	public void drawUI() {
		for(UIElement ui : uiElements) {
			ui.draw();
		}
		if(getFocus() != null && !(getFocus() instanceof UIContainer)) {
			getFocus().drawBounds();
		}
	}
	
	public ArrayList<UIElement> getUI() {
		return uiElements;
	}
	
	@Override
	public UIElement getFocus() {
		return focus;
	}
	
	public String printFocusHierarchy() {
		String focusTree = "";
		if(getFocus() != null) {
			focusTree += this.getClass() + ": " + focus.toString();
			if(getFocus() instanceof UIContainer) {
				if(((UIContainer) getFocus()).getFocus() != null) {
					focusTree += getFocus().getClass() + ": " + ((UIContainer) getFocus()).getFocus().toString();
				}
			}
		}
		
		return focusTree;
	}
	
	@Override
	public void setFocus(UIElement element) {
		if(!(element instanceof Accessible)) {
			return;
		}
		if(focus != null) {
			((Accessible) focus).access(false);
		}
		focus = element;
		((Accessible) focus).access(true);
	}
	
	public UIElement getElement(int index) {
		return uiElements.get(index);
	}
	
	public boolean removeElement(UIElement element) {
		boolean removed = uiElements.remove(element);
		if(removed && getFocus() == element) {
			UIElement tempFocus = null;
			for(UIElement e : uiElements) {
				if(e instanceof Accessible) {
					tempFocus = e;
					break;
				}
			}
			focus = tempFocus;
			if(focus != null) {
				setFocus(focus);
			}
		}
		return removed;
	}
	
	public void addUI(UIElement element) {
		element.setContainer(this);
		uiElements.add(element);
	}
	
	public void addUI(UIElement element, int index) {
		element.setContainer(this);
		uiElements.add(index, element);
	}
	
	public void fireEvent(UIEvent e) {
		if(e instanceof StateChangeEvent) {
			processStateChangeEvent(e);
		} else if(e instanceof TimeEvent) {
			processTimeEvent((TimeEvent) e);
		} else if(e instanceof MouseEvent) {
			processMouseEvent((MouseEvent) e);
		} else if(e instanceof MouseWheelEvent) {
			processMouseWheelEvent((MouseWheelEvent) e);
		} else if(e instanceof KeyEvent) {
			processKeyEvent((KeyEvent) e);
		} else if(e instanceof KeybindEvent) {
			processKeybindEvent((KeybindEvent) e);
		}
	}
	
	protected void processStateChangeEvent(UIEvent e) {
		if(stateChangeListener != null) {
			stateChangeListener.changeState((StateChangeEvent) e);
		}
	}
	
	protected void processTimeEvent(TimeEvent e) {
		for(int i = 0; i < uiElements.size(); i++) {
			uiElements.get(i).fireEvent(e);
		}
	}

	protected void processMouseEvent(MouseEvent e) {
		if(mouseListener != null) {
			if(e.getEvent() == MouseEvent.CLICKED) {
				mouseListener.mouseClicked(e);
			}
		}
		
		for(int i = 0; i < uiElements.size(); i++) {
			if(e.isConsumed()) {
				break;
			}
			if(!uiElements.get(i).isEnabled()) {
				continue;
			}
			if(uiElements.get(i).getBounds().contains(e.getPosition()) || uiElements.get(i).getBounds().contains(e.getPrevPosition())) {
				uiElements.get(i).fireEvent(e);
				// Only fire the event to the UI once, after it hits a target we shouldn't continue checking
				break;
			}
		}
	}
	
	protected void processMouseWheelEvent(MouseWheelEvent e) {
		
	}
	
	protected void processKeyEvent(KeyEvent e) {
		if(getFocus() != null) {
			getFocus().fireEvent(e);
		}
	}
	
	protected void processKeybindEvent(KeybindEvent e) {
		if(getFocus() != null) {
			getFocus().fireEvent(e);
		}
	}
}
