package com.rs.game.objects;

import com.rs.game.WorldObject;
import com.rs.game.player.Player;

public abstract class ObjectScript {
	
	public abstract Object[] getKeys();

	public boolean processObject(Player player, WorldObject object) {
		return true;
	}
	
	public boolean processObject2(Player player, WorldObject object) {
		return true;
	}
	
	public boolean processObject3(Player player, WorldObject object) {
		return true;
	}
	
	public boolean processObject4(Player player, WorldObject object) {
		return true;
	}
	
	public boolean processObject5(Player player, WorldObject object) {
		return true;
	}
	

	public int getDistance() {
		return 0;
	}

}
