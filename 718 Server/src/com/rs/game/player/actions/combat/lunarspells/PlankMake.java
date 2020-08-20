package com.rs.game.player.actions.combat.lunarspells;

import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.utils.Utils;

/**
 *
 * @Author Tristam <Hassan>
 * @Project - 1. Rain
 * @Date - 26 Apr 2016
 *
 **/

public class PlankMake {

	public static enum Planks {

		REGULAR_PLANK(2319, 960, 175),

		OAK_PLANK(2321, 8778, 225),

		TEAK_PLANK(2321, 8780, 225),

		MAHOGANY_PLANK(2321, 8782, 225);

		private int baseId;
		private int newId;
		private int cost;

		private Planks(int baseId, int newId, int cost) {
			this.baseId = baseId;
			this.newId = newId;
			this.cost = cost;
		}

		public int getBaseId() {
			return baseId;
		}

		public int getNewId() {
			return newId;
		}

		public int getCost() {
			return cost;
		}
	}

	public static boolean isLog(int log) {
		return (log == 1511 || log == 1521 || log == 6333 || log == 6332);
	}

	public static boolean cast(Player player, double xp, int spellId, int itemId, int slotId) {
		if ((Long) player.getTemporaryAttributtes().get("LAST_SPELL") != null
				&& (long) player.getTemporaryAttributtes().get("LAST_SPELL") + 1800 > Utils.currentTimeMillis()) {
			return false;
		}
		if (!isLog(itemId)) {
			player.sm("You can only convert: plain, oak, teak and mahogany logs into planks.");
			return false;
		}
		for (Planks plank : Planks.values()) {
			if (plank == null)
				continue;
			if (plank.getBaseId() == itemId) {
				if (player.canBuy(plank.getCost())) {
					player.lock(3);
					player.addXp(Skills.MAGIC, xp);
					player.animate(new Animation(6298));
					player.gfx(new Graphics(1063));
					player.getInventory().deleteItem(slotId, plank.getBaseId());
					player.addItem(plank.getNewId(), 1);
					player.getInterfaceManager().openGameTab(7);
					player.getTemporaryAttributtes().put("LAST_SPELL", Utils.currentTimeMillis());
					return true;
				} else {
					player.sm("You need at least " + plank.getCost() + " coins to cast this spell on this log.");
					return false;
				}
			} else {
				player.sm("You can't use this spell on this item.");
				return false;
			}
		}
		return false;
	}

}
