package core.setups;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import core.Camera;
import core.entities.Entity;
import core.entities.bodies.PlainBody;
import core.entities.bodies.RigidBody;
import core.entities.controllers.PlayerController;
import core.entities.events.BodyEvent;
import core.entities.renders.PlainRender;
import core.physics.world.World;
import core.render.DrawUtils;
import core.ui.event.KeybindEvent;
import core.ui.event.KeybindListener;
import core.ui.overlays.GameMenu;
import core.utilities.keyboard.Keybind;
import core.utilities.text.Text;

public class Stage extends GameSetup {
			
	private boolean pause;
	
	private World world;
	
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	
	private KeybindListener keybindListener = new StageKeybindListener();
	
	public Stage() {
		
		world = new World();
		Entity ent = new Entity();
		ent.setRender(new PlainRender(ent, "AGDG Logo"));
		ent.setBody(new RigidBody(ent, new Vector3f(-432f, -32f, 0f), 32, 32));
		entities.add(ent);
		world.addBody(ent.getBody());
		
		ent = new Entity();
		ent.setRender(new PlainRender(ent, "AGDG Logo"));
		ent.setBody(new RigidBody(ent, new Vector3f(-416f, -16f, 0f), 32, 32));
		entities.add(ent);
		ent.fireEvent(new BodyEvent(BodyEvent.IMPULSE, new Vector3f(200,-200,0)));
		world.addBody(ent.getBody());
		
		ent = new Entity();
		ent.setRender(new PlainRender(ent, "AGDG Logo"));
		ent.setBody(new RigidBody(ent, new Vector3f(-400f, 0f, 0f), 32, 32));
		entities.add(ent);
		world.addBody(ent.getBody());
		
		ent = new Entity();
		ent.setBody(new PlainBody(ent, new Vector3f(0f, 0f, 0f), 32, 32));
		entities.add(ent);
		
		world.setGravity(100);
		
		Camera.get().setFocus(ent.getBody());
		
		Camera.get().setClear(new Vector4f(1f, 1f, 0f, 1f));
	}
	
	@Override
	public void update() {
		if(pause) {
			return;
		}
		world.update();
		entities.stream().sorted().forEach(Entity::update);
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
				.filter(ent -> ent.controllable() && ent.getController() instanceof PlayerController)
				.forEach(ent -> ent.getController().control(e));
				break;
			}
		}
	}
	
}
