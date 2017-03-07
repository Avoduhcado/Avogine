package core.ui.utils.layout;

import core.ui.Panel;
import core.ui.UIElement;

public class FlowLayout extends UILayout {

	@Override
	public UIElement sortElement(UIElement element, Panel container) {
		// TODO Implement max size and layout wrapping, but for now just put everything on the right
		if(!container.getElements().isEmpty()) {
			UIElement rightMost = container.getElements().get(container.getElements().size() - 1);
			element.setPosition(() -> rightMost.getBounds().getMaxX(), () -> container.getBounds().getY());
		}
		return element;
	}

}
