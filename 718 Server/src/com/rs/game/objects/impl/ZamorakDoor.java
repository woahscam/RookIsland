package com.rs.game.objects.impl;

import com.rs.game.WorldObject;
import com.rs.game.objects.ObjectScript;
import com.rs.game.player.Player;

public class ZamorakDoor extends ObjectScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 26428 };
	}
	
	@Override
	public boolean processObject(Player player, WorldObject object) {
		player.getDialogueManager().startDialogue("GodWarsDoor", object.getId(), object.getX(),
				object.getY(), object.getPlane(), 3);
		return true;
	}
}
