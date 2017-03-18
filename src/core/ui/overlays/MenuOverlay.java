package core.ui.overlays;

import org.lwjgl.util.vector.Vector4f;

import core.render.DrawUtils;
import core.ui.ElementGroup;
import core.ui.UIElement;

public abstract class MenuOverlay extends ElementGroup {

	protected Vector4f glassColor = new Vector4f(0f, 0f, 0f, 0.35f);
	
	public MenuOverlay() {
		super();
	}
	
	@Override
	public void draw() {
		DrawUtils.fillScreen(glassColor);
		
		super.draw();
	}
	
	@Override
	public void drawUI() {
		for(UIElement ui : uiElements) {
			ui.draw();
		}
	}

}
