package core.setups;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import core.Camera;
import core.entities.Entity;
import core.entities.bodies.PlainBody;
import core.entities.controllers.PlayerController;
import core.entities.renders.PlainRender;
import core.render.DrawUtils;
import core.ui.event.KeybindEvent;
import core.ui.event.KeybindListener;
import core.ui.overlays.GameMenu;
import core.utilities.keyboard.Keybind;
import core.utilities.text.Text;

public class Stage extends GameSetup {
			
	private boolean pause;
	
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	
	private KeybindListener keybindListener = new StageKeybindListener();
	
	public Stage() {
		Entity ent = new Entity();
		ent.setRender(new PlainRender(ent, "AGDG Logo"));
		ent.setBody(new PlainBody(ent, new Vector3f(-16f, -16f, 0f), new Vector3f(256f, 256f, 0f)));
		//ent.addComponent(AutorunInteraction.class, new AutorunInteraction(ent, new Script(ent, "Butts")));
		entities.add(ent);
		
		ent = new Entity();
		ent.setRender(new PlainRender(ent, "AGDG Logo"));
		ent.setBody(new PlainBody(ent, new Vector3f(0f, 0f, 0f), new Vector3f(32f, 32f, 0f)));
		ent.setController(new PlayerController(ent));
		//ent.addComponent(AutorunInteraction.class, new AutorunInteraction(ent, new Script(ent, "Butts")));
		entities.add(ent);
		
		Camera.get().setFocus(ent.getBody());
		
		Camera.get().setClear(new Vector4f(101f / 255f, 156f / 255f, 239f / 255f, 1f));
	}
	
	@Override
	public void update() {
		if(pause) {
			return;
		}
	}

	@Override
	public void draw() {
		entities.stream().sorted().forEach(Entity::draw);
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
			default:
				entities.stream()
				.filter(ent -> ent.controllable())
				.forEach(ent -> ent.getController().keybindClicked(e));
				break;
			}
		}
	}
	
}
