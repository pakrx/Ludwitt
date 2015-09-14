package com.pakrx.platformgame.gameplay.objects;


public abstract class Pickup extends GameObject {

	public Pickup() {
		pickable = true;
	}

	public boolean isPickable() {
		return true;
	}
	
}
