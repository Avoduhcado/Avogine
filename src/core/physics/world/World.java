package core.physics.world;

public class World {
	
	boolean gravity;
	boolean airResist;
	
	public World() {
		gravity = false;
		airResist = false;
	}
	
	public void enableGravity() {
		gravity = true;
	}
	
	public void enableAirResist() {
		airResist = true;
	}
	
	public void disableGravity() {
		
	}
	
}
