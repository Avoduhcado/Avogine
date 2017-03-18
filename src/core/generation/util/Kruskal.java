package core.generation.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import core.generation.box2d.RoomBox2D;

public class Kruskal {

	private static Map<RoomBox2D, RoomBox2D> parent;
	private static Map<RoomBox2D, Integer> rank;

	public static void generateMinimumSpanningTree(List<RoomBox2D> rooms) {
		parent = new HashMap<>();
		rank = new HashMap<>();
		
		GraphBox2D graph = new GraphBox2D(rooms);
		TreeSet<Edge> mst = new TreeSet<Edge>();
		// Populate disjointed set
		for(RoomBox2D room : graph.vertices) {
			makeSet(room);
		}
		
		// Add all edges that don't cause a cycle
		for(Edge e : graph.edges) {
			RoomBox2D room1 = find(e.getRoomA());
			RoomBox2D room2 = find(e.getRoomB());
			if(room1 != room2) {
				mst.add(e);
				union(room1, room2);
			}
		}
		
		// Remove edges and add in only Minimum Spanning Tree
		rooms.stream().forEach(RoomBox2D::disableEdges);
		for(Edge edge : mst) {
			rooms.get(rooms.indexOf(edge.getRoomA())).setEnabledEdge(edge.roomB, true);
		}
		
		System.out.println("Kruskal: Tree size: " + mst.size() + " Room size: " + rooms.size());
	}
	
	private static void makeSet(RoomBox2D room) {
		parent.put(room, room);
		rank.put(room, 0);
	}

	private static RoomBox2D find(RoomBox2D room) {
		if(parent.get(room) == room) {
			return room;
		} else {
			return find(parent.get(room));
		}
	}

	private static void union(RoomBox2D room1, RoomBox2D room2) {
		if(rank.get(room1) > rank.get(room2)) {
			parent.put(room2, room1);
		} else if(rank.get(room2) > rank.get(room1)) {
			parent.put(room1, room2);
		} else {
			parent.put(room1, room2);
			rank.put(room2, rank.get(room2) + 1);
		}
	}
	
	private static class GraphBox2D {
		private Set<RoomBox2D> vertices = new HashSet<>();
		private Set<Edge> edges = new TreeSet<>();
		
		public GraphBox2D(List<RoomBox2D> rooms) {
			for(RoomBox2D room : rooms) {
				vertices.add(room);
				for(RoomBox2D edge : room.getEdges()) {
					edges.add(new Edge(room, edge));
				}
			}
		}
	}
	
	private static class Edge implements Comparable<Edge> {

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
		
	}
	
}
