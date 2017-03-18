package core.generation.util;

import java.awt.Point;
import java.util.List;

import core.generation.box2d.RoomBox2D;

public class Delaunay {

	public static void triangulate(List<RoomBox2D> mainRooms) {
		for(int i = 0; i < mainRooms.size(); i++) {
            for(int j = i+1; j < mainRooms.size(); j++) {
                for(int k = j+1; k < mainRooms.size(); k++) {
                    boolean isTriangle = true;
                    for(int a = 0; a < mainRooms.size(); a++) {
                        if(a == i || a == j || a == k) {
                        	continue;
                        }
                        if(Circle.inside(mainRooms.get(a).getCenter(), mainRooms.get(i).getCenter(), mainRooms.get(j).getCenter(), mainRooms.get(k).getCenter())) {
                           isTriangle = false;
                           break;
                        }
                    }
                    if (isTriangle) {
                    	mainRooms.get(i).makeEdge(mainRooms.get(j));
                    	mainRooms.get(j).makeEdge(mainRooms.get(k));
                    	mainRooms.get(k).makeEdge(mainRooms.get(i));
                    }
                }
            }
        }
	}
	
	/**
	 * Utility class for defining Circles used in Delaunay Triangulation.
	 */
	private static class Circle {

		private static final double TOL = 0.0000001;
		
		private final Point center;
		private final double radius;
		
		public Circle(Point center, double radius) {
			this.center = center;
			this.radius = radius;
		}

		public static boolean inside(Point toTest, Point a, Point b, Point c) {
			Circle circumcircle = circleFromPoints(a, b, c);

			return circumcircle.contains(toTest);
		}

		public static Circle circleFromPoints(final Point p1, final Point p2, final Point p3) {
			final double offset = Math.pow(p2.x, 2) + Math.pow(p2.y, 2);
			final double bc = (Math.pow(p1.x, 2) + Math.pow(p1.y, 2) - offset) / 2.0;
			final double cd = (offset - Math.pow(p3.x, 2) - Math.pow(p3.y, 2)) / 2.0;
			double det = (p1.x - p2.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p2.y);

			if(Math.abs(det) < TOL) {
				det += TOL;
				// TODO: Solve this?
				//throw new IllegalArgumentException("Yeah, lazy.");
			}

			final double idet = 1 / det;

			final double centerx = (bc * (p2.y - p3.y) - cd * (p1.y - p2.y)) * idet;
			final double centery = (cd * (p1.x - p2.x) - bc * (p2.x - p3.x)) * idet;
			final double radius = Math.sqrt(Math.pow(p2.x - centerx, 2) + Math.pow(p2.y-centery, 2));

			return new Circle(new Point((int) centerx, (int) centery), radius);
		}
		
		public boolean contains(Point point) {
			return Math.pow(point.x - center.x, 2) + Math.pow(point.y - center.y, 2) <= Math.pow(radius, 2);
		}

		@Override
		public String toString() {
			return new StringBuilder().append("Center= ").append(center).append(", r=").append(radius).toString();
		}
	}
	
}
