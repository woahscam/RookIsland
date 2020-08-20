package com.rs.game.npc.others;

import com.rs.game.Entity;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.utils.Utils;

public class RockCrabs extends NPC {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1776392517680641886L;

	public RockCrabs(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, true);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isDead())
			return;
		System.out.println("Rockcrab");
		if (getId() == 1265 || getId() == 1267) {
			if (getAttackedByDelay() < Utils.currentTimeMillis()) {
				this.transformIntoNPC(getId() + 1);
			}
		}
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		System.out.println("Crab dead");
		this.transformIntoNPC(getId() + 1);
		super.sendDeath(source);
	}
}
