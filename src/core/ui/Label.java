package core.ui;

import core.utilities.text.Text;

public class Label extends UIElement {

	private String text;
	
	public Label(String text) {
		this.text = text;
		setBounds(() -> 0d, () -> 0d, 
				() -> (double) Text.getDefault().getWidth(this.text), () -> (double) Text.getDefault().getHeight(this.text));
	}
	
	@Override
	public void draw() {
		super.draw();
		
		Text.drawString(text, getBounds().getX(), getBounds().getY());
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
}
