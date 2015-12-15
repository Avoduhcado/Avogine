package core.entities;

import java.io.Serializable;

import core.entities.controllers.Controller;
import core.entities.physics.Body;
import core.entities.render.Render;

public class Entity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Body body;
	private Render render;
	private Controller controller;
		
	public Entity() {
		
	}
	
	public void update() {
		if(controller != null) {
			controller.control();
		}
		
		if(body != null) {
			body.update();
		}
	}
	
	public void draw() {
		if(render != null) {
			render.draw(body.getPosition().x, body.getPosition().y);
		}
	}
	
	public void setBody(Body body) {
		this.body = body;
	}
	
	public void setRender(Render render) {
		this.render = render;
	}
	
	public void setController(Controller controller) {
		this.controller = controller;
	}
	
}
