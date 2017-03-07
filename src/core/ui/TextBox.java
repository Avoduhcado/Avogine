package core.ui;

import java.util.ArrayList;

import core.event.AvoEvent;
import core.ui.event.CompleteScriptEvent;
import core.ui.event.KeybindEvent;
import core.ui.event.KeybindListener;
import core.ui.event.TimeEvent;
import core.ui.event.TimeListener;
import core.ui.utils.Accessible;
import core.ui.utils.HasText;
import core.utilities.keyboard.Keybind;
import core.utilities.text.Text;
import core.utilities.text.TextModifier;

public class TextBox extends UIElement implements HasText, Accessible {

	protected ArrayList<TextLine> lines = new ArrayList<TextLine>();

	protected float textFill = 0f;
	protected float fillSpeed = 12.5f;
		
	/**
	 * A simple box to display a block of text.
	 * @param text The text to be written, including any text modifiers
	 * @param x X position
	 * @param y Y position
	 * @param image The text box background, or null for no background
	 */
	public TextBox(String text, boolean fill) {
		parseText(text);
		
		if(fill) {
			addTimeListener(new TimeListener() {
				public void timeStep(TimeEvent e) {
					textFill += e.getDelta() * fillSpeed;
					
					if(textFill >= TextBox.this.getLength()) {
						TextBox.this.removeTimeListener(this);
					}
				}
			});
		} else {
			textFill = getLength();
		}
		
		setBounds(0, 0, getWidth(), getHeight());
		
		addKeybindListener(new DefaultKeybindAdapter());
	}
	
	public TextBox() {
		this("", false);
	}

	@Override
	public void draw() {
		if(frame != null) {
			if(textFill < getLength()) {
				setSize(getWidth((int) textFill + 1), getHeight((int) textFill + 1));
			}
			frame.draw(getBoundsAsRect());
		}

		if(!lines.isEmpty()) {
			// Draw first line
			lines.get(0).draw(getBounds().getX(), getBounds().getY(), (int) textFill);
			// Text limit for subsequent lines
			int limit = (int) textFill - lines.get(0).getLength();
			if(limit > 0) {
				// Text y offset for subsequent lines
				float yOffset = lines.get(0).getHeight();
				for(int i = 1; i < lines.size(); i++) {
					// Draw line
					lines.get(i).draw(getBounds().getX(), getBounds().getY() + yOffset, limit);
					// Increment y offset
					yOffset += lines.get(i).getHeight();
					// Decrement limit
					limit -= lines.get(i).getLength();
					// End draw if you've run out of available text
					if(limit <= 0)
						break;
				}
			}
		}
	}

	private void parseText(String text) {
		if(text.contains(";")) {
			String[] lineArray = text.split(";");
			for(int i = 0; i<lineArray.length; i++) {
				if(i > 0 && !lineArray[i].startsWith("<") && lineArray[i - 1].contains("<")) {
					lineArray[i] = lineArray[i - 1].substring(lineArray[i - 1].lastIndexOf('<'),
							lineArray[i - 1].lastIndexOf('>') + 1) + lineArray[i];
				}
				lines.add(new TextLine(lineArray[i]));
			}
		} else {
			lines.add(new TextLine(text));
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
	
	public float getTextFill() {
		return textFill;
	}
	
	public void setTextFill(float textFill) {
		if(textFill == -1)
			this.textFill = getLength();
		else
			this.textFill = textFill;
	}

	public void setFillSpeed(float fillSpeed) {
		this.fillSpeed = fillSpeed;
	}
	
	public void setKillTimer(float countdown) {
		addTimeListener(new TimeListener() {
			private float killTimer = countdown;
			
			@Override
			public void timeStep(TimeEvent e) {
				killTimer -= e.getDelta();
				
				if(killTimer <= 0) {
					setState(UIElement.KILL_FLAG);
				}
			}
		});
	}

	@Override
	public void fireEvent(AvoEvent e) {
		super.fireEvent(e);
		
		if(e instanceof KeybindEvent) {
			processKeybindEvent((KeybindEvent) e);
		}
	}

	protected void processKeybindEvent(KeybindEvent e) {
		if(keybindListener != null) {
			keybindListener.keybindClicked(e);
		}
	}
	
	class DefaultKeybindAdapter implements KeybindListener {
		@Override
		public void keybindClicked(KeybindEvent e) {
			if(e.getKeybind().equals(Keybind.CONFIRM) && e.getKeybind().clicked()) {
				if(textFill < getLength()) {
					textFill = getLength();
					setSize(getWidth((int) textFill + 1), getHeight((int) textFill + 1));
					
					removeTimeListener(timeListener);
				} else {
					fireEvent(new CompleteScriptEvent());
				}
				e.consume();
			}
		}
	}
	
	public void setText(String text) {
		lines.clear();
		parseText(text);
		setSize(getWidth(), getHeight());
		// TODO Textfill should either be refactored to be -1 == draw all, or always set to max by default
		textFill = getLength();
	}
	
	// TODO Provide support to add text to the same line, or more than one line
	public void addText(String text) {
		if(text.startsWith(";")) {
			lines.add(new TextLine(text.substring(1)));
		}
	}
	
	public float getWidth() {
		float width = 0;
		for(TextLine l : lines) {
			if(l.getWidth() > width) {
				width = l.getWidth();
			}
		}
		
		return width;
	}
	
	public float getHeight() {
		float height = 0;
		for(TextLine l : lines) {
			height += l.getHeight();
		}
		
		return height;
	}

	public float getWidth(int limit) {
		float width = 0;
		for(TextLine l : lines) {
			if(l.getWidth(limit) > width) {
				width = l.getWidth(limit);
			}
			limit -= l.getLength();
			if(limit <= 0)
				break;
		}
		
		return width;
	}
	
	public float getHeight(int limit) {
		float height = 0;
		for(TextLine l : lines) {
			height += l.getHeight(limit);
			limit -= l.getLength();
			if(limit <= 0)
				break;
		}
		
		return height;
	}
	
	public int getLength() {
		int length = 0;
		for(TextLine l : lines) {
			length += l.getLength();
		}
		
		return length;
	}
	
	public class TextLine {
		
		private ArrayList<TextSegment> segments = new ArrayList<TextSegment>();
		
		public TextLine(String text) {
			if(text.contains("<")) {
				for(String segment : text.split("<")) {
					if(!segment.isEmpty()) {
						segments.add(new TextSegment(segment.split(">")[0], segment.split(">")[1]));
					}
				}
			} else {
				segments.add(new TextSegment("", text));
			}
		}
		
		public void draw(double x, double y, int limit) {
			double xOffset = 0;
			for(int i = 0; i<segments.size(); i++) {
				segments.get(i).draw(x + xOffset, y, limit);
				xOffset += segments.get(i).getWidth();
				limit -= segments.get(i).getLength();
				if(limit <= 0)
					break;
			}
		}
		
		// TODO Per segment adjustment?
		public void appendModifier(String type, String value) {
			for(TextSegment s : segments) {
				s.modifier.addModifier(type, value);
			}
		}
		
		public float getWidth() {
			float width = 0;
			for(TextSegment s : segments) {
				width += s.getWidth();
			}
			
			return width;
		}
		
		public float getHeight() {
			float height = 0;
			for(TextSegment s : segments) {
				if(s.getHeight() > height) {
					height = s.getHeight();
				}
			}
			
			return height;
		}
		
		public float getWidth(int limit) {
			float width = 0;
			for(int i = 0; i<segments.size(); i++) {
				width += segments.get(i).getWidth(limit);
				limit -= segments.get(i).getLength();
				if(limit <= 0)
					break;
			}
			
			return width;
		}
		
		public float getHeight(int limit) {
			float height = 0;
			for(int i = 0; i<segments.size(); i++) {
				if(segments.get(i).getHeight(limit) > height)
					height = segments.get(i).getHeight(limit);
				limit -= segments.get(i).getLength();
				if(limit <= 0)
					break;
			}
			
			return height;
		}
		
		public int getLength() {
			int length = 0;
			for(TextSegment s : segments) {
				length += s.getLength();
			}
			
			return length;
		}
		
		private class TextSegment {
			
			private TextModifier modifier;
			private String text;
			
			public TextSegment(String modifier, String text) {
				this.modifier = TextModifier.compile(modifier.split(","));
				this.text = text;
			}
			
			public void draw(double x, double y, int limit) {
				Text.getFont(modifier.getFontFace()).drawStringSegment(modifier.getReplacement() + text, x, y, 0,
						limit > getLength() ? getLength() : limit, modifier);
			}
			
			public void addModifier(String type, String value) {
				this.modifier.addModifier(type, value);
			}
			
			public float getWidth() {
				return Text.getFont(modifier.getFontFace()).getWidth(modifier.getReplacement() + text);
			}
			
			public float getHeight() {
				return Text.getFont(modifier.getFontFace()).getHeight(modifier.getReplacement() + text);
			}
			
			public float getWidth(int limit) {
				return Text.getFont(modifier.getFontFace()).getWidth((modifier.getReplacement() + text)
						.substring(0, limit > getLength() ? getLength() : limit));
			}
			
			public float getHeight(int limit) {
				return Text.getFont(modifier.getFontFace()).getHeight((modifier.getReplacement() + text)
						.substring(0, limit > getLength() ? getLength() : limit));
			}
			
			public int getLength() {
				return text.length() + modifier.getReplacement().length();
			}
			
		}
		
	}

}
