package core.ui.utils.layout;

import core.ui.Panel;
import core.ui.UIElement;

public class RelativeLayout extends UILayout {

	@Override
	public UIElement sortElement(UIElement element, Panel container) {
		element.setPosition(container.getBounds().getX() + element.getBounds().getX(),
				container.getBounds().getY() + element.getBounds().getY());
		
		return element;
	}

}
