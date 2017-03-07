package core.ui.compoundui;

import core.ui.Button;
import core.ui.Panel;
import core.ui.TextBox;
import core.ui.utils.layout.FlowLayout;

public class SelectionBox extends Panel {
	
	private TextBox title;
	private Panel choicePanel;

	public SelectionBox() {
		super();
		
		title = new TextBox(";", false);
		//title.setPosition(() -> Camera.get().getDisplayWidth(0.5f), () -> 0.0);
		//title.setHorizontalAlign(HorizontalAlign.CENTER);
		addElement(title);
		
		choicePanel = new Panel();
		choicePanel.setLayout(new FlowLayout());
		//choicePanel.setPosition(() -> Camera.get().getDisplayWidth(0.5f), () -> title.getBounds().getMaxY());
		addElement(choicePanel);
	}

	public TextBox getTitle() {
		return title;
	}

	public void addChoice(Button choice) {
		this.choicePanel.addElement(choice);
	}
	
}
