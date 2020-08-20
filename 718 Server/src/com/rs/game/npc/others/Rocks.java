package com.rs.game.npc.others;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.utils.Utils;

public class Rocks extends NPC {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6635229612876021731L;

	public Rocks(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		if (getId() == 1265 || getId() == 1267)
			this.transformIntoNPC(getId() + 1);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isDead())
			return;
		for (Player players : World.getPlayers()) {
			if (Utils.inCircle(players, this, 1) && players.getAttackedByDelay() < Utils.currentTimeMillis()) {
				if (getId() == 1266 || getId() == 1268) {
					transformIntoNPC(getId() - 1);
					setTarget(players);
					setAttackedByDelay(4000);
					this.setBonuses();
					this.setHitpoints(this.getMaxHitpoints());
				}
			}
		}
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		if (getId() == 1265 || getId() == 1267)
		transformIntoNPC(getId() + 1);
	}
}
