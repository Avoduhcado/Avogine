package core.ui.overlays;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector4f;

import core.Camera;
import core.ui.Button;
import core.ui.Label;
import core.ui.utils.HorizontalAlign;

public class SelectionMenu extends MenuOverlay {
	
	public static final int VERTICAL = 0;
	public static final int HORIZONTAL = 1;
	
	private int orientation;
	private int horizontalSpacing = 10;
	
	private Label titleLabel;
	private ArrayList<Button> options = new ArrayList<Button>();
	
	public SelectionMenu(int orientation) {
		super();
		glassColor = new Vector4f(0, 0, 0, 0);
		
		this.orientation = orientation;
		
		titleLabel = new Label("");
		titleLabel.setPosition(() -> Camera.get().getDisplayWidth(0.5f), () -> Camera.get().getDisplayHeight(0.75f));
		titleLabel.setHorizontalAlign(HorizontalAlign.CENTER);
		addUI(titleLabel);
		
		setFrame("Menu5");
	}
	
	public void setTitle(String title) {
		titleLabel.setText(title);
	}
	
	public void addOptionButton(Button button) {
		if(orientation == VERTICAL) {
			button.setPosition(() -> Camera.get().getDisplayWidth(0.5f), 
					() -> {
						for(int i = 1; i < options.size(); i++) {
							if(options.get(i) == button) {
								return options.get(i - 1).getBounds().getMaxY();
							}
						}
						return titleLabel.getBounds().getMaxY();
					});
			button.setHorizontalAlign(HorizontalAlign.CENTER);
		} else if(orientation == HORIZONTAL) {
			button.setPosition(() -> {
				double width = 0;
				for(Button b : options) {
					width += b.getBounds().getWidth() + horizontalSpacing;
				}
				width = Camera.get().getDisplayWidth(0.5f) - (width / 2);
				for(Button b : options) {
					if(b == options.get(options.indexOf(button))) {
						return width;
					}
					width += b.getBounds().getWidth() + horizontalSpacing;
				}
				
				return width;
			}, () -> titleLabel.getBounds().getMaxY());
			button.setHorizontalAlign(HorizontalAlign.LEFT);
		}

		options.add(button);
		addUI(button);
		
		for(int i = 0; i < options.size(); i++) {
			options.get(i).setSurrounding(3, i < options.size() - 1 ? options.get(i + 1) : options.get(0));
			options.get(i).setSurrounding(1, i < options.size() - 1 ? options.get(i + 1) : options.get(0));
		}
		setKeyboardNavigable(true, options.get(0));
	}
	
}
