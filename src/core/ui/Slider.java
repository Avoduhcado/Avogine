package core.ui;

import core.render.SpriteList;
import core.render.Transform;
import core.ui.event.MouseEvent;
import core.ui.event.MouseListener;
import core.ui.event.MouseMotionListener;
import core.ui.event.UIEvent;
import core.ui.event.ValueChangeEvent;
import core.ui.event.ValueChangeListener;
import core.ui.utils.Accessible;
import core.utilities.MathUtils;

// TODO Extend this functionality for snapping? So like only 4 points to slide to and stuff
public class Slider extends UIElement implements Accessible {

	private double value;
	
	private String slideBar;
	private Transform slideTransform;
	private String sliderKnob;
	
	private ValueChangeListener valueChangeListener;
	
	public Slider(float value) {
		this.value = value;

		// TODO Disgusting trash of using constant slider UI and basing the size off of it
		this.slideBar = "SliderBG";
		this.sliderKnob = "SliderValue";
		this.slideTransform = new Transform();
		setBounds(0, 0, SpriteList.get(slideBar).getWidth(), SpriteList.get(slideBar).getHeight());
		
		addMouseListener(new DefaultSliderAdapter());
		addMouseMotionListener(new DefaultSliderMotionAdapter());
	}
	
	@Override
	public void draw() {
		super.draw();
		
		setTransform(true);
		SpriteList.get(slideBar).draw(slideTransform);
		setTransform(false);
		SpriteList.get(sliderKnob).draw(slideTransform);
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

	public double getValue() {
		return value;
	}
	
	public void setValue(double value) {
		if(this.value != value) {
			fireEvent(new ValueChangeEvent(value, value - this.value));
			this.value = value;
		}
	}
	
	private void setTransform(boolean slide) {
		if(slide) {
			slideTransform.setPosition(getBounds().getX(), getBounds().getY());
		} else {
			slideTransform.setPosition((getBounds().getX() + (getBounds().getWidth() * value) - (SpriteList.get(sliderKnob).getWidth() / 2f)),
					getBounds().getY());
		}
	}
	
	public void removeValueChangeListener(ValueChangeListener l) {
		if(l == null) {
			return;
		}
		
		valueChangeListener = null;
	}
	
	public void addValueChangeListener(ValueChangeListener l) {
		this.valueChangeListener = l;
	}
	
	@Override
	public void fireEvent(UIEvent e) {
		super.fireEvent(e);
		
		if(e instanceof ValueChangeEvent) {
			processValueChangeEvent((ValueChangeEvent) e);
		}
	}
	
	protected void processValueChangeEvent(ValueChangeEvent e) {
		if(valueChangeListener != null) {
			valueChangeListener.valueChanged(e);
		}
	}
	
	class DefaultSliderAdapter implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			setValue(MathUtils.clamp((e.getX() - getBounds().getX()) / (getBounds().getMaxX() - getBounds().getX()), 0f, 1f));
		}

		@Override
		public void mousePressed(MouseEvent e) {
			setValue(MathUtils.clamp((e.getX() - getBounds().getX()) / (getBounds().getMaxX() - getBounds().getX()), 0f, 1f));
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

	}
	
	class DefaultSliderMotionAdapter implements MouseMotionListener {

		@Override
		public void mouseMoved(MouseEvent e) {
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			setValue(MathUtils.clamp((e.getX() - getBounds().getX()) / getBounds().getWidth(), 0f, 1f));
		}
	}

}
