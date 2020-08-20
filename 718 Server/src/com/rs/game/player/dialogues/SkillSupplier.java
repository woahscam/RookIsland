package com.rs.game.player.dialogues;

public class SkillSupplier extends Dialogue {

	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, 9827, "Hello " + player.getDisplayName() + ", What can i do for you?");
	}

	int greenHides;
	int blueHides;
	int redHides;
	int blackHides;
	int royalHides;

	private void tanHide(int hide) {
		if (hide == 1) {
			if (!player.getInventory().containsItem(1753, 1)) {
				player.getPackets().sendGameMessage("You don't have any of that type of dragonhide.");
				return;
			}
			if (player.getInventory().getNumberOf(1753) * 1000 <= player.getInventory().getNumberOf(995)) {
				greenHides = player.getInventory().getNumberOf(1753);
				player.getInventory().deleteItem(1753, player.getInventory().getNumberOf(1753));
				player.getInventory().deleteItem(995, greenHides * 1000);
				player.getInventory().addItem(1745, greenHides);
				player.getPackets()
						.sendGameMessage("You tanned your " + greenHides + " green dragonhides to green d-leather");
			} else if (player.getInventory().getNumberOf(1753) * 1000 <= player.getMoneyPouchValue()) {
				greenHides = player.getInventory().getNumberOf(1753);
				player.getInventory().deleteItem(1753, player.getInventory().getNumberOf(1753));
				player.money -= greenHides * 1000;
				player.getPackets().sendRunScript(5561, 0, greenHides * 1000);
				player.refreshMoneyPouch();
				player.getInventory().addItem(1745, greenHides);
				player.getPackets()
						.sendGameMessage("You tanned your " + greenHides + " green dragonhides to green d-leather");
			} else {
				stage = 5;
				player.getPackets().sendGameMessage("You do not have enough coins for that.");
				player.getPackets().sendGameMessage("Coins needed: " + player.getInventory().getNumberOf(1753) * 1000);
			}
		} else if (hide == 2) {
			if (!player.getInventory().containsItem(1751, 1)) {
				player.getPackets().sendGameMessage("You don't have any of that type of dragonhide.");
				return;
			}
			if (player.getInventory().getNumberOf(1751) * 2000 <= player.getInventory().getNumberOf(995)) {
				blueHides = player.getInventory().getNumberOf(1751);
				player.getInventory().deleteItem(1751, player.getInventory().getNumberOf(1751));
				player.getInventory().deleteItem(995, blueHides * 2000);
				player.getInventory().addItem(2505, blueHides);
				player.getPackets()
						.sendGameMessage("You tanned your " + blueHides + " blue dragonhides to blue d-leather");
			} else if (player.getInventory().getNumberOf(1751) * 2000 <= player.money) {
				blueHides = player.getInventory().getNumberOf(1751);
				player.getInventory().deleteItem(1751, player.getInventory().getNumberOf(1751));
				player.money -= blueHides * 2000;
				player.getPackets().sendRunScript(5561, 0, blueHides * 2000);
				player.refreshMoneyPouch();
				player.getInventory().addItem(2505, blueHides);
				player.getPackets()
						.sendGameMessage("You tanned your " + blueHides + " blue dragonhides to blue d-leather");
			} else {
				stage = 5;
				player.getPackets().sendGameMessage("You do not have enough coins for that.");
				player.getPackets().sendGameMessage("Coins needed: " + player.getInventory().getNumberOf(1751) * 2000);
			}
		} else if (hide == 3) {
			if (!player.getInventory().containsItem(1749, 1)) {
				player.getPackets().sendGameMessage("You don't have any of that type of dragonhide.");
				return;
			}
			if (player.getInventory().getNumberOf(1749) * 4000 <= player.getInventory().getNumberOf(995)) {
				redHides = player.getInventory().getNumberOf(1749);
				player.getInventory().deleteItem(1749, player.getInventory().getNumberOf(1749));
				player.getInventory().deleteItem(995, redHides * 4000);
				player.getInventory().addItem(2507, redHides);
				player.getPackets()
						.sendGameMessage("You tanned your " + redHides + " red dragonhides to red d-leather");
			} else if (player.getInventory().getNumberOf(1749) * 4000 <= player.money) {
				redHides = player.getInventory().getNumberOf(1749);
				player.getInventory().deleteItem(1749, player.getInventory().getNumberOf(1749));
				player.money -= redHides * 4000;
				player.getPackets().sendRunScript(5561, 0, redHides * 4000);
				player.refreshMoneyPouch();
				player.getInventory().addItem(2507, redHides);
				player.getPackets()
						.sendGameMessage("You tanned your " + redHides + " red dragonhides to red d-leather");
			} else {
				stage = 5;
				player.getPackets().sendGameMessage("You do not have enough coins for that.");
				player.getPackets().sendGameMessage("Coins needed: " + player.getInventory().getNumberOf(1749) * 4000);
			}
		} else if (hide == 4) {
			if (!player.getInventory().containsItem(1747, 1)) {
				player.getPackets().sendGameMessage("You don't have any of that type of dragonhide.");
				return;
			}
			if (player.getInventory().getNumberOf(1747) * 6000 <= player.getInventory().getNumberOf(995)) {
				blackHides = player.getInventory().getNumberOf(1747);
				player.getInventory().deleteItem(1747, player.getInventory().getNumberOf(1747));
				player.getInventory().deleteItem(995, blackHides * 6000);
				player.getInventory().addItem(2509, blackHides);
				player.getPackets()
						.sendGameMessage("You tanned your " + blackHides + " black dragonhides to black d-leather");
			} else if (player.getInventory().getNumberOf(1747) * 6000 <= player.money) {
				blackHides = player.getInventory().getNumberOf(1747);
				player.getInventory().deleteItem(1747, player.getInventory().getNumberOf(1747));
				player.money -= blackHides * 6000;
				player.getPackets().sendRunScript(5561, 0, blackHides * 6000);
				player.refreshMoneyPouch();
				player.getInventory().addItem(2509, blackHides);
				player.getPackets()
						.sendGameMessage("You tanned your " + blackHides + " black dragonhides to black d-leather");
			} else {
				stage = 5;
				player.getPackets().sendGameMessage("You do not have enough coins for that.");
				player.getPackets().sendGameMessage("Coins needed: " + player.getInventory().getNumberOf(1747) * 6000);
			}
		} else if (hide == 5) {
			if (!player.getInventory().containsItem(24372, 1)) {
				player.getPackets().sendGameMessage("You don't have any of that type of dragonhide.");
				return;
			}
			if (player.getInventory().getNumberOf(24372) * 10000 <= player.getInventory().getNumberOf(995)) {
				royalHides = player.getInventory().getNumberOf(24372);
				player.getInventory().deleteItem(24372, player.getInventory().getNumberOf(24372));
				player.getInventory().deleteItem(995, royalHides * 10000);
				player.getInventory().addItem(24374, royalHides);
				player.getPackets()
						.sendGameMessage("You tanned your " + royalHides + " royal dragonhides to royal d-leather");
			} else if (player.getInventory().getNumberOf(24372) * 10000 <= player.money) {
				royalHides = player.getInventory().getNumberOf(24372);
				player.getInventory().deleteItem(24372, player.getInventory().getNumberOf(24372));
				player.money -= royalHides * 10000;
				player.getPackets().sendRunScript(5561, 0, royalHides * 10000);
				player.refreshMoneyPouch();
				player.getInventory().addItem(24374, royalHides);
				player.getPackets()
						.sendGameMessage("You tanned your " + royalHides + " royal dragonhides to royal d-leather");
			} else {
				stage = 5;
				player.getPackets().sendGameMessage("You do not have enough coins for that.");
				player.getPackets()
						.sendGameMessage("Coins needed: " + player.getInventory().getNumberOf(24372) * 10000);
			}
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 1;
			sendOptionsDialogue("Choose an option", "I'd like to tan some dragon hides, please.",
					"Nothing, just walking by.");
		} else if (stage == 1) {
			if (componentId == OPTION_1) {
				stage = 4;
				sendOptionsDialogue("Choose an option", "Green D'hide (1000)", "Blue D'hide (2000)",
						"Red D'hide (4000)", "Black D'hide (6000)", "Royal D'hide (10000)");
			} else if (componentId == OPTION_2) {
				end();
			}
		} else if (stage == 4) {
			if (componentId == OPTION_1) {
				end();
				tanHide(1);
			} else if (componentId == OPTION_2) {
				end();
				tanHide(2);
			} else if (componentId == OPTION_3) {
				end();
				tanHide(3);
			} else if (componentId == OPTION_4) {
				end();
				tanHide(4);
			} else if (componentId == OPTION_5) {
				end();
				tanHide(5);
			}
		}

	}

	@Override
	public void finish() {
	}

}
