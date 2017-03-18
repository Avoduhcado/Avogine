package core.generation;

import java.util.List;
import java.util.Random;

import core.generation.box2d.RoomBox2D;

public abstract class WorldGenerator {
	
	public enum GeneratorState {
		FRESH("Powering on"),
		GENERATING("Spawning rooms"),
		SEPARATING("Separating rooms"),
		CULLING("Selecting main rooms"),
		TRIANGULATING("Triangulating main rooms"),
		PATHING("KRUSKAL"),
		CONNECTING("Building hallways"),
		WING_DING_WILDCARD("wew boy"),
		FINISHED("Finished generation");
		
		String generationText;
		
		GeneratorState(String text) {
			this.generationText = text;
		}

		public String getText() {
			return generationText;
		}
	}

	/** Debug commands */
	public static boolean hideInactiveRooms;
	public static boolean hideConnections;
	public static boolean hideHallwayLines;
	public static boolean showGrid;
	public static boolean showRoomNumber;
	public static boolean behindTheScenes;
	
	public static final int TILE_SIZE = 4;
	protected static final int WORLD_WIDTH = 500;
	protected static final int WORLD_HEIGHT = 40;
	protected static final int ROOM_WIDTH = 48;
	protected static final int ROOM_HEIGHT = 20;
	protected static final double MEAN_SIZE_LIMIT = 1.25;
	protected static final int TOTAL_ROOM_LOWER_BOUND = 150;
	protected static final int TOTAL_ROOM_UPPER_BOUND = 50;
	protected static final int BONUS_ROOMS = 2;
	protected static final int BONUS_ROOMS_MIN = 2;
	
	protected final Long seed;
	
	protected Random globalRandom;
	protected Random roomRandom;
	protected Random effectsRandom;
	
	protected int totalRoomsLeftToGenerate;
	
	protected GeneratorState state = GeneratorState.FRESH;
	
	public WorldGenerator(Long seed) {
		if(seed == null) {
			this.seed = System.currentTimeMillis();
		} else {
			this.seed = seed;
		}
	}
	
	protected void initRandoms() {
		globalRandom = new Random(seed);
		roomRandom = new Random(globalRandom.nextLong());
		effectsRandom = new Random(roomRandom.nextLong());
	}

	public GeneratorState getState() {
		return state;
	}
	
	public abstract void generate();
	public abstract void draw();

	protected abstract void freshGeneration();
	protected abstract void generateRooms();
	protected abstract void separateRooms();
	protected abstract void findMainRooms();
	protected abstract void triangulateMainRooms();
	protected abstract void limitTriangulation();
	protected abstract void buildHallways();
	
	public abstract List<RoomBox2D> getRooms();
	
	public static int roundM(double n, int m) {
		return (int) (Math.floor(((n + m - 1) / m)) * m);
	}
	
	public static int clamp(int value, int min, int max) {
		if(value < min) {
			return min;
		} else if(value > max) {
			return max;
		}
		return value;
	}

}
