package core.setups.scenes.components;

import core.setups.scenes.Scene;

public abstract class SceneComponent {

	protected Scene scene;

	public SceneComponent(Scene scene) {
		this.scene = scene;
	}
	
	public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}
	
}
