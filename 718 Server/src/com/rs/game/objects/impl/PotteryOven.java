package com.rs.game.objects.impl;

import com.rs.game.WorldObject;
import com.rs.game.objects.ObjectScript;
import com.rs.game.player.Player;

public class PotteryOven extends ObjectScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2643, 11601, "Pottery Oven" };
	}

	@Override
	public int getDistance() {
		return 0;
	}

	@Override
	public boolean processObject(Player player, WorldObject object) {
		player.getDialogueManager().startDialogue("PotterOvenD", object);
		return true;
	}
}
