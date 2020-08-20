package com.rs;

import java.util.ArrayList;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.utils.Utils;

public class CrystalChestRewards {
	
	/**
	 * @author Andreas
	 *	Made for Knd on rune-server, rs3 crystal chest reward tables
	 */

	public enum Rewards {

		LOOT_1(new Item(5290), new Item(5288), new Item(5289)), // fruit tree seeds

		LOOT_2(new Item(5302, 2)), // lantadyme seed

		LOOT_3(new Item(219, 2)), // grimy torstol

		LOOT_4(new Item(556, 200), new Item(559, 200), new Item(562, 50), new Item(564, 50), new Item(560, 50),
				new Item(557, 200), new Item(554, 200), new Item(563, 50), new Item(558, 200), new Item(561, 50),
				new Item(555, 200)), // runes

		//TODO LOOT_5(new Item(000, 200)), // iron stone spirit

		//TODO LOOT_6(new Item(000, 150)), // coal stone spirit

		//TODO LOOT_7(new Item(000, 3)), // runite stone spirit

		LOOT_8(new Item(1617, 3), new Item(1619, 3)), // gems

		LOOT_9(new Item(995, 15000), new Item(987)), // loop half of a key

		LOOT_10(new Item(995, 15000), new Item(985)), // tooth half of a key

		LOOT_11(new Item(000, 1)), // huge plated rune salvage

		LOOT_12(new Item(000, 1)), // large plated rune salvage

		LOOT_13(0.1, new Item(28537), new Item(28539), new Item(28541), new Item(28543), new Item(28545)), // dragonstone
																											// outfit

		;

		private Item[] items;
		private double rarity;
		
		private Rewards(Item... items) {
			this(100, items);
		}
		
		private Rewards(double rarity, Item... items) {
			this.setItems(items);
			this.setRarity(rarity);
		}

		public Item[] getItems() {
			return items;
		}

		public void setItems(Item[] items) {
			this.items = items;
		}

		public double getRarity() {
			return rarity;
		}

		public void setRarity(double rarity) {
			this.rarity = rarity;
		}
	}

	public static ArrayList<Item> rewards = new ArrayList<>();

	public static void addReward(Player player) {
		rewards.clear();
		Rewards reward = generateRewards(player);
		rewards.add(new Item(1631, 1)); // uncut dragonstone 100%
		if (reward.getRarity() != 100) {// if rare, give 1 piece
			rewards.add(reward.getItems()[Utils.getRandom(reward.getItems().length - 1)]);
		} else {// else give all rewards from that table
			for (Item item : reward.getItems()) {
				if (item == null)
					continue;
				rewards.add(item);
			}
		}
		ItemDefinitions defs;
		StringBuffer names = new StringBuffer();
		for (Item item : rewards) {
			if (item == null)
				continue;
			defs = ItemDefinitions.getItemDefinitions(item.getId());
			if (!player.getInventory().hasFreeSlots() && !defs.isStackable() || (!player.getInventory().hasFreeSlots()
					&& defs.isStackable() && !player.getInventory().containsOneItem(item.getId()))) {
				World.updateGroundItem(item, player, player, 60, 0);
			} else {
				player.getInventory().addItem(item);
			}
			names.append(item.getName()).append(", ");
		}
		player.sm("You recieved " + names.replace(names.length() - 2, names.length(), "").toString() + " as reward.");
	}

	private static Rewards[] rewardData = Rewards.values();

	private static Rewards generateRewards(Player player) {
		while (true) {
			double chance = Utils.getRandomDouble(100);
			Rewards reward = rewardData[Utils.getRandom(rewardData.length - 1)];
			if (chance <= reward.getRarity()) {
				System.out.println("Sucess! " + Utils.getFormattedNumber2(chance, '.') + " - " + reward.getRarity()
						+ "% for " + reward.name());
				return reward;
			} else {
				System.out.println("Failed! " + Utils.getFormattedNumber2(chance, '.') + " - " + reward.getRarity()
						+ "% for " + reward.name());
				continue;
			}
		}
	}

}
