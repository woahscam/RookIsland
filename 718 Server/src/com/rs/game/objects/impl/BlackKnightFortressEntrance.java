package com.rs.game.objects.impl;

import com.rs.game.WorldObject;
import com.rs.game.objects.ObjectScript;
import com.rs.game.player.Player;
import com.rs.net.decoders.handlers.ObjectHandler;

public class BlackKnightFortressEntrance extends ObjectScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2337, 2341, 2338 };
	}

	@Override
	public boolean processObject(Player player, WorldObject object) {
		ObjectHandler.handleDoorTemporary(player, object, 1200);
		return true;
	}
}
