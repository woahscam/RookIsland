package com.rs.game.player.content;

import java.util.List;

import com.rs.game.ForceTalk;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.actions.combat.OldMagicSystem;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

public class DonatorZone {

	public static void enterDonatorzone(final Player player) {
		OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2582, 3910, 0));
		for (int regionId : player.getMapRegionsIds()) {
			List<Integer> npcIndexes = World.getRegion(regionId).getNPCsIndexes();
			if (npcIndexes != null) {
				for (int npcIndex : npcIndexes) {
					final NPC n = World.getNPCs().get(npcIndex);
					if (n == null || n.getId() != 5445)
						continue;
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							final int random = Utils.getRandom(3);
							if (random == 0)
								n.setNextForceTalk(new ForceTalk(""));
							else if (random == 1)
								n.setNextForceTalk(new ForceTalk(""));
							else if (random == 2)
								n.setNextForceTalk(new ForceTalk(""));
							else if (random == 3)
								n.setNextForceTalk(new ForceTalk(""));
						}
					}, 4);
				}
			}
		}
	}
}
