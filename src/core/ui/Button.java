package core.ui;

import core.event.AvoEvent;
import core.ui.event.ActionEvent;
import core.ui.event.ActionListener;
import core.ui.event.MouseEvent;
import core.ui.event.MouseListener;
import core.ui.utils.Accessible;
import core.ui.utils.HasText;
import core.utilities.text.Text;
import core.utilities.text.TextModifier;
import core.utilities.text.TextModifier.TextModValue;

public class Button extends UIElement implements HasText, Accessible {
	
	protected String text;
	protected String textColor = "gray";
	protected boolean focus;
	
	private ActionListener actionListener;
			
	public Button(String text) {
		this.text = text;
		setBounds(0, 0,
				text != null ? Text.getDefault().getWidth(text) : 1,
				text != null ? Text.getDefault().getHeight(text) : 1);
		
		addMouseListener(new DefaultButtonAdapter());
	}
	
	@Override
	public void draw() {
		super.draw();

		if(text != null) {
			Text.drawString(text, getBounds().getX(), getBounds().getY(), getTextModifier());
		}
	}

	@Override
	public void access(boolean accessed) {
		this.focus = accessed;
		// TODO Adapt these into some kind of const accessible from an exterior system for color coordination
		if(accessed) {
			textColor = "white";
		} else {
			textColor = "gray";
		}
	}
	
	@Override
	public boolean hasFocus() {
		return focus;
	}

	@Override
	public TextModifier getTextModifier() {
		TextModifier modifier = new TextModifier();
		if(textColor != null) {
			modifier.addModifier(TextModValue.COLOR, textColor);
		}
		
		return modifier;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getTextColor() {
		return textColor;
	}

	public void setTextColor(String textColor) {
		this.textColor = textColor;
	}
	
	/**
	 * Not recommended to override default <code>Button</code> behavior.
	 */
	@Override
	public void addMouseListener(MouseListener l) {
		this.mouseListener = l;
	}
	
	public void removeActionListener(ActionListener l) {
		if(l == null) {
			return;
		}
		
		actionListener = null;
	}
	
	public void addActionListener(ActionListener l) {
		this.actionListener = l;
	}
	
	@Override
	public void fireEvent(AvoEvent e) {
		super.fireEvent(e);
		
		if(e instanceof ActionEvent) {
			processActionEvent((ActionEvent) e);
		}
	}
	
	protected void processActionEvent(ActionEvent e) {
		if(actionListener != null) {
			actionListener.actionPerformed(e);
		}
	}

	/**
	 * Handle the default actions for mouse events on a button
	 */
	class DefaultButtonAdapter implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			Button.this.fireEvent(new ActionEvent());
			e.consume();
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
		}
		
		public void mouseEntered(MouseEvent e) {
			if(!hasFocus()) {
				textColor = "lightGray";
			}
		}
		
		public void mouseExited(MouseEvent e) {
			if(!hasFocus()) {
				textColor = "gray";
			}
		}
	}
}

