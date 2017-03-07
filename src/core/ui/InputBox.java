package core.ui;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.lwjgl.input.Keyboard;

import core.event.AvoEvent;
import core.ui.event.ActionEvent;
import core.ui.event.ActionListener;
import core.ui.event.KeyEvent;
import core.ui.event.KeyListener;
import core.ui.event.MouseEvent;
import core.ui.event.MouseListener;
import core.ui.event.TimeEvent;
import core.ui.event.TimeListener;
import core.ui.event.ValueChangeEvent;
import core.ui.event.ValueChangeListener;
import core.ui.utils.Accessible;
import core.ui.utils.HasText;
import core.ui.utils.InputStyle;
import core.utilities.text.Text;
import core.utilities.text.TextModifier;
import core.utilities.text.TextModifier.TextModValue;

// TODO Include option to limit size bounds? Beyond just a character limit

public class InputBox extends UIElement implements Accessible, HasText {

	private static final String CARET = "/";
	
	private InputStyle style;
	private String text;
	protected boolean focus;
	private int textLimit;
	private String textColor = "gray";
	private float flash = 0.0f;
	
	private ActionListener actionListener;
	private KeyListener keyListener;
	private ValueChangeListener valueChangeListener;
	
	/**
	 * @param text Preset text
	 * @param style Type of input accepted
	 * @param textLimit Total number of characters accepted, enter 0 for no limit
	 */
	public InputBox(String text, InputStyle style, int textLimit) {
		this.style = style;
		this.text = text != null ? text : "";
		this.textLimit = textLimit;
		trimText();
		
		setSize(text != null ? Text.getDefault().getWidth(text) : Text.getDefault().getWidth(CARET),
				text != null ? Text.getDefault().getHeight(text) : Text.getDefault().getHeight(CARET));
		
		addMouseListener(new DefaultInputMouseAdapter());
		addTimeListener(new DefaultInputTimeAdapter());
		addKeyListener(createKeyListener(style));
	}
	
	public InputBox(String text, InputStyle style) {
		this(text, style, 100);
	}
	
	@Override
	public void draw() {
		super.draw();
		
		Text.drawString(flash > 0.5f ? text + CARET : text, getBounds().getX(), getBounds().getY(), getTextModifier());
	}

	@Override
	public TextModifier getTextModifier() {
		TextModifier modifier = new TextModifier();
		if(textColor != null) {
			modifier.addModifier(TextModValue.COLOR, textColor);
		}
		
		return modifier;
	}
	
	/**
	 * Fit bounds to included text
	 */
	protected void resize() {
		setSize(Text.getDefault().getWidth(text), Text.getDefault().getHeight(text));
	}
	
	/**
	 * Paste contents of clipboard into text.
	 */
	private void paste() {
		try {
			switch(style) {
			case INTEGERS: 
				text += Integer.parseInt((String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
				break;
			default: 
				text += (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
				break;
			}
		} catch (NumberFormatException e) {
		} catch (IOException | HeadlessException | UnsupportedFlavorException  e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Add the next character from the keyboard to text.
	 */
	private void addCharacter(char c) {
		switch(style) {
		case PLAIN_TEXT: 
			text = text + c;
			break;
		case INTEGERS:
			try {
				Integer.parseInt(text + c);
				text = text + c;
			} catch (NumberFormatException e) {}
			break;
		default:
			break;
		}
	}
	
	/**
	 * Trim the text to fit limit constraints.
	 */
	private void trimText() {
		if(textLimit > 0 && text.length() > textLimit) {
			text = text.substring(0, textLimit);
			resize();
		}
	}
	
	/**
	 * @return true if the next key is a modifier
	 */
	private boolean isModifierKey(int key) {
		switch(key) {
		case Keyboard.KEY_LSHIFT:
		case Keyboard.KEY_RSHIFT:
		case Keyboard.KEY_RIGHT:
		case Keyboard.KEY_LEFT:
		case Keyboard.KEY_DOWN:
		case Keyboard.KEY_UP:
		case Keyboard.KEY_LCONTROL:
		case Keyboard.KEY_RCONTROL:
			return true;
		default:
			return false;
		}
	}
	
	@Override
	public void access(boolean accessed) {
		this.focus = accessed;
		if(accessed) {
			textColor = "white";
		} else {
			flash = 0;
			textColor = "gray";
		}
	}
	
	@Override
	public boolean hasFocus() {
		return focus;
	}

	/**
	 * Limit the text to a specific value, or enter 0 for no limit.
	 * @param textLimit
	 */
	public void setTextLimit(int textLimit) {
		this.textLimit = textLimit;
		trimText();
	}
	
	/**
	 * @return text from this input.
	 */
	public String getText() {
		return text;
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
	
	private KeyListener createKeyListener(InputStyle style) {
		switch(style) {
		case PLAIN_TEXT:
		case INTEGERS:
			return (e -> {
				if(!isModifierKey(e.getKey())) {
					if(e.getKey() == Keyboard.KEY_BACK && text.isEmpty()) {
						return;
					} else if(e.getKey() == Keyboard.KEY_BACK && !text.isEmpty()) {
						// Remove the last character from text
						text = text.substring(0, text.length() - 1);
					} else if(text.length() <= textLimit) {
						// If the user is pasting text
						if(e.getKey() == Keyboard.KEY_V && (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) 
								|| Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))) {
							paste();
						} else if(e.getKeyChar() != Keyboard.CHAR_NONE && e.getKey() != Keyboard.KEY_RETURN) {
							// Add a regular character
							addCharacter(e.getKeyChar());
						}
						// Make sure text is still inside limit
						trimText();
					}
					resize();
				}
				
				if(e.getKey() == Keyboard.KEY_RETURN) {
					InputBox.this.fireEvent(new ValueChangeEvent(text, null));
					//InputBox.this.setState(DISABLED);
				}
			});
		case KEYBINDS:
			return (e -> {
				text = e.getKeyName();
				resize();

				//InputBox.this.setState(DISABLED);
				InputBox.this.fireEvent(new ValueChangeEvent(text, null));
			});
		}
		
		return null;
	}
	
	public void removeKeyListener(KeyListener l) {
		if(l == null) {
			return;
		}
		this.keyListener = null;
	}

	public void addKeyListener(KeyListener l) {
		this.keyListener = l;
	}
	
	public void removeValueChangeListener(ValueChangeListener l) {
		if(l == null) {
			return;
		}
		this.valueChangeListener = null;
	}

	public void addValueChangeListener(ValueChangeListener l) {
		this.valueChangeListener = l;
	}
	
	@Override
	public void fireEvent(AvoEvent e) {
		super.fireEvent(e);
		
		if(e instanceof ActionEvent) {
			processActionEvent((ActionEvent) e);
		} else if(e instanceof KeyEvent) {
			processKeyEvent((KeyEvent) e);
		} else if(e instanceof ValueChangeEvent) {
			processValueChangeEvent((ValueChangeEvent) e);
		}
	}
	
	protected void processActionEvent(ActionEvent e) {
		if(actionListener != null) {
			actionListener.actionPerformed(e);
		}
	}
	
	protected void processKeyEvent(KeyEvent e) {
		if(keyListener != null) {
			keyListener.keyPress(e);
		}
	}

	protected void processValueChangeEvent(ValueChangeEvent e) {
		if(valueChangeListener != null) {
			valueChangeListener.valueChanged(e);
		}
	}
	
	class DefaultInputMouseAdapter implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			//setState(getState() == ENABLED ? DISABLED : ENABLED);
			access(!hasFocus());
			InputBox.this.fireEvent(new ActionEvent());
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
			//textColor = "white";
		}
		
		public void mouseExited(MouseEvent e) {
			if(!hasFocus()) {
				textColor = "gray";
			}
			//textColor = "gray";
		}
	}

	class DefaultInputTimeAdapter implements TimeListener {
		@Override
		public void timeStep(TimeEvent e) {
			if(hasFocus()) {
				flash += e.getDelta();
				if(flash > 1f) {
					flash = 0;
				}
			}
		}
	}
	
}
