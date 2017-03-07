package core.ui;

import java.util.ArrayList;

import core.ui.event.KeybindEvent;
import core.ui.event.KeybindListener;
import core.ui.utils.layout.RelativeLayout;
import core.ui.utils.layout.UILayout;
import core.utilities.ValueSupplier;

public class Panel extends UIElement {

	private UILayout layout;
	
	private ArrayList<UIElement> elements = new ArrayList<>();
	
	public Panel(UILayout layout) {
		setLayout(layout);
		
		addKeybindListener(new DefaultKeybindListener());
	}
	
	public Panel() {
		this(new RelativeLayout());
	}
	
	@Override
	public void draw() {
		super.draw();
		
		for(UIElement e : elements) {
			e.draw();
		}
	}

	public void setLayout(UILayout layout) {
		this.layout = layout;
	}

	public ArrayList<UIElement> getElements() {
		return elements;
	}
	
	public UIElement addElement(UIElement element) {
		if(layout != null) {
			element = layout.sortElement(element, this);
		}
		element.parentPanel = this;
		elements.add(element);
		
		compileBounds();
		return element;
	}
	
	@Override
	public void setPosition(ValueSupplier<Double> x, ValueSupplier<Double> y) {
		super.setPosition(x, y);
		
		UIElement tempElement;
		for(UIElement e : elements) {
			tempElement = layout.sortElement(e, this);
			e.setPosition(tempElement.getBounds().getXSupplier(), tempElement.getBounds().getYSupplier());
		}
	}
	
	private void compileBounds() {
		// XXX Probs kinda expensive with larger panels, should look into fixing
		final UIElement x = elements.stream().min((a, b) -> (int) a.getBounds().getX() - (int) b.getBounds().getX()).get();
		final UIElement y = elements.stream().min((a, b) -> (int) a.getBounds().getY() - (int) b.getBounds().getY()).get();
		final UIElement maxX = elements.stream().max((a, b) -> (int) a.getBounds().getMaxX() - (int) b.getBounds().getMaxX()).get();
		final UIElement maxY = elements.stream().max((a, b) -> (int) a.getBounds().getMaxY() - (int) b.getBounds().getMaxY()).get();

		setBounds(x.getBounds().getXAsSupplier(), y.getBounds().getYAsSupplier(), 
				() -> maxX.getBounds().getMaxX() - x.getBounds().getX(), 
				() -> maxY.getBounds().getMaxY() - y.getBounds().getY());
	}
	
	class DefaultKeybindListener implements KeybindListener {
		@Override
		public void keybindClicked(KeybindEvent e) {
			/*if(getFocus() == null || !e.getKeybind().matches(Keybind.UP, Keybind.RIGHT, Keybind.LEFT, Keybind.DOWN, Keybind.CONFIRM)) {
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
			}*/
		}
	}

}
