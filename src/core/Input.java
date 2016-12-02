package core;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector4f;

import core.render.effects.RotateEffect;
import core.render.effects.ScaleEffect;
import core.render.effects.ScreenEffect;
import core.render.effects.TintEffect;
import core.render.effects.TranslateEffect;
import core.render.effects.Tween;
import core.setups.Stage;
import core.ui.UIElement;
import core.ui.event.KeyEvent;
import core.ui.event.KeybindEvent;
import core.ui.event.MouseEvent;
import core.ui.overlays.GameMenu;
import core.ui.utils.UIContainer;
import core.utilities.keyboard.Keybind;

public class Input {
	
	public Input() {
		// TODO I guess this could be a property read from a file maybe?
		// I don't remember why I added it but I'm p sure it was necessary for something
		Keyboard.enableRepeatEvents(true);
	}
	
	private static Input input = new Input();
	
	public static Input get() {
		return input;
	}
	
	/**
	 * Main processing of any and all input depending on current setup.
	 * @param setup The current setup of the game
	 */
	public void checkInput(UIContainer setup) {
		// TODO Menu Overlays isn't processed as ElementGroup
		if(!setup.getUI().isEmpty()) {
			while(setup.getElement(setup.getUI().size() - 1) != null && setup.getElement(setup.getUI().size() - 1) instanceof UIContainer) {
				setup = (UIContainer) setup.getElement(setup.getUI().size() - 1);
			}
		}
		
		// Detect any keyboard events
		processKeyboard(setup);
		
		// Detect any keybind events
		processKeybinds(setup);
		
		// Detect any mouse events
		processMouse(setup);
		
		// TODO Hide behind a keybind listener?
		// Enter debug mode
		if(Keybind.DEBUG.clicked()) {
			Theater.debug = !Theater.debug;
		}
		
		if(Keybind.SLOT4.clicked()) {
			Camera.get().cancelAllEffects();
			Camera.get().addScreenEffect(new ScaleEffect(new Vector4f(1f, 1f, 1f, 1f), 0f, true, Tween.LINEAR));
			Camera.get().addScreenEffect(new TranslateEffect(new Vector4f(), 0f, true, Tween.LINEAR));
			Camera.get().addScreenEffect(new RotateEffect(new Vector4f(), 0f, true, Tween.LINEAR));
			Camera.get().addScreenEffect(new TintEffect(new Vector4f(), 0f, true, Tween.LINEAR));
		}
		if(Keybind.SLOT5.clicked()) {
			ScreenEffect rotation = new RotateEffect(new Vector4f(-360f, 0f, 0f, 0f), 2f, true, Tween.IN_OUT);
			rotation.setLoop(true);
			Camera.get().addScreenEffect(rotation);
			
			ScreenEffect scale = new ScaleEffect(new Vector4f(2f, 2f, 1f, 1f), 2f, true, Tween.IN_OUT);
			scale.setLoop(true);
			scale.setReverse(true);
			Camera.get().addScreenEffect(scale);
			
			ScreenEffect translate = new TranslateEffect(new Vector4f(10f, 10f, 0f, 0f), 2f, true, Tween.IN_OUT);
			translate.setLoop(true);
			translate.setReverse(true);
			Camera.get().addScreenEffect(translate);
		}
		if(Keybind.SLOT6.clicked()) {
			ScreenEffect colorFade = new TintEffect(new Vector4f(0f, 0f, 0f, 1f), 3f, true, Tween.OUT);
			colorFade.setLoop(true);
			colorFade.setReverse(true);
			Camera.get().addScreenEffect(colorFade);
		}
		if(Keybind.SLOT7.clicked()) {
			Camera.get().setFullscreen(!Camera.get().isFullscreen());
		}
		
		// Setup specific processing
		if(setup instanceof Stage) {
			if(Keybind.PAUSE.clicked()) {
				Theater.get().pause();
			} else if(Keybind.EXIT.clicked()) {
				setup.addUI(new GameMenu());
			}
		}
	}
	
	private void processKeyboard(UIContainer setup) {
		while(Keyboard.next()) {
			if(Keyboard.getEventKeyState()) {
				for(int i = 0; i<setup.getUI().size(); i++) {
					UIElement ui = setup.getUI().get(i);
					
					if(ui.getState() == UIElement.ENABLED) {
						ui.fireEvent(new KeyEvent(Keyboard.getEventKey(), 
								Keyboard.getEventCharacter(), 
								Keyboard.getKeyName(Keyboard.getEventKey())));
					}
				}
			}
		}
	}
	
	private void processKeybinds(UIContainer setup) {
		Keybind.update();
		for(Keybind k : Keybind.values()) {
			if(k.clicked()) {
				for(int i = 0; i<setup.getUI().size(); i++) {
					UIElement ui = setup.getUI().get(i);
					
					if(ui.getState() == UIElement.ENABLED) {
						ui.fireEvent(new KeybindEvent(k));
					}
				}
			}
		}
	}
	
	private void processMouse(UIContainer setup) {
		while(Mouse.next()) {
			if(Mouse.getEventButton() != -1) {
				if(Mouse.getEventButtonState()) {
					processMouseEvent(setup,
							new MouseEvent(MouseEvent.PRESSED, getMouseEventX(), getMouseEventY()));
					//System.out.println(Mouse.getEventButton() + " " + Mouse.getEventButtonState());
				} else {
					processMouseEvent(setup,
							new MouseEvent(MouseEvent.CLICKED, getMouseEventX(), getMouseEventY()));

					//System.out.println(Mouse.getEventX() + " " + (Camera.get().displayHeight - Mouse.getEventY()));
					//System.out.println(Mouse.getEventButton() + " " + Mouse.getEventButtonState());
				}
			} else if(Mouse.getDX() != 0 || Mouse.getDY() != 0) {
				MouseEvent me = new MouseEvent(MouseEvent.MOVED, getMouseEventX(), getMouseEventY());
				me.setDx(Mouse.getEventDX());
				me.setDy(-Mouse.getEventDY());
				processMouseEvent(setup, me);
				//System.out.println(Mouse.getEventDX() + " " + Mouse.getEventDY());
				
				if(Mouse.isButtonDown(0)) {
					MouseEvent med = new MouseEvent(MouseEvent.DRAGGED, getMouseEventX(), getMouseEventY());
					med.setDx(Mouse.getEventDX());
					med.setDy(-Mouse.getEventDY());
					processMouseEvent(setup, med);
				}
			}
		}
		
		if(Mouse.hasWheel() && Mouse.getDWheel() != 0) {
			// TODO Implement mouseWheelListener
			System.out.println(Mouse.getEventDWheel());
		}
	}
	
	private void processMouseEvent(UIContainer setup, MouseEvent e) {
		for(int i = 0; i<setup.getUI().size(); i++) {
			UIElement ui = setup.getUI().get(i);
			switch(e.getEvent()) {
			case MouseEvent.CLICKED:
			case MouseEvent.RELEASED:
			case MouseEvent.PRESSED:
				if(ui.getBounds().contains(e.getPosition())) {
					ui.fireEvent(e);
				}
				break;
			case MouseEvent.MOVED:
			case MouseEvent.DRAGGED:
				if(ui.getBounds().contains(e.getPosition()) || ui.getBounds().contains(e.getPrevPosition())) {
					ui.fireEvent(e);
				}
				break;
			}
		}
	}
	
	public static int getMouseX() {
		return Mouse.getX();
	}
	
	public static int getMouseY() {
		return Camera.get().displayHeight - Mouse.getY();
	}
	
	public static int getMouseEventX() {
		return Mouse.getEventX();
	}
	
	public static int getMouseEventY() {
		return Camera.get().displayHeight - Mouse.getEventY();
	}
	
}
