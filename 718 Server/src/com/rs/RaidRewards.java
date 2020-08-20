package com.rs;

import java.util.ArrayList;

import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.utils.Utils;

public class RaidRewards {
	
	/**
	 * @author Andreas
	 *	test random reward system
	 */

	private static double COMMON = 33.0, UNCOMMON = 15.0, RARE = 5.0, VERY_RARE = 0.5;

	private enum RewardStore {

		FUCKING_RAID_ITEM1(1333, 1, COMMON),

		FUCKING_RAID_ITEM2(4587, 1, UNCOMMON),

		FUCKING_RAID_ITEM3(4151, 1, RARE),

		FUCKING_RAID_ITEM4(14484, 1, VERY_RARE);

		private int itemId, amount;
		private double chance;

		private RewardStore(int itemId, int amount, double chance) {
			this.itemId = itemId;
			this.amount = amount;
			this.chance = chance;
		}
	}

	public static ArrayList<Item> rewards = new ArrayList<>();

	public static void addReward(Player player) {
		rewards.clear();
		for (int i = 0; i < 3; i++) {
			RewardStore reward = generateRewards(player);
			rewards.add(new Item(reward.itemId, reward.amount));
		}
		StringBuffer names = new StringBuffer();
		for (Item item : rewards) {
			if (item == null)
				continue;
			player.getInventory().addItem(item);
			names.append(item.getName()).append(", ");
		}
		player.sm("You recieved " + names.replace(names.length() - 2, names.length(), "").toString() + " as reward.");
	}

	private static RewardStore[] dataValues = RewardStore.values();

	private static RewardStore generateRewards(Player player) {
		while (true) {
			double chance = Utils.getRandomDouble(100);
			RewardStore reward = dataValues[Utils.getRandom(dataValues.length - 1)];
			
			if ((reward.chance) > chance) {
				System.out.println("Sucess! " + Utils.getFormattedNumber2(chance, '.') + " - " + reward.chance + "% for " + reward.name());
				return reward;
			} else {
				System.out.println("Failed! " + Utils.getFormattedNumber2(chance, '.') + " - " + reward.chance + "% for " + reward.name());
				continue;
			}
		}
	}

}
