package core.entities.renders;

import core.Camera;
import core.Theater;
import core.entities.Entity;
import core.render.SpriteList;
import core.utilities.text.Text;
import core.utilities.text.TextModifier;
import core.utilities.text.TextModifier.TextModValue;

public class PlainRender extends Render {

	private String sprite;
	
	public PlainRender(Entity entity, String sprite) {
		super(entity);
		this.sprite = sprite;
	}
	
	@Override
	public void draw() {
		super.draw();
		
		SpriteList.get(sprite).draw(transform);
		
		if(Theater.debug) {
			TextModifier modifier = TextModifier.compile(TextModValue.COLOR + "=white");
			if(this.entity.hasBody() && this.entity.getBody() == Camera.get().getFocus()) {
				modifier.addModifier(TextModValue.COLOR, "green");
			}
			Text.drawString(sprite, transform.getPosition().x, transform.getPosition().y, modifier);
		}
	}

}
