package com.rs.game.npc.combat.impl;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.utils.Utils;

public class MonkeyGuard extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 1459, 1460 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int timesHealed = 0;
		int attackStyle = 0;
		switch (attackStyle) {
		case 0: // melee
			if (npc.getHitpoints() < npc.getMaxHitpoints() / 3) { // if lower
																	// than 25%
																	// hp
				if (timesHealed < 15) { // can only heal 15 times
					timesHealed++;
					npc.heal(150 + Utils.random(300));
					npc.animate(new Animation(1405));
				}
				break;
			}
			delayHit(npc, 0, target,
					getMeleeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target)));
			npc.animate(new Animation(defs.getAttackEmote()));
			break;
		}
		return defs.getAttackDelay();
	}
}
