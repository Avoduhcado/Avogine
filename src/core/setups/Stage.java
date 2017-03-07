package core.setups;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import core.Camera;
import core.entities.events.InteractEvent;
import core.render.DrawUtils;
import core.setups.scenes.Scene;
import core.ui.event.KeybindEvent;
import core.ui.event.KeybindListener;
import core.ui.event.MouseWheelEvent;
import core.ui.event.MouseWheelListener;
import core.ui.overlays.GameMenu;
import core.utilities.keyboard.Keybind;
import core.utilities.text.Text;

public class Stage extends GameSetup {
			
	private boolean pause;
	
	private Scene scene;
	
	private KeybindListener keybindListener = new StageKeybindListener();
	private MouseWheelListener mouseWheelListener = new StageMouseWheelListener();
	
	public Stage() {
		// CORNFLOWER BLUE
		Camera.get().setClear(new Vector4f(101f / 255f, 156f / 255f, 239f / 255f, 1f));
		
		initScene();
	}
	
	private void initScene() {
		scene = new Scene();
		
		scene.fireEvent(new InteractEvent(InteractEvent.INTERACT, null));
	}
	
	@Override
	public void update() {
		if(pause) {
			return;
		}
	}

	@Override
	public void draw() {
		scene.draw();
	}
	
	@Override
	public void drawUI() {
		super.drawUI();
		
		if(pause) {
			DrawUtils.fillScreen(0, 0, 0, 0.65f);
			Text.drawString("Paused", Camera.get().getDisplayWidth(0.5f), Camera.get().getDisplayHeight(0.5f));
		}
	}

	@Override
	protected void processKeybindEvent(KeybindEvent e) {
		super.processKeybindEvent(e);
		
		if(keybindListener != null) {
			keybindListener.keybindClicked(e);
		}
	}

	@Override
	protected void processMouseWheelEvent(MouseWheelEvent e) {
		if(mouseWheelListener != null) {
			mouseWheelListener.wheelScrolled(e);
		}
	}
	
	private class StageKeybindListener implements KeybindListener {
		@Override
		public void keybindClicked(KeybindEvent e) {
			if(e.isConsumed() || (pause && e.getKeybind() != Keybind.PAUSE)) {
				return;
			}
			
			switch(e.getKeybind()) {
			case PAUSE:
				e.consume();
				if(e.getKeybind().clicked()) {
					pause = !pause;
				}
				break;
			case EXIT:
				e.consume();
				if(e.getKeybind().clicked()) {
					GameMenu gameMenu = new GameMenu();
					addUI(gameMenu);
					setFocus(gameMenu);
				}
				break;
			/*case CONFIRM:
				e.consume();
				// TODO
				// Filter to only find events that the player overlaps and build a list you can cycle through for things to activate
				InteractEvent ie = new InteractEvent(InteractEvent.INTERACT, null);
				scene.getEntities().stream()
					.map(ent -> ent.getComponent(Interaction.class))
					.filter(Objects::nonNull)
					.findFirst()
					.ifPresent(ent -> ent.interact(ie));*/
			default:
				// TODO Fix this yo, it's slop
				scene.getEntities().stream()
					.filter(ent -> ent.hasController())
					.forEach(ent -> ent.getController().keybindClicked(e));
				break;
			}
		}
	}
	
	private class StageMouseWheelListener implements MouseWheelListener {
		@Override
		public void wheelScrolled(MouseWheelEvent e) {
			Camera.get().setScale(Vector3f.add(Camera.get().getScale(), new Vector3f(e.getScroll() / 1000f, e.getScroll() / 1000f, 0f), null));
		}
	}
	
}
