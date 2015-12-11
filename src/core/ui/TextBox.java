package core.ui;

import java.util.ArrayList;

import core.Theater;
import core.ui.utils.Align;
import core.utilities.keyboard.Keybinds;
import core.utilities.keyboard.KeybindListener;
import core.utilities.text.Text;
import core.utilities.text.TextModifier;

public class TextBox extends UIElement implements KeybindListener {

	protected ArrayList<TextLine> lines = new ArrayList<TextLine>();

	protected float textFill;
	private float fillSpeed = 0.5f;
	private float killTimer;
	//protected ScriptEvent scriptEvent;
	
	/**
	 * A simple box to display a block of text.
	 * @param text The text to be written, including any text modifiers
	 * @param x X position
	 * @param y Y position
	 * @param image The textbox background, or null for no background
	 */
	public TextBox(String text, float x, float y, String image, boolean fill) {		
		parseText(text);
		
		if(fill) {
			textFill = 0f;
		} else {
			textFill = getLength();
		}
		
		setBounds(x, y, getWidth(), getHeight());
		setFrame(image);
	}
	
	public void update() {
		if(textFill < getLength()) {
			fill();
		}
		
		if(killTimer > 0) {
			killTimer -= Theater.getDeltaSpeed(0.025f);
			if(killTimer <= 0) {
				// TODO Extend to all UI classes, maybe add a fancy fade effect
				setDead(true);
			}
		}
	}
	
	@Override
	public void draw() {
		if(frame != null) {
			if(textFill < getLength()) {
				bounds.setFrame(bounds.getX(), bounds.getY(), getWidth((int) textFill + 1), getHeight((int) textFill + 1));
			}
			frame.draw(bounds);
		}

		if(!lines.isEmpty()) {
			// Draw first line
			lines.get(0).draw((float) bounds.getX(), (float) bounds.getY(), (int) textFill);
			// Text limit for subsequent lines
			int limit = (int) textFill - lines.get(0).getLength();
			if(limit > 0) {
				// Text y offset for subsequent lines
				float yOffset = lines.get(0).getHeight();
				for(int i = 1; i < lines.size(); i++) {
					// Draw line
					lines.get(i).draw((float) bounds.getX(), (float) (bounds.getY() + yOffset), limit);
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

	@Override
	public void KeybindTouched(Keybinds k) {
		if(k.equals(Keybinds.CONFIRM) && k.clicked()) {
			if(textFill < getLength()) {
				textFill = getLength();
				bounds.setFrame(bounds.getX(), bounds.getY(), getWidth((int) textFill + 1), getHeight((int) textFill + 1));
			} else {
				//scriptEvent.processed();
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
	public void setAlign(Align border) {
		if(!lines.isEmpty()) {
			super.setAlign(border);
		}
	}
	
	/*public void setScriptEvent(ScriptEvent script) {
		this.scriptEvent = script;
	}*/
	
	public void fill() {
		textFill += Theater.getDeltaSpeed(fillSpeed);
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
	
	public void setKillTimer(float killTimer) {
		this.killTimer = killTimer;
	}

	public void setText(String text) {
		parseText(text);		
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
				segments.add(new TextSegment(null, text));
			}
		}
		
		public void draw(float x, float y, int limit) {
			float xOffset = 0;
			for(int i = 0; i<segments.size(); i++) {
				if(TextBox.this.still) {
					segments.get(i).addModifier("t+");
				}
				segments.get(i).draw(x + xOffset, y, limit);
				xOffset += segments.get(i).getWidth();
				limit -= segments.get(i).getLength();
				if(limit <= 0)
					break;
			}
		}
		
		// TODO Per segment adjustment?
		public void append(String modifier) {
			for(TextSegment s : segments) {
				s.modifier.concat(modifier);
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
				this.modifier = new TextModifier(modifier);
				this.text = text;
			}
			
			public void draw(float x, float y, int limit) {
				Text.getFont(modifier.fontFace).drawStringSegment(modifier.addIn + text, x, y, 0,
						limit > getLength() ? getLength() : limit, modifier);
			}
			
			public void addModifier(String modifier) {
				this.modifier.concat(modifier);
			}
			
			public float getWidth() {
				return Text.getFont(modifier.fontFace).getWidth(modifier.addIn + text);
			}
			
			public float getHeight() {
				return Text.getFont(modifier.fontFace).getHeight(modifier.addIn + text);
			}
			
			public float getWidth(int limit) {
				return Text.getFont(modifier.fontFace).getWidth((modifier.addIn + text)
						.substring(0, limit > getLength() ? getLength() : limit));
			}
			
			public float getHeight(int limit) {
				return Text.getFont(modifier.fontFace).getHeight((modifier.addIn + text)
						.substring(0, limit > getLength() ? getLength() : limit));
			}
			
			public int getLength() {
				return text.length() + modifier.addIn.length();
			}
			
		}
		
	}

}
