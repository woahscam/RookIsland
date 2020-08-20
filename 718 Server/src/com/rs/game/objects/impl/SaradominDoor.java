package com.rs.game.objects.impl;

import com.rs.game.WorldObject;
import com.rs.game.objects.ObjectScript;
import com.rs.game.player.Player;

public class SaradominDoor extends ObjectScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 26427 };
	}
	
	@Override
	public boolean processObject(Player player, WorldObject object) {
		player.getDialogueManager().startDialogue("GodWarsDoor", object.getId(), object.getX(),
				object.getY(), object.getPlane(), 2);
		return true;
	}
}
