package core.ui;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

import org.lwjgl.util.vector.Vector3f;

import core.event.AvoEvent;
import core.render.DrawUtils;
import core.ui.event.ActionEvent;
import core.ui.event.ActionListener;
import core.ui.event.MouseEvent;
import core.ui.event.MouseListener;
import core.ui.utils.Accessible;
import core.ui.utils.HasText;
import core.utilities.ValueSupplier;
import core.utilities.text.Text;
import core.utilities.text.TextModifier;

public class CheckBox extends UIElement implements HasText, Accessible {

	protected Dimension checkSize = new Dimension(16, 16);
	
	protected boolean checked;
	protected String text;
	
	private ActionListener actionListener;
	
	public CheckBox(String text) {
		this.text = text;
		setBounds(0, 0,
				text != null ? Text.getDefault().getWidth(text) : 1,
				text != null ? Text.getDefault().getHeight(text) : 1);
				
		addMouseListener(new DefaultCheckBoxAdapter());
	}
	
	@Override
	public void draw() {
		super.draw();

		DrawUtils.setColor(new Vector3f(1, 1, 1));
		if(checked) {
			DrawUtils.fillRect(1, 1, 1, 1, new Rectangle2D.Double(uiBounds.getX(), uiBounds.getY() + (getBounds().getHeight() - checkSize.getHeight()) / 2,
					checkSize.getWidth(), checkSize.getHeight()));
		} else {
			DrawUtils.drawRect(new Rectangle2D.Double(uiBounds.getX(), uiBounds.getY() + (getBounds().getHeight() - checkSize.getHeight()) / 2,
					checkSize.getWidth(), checkSize.getHeight()));
		}
		
		if(text != null) {
			Text.drawString(text, getBounds().getX() + checkSize.getWidth(), getBounds().getY(), getTextModifier());
		}
	}

	@Override
	public void access(boolean accessed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasFocus() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TextModifier getTextModifier() {
		return new TextModifier();
	}

	@Override
	public void setBounds(ValueSupplier<Double> x, ValueSupplier<Double> y, ValueSupplier<Double> width, ValueSupplier<Double> height) {
		uiBounds.setFrame(x, y, () -> width.getValue() + checkSize.getWidth(), height);
	}
	
	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
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
	class DefaultCheckBoxAdapter implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			checked = !checked;
			CheckBox.this.fireEvent(new ActionEvent());
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
		}
		
		public void mouseEntered(MouseEvent e) {
		}
		
		public void mouseExited(MouseEvent e) {
		}
	}

}
