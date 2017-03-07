package core.setups.utils;

import java.util.ArrayList;

import core.event.AvoEvent;
import core.ui.UIElement;

public interface UIContainer {

	public void drawUI();
	
	public ArrayList<UIElement> getUI();
	public UIElement getElement(int index);
	public boolean removeElement(UIElement element);
	public void addUI(UIElement element);
	public void addUI(UIElement element, int index);
	public UIElement getFocus();
	public void setFocus(UIElement element);
	
	public void fireEvent(AvoEvent e);
	
}
