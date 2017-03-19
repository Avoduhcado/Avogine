package core.setups;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.dynamics.Body;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import core.Camera;
import core.entities.controllers.PlayerController;
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
		loadScene(new boolean[0][0]);
		
		scene.fireEvent(new InteractEvent(InteractEvent.INTERACT, null));
	}
	
	public void loadScene(boolean[][] gridArray) {
		// TODO Load this properly
		scene = new Scene();
		scene.setGrid(gridArray);
		
		final Vector3f spawn = scene.getOriginRoomCoords(gridArray);
	
		scene.getEntities().stream()
			.filter(e -> e.hasController() && e.getController() instanceof PlayerController)
			.map(e -> e.getBody())
			.forEach(e -> e.setPosition(spawn));
	}
	
	@Override
	public void update() {
		if(pause) {
			return;
		}
		
		scene.getWorld().step(1 / 60f, 8, 3);
	}

	@Override
	public void draw() {
		scene.draw();
	}
	
	@Override
	public void drawUI() {
		super.drawUI();
		
		for(Body body = scene.getWorld().getBodyList(); body.getNext() != null; body = body.getNext()) {
			switch(body.getFixtureList().getShape().getType()) {
			case CHAIN:
				break;
			case CIRCLE:
				DrawUtils.setColor(new Vector3f(1, 0, 1));
				CircleShape circle = (CircleShape) body.getFixtureList().getShape();
				DrawUtils.drawBox2DCircle(body, circle, 1);
				break;
			case EDGE:
				DrawUtils.setColor(new Vector3f(1, 0, 0));
				EdgeShape edge = (EdgeShape) body.getFixtureList().getShape();
				DrawUtils.drawBox2DEdge(body, edge, 1);
				break;
			case POLYGON:
				break;
			default:
				break;
			}
		}
		
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
