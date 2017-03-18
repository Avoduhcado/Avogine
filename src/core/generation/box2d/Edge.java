package core.generation.box2d;

public class Edge implements Comparable<Edge> {

	private RoomBox2D roomA, roomB;
	private final int weight;
	
	public Edge(RoomBox2D va, RoomBox2D vb) {
		setRoomA(va);
		setRoomB(vb);
		weight = (int) Math.sqrt(Math.pow((va.x - vb.x), 2) + Math.pow((va.y - vb.y), 2));
	}
	
	@Override
	public int compareTo(Edge anotherEdge) {
		if(this.weight < anotherEdge.weight) {
            return -1;
        } else if(this.weight == anotherEdge.weight) {
            return 0;
        } else {
            return 1;
        }
	}

	public RoomBox2D getRoomA() {
		return roomA;
	}

	public void setRoomA(RoomBox2D vertexA) {
		this.roomA = vertexA;
	}

	public RoomBox2D getRoomB() {
		return roomB;
	}

	public void setRoomB(RoomBox2D vertexB) {
		this.roomB = vertexB;
	}
	
	public int getWeight() {
		return weight;
	}
	
}

