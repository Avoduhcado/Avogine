package core.entities.components.interactions;

import core.entities.Entity;
import core.entities.events.InteractEvent;
import core.scripts.Script;

public class ActivateInteraction extends Interaction {

	public ActivateInteraction(Entity entity, Script script) {
		super(entity, script);
		
		//entity.getBody().createFixture(createActivationRange());
	}

	@Override
	public void interact(InteractEvent e) {
		if(!interactorIsInteracting(e.getInteractor())) {
			//script.endReading();
			return;
		}
		
		if(!script.isBusyReading()) {
			script.startReading(e.getInteractor());
		} /*else {
			script.read();
		}*/
	}

	@Override
	public void interupt(InteractEvent e) {
		// TODO Auto-generated method stub
		// TODO Should be an addable interface, like when you create an Interaction you can add an Interrupt runnable
		
	}
	
	private boolean interactorIsInteracting(Entity interactor) {
		if((interactor == null || !interactor.hasBody()) || !entity.hasBody()) {
			return false;
		} else {
			return interactor.getBody().intersects(entity.getBody());
		}
	}
	
	/*private FixtureDef createActivationRange() {
		CircleShape bodyShape = new CircleShape();
		bodyShape.setRadius(100f / Stage.SCALE_FACTOR);

		FixtureDef boxFixture = new FixtureDef();
		boxFixture.density = 0f;
		boxFixture.shape = bodyShape;
		boxFixture.isSensor = true;
		boxFixture.filter.categoryBits = 0b0001;
		boxFixture.filter.maskBits = 0b0110;
		boxFixture.userData = new SensorData(entity, "Activator", SensorData.INTERACTION);
		
		return boxFixture;
	}*/
	
}
