package com.rs.game.player.dialogues;

import com.rs.game.player.Player;

public class StatueSeller extends Dialogue {

	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, 9843, "Hello, do you have any artifacts for sale?");
	}

	public enum Artifacts {

		BROKEN_STATUE_HEADDRESS(14892, 5000),

		THIRD_AGE_CARAFE(14891, 10000),

		BRONZED_DRAGON_CLAW(14890, 20000),

		ANCIENT_PSALTERY_BRIDGE(14889, 30000),

		SARADOMIN_AMPHORA(14888, 40000),

		BANDOS_SCRIMSHAW(14887, 50000),

		SARADOMIN_CARVING(14886, 75000),

		ZAMORAK_MEDALLION(14885, 100000),

		ARMADYL_TOTEM(14884, 150000),

		GUTHIXIAN_BRAZIER(14883, 200000),

		RUBY_CHALICE(14882, 250000),

		BANDOS_STATUETTE(14881, 300000),

		SARADOMIN_STATUETTE(14880, 400000),

		ZAMORAK_STATUETTE(14879, 500000),

		ARMADYL_STATUETTE(14878, 750000),

		SEREN_STATUETTE(14877, 1000000),

		ANCIENT_STATUETTE(14876, 5000000);

		private int itemId;

		private int price;

		private Artifacts(int itemId, int price) {
			this.itemId = itemId;
			this.price = price;
		}

		public int getItemId() {
			return itemId;
		}

		public void setItemId(int itemId) {
			this.itemId = itemId;
		}

		public int getPrice() {
			return price;
		}

		public void setPrice(int price) {
			this.price = price;
		}
	}

	public void sellArtifact(Player player) {
		for (Artifacts a : Artifacts.values()) {
			int artifactsInventory = player.getInventory().getNumberOf(a.itemId);
			int price = a.price;
			int moneyPouch = player.money;
			int totalPrice = artifactsInventory * price;
			if (!(moneyPouch + totalPrice < 0)) {
				player.money += totalPrice;
				player.getPackets().sendRunScript(5561, 1, price);
				player.refreshMoneyPouch();
				player.getInventory().deleteItem(a.itemId, artifactsInventory);
			} else {
				player.getInventory().addItem(995, totalPrice);
				player.getInventory().deleteItem(a.itemId, artifactsInventory);
			}
		}
	}

	public static boolean containsArtifacts(Player player) {
		for (Artifacts a : Artifacts.values()) {
			if (player.getInventory().containsItem(a.itemId, 1))
				return true;
		}
		return false;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			if (!containsArtifacts(player)) {
				stage = 25;
				sendPlayerDialogue(9847, "No, i don't.");
			} else {
				stage = 0;
				sendPlayerDialogue(9847, "Yes, i do!");
			}
		} else if (stage == 0) {
			stage = 1;
			sendNPCDialogue(npcId, 9843, "Would you like to sell them to me?");
		} else if (stage == 1) {
			stage = 3;
			sendPlayerDialogue(9847, "Sure!");
		} else if (stage == 3) {
			for (Artifacts a : Artifacts.values()) {
				int artifactsInventory = player.getInventory().getNumberOf(a.itemId);
				int money = player.getInventory().getNumberOf(995);
				if (money + artifactsInventory * a.price < 0 || !player.getInventory().hasFreeSlots()) {
					stage = 25;
					sendNPCDialogue(npcId, 9843, "You don't have enough inventory space.");
					return;
				} else if (player.money + artifactsInventory * a.price < 0 || !player.getInventory().hasFreeSlots()) {
					stage = 25;
					sendNPCDialogue(npcId, 9843, "You don't have enough inventory space.");
					return;
				} else {
					stage = 25;
					sendNPCDialogue(npcId, 9843, "Thank you!");
					sellArtifact(player);
				}
			}
		} else if (stage == 25) {
			end();
		} else
			end();
	}

	@Override
	public void finish() {

	}

}
