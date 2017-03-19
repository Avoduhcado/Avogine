package core.generation.box2d;

import java.awt.Dimension;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.lwjgl.util.vector.Vector3f;

import core.generation.WorldGenerator;
import core.generation.util.Delaunay;
import core.generation.util.Kruskal;
import core.render.DrawUtils;
import core.utilities.MathUtils;

public class WorldGeneratorBox2D extends WorldGenerator {

	public static final float SCALE_FACTOR = 60f;
	private static final float timeStep = 1 / 60f;
	private static final int velocityIterations = 8;
	private static final int positionIterations = 3;
	
	protected List<RoomBox2D> rooms = new ArrayList<>();
	protected List<RoomBox2D> mainRooms = new ArrayList<RoomBox2D>();
	protected List<Line2D> hallways = new ArrayList<>();
	protected List<Arc2D> curvedHallways = new ArrayList<>();
	
	private World world;
	
	public WorldGeneratorBox2D(Long seed) {
		super(seed);
		
		world = new World(new Vec2(0, 0));
		RoomBox2D.resetRoomCount();
	}
	
	public WorldGeneratorBox2D(Long seed, World world) {
		super(seed);
		
		this.world = world;
		RoomBox2D.resetRoomCount();
	}

	@Override
	public void generate() {
		switch(state) {
		case FRESH:
			freshGeneration();
			break;
		case GENERATING:
			generateRooms();
			break;
		case SEPARATING:
			separateRooms();
			break;
		case CULLING:
			findMainRooms();
			break;
		case TRIANGULATING:
			triangulateMainRooms();
			break;
		case PATHING:
			limitTriangulation();
			break;
		case CONNECTING:
			buildHallways();
			break;
		case WING_DING_WILDCARD:
			break;
		case FINISHED:
			break;
		default:
			break;
		}
	}

	@Override
	public void draw() {
		if(behindTheScenes || state == GeneratorState.FINISHED) {
			rooms.stream().forEach(RoomBox2D::draw);
			
			if(hideHallwayLines) {
				return;
			}
			for(Line2D hall : hallways) {
				DrawUtils.setColor(new Vector3f(1f, 1f, 0f));
				DrawUtils.drawLine(hall.getX1(), hall.getY1(), hall.getX2(), hall.getY2());
			}
			/*for(Arc2D curve : curvedHallways) {
				DrawUtils.setColor(new Vector3f(1f, 1f, 0f));
				DrawUtils.drawLine(hall.getX1(), hall.getY1(), hall.getX2(), hall.getY2());
			}*/
		}
	}

	@Override
	protected void freshGeneration() {
		initRandoms();
		totalRoomsLeftToGenerate = globalRandom.nextInt(TOTAL_ROOM_UPPER_BOUND) + TOTAL_ROOM_LOWER_BOUND;
		
		System.out.println("Generating " + totalRoomsLeftToGenerate + " rooms.");
		
		state = GeneratorState.GENERATING;
	}
	
	@Override
	protected void generateRooms() {
		do {
			rooms.add(generateRoom());
			
			totalRoomsLeftToGenerate--;
			
			if(totalRoomsLeftToGenerate <= 0) {
				System.out.println("Finished spawning rooms.");
				
				state = GeneratorState.SEPARATING;
			}
		} while(!behindTheScenes && totalRoomsLeftToGenerate > 0);
	}
	
	private RoomBox2D generateRoom() {
		Vec2 roomCenter = getRandomPointInEllipse(WORLD_WIDTH, WORLD_HEIGHT);
		Vec2 roomSize = getRandomSize(ROOM_WIDTH, ROOM_HEIGHT);
		RoomBox2D rectRoom = new RoomBox2D(world, roomCenter, roomSize);

		return rectRoom;
	}

	@Override
	protected void separateRooms() {
		boolean separated = false;
		do {
			world.step(timeStep, velocityIterations, positionIterations);
			
			separated = true;
			for(RoomBox2D room : rooms) {
				if(room.getBody().isAwake()) {
					separated = false;
					room.updateRoomToGrid();
				}
			}
		} while(!behindTheScenes && !separated);
		
		if(separated) {
			rooms.stream().forEach(RoomBox2D::destroyBody);
			System.out.println("Rooms finished separating, box2D bodies removed.");
				
			state = GeneratorState.CULLING;
		}
	}
	
	@Override
	protected void findMainRooms() {
		double roomWidths = 0;
		double roomHeights = 0;
		for(RoomBox2D room : rooms) {
			roomWidths += room.getWidth();
			roomHeights += room.getHeight();
		}
		Dimension meanSize = new Dimension((int) ((roomWidths / rooms.size()) * MEAN_SIZE_LIMIT), (int) ((roomHeights / rooms.size()) * MEAN_SIZE_LIMIT));
		
		rooms.stream()
			.filter(e -> e.getWidth() >= meanSize.getWidth() && e.getHeight() >= meanSize.getHeight())
			.forEach(e -> e.setActive(true));

		// Include some extra rooms cause why not
		int randomMainRooms = globalRandom.nextInt(BONUS_ROOMS) + BONUS_ROOMS_MIN;
		int randomRoomSelect;
		for(int i = 0; i < randomMainRooms; i++) {
			do {
				randomRoomSelect = effectsRandom.nextInt(rooms.size());
			} while(rooms.get(randomRoomSelect).isActive());
			rooms.get(randomRoomSelect).setActive(true);
		}
		
		System.out.println("Main rooms activated. Mean room size: " + meanSize.toString());
		
		state = GeneratorState.TRIANGULATING;
	}

	@Override
	protected void triangulateMainRooms() {
		mainRooms.stream().forEach(e -> e.clearEdges());
		
		for(RoomBox2D room : rooms) {
			if(room.isActive()) {
				mainRooms.add(room);
			}
		}

		Delaunay.triangulate(mainRooms);
		
		System.out.println("Triangulated. Total rooms: " + mainRooms.size());
		
		state = GeneratorState.PATHING;
	}

	@Override
	protected void limitTriangulation() {
		Kruskal.generateMinimumSpanningTree(mainRooms);
		
		for(RoomBox2D room : mainRooms) {
			if(roomRandom.nextDouble() < 0.1) {
				room.setEnabledEdge(room.getEdgesAsList().get(roomRandom.nextInt(room.getEdges().size())), true);
			}
		}
		
		System.out.println("Spanning Tree formed.");
		
		state = GeneratorState.CONNECTING;
	}

	@Override
	protected void buildHallways() {
		convertEdgesToHallways();
		
		rooms.addAll(constructHallwayRooms());
		
		System.out.println("Hallways built.");
		
		state = GeneratorState.FINISHED;
	}
	
	private void convertEdgesToHallways() {
		for(RoomBox2D room : mainRooms) {
			for(RoomBox2D e : room.getEnabledEdges()) {
				final Rectangle2D hallRect = room.createIntersection(e);
				if(room.intersects(e.getX(), room.getY(), e.getWidth(), e.getHeight())) {
					// Vertical hall
					hallways.add(new Line2D.Double(hallRect.getCenterX(), hallRect.getY(), hallRect.getCenterX(), hallRect.getMaxY()));
				} else if(room.intersects(room.getX(), e.getY(), e.getWidth(), e.getHeight())) {
					// Horizontal hall
					hallways.add(new Line2D.Double(hallRect.getX(), hallRect.getCenterY(), hallRect.getMaxX(), hallRect.getCenterY()));
				} else {
					/*if(Math.random() > 0.5) {
						curvedHallways.add(new Arc2D.Double(room.union(e), 0, 360, 0));
					} else {*/
						hallways.add(new Line2D.Double(room.getCenterX() > e.getCenterX() ? room.getX() : room.getMaxX(),
								room.getCenterY(), e.getCenterX(),
								room.getCenterY() < e.getCenterY() ? e.getY() : e.getMaxY()));
					//}
					/*
					
					// Corner hall
					if(room.outcode(e.getCenterX(), room.getCenterY()) == Rectangle.OUT_LEFT) {
						hallways.add(new Line2D.Double(e.getCenterX(), room.getCenterY(), room.getX(), room.getCenterY()));
					} else {
						hallways.add(new Line2D.Double(room.getMaxX(), room.getCenterY(), e.getCenterX(), room.getCenterY()));
					}
					if(room.outcode(room.getCenterX(), e.getCenterY()) == Rectangle.OUT_TOP) {
						hallways.add(new Line2D.Double(e.getCenterX(), e.getMaxY(), e.getCenterX(), room.getCenterY()));
					} else {
						hallways.add(new Line2D.Double(e.getCenterX(), room.getCenterY(), e.getCenterX(), e.getY()));
					}*/
				}
			}
		}
	}
	
	private ArrayList<RoomBox2D> constructHallwayRooms() {
		ArrayList<RoomBox2D> hallwayRooms = new ArrayList<>();
		
		for(Line2D hall : hallways) {
			lineLerp(hall, hallwayRooms);
		}
		/*for(Arc2D curve : curvedHallways) {
			arcLerp(curve, hallwayRooms);
		}*/
		
		return hallwayRooms;
	}
	
	private void lineLerp(Line2D line, ArrayList<RoomBox2D> hallwayRooms) {
		double increment = 1 / (MathUtils.distance(line.getX1(), line.getY1(), line.getX2(), line.getY2()) / TILE_SIZE);
		double xLerp;
		double yLerp;
		for(double t = 0; t<1; t += increment) {
			xLerp = MathUtils.lerp(line.getX1(), line.getX2(), t);
			yLerp = MathUtils.lerp(line.getY1(), line.getY2(), t);
			xLerp = roundM(Double.isInfinite(xLerp) ? line.getX1() : xLerp, TILE_SIZE);
			yLerp = roundM(Double.isInfinite(yLerp) ? line.getY1() : yLerp, TILE_SIZE);
			addHallwayRoom(hallwayRooms, new RoomBox2D(new Vec2((float) xLerp, (float) yLerp), new Vec2(TILE_SIZE, TILE_SIZE)));
			
			addHallwayRoom(hallwayRooms, new RoomBox2D(new Vec2((float) xLerp + TILE_SIZE, (float) yLerp), new Vec2(TILE_SIZE, TILE_SIZE)));
			addHallwayRoom(hallwayRooms, new RoomBox2D(new Vec2((float) xLerp - TILE_SIZE, (float) yLerp), new Vec2(TILE_SIZE, TILE_SIZE)));
			addHallwayRoom(hallwayRooms, new RoomBox2D(new Vec2((float) xLerp, (float) yLerp + TILE_SIZE), new Vec2(TILE_SIZE, TILE_SIZE)));
			addHallwayRoom(hallwayRooms, new RoomBox2D(new Vec2((float) xLerp, (float) yLerp - TILE_SIZE), new Vec2(TILE_SIZE, TILE_SIZE)));
		}
	}
	
	private void arcLerp(Arc2D arc, ArrayList<RoomBox2D> hallwayRooms) {
		//Arc2D arc = new Arc2D.Double(new Rectangle2D.Double(100, 100, 50, 50), 0, 45, 0);
		List<Point2D> arcPoints = new ArrayList<Point2D>();
		for(int i = (int) arc.getAngleStart(); i <= arc.getAngleStart() + arc.getAngleExtent(); i += 10) {
			arcPoints.add(new Point2D.Double(arc.getX() + (Math.sin(Math.toRadians(i + 90)) * arc.getWidth()),
					arc.getY() + (Math.cos(Math.toRadians(i + 90)) * arc.getHeight())));
		}

		List<Line2D> arcLines = new ArrayList<Line2D>();
		for(int i = 0; i < arcPoints.size() - 1; i++) {
			Line2D line2 = new Line2D.Double(arcPoints.get(i), arcPoints.get(i + 1));
			arcLines.add(line2);
			//System.out.println(line2.getX1() + " " + line2.getY1() + " " + line2.getX2() + " " + line2.getY2());
		}

		for(Line2D line : arcLines) {
			lineLerp(line, hallwayRooms);
		}
	}
	
	private void addHallwayRoom(ArrayList<RoomBox2D> hallwayRooms, RoomBox2D hallRoom) {
		hallRoom.setActive(true);
		hallRoom.updateRoomToGrid();
		if(!rooms.stream().anyMatch(e -> e.contains(hallRoom)) && !hallwayRooms.stream().anyMatch(e -> e.contains(hallRoom))) {
			hallwayRooms.add(hallRoom);
		} else {
			rooms.stream().filter(e -> e.contains(hallRoom)).findFirst().ifPresent(e -> e.setActive(true));
		}
	}
	
	public List<RoomBox2D> getRooms() {
		return rooms;
	}

	private Vec2 getRandomPointInCircle(double radius) {
		return getRandomPointInEllipse(radius, radius);
	}
	
	private Vec2 getRandomPointInEllipse(double ellipseWidth, double ellipseHeight) {
		double t = 2 * Math.PI * globalRandom.nextDouble();
		double u = globalRandom.nextDouble() + globalRandom.nextDouble();
		double r = 0;
		if(u > 1) {
			r = 2-u;
		} else {
			r = u;
		}
		
		return new Vec2(roundM(ellipseWidth * r * Math.cos(t), TILE_SIZE),
				roundM(ellipseHeight * r * Math.sin(t), TILE_SIZE));
	}
	
	private Vec2 getRandomSize(double width, double height) {
		return new Vec2(roundM(globalRandom.nextDouble() * width, TILE_SIZE) + (TILE_SIZE * 3),
				roundM(globalRandom.nextDouble() * height, TILE_SIZE) + (TILE_SIZE * 3));
	}

}
