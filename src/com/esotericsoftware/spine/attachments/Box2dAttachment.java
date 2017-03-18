package com.esotericsoftware.spine.attachments;

import org.jbox2d.dynamics.Fixture;

public class Box2dAttachment extends RegionAttachment {
	private Fixture fixture;

	public Box2dAttachment(String name) {
		super(name);
	}
	
	public Fixture getFixture() {
		return fixture;
	}
	
	public void setFixture(Fixture fixture) {
		this.fixture = fixture;
	}
}