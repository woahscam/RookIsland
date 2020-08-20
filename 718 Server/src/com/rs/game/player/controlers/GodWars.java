package com.rs.game.player.controlers;

import com.rs.game.WorldObject;
import com.rs.game.WorldTile;

public class GodWars extends Controler {

	@Override
	public void start() {
		sendInterfaces();
	}

	@Override
	public boolean logout() {
		return false;
	}

	@Override
	public boolean login() {
		sendInterfaces();
		return false;
	}

	@Override
	public boolean processObjectClick1(final WorldObject object) {
		if (object.getId() == 57225) {
			player.getDialogueManager().startDialogue("NexEntrance");
			return false;
		}
		return true;
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().sendTab(player.getInterfaceManager().hasRezizableScreen() ? 34 : 8,
				getInterface());
		player.getPackets().sendIComponentText(601, 8, "0");
		player.getPackets().sendIComponentText(601, 9, "0");
		player.getPackets().sendIComponentText(601, 10, "0");
		player.getPackets().sendIComponentText(601, 11, "0");
	}

	private int getInterface() {
		return 601;
	}

	@Override
	public boolean sendDeath() {
		remove();
		forceClose();
		return true;
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		remove();
		forceClose();
		return true;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		remove();
		forceClose();
		return true;
	}

	@Override
	public boolean processJewerlyTeleport(WorldTile toTile) {
		remove();
		forceClose();
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		remove();
		forceClose();
	}

	@Override
	public void forceClose() {
		remove();
		removeControler();
	}

	public void remove() {
		player.getPackets().closeInterface(player.getInterfaceManager().hasRezizableScreen() ? 34 : 8);
	}

}
