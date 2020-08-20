package com.rs.game.objects.impl;

import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.objects.ObjectScript;
import com.rs.game.player.Player;

public class SaradominShortcuts extends ObjectScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 26445, 3829 };
	}
	
	@Override
	public boolean processObject(Player player, WorldObject object) {
		if ((object.getX() == 2919 && object.getY() == 5274 && object.getPlane() == 0)
				|| (object.getX() == 2920 && object.getY() == 5274 && object.getPlane() == 1)) {
			player.setNextWorldTile(
					player.getPlane() == 0 ? new WorldTile(2920, 5276, 1) : new WorldTile(2919, 5273, 0));
		}
		return true;
	}
}
