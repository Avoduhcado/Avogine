package core.setups;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import core.Camera;
import core.Theater;
import core.entities.Entity;
import core.entities.bodies.PlainBody;
import core.entities.controllers.PlayerController;
import core.generation.WorldGenerator;
import core.generation.WorldGenerator.GeneratorState;
import core.generation.box2d.RoomBox2D;
import core.generation.box2d.WorldGeneratorBox2D;
import core.render.DrawUtils;
import core.setups.utils.EntityContainer;
import core.ui.Button;
import core.ui.CheckBox;
import core.ui.event.KeybindEvent;
import core.ui.event.KeybindListener;
import core.utilities.text.Text;
import core.utilities.text.TextModifier;
import core.utilities.text.TextModifier.TextModValue;

public class TestWorldGenerator extends GameSetup implements EntityContainer {

	private WorldGenerator worldGen;
	private Entity player;
	private Rectangle2D worldGrid;
		
	private KeybindListener keybindListener = new StageKeybindListener();
	
	public TestWorldGenerator() {
		player = new Entity(this);
		player.addBody(new PlainBody(player, new Vector3f(), new Vector3f(32, 32, 0)));
		player.addController(new PlayerController(player, 2.5f));
		Camera.get().setFocus(player.getBody());
		
		this.worldGen = new WorldGeneratorBox2D(null);
		
		initUI();
	}
	
	private void initUI() {
		Camera.get().setClear(new Vector4f(0f, 0f, 0f, 1f));
		
		Button regenWorld = new Button("Regenerate World");
		regenWorld.setPosition(15, 15);
		regenWorld.addActionListener(e -> this.worldGen = new WorldGeneratorBox2D(null));
		addUI(regenWorld);
		
		CheckBox hideInactive = new CheckBox("Hide Inactive Rooms");
		hideInactive.setPosition(15, regenWorld.getBounds().getMaxY());
		hideInactive.addActionListener(e -> WorldGenerator.hideInactiveRooms = !WorldGenerator.hideInactiveRooms);
		addUI(hideInactive);

		CheckBox hideConnections = new CheckBox("Hide Connections");
		hideConnections.setPosition(15, hideInactive.getBounds().getMaxY());
		hideConnections.addActionListener(e -> WorldGenerator.hideConnections = !WorldGenerator.hideConnections);
		addUI(hideConnections);

		CheckBox hideHalls = new CheckBox("Hide Hall Lines");
		hideHalls.setPosition(15, hideConnections.getBounds().getMaxY());
		hideHalls.addActionListener(e -> WorldGenerator.hideHallwayLines = !WorldGenerator.hideHallwayLines);
		addUI(hideHalls);

		CheckBox showNumbers = new CheckBox("Show IDs");
		showNumbers.setPosition(15, hideHalls.getBounds().getMaxY());
		showNumbers.addActionListener(e -> WorldGenerator.showRoomNumber = !WorldGenerator.showRoomNumber);
		addUI(showNumbers);

		CheckBox behindTheScenes = new CheckBox("Show Generation");
		behindTheScenes.setPosition(15, showNumbers.getBounds().getMaxY());
		behindTheScenes.addActionListener(e -> WorldGenerator.behindTheScenes = !WorldGenerator.behindTheScenes);
		addUI(behindTheScenes);
		
		Button convertToRealRooms = new Button("Convert to real rooms");
		convertToRealRooms.setPosition(15, behindTheScenes.getBounds().getMaxY());
		convertToRealRooms.addActionListener(e -> convertToStageWorld());
		addUI(convertToRealRooms);
	}
	
	@Override
	public void update() {
		worldGen.generate();
	}

	@Override
	public void draw() {
		if(worldGrid != null) {
			DrawUtils.setColor(new Vector3f(1, 1, 1));
			DrawUtils.drawRect(worldGrid);
			for(int x = (int) worldGrid.getX(); x < worldGrid.getMaxX(); x += WorldGenerator.TILE_SIZE) {
				DrawUtils.setColor(new Vector3f(1, 1, 1));
				DrawUtils.drawLine(x, worldGrid.getY(), x, worldGrid.getMaxY());
			}
			for(int y = (int) worldGrid.getY(); y < worldGrid.getMaxY(); y += WorldGenerator.TILE_SIZE) {
				DrawUtils.setColor(new Vector3f(1, 1, 1));
				DrawUtils.drawLine(worldGrid.getX(), y, worldGrid.getMaxX(), y);
			}
		}
		worldGen.draw();
		player.draw();
	}
	
	@Override
	public void drawUI() {
		super.drawUI();
		
		Text.getFont("DEBUG").drawString(worldGen.getState().getText(), 15, Camera.get().getDisplayHeight(0.5f), TextModifier.compile(TextModValue.SIZE + "=2"));
	}

	private void convertToStageWorld() {
		if(worldGen.getState() != GeneratorState.FINISHED) {
			return;
		}
		
		int lowX = worldGen.getRooms().stream().filter(e -> e.isActive())
			.min((RoomBox2D o1, RoomBox2D o2) -> (int) (o1.getX() - o2.getX())).get().x;
		int farX = (int) worldGen.getRooms().stream().filter(e -> e.isActive())
				.max((RoomBox2D o1, RoomBox2D o2) -> (int) (o1.getMaxX() - o2.getMaxX())).get().getMaxX();
		int lowY = worldGen.getRooms().stream().filter(e -> e.isActive())
				.min((RoomBox2D o1, RoomBox2D o2) -> (int) (o1.getY() - o2.getY())).get().y;
		int farY = (int) worldGen.getRooms().stream().filter(e -> e.isActive())
				.max((RoomBox2D o1, RoomBox2D o2) -> (int) (o1.getMaxY() - o2.getMaxY())).get().getMaxY();
		
		worldGrid = new Rectangle2D.Double();
		worldGrid.setFrameFromDiagonal(lowX, lowY, farX, farY);
		System.out.println("World grid spawned with size: " + worldGrid.toString());
		
		boolean[][] gridArray = new boolean[(int) (worldGrid.getWidth() / WorldGenerator.TILE_SIZE)][(int) (worldGrid.getHeight() / WorldGenerator.TILE_SIZE)];
		
		for(RoomBox2D room : worldGen.getRooms()) {
			if(room.isActive()) {
				for(int x = (int) ((room.getX() - worldGrid.getX()) / WorldGenerator.TILE_SIZE); 
						x < (room.getMaxX() - worldGrid.getX()) / WorldGenerator.TILE_SIZE; x++) {
					for(int y = (int) ((room.getY() - worldGrid.getY()) / WorldGenerator.TILE_SIZE);
							y < (room.getMaxY() - worldGrid.getY()) / WorldGenerator.TILE_SIZE; y++) {
						gridArray[x][y] = true;
					}
				}
			}
		}
		Stage stage = new Stage();
		stage.loadScene(gridArray);
		
		Theater.get().setSetup(stage);
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
			if(e.isConsumed()) {
				return;
			}
			
			switch(e.getKeybind()) {
			default:
				player.getController().keybindClicked(e);
				break;
			}
		}
	}

	@Override
	public void drawEntities() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addEntity(Entity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Entity getEntity(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Entity> getEntities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeEntity(Entity entity) {
		// TODO Auto-generated method stub
		return false;
	}

}
