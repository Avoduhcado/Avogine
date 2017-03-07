package core.entities.renders;

import java.util.ArrayList;

import core.entities.Entity;
import core.entities.components.EntityComponent;
import core.render.Transform;
import core.render.effects.TweenEffect;

public abstract class Render extends EntityComponent {
	
	protected Transform transform = new Transform();
	protected ArrayList<TweenEffect<?>> effects = new ArrayList<>();
	
	public Render(Entity entity) {
		super(entity);
	}

	public void draw() {
		compileTransform();
	}
	
	protected void compileTransform() {
		entity.getComponents().values().stream()
			.forEach(e -> e.applyTransformEffect(transform));
	}
	
	@Override
	public void applyTransformEffect(Transform transform) {
		effects.stream().forEach(TweenEffect::apply);
	}
	
	public Transform getTransform() {
		return transform;
	}
	
	public ArrayList<TweenEffect<?>> getEffects() {
		return effects;
	}
	
	public void setEffects(ArrayList<TweenEffect<?>> effects) {
		this.effects = effects;
	}
	
	public void addTweenEffect(TweenEffect<?> effect) {
		this.effects.add(effect);
	}
	
	public void removeTweenEffect(TweenEffect<?> effect) {
		this.effects.remove(effect);
	}
	
}
