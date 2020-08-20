package com.rs.game.player.content;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.game.Animation;
import com.rs.game.ForceMovement;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.combat.OldMagicSystem;
import com.rs.game.player.actions.skills.construction.ConstructorsOutfit;
import com.rs.game.player.actions.skills.summoning.Summoning;
import com.rs.game.player.actions.skills.summoning.Summoning.Pouch;
import com.rs.game.player.content.WildernessArtefacts.Artefacts;
import com.rs.game.player.content.grandexchange.GrandExchange;
import com.rs.game.player.content.grandexchange.GrandExchangeManager;
import com.rs.game.player.controlers.EdgevillePvPControler;
import com.rs.net.decoders.handlers.ButtonHandler;
import com.rs.utils.Encrypt;
import com.rs.utils.IPBanL;
import com.rs.utils.Logger;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.Utils;

public final class Commands {

	/**
	 * returns if command was processed
	 */

	public static boolean processCommand(Player player, String command, boolean console, boolean clientCommand) {
		if (command.length() == 0)
			return false;
		String[] cmd = command.toLowerCase().split(" ");
		if (cmd.length == 0)
			return false;
		if (player.getRights() >= 2 && (processAdminCommands(player, cmd, console, clientCommand))) {
			archiveLogs(player, cmd);
			return true;
		}
		if (player.getRights() >= 1 && (processModCommand(player, cmd, console, clientCommand))) {
			archiveLogs(player, cmd);
			return true;
		}
		if ((player.isSupporter()) && processSupportCommands(player, cmd, console, clientCommand)) {
			archiveLogs(player, cmd);
			return true;
		}
		if (Settings.ECONOMY) {
			player.getPackets().sendGameMessage("You can't use any commands in economy mode!");
			return true;
		}
		return processNormalCommand(player, cmd, console, clientCommand);
	}

	public static boolean processNormalCommand(final Player player, String[] cmd, boolean console,
			boolean clientCommand) {
		if (clientCommand) {
		} else {
			String name;
			switch (cmd[0]) {
			case "barragerunes":
				if (player.getInventory().containsOneItem(24497)) {
					player.getRunePouch().reset();
					player.getRunePouch().add(new Item(565, 200));
					player.getRunePouch().add(new Item(560, 400));
					player.getRunePouch().add(new Item(555, 600));
					player.getRunePouch().shift();
					player.getPackets().sendGameMessage("100 ice barrage runes added to your rune pouch.");
				} else {
					if (!player.getInventory().hasFreeSlots() && !player.getInventory().containsOneItem(565))
						player.getPackets().sendGameMessage("You don't have enough space for "
								+ ItemDefinitions.getItemDefinitions(565).getName() + ".");
					else {
						player.getInventory().addItem(565, 200);
						player.getPackets().sendGameMessage("200 " + ItemDefinitions.getItemDefinitions(565).getName()
								+ "s added to your inventory.");
					}
					if (!player.getInventory().hasFreeSlots() && !player.getInventory().containsOneItem(560))
						player.getPackets().sendGameMessage("You don't have enough space for "
								+ ItemDefinitions.getItemDefinitions(560).getName() + ".");
					else {
						player.getInventory().addItem(560, 400);
						player.getPackets().sendGameMessage("400 " + ItemDefinitions.getItemDefinitions(560).getName()
								+ "s added to your inventory.");
					}
					if (!player.getInventory().hasFreeSlots() && !player.getInventory().containsOneItem(555))
						player.getPackets().sendGameMessage("You don't have enough space for "
								+ ItemDefinitions.getItemDefinitions(555).getName() + ".");
					else {
						player.getInventory().addItem(555, 600);
						player.getPackets().sendGameMessage("600 " + ItemDefinitions.getItemDefinitions(555).getName()
								+ "s added to your inventory.");
					}
				}
				return true;
			case "vengrunes":
				if (player.getInventory().containsOneItem(24497)) {
					player.getRunePouch().reset();
					player.getRunePouch().add(new Item(560, 40));
					player.getRunePouch().add(new Item(9075, 80));
					player.getRunePouch().add(new Item(557, 200));
					player.getRunePouch().shift();
					player.getPackets().sendGameMessage("100 vengeance runes added to your rune pouch.");
				} else {
					if (!player.getInventory().hasFreeSlots() && !player.getInventory().containsOneItem(560))
						player.getPackets().sendGameMessage("You don't have enough space for "
								+ ItemDefinitions.getItemDefinitions(560).getName() + ".");
					else {
						player.getInventory().addItem(560, 40);
						player.getPackets().sendGameMessage("40 " + ItemDefinitions.getItemDefinitions(560).getName()
								+ "s added to your inventory.");
					}
					if (!player.getInventory().hasFreeSlots() && !player.getInventory().containsOneItem(9075))
						player.getPackets().sendGameMessage("You don't have enough space for "
								+ ItemDefinitions.getItemDefinitions(9075).getName() + ".");
					else {
						player.getInventory().addItem(9075, 80);
						player.getPackets().sendGameMessage("80 " + ItemDefinitions.getItemDefinitions(9075).getName()
								+ "s added to your inventory.");
					}
					if (!player.getInventory().hasFreeSlots() && !player.getInventory().containsOneItem(557))
						player.getPackets().sendGameMessage("You don't have enough space for "
								+ ItemDefinitions.getItemDefinitions(557).getName() + ".");
					else {
						player.getInventory().addItem(557, 200);
						player.getPackets().sendGameMessage("200 " + ItemDefinitions.getItemDefinitions(557).getName()
								+ "s added to your inventory.");
					}
				}
				return true;
			case "customname":
				player.getPackets().sendRunScript(109,
						new Object[] { "Please enter the color you would like. (HEX FORMAT)" });
				player.temporaryAttribute().put("customname", Boolean.TRUE);
				return true;
			case "claim":
				ButtonHandler.refreshUntradeables(player);
				return true;
			case "emptybank":
				player.getDialogueManager().startDialogue("EmptyBank");
				return true;
			case "youtube":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				player.getPackets().sendOpenURL("www.youtube.com/results?search_query=" + name + "&sm=3");
				return true;
			case "channel":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				player.getPackets().sendOpenURL("https://www.youtube.com/user/" + name);
				return true;
			case "geoffers":
				GrandExchange.sendOfferTracker(player);
				return true;
			case "pvp":
			case "edgepvp":
				EdgevillePvPControler.enterPVP(player);
				return true;
			case "ancient":
			case "ancients":
				if (Settings.ECONOMY_MODE == 0 && player.getRights() < 2)
					return false;
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't use ::" + cmd[0] + " at this location.");
					return true;
				}
				player.getPackets().sendGameMessage("Your mind clears and you switch back to the ancient spellbook.");
				player.getCombatDefinitions().setSpellBook(1);
				return true;
			case "prayers":
			case "prayer":
			case "curses":
			case "regular":
			case "regulars":
			case "pbook":
				if (Settings.ECONOMY_MODE == 0 && player.getRights() < 2)
					return false;
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't use ::" + cmd[0] + " at this location.");
					return true;
				}
				player.getPrayer().setPrayerBook(player.getPrayer().ancientcurses ? false : true);
				player.getPackets().sendGameMessage("You switch your prayer book.");
				return true;
			case "switch":
			case "spellbook":
			case "book":
				if (Settings.ECONOMY_MODE == 0 && player.getRights() < 2)
					return false;
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't use ::" + cmd[0] + " at this location.");
					return true;
				}
				int spellBook = player.getCombatDefinitions().spellBook;
				player.getCombatDefinitions().setSpellBook(spellBook == 0 ? 1 : spellBook == 1 ? 2 : 0);
				player.getPackets().sendGameMessage("You switch your spellbook.");
				return true;
			case "normal":
			case "modern":
				if (Settings.ECONOMY_MODE == 0 && player.getRights() < 2)
					return false;
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't use ::" + cmd[0] + " at this location.");
					return true;
				}
				player.getPackets().sendGameMessage("Your mind clears and you switch back to the normal spellbook.");
				player.getCombatDefinitions().setSpellBook(0);
				return true;
			case "yell":
				String data = "";
				for (int i = 1; i < cmd.length; i++) {
					data += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				}
				ServerMessage.filterMessage(player, Utils.fixChatMessage(data), false);
				archiveYell(player, Utils.fixChatMessage(data));
				return true;
			case "mb":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't use ::" + cmd[0] + " at this location.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2539, 4716, 0));
				player.getPackets().sendGameMessage("You have teleported to mage bank.");
				return true;

			case "clanwars":
			case "cw":
			case "clws":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't use ::" + cmd[0] + " at this location.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2970, 9679, 0));
				player.getPackets().sendGameMessage("You have teleported to clan wars.");
				return true;

			case "home":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't use ::" + cmd[0] + " at this location.");
					return true;
				}
				if (player.isInCombat(10000)) {
					player.getPackets().sendGameMessage("You can't teleport out of combat.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(Settings.HOME_PLAYER_LOCATION));
				player.getPackets().sendGameMessage("You have teleported home.");
				return true;
			case "east":
			case "easts":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't use ::" + cmd[0] + " at this location.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3349, 3647, 0));
				player.getPackets().sendGameMessage("You have teleported to east dragons.");
				return true;

			case "edgeville":
			case "edge":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't use ::" + cmd[0] + " at this location.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3082, 3545, 0));
				player.getPackets().sendGameMessage("You have teleported to edgeville.");
				return true;

			case "gdz":
			case "gds":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't use ::" + cmd[0] + " at this location.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3289, 3886, 0));
				player.getPackets().sendGameMessage("You have teleported to greater demons.");
				return true;

			case "kbd":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't use ::" + cmd[0] + " at this location.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3032, 3836, 0));
				player.getPackets().sendGameMessage("You have teleported outside king black dragon lair.");
				return true;

			case "44ports":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't use ::" + cmd[0] + " at this location.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2980, 3867, 0));
				player.getPackets().sendGameMessage("You have teleported lvl 44 wilderness port.");
				return true;

			case "iceplatue":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't use ::" + cmd[0] + " at this location.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2962, 3918, 0));
				player.getPackets().sendGameMessage("You have teleported to ice platue.");
				return true;

			case "50ports":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't use ::" + cmd[0] + " at this location.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3308, 3916, 0));
				player.getPackets().sendGameMessage("You have teleported to lvl 50 wilderness portal.");
				return true;

			case "castle":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't use ::" + cmd[0] + " at this location.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3012, 3631, 0));
				player.getPackets().sendGameMessage("You have teleported to castle.");
				return true;

			case "bh":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't use ::" + cmd[0] + " at this location.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3153, 3709, 0));
				player.getPackets().sendGameMessage("You have teleported to bounty hunter crater.");
				return true;

			case "altar":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't use ::" + cmd[0] + " at this location.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2950, 3821, 0));
				player.getPackets().sendGameMessage("You have teleported to wilderness altar.");
				return true;

			case "wests":
			case "west":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't use ::" + cmd[0] + " at this location.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2978, 3598, 0));
				player.getPackets().sendGameMessage("You have teleported to west dragons.");
				return true;

			case "zerk":
			case "zerkspot":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't use ::" + cmd[0] + " at this location.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3043, 3552, 0));
				player.getPackets().sendGameMessage("You have teleported to zerker spot.");
				return true;

			case "bridspot":
			case "brid":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't use ::" + cmd[0] + " at this location.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3013, 3553, 0));
				player.getPackets().sendGameMessage("You have teleported to hybridding spot.");
				return true;

			case "teles":
				player.getInterfaceManager().sendInterface(275);
				player.getPackets().sendIComponentText(275, 1, "*Teleports*");
				player.getPackets().sendIComponentText(275, 10, "");
				player.getPackets().sendIComponentText(275, 11,
						"::zerk *Teleports to western side of edgeville. SINGLE");
				player.getPackets().sendIComponentText(275, 12, "::easts *Teleports to lvl 20 east dragons. SINGLE");
				player.getPackets().sendIComponentText(275, 13, "::wests *Teleports to lvl 13 west dragons. SINGLE");
				player.getPackets().sendIComponentText(275, 14, "::mb *Teleports inside the mage bank. NOT WILDY");
				player.getPackets().sendIComponentText(275, 15,
						"::brid *Teleports to west side of edgeville wilderness. SINGLE");
				player.getPackets().sendIComponentText(275, 16, "::gdz *Teleports to Greater demons in lvl 48. MULTI");
				player.getPackets().sendIComponentText(275, 17,
						"::44ports *Teleports to lvl 44 wilderness portal. SINGLE");
				player.getPackets().sendIComponentText(275, 18,
						"::iceplatue *Teleports to ice platue in lvl 50 wilderness. SINGLE");
				player.getPackets().sendIComponentText(275, 19,
						"::kbd *Teleports outside king black dragon lair. MULTI");
				player.getPackets().sendIComponentText(275, 20,
						"::50ports *Teleports to lvl 50 wilderness portal. MULTI");
				player.getPackets().sendIComponentText(275, 21,
						"::bh *Teleports inside the Bounty hunter crate. MULTI");
				player.getPackets().sendIComponentText(275, 22, "::revs *Teleports to rev cave. SINGLE & MULTI");
				player.getPackets().sendIComponentText(275, 23,
						"::altar *Teleports to an altar deep in west of wilderness.");
				player.getPackets().sendIComponentText(275, 24,
						"::castle *Teleports to castle near west dragons. MULTI");
				return true;
			case "commands":
				player.getInterfaceManager().sendInterface(275);
				player.getPackets().sendIComponentText(275, 1, "*Commands*");
				player.getPackets().sendIComponentText(275, 10, "");
				player.getPackets().sendIComponentText(275, 11,
						"::setlevel skillId level - Set your own combat skills<br>You can only set skillIds 1-6 & 23");
				player.getPackets().sendIComponentText(275, 12, "");
				player.getPackets().sendIComponentText(275, 13, "::teles - Shows all teleport commands");
				player.getPackets().sendIComponentText(275, 14, "::tasks - Shows list of all tasks");
				player.getPackets().sendIComponentText(275, 15, "::players - Tells you how many players are online");
				player.getPackets().sendIComponentText(275, 16, "::playerslist - Shows a list of all players online");
				player.getPackets().sendIComponentText(275, 17, "::pricecheck - Search & price checks an item");
				player.getPackets().sendIComponentText(275, 18, "::checkoffers - Shows all Grand exchange offers");
				player.getPackets().sendIComponentText(275, 19, "::kdr - Prints out your kills & deaths ratio");
				player.getPackets().sendIComponentText(275, 20, "::skull - Makes you skulled");
				player.getPackets().sendIComponentText(275, 21, "::droplog - Shows droplog");
				player.getPackets().sendIComponentText(275, 22, "::cleardroplog - Clears all drops from droplog");
				player.getPackets().sendIComponentText(275, 23,
						"::toggledroplogmessage - Toggle on & off droplog messages");
				player.getPackets().sendIComponentText(275, 24,
						"::droplogvalue value - Set value of which items should be logged");
				player.getPackets().sendIComponentText(275, 25, "::droplog - Shows droplog");
				player.getPackets().sendIComponentText(275, 26,
						"::switchitemslook - Changes items look from old / new");
				player.getPackets().sendIComponentText(275, 27, "::compreqs - Shows completionist cape requirements");
				player.getPackets().sendIComponentText(275, 28, "::emptybank - Resets your whole bank.");
				for (int i = 29; i <= 150; i++)
					player.getPackets().sendIComponentText(275, i, "");
				return true;
			case "bank":
				if (player.isAtWild()) {
					player.getPackets().sendGameMessage("You can't use ::" + cmd[0] + " in the wilderness.");
					return true;
				}
				player.getBank().openBank();
				return true;
			case "players":
			case "online":
				player.getPackets().sendGameMessage("Players online: " + World.getPlayers().size());
				return true;
			case "pc":
			case "price":
			case "pricecheck":
				player.getPackets().sendHideIComponent(105, 196, true);
				player.getPackets().sendConfig(1109, -1);
				player.getPackets().sendConfig1(1241, 16750848);
				player.getPackets().sendConfig1(1242, 15439903);
				player.getPackets().sendConfig1(741, -1);
				player.getPackets().sendConfig1(743, -1);
				player.getPackets().sendConfig1(744, 0);
				player.getPackets().sendInterface(true, 752, 7, 389);
				player.getPackets().sendRunScript(570, new Object[] { "Price checker" });
				return true;
			case "closeticket":
				TicketSystem.closeTicket(player);
				return true;
			case "checkoffers":
				GrandExchange.sendOfferTracker(player);
				return true;
			case "droplog":
				player.getDropLogs().displayInterface();
				return true;
			case "cleardroplog":
				player.getDropLogs().clearDrops();
				return true;
			case "toggledroplogmessage":
				player.getDropLogs().toggleMessage();
				return true;
			case "droplogvalue":
				player.getDropLogs().setLowestValue(Integer.valueOf(cmd[1]));
				player.getPackets().sendGameMessage("Lowest droplog value is now: "
						+ Utils.getFormattedNumber(Integer.valueOf(cmd[1]), ',') + " gp.");
				return true;
			case "skull":
				player.skullDelay = 2000; // 20minutes
				player.skullId = 0;
				player.getAppearence().generateAppearenceData();
				return true;
			case "score":
			case "kdr":
				double kill = player.getKillCount();
				double death = player.getDeathCount();
				double dr = kill / death;
				if (kill == 0 && death == 0)
					dr = 0;
				player.setNextForceTalk(
						new ForceTalk("Kills: " + player.getKillCount() + " Deaths: " + player.getDeathCount()
								+ " Streak: " + player.killStreak + " Ratio: " + new DecimalFormat("##.#").format(dr)));
				return true;
			case "switchitemslook":
				player.switchItemsLook();
				player.getPackets()
						.sendGameMessage("You now have " + (player.isOldItemsLook() ? " old" : "new") + " items look.");
				return true;
			case "compreqs":
				player.sendCompReqMessages();
				return true;
			}
		}
		return true;
	}

	/**
	 * Section was created for secure things, admin section is for anyone with
	 * rights = 2
	 */

	public static boolean processAdminCommands(final Player player, String[] cmd, boolean console,
			boolean clientCommand) {
		if (clientCommand) {
			switch (cmd[0]) {
			case "tele":
				cmd = cmd[1].split(",");
				int plane = Integer.valueOf(cmd[0]);
				int x = Integer.valueOf(cmd[1]) << 6 | Integer.valueOf(cmd[3]);
				int y = Integer.valueOf(cmd[2]) << 6 | Integer.valueOf(cmd[4]);
				player.setNextWorldTile(new WorldTile(x, y, plane));
				if (player.getInterfaceManager().containsInterface(755))
					player.getInterfaceManager().closeInterface(755, 1);
				player.getPackets()
						.sendPanelBoxMessage("Teleported to " + Integer.valueOf(cmd[0]) + "," + Integer.valueOf(cmd[1])
								+ "," + Integer.valueOf(cmd[2]) + "," + Integer.valueOf(cmd[3]) + ","
								+ Integer.valueOf(cmd[4]));
				return true;
			}
		} else {
			String name;
			Player target;
			switch (cmd[0]) {
			case "testarti":
				double rollChance = 100 - (Integer.valueOf(cmd[1]) * 0.30);
				double c = Utils.getRandomDouble2(rollChance);
				Artefacts rolledItem = Artefacts.values()[Utils.getRandom(Artefacts.values().length - 1)];
				System.out.println("--------------");
				System.out.println("Your ep: " + Integer.valueOf(cmd[1]) + "%");
				System.out.println("Roll: " + c + " [100 - (yourEp * 0.10)] = " + 0.0 + "-" + rollChance);
				System.out.println("Rolling for drop: " + rolledItem.getName());
				System.out.println("Rolled Item %chance: " + rolledItem.getChance() + "%");
				System.out.println("--------------");
				if (c <= rolledItem.getChance()) {
					System.out.println("Success!");
				} else
					System.out.println("Failed!");
				System.out.println("--------------");
				return true;
			case "piece":
				if (!ConstructorsOutfit.addPiece(player))
					return true;
				return true;
			case "ecomode":
				int ecoMode = Integer.parseInt(cmd[1]);
				Settings.ECONOMY_MODE = ecoMode;
				World.sendWorldMessage(
						"Server Economy mode has been set to: "
								+ (ecoMode == 0 ? "Full Economy" : ecoMode == 1 ? "Half Economy" : "Full Spawn") + "!",
						false);
				return true;
			case "openassist":
				player.getAssist().Open();
				return true;
			case "claimtaskrewards":
				player.getTaskManager().claimRewards();
				return true;
			case "resetalltasks":
				player.getTaskManager().resetAllTasks();
				return true;
			case "lowhp":
				player.applyHit(new Hit(player, player.getHitpoints() - 1, HitLook.REGULAR_DAMAGE));
				return true;
			case "serverdoubledrop":
				try {
					boolean doubledrop = Boolean.valueOf(cmd[1]);
					;
					if (Settings.DOUBLE_DROP == doubledrop) {
						player.getPackets().sendGameMessage("Nothing interesting happens.");
					} else if (doubledrop == false) {
						ServerMessage.sendNews(true, "<img=12>Update: Double drops has been deactivated.", false, true);
					} else
						ServerMessage.sendNews(true, "<img=12>Update: Double drops has been activated!", false, true);
					Settings.DOUBLE_DROP = doubledrop;
				} catch (NumberFormatException f) {
					player.getPackets().sendGameMessage("Use ::serverdoubledrop false-true.");
				}
				return true;
			case "serverskillingxp":
				try {
					int rate = Integer.valueOf(cmd[1]);
					;
					if (rate > Integer.MAX_VALUE || rate < 1) {
						player.getPackets().sendGameMessage("Use ::serverxp (int) 1 >.");
						return true;
					} else {
						if (Settings.SKILLING_XP_RATE == rate) {
							player.getPackets().sendGameMessage("Nothing interesting happens.");
						} else if (rate == 1) {
							ServerMessage.sendNews(true, "<img=12>Update: Skilling XP has been set to normal.", false,
									true);
						} else
							ServerMessage.sendNews(true, "<img=12>Update: Skilling XP has been set to (" + rate + ")",
									false, true);

						Settings.SKILLING_XP_RATE = rate;
					}
				} catch (NumberFormatException f) {
					player.getPackets().sendGameMessage("Use ::serverskillingxp (int) 1 >.");
				}
				return true;
			case "dxp":
			case "serverbonusxp":
				try {
					double rate = Double.valueOf(cmd[1]);
					;
					if (rate > Integer.MAX_VALUE || rate < 1.0) {
						player.getPackets().sendGameMessage("Use ::serverxp (double) 1-50.");
						return true;
					} else {
						if (Settings.BONUS_EXP_WEEK_MULTIPLIER == rate) {
							player.getPackets().sendGameMessage("Nothing interesting happens.");
						} else if (rate == 1.0) {
							ServerMessage.sendNews(true, "<img=12>Update: Bonus XP has been deactivated.", false, true);
						} else
							ServerMessage.sendNews(true, "<img=12>Update: Bonus XP (" + rate + ") has been activated!",
									false, true);

						Settings.BONUS_EXP_WEEK_MULTIPLIER = rate;
					}
				} catch (NumberFormatException f) {
					player.getPackets().sendGameMessage("Use ::serverxp (double) 1-50.");
				}
				return true;
			case "serverbonuspts":
				try {
					double rate = Double.valueOf(cmd[1]);
					;
					if (rate > 50.0 || rate < 1.0) {
						player.getPackets().sendGameMessage("Use ::serverbonuspts (double) 1-50.");
						return true;
					} else {
						if (Settings.BONUS_POINTS_WEEK_MULTIPLIER == rate) {
							player.getPackets().sendGameMessage("Nothing interesting happens.");
						} else if (rate == 1.0) {
							ServerMessage.sendNews(true,
									"<img=12>Update: Bonus  " + Settings.SERVER_NAME + " points has been deactivated.",
									false, true);
						} else
							ServerMessage.sendNews(true, "<img=12>Update: Bonus  " + Settings.SERVER_NAME + " points ("
									+ rate + ") has been activated!", false, true);

						Settings.BONUS_POINTS_WEEK_MULTIPLIER = rate;
					}
				} catch (NumberFormatException f) {
					player.getPackets().sendGameMessage("Use ::serverxp (double) 1-50.");
				}
				return true;
			case "checkinv":
				name = "";
				for (int i = 1; i < cmd.length; i++) {
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				}
				target = World.getPlayerByDisplayName(name);
				try {
					String contentsFinal = "";
					String inventoryContents = "";
					int contentsAmount;
					int freeSlots = target.getInventory().getFreeSlots();
					int usedSlots = 28 - freeSlots;
					for (int i = 0; i < 28; i++) {
						if (target.getInventory().getItem(i) == null) {
							contentsAmount = 0;
							inventoryContents = "";
						} else {
							int id = target.getInventory().getItem(i).getId();
							contentsAmount = target.getInventory().getNumberOf(id);
							inventoryContents = "slot " + (i + 1) + " - " + target.getInventory().getItem(i).getName()
									+ " - " + contentsAmount + "<br>";
						}
						contentsFinal += inventoryContents;
					}
					player.getInterfaceManager().sendInterface(1166);
					player.getPackets().sendIComponentText(1166, 1, contentsFinal);
					player.getPackets().sendIComponentText(1166, 2, usedSlots + " / 28 Inventory slots used.");
					player.getPackets().sendIComponentText(1166, 23,
							"<col=FFFFFF><shad=000000>" + target.getDisplayName() + "</shad></col>");
				} catch (Exception e) {
					player.getPackets().sendGameMessage(
							"[<col=FF0000>" + Utils.formatPlayerNameForDisplay(name) + "</col>] wasn't found.");
				}
				return true;
			case "shutdown":
			case "shutoff":
				int delay = 60;
				if (cmd.length >= 2) {
					try {
						delay = Integer.valueOf(cmd[1]);
					} catch (NumberFormatException e) {
						player.getPackets().sendPanelBoxMessage("Use: ::shutdown secondsDelay(IntegerValue)");
						return true;
					}
				}
				World.safeShutdown(delay);
				return true;
			case "wolp":
				Summoning.spawnFamiliar(player, Pouch.WOLPERTINGER);
				return true;
			case "jail":
				if (player.getRights() < 1) {
					player.getPackets().sendGameMessage("Insufficient Permissions");
					return true;
				}

				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				if (target != null) {

					SerializableFilesManager.savePlayer(target);
				} else {
					target = (Player) SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));

					SerializableFilesManager.savePlayer(target);
				}
				return true;
			case "cstore":
				int type = Integer.valueOf(cmd[1]);
				int shop = Integer.valueOf(cmd[2]);
				player.getCustomStore().sendInterface(player, type, shop);
				return true;
			case "tradestore":
				player.getTradeStore().openTrade();
				return true;
			case "drop":
				if (cmd.length < 1) {
					player.getPackets()
							.sendGameMessage("::drop 'amount of drops' 'amount of squares random generated'");
					return true;
				}
				if (Integer.valueOf(cmd[1]) > 100)
					return true;
				if (Integer.valueOf(cmd[2]) > 32)
					return true;
				int itemIds[] = { 4151, 15486, 11694, 11696, 11698, 11700, 11724, 11726, 11728, 11718, 11720, 11722,
						6585, 6737, 6731, 6733, 6735, 14484, 15220, 15017, 15018, 15019, 15020, 4708, 4710, 4712, 4714,
						4716, 4718, 4720, 4722, 4724, 4726, 4728, 4730, 4732, 4734, 4736, 4738, 4745, 4747, 4749, 4751,
						4753, 4755, 4757, 4759 };
				for (int i = 0; i < Integer.valueOf(cmd[1]); i++) {
					WorldTile tiles[] = {
							new WorldTile(player.getX() + Utils.random(Integer.valueOf(cmd[2])),
									player.getY() + Utils.random(Integer.valueOf(cmd[2])), player.getPlane()),
							new WorldTile(player.getX() - Utils.random(Integer.valueOf(cmd[2])),
									player.getY() + Utils.random(Integer.valueOf(cmd[2])), player.getPlane()),
							new WorldTile(player.getX() - Utils.random(Integer.valueOf(cmd[2])),
									player.getY() - Utils.random(Integer.valueOf(cmd[2])), player.getPlane()),
							new WorldTile(player.getX() + Utils.random(Integer.valueOf(cmd[2])),
									player.getY() - Utils.random(Integer.valueOf(cmd[2])), player.getPlane()) };
					World.addGroundItem(new Item(itemIds[Utils.getRandom(itemIds.length - 1)], 1),
							new WorldTile(tiles[Utils.getRandom(tiles.length - 1)]), player, false, 0);
				}
				return true;
			case "raredrop":
				if (cmd.length < 1) {
					player.getPackets()
							.sendGameMessage("::drop 'amount of drops' 'amount of squares random generated'");
					return true;
				}
				if (Integer.valueOf(cmd[1]) > 100)
					return true;
				if (Integer.valueOf(cmd[2]) > 32)
					return true;
				int rareIds[] = { 1038, 1040, 1042, 1044, 1046, 1048, 1050, 1053, 1055, 1057 };
				for (int i = 0; i < Integer.valueOf(cmd[1]); i++) {
					WorldTile tiles[] = {
							new WorldTile(player.getX() + Utils.random(Integer.valueOf(cmd[2])),
									player.getY() + Utils.random(Integer.valueOf(cmd[2])), player.getPlane()),
							new WorldTile(player.getX() - Utils.random(Integer.valueOf(cmd[2])),
									player.getY() + Utils.random(Integer.valueOf(cmd[2])), player.getPlane()),
							new WorldTile(player.getX() - Utils.random(Integer.valueOf(cmd[2])),
									player.getY() - Utils.random(Integer.valueOf(cmd[2])), player.getPlane()),
							new WorldTile(player.getX() + Utils.random(Integer.valueOf(cmd[2])),
									player.getY() - Utils.random(Integer.valueOf(cmd[2])), player.getPlane()) };
					World.addGroundItem(new Item(rareIds[Utils.getRandom(rareIds.length - 1)], 1),
							new WorldTile(tiles[Utils.getRandom(tiles.length - 1)]), player, false, 0);
				}
				return true;
			case "give":
				StringBuilder itemName = new StringBuilder(cmd[1]);
				int quantity = 1;
				name = "";
				if (cmd.length > 2) {
					for (int i = 2; i < cmd.length; i++) {
						if (cmd[i].startsWith("+")) {
							quantity = Integer.parseInt(cmd[i].replace("+", ""));
						}
						if (cmd[i].startsWith("@")) {
							name = cmd[i].replace("@", "");
						} else if (cmd[i].startsWith("_")) {
							itemName.append(" ").append(cmd[i]);
						}
					}
				} else if (cmd.length > 3) {
					for (int i = 3; i < cmd.length; i++) {
						if (cmd[i].startsWith("+")) {
							quantity = Integer.parseInt(cmd[i].replace("+", ""));
						}
						if (cmd[i].startsWith("@")) {
							name = cmd[i].replace("@", "");
						} else if (cmd[i].startsWith("_")) {
							itemName.append(" ").append(cmd[i]);
						}
					}
				}
				String item1 = itemName.toString().toLowerCase().replace("[", "").replace("]", "").replace("(", "")
						.replace(")", "").replaceAll(",", "'").replaceAll("_", " ").replace("#6", " (6)")
						.replace("#5", " (5)").replace("#4", " (4)").replace("#3", " (3)").replace("#2", " (2)")
						.replace("#1", " (1)").replace("#e", " (e)").replace("#i", " (i)").replace("#g", " (g)")
						.replace("#or", " (or)").replace("#sp", " (sp)").replace("#t", " (t)");
				target = World.getPlayerByDisplayName(name);
				for (int i = 0; i < Utils.getItemDefinitionsSize(); i++) {
					ItemDefinitions def = ItemDefinitions.getItemDefinitions(i);
					if (def.getName().toLowerCase().equalsIgnoreCase(item1)) {
						if (name.length() > 0 && target == null) {
							player.getPackets().sendGameMessage("Couldn't find player " + name + ".");
							return true;
						}
						if (target != null) {
							target.getInventory().addItem(i, quantity);
							target.stopAll();
						} else {
							player.getInventory().addItem(i, quantity);
							player.stopAll();
						}
						player.getPackets()
								.sendGameMessage("Gave item " + item1 + " (" + def.getId() + ")"
										+ (quantity > 1 ? " x " + quantity + "" : "") + ""
										+ (target != null ? " to " + target.getUsername() : "") + ".");
						return true;
					}
				}
				player.getPackets().sendGameMessage("Could not find item by the name " + item1 + ".");
				return true;
			case "bgive":
				itemName = new StringBuilder(cmd[1]);
				quantity = 1;
				name = "";
				if (cmd.length > 2) {
					for (int i = 2; i < cmd.length; i++) {
						if (cmd[i].startsWith("+")) {
							quantity = Integer.parseInt(cmd[i].replace("+", ""));
						}
						if (cmd[i].startsWith("@")) {
							name = cmd[i].replace("@", "");
						} else if (cmd[i].startsWith("_")) {
							itemName.append(" ").append(cmd[i]);
						}
					}
				} else if (cmd.length > 3) {
					for (int i = 3; i < cmd.length; i++) {
						if (cmd[i].startsWith("+")) {
							quantity = Integer.parseInt(cmd[i].replace("+", ""));
						}
						if (cmd[i].startsWith("@")) {
							name = cmd[i].replace("@", "");
						} else if (cmd[i].startsWith("_")) {
							itemName.append(" ").append(cmd[i]);
						}
					}
				}
				item1 = itemName.toString().toLowerCase().replace("[", "").replace("]", "").replace("(", "")
						.replace(")", "").replaceAll(",", "'").replaceAll("_", " ").replace("#6", " (6)")
						.replace("#5", " (5)").replace("#4", " (4)").replace("#3", " (3)").replace("#2", " (2)")
						.replace("#1", " (1)").replace("#e", " (e)").replace("#i", " (i)").replace("#g", " (g)")
						.replace("#or", " (or)").replace("#sp", " (sp)").replace("#t", " (t)");
				target = World.getPlayerByDisplayName(name);
				for (int i = 0; i < Utils.getItemDefinitionsSize(); i++) {
					ItemDefinitions def = ItemDefinitions.getItemDefinitions(i);
					if (def.getName().toLowerCase().equalsIgnoreCase(item1)) {
						if (name.length() > 0 && target == null) {
							player.getPackets().sendGameMessage("Couldn't find player " + name + ".");
							return true;
						}
						if (target != null) {
							target.getBank().addItem(i, quantity, true);
						} else {
							player.getBank().addItem(i, quantity, true);
						}
						player.getPackets()
								.sendGameMessage("Gave item " + item1 + " (" + def.getId() + ")"
										+ (quantity > 1 ? " x " + quantity + "" : "") + ""
										+ (target != null ? " to " + target.getUsername() : "") + ".");
						return true;
					}
				}
				player.getPackets().sendGameMessage("Could not find item by the name " + item1 + ".");
				return true;
			case "healmode":
				if (!isDeveloper(player))
					return true;
				player.healMode = player.healMode ? false : true;
				player.sm("You have successfully " + (player.healMode ? "enabled" : "disabled") + " immunity to hits.");
				return true;
			case "droptest":
				player.dropTesting = player.dropTesting ? false : true;
				player.getPackets()
						.sendGameMessage("Drop testing: " + (player.dropTesting ? "Enabled" : "Disabled") + ".");
				return true;
			case "dropamount":
				int droptimes = Integer.valueOf(cmd[1]);
				player.dropTestingAmount = droptimes;
				player.getPackets().sendGameMessage("Drop testing amount set to: " + (droptimes) + ".");
				return true;
			case "zoom":
				int zoomId = Integer.valueOf(cmd[1]);
				if (zoomId < 25 || zoomId > 2500) {
					player.getPackets().sendGameMessage("You can't zoom that much.");
					return true;
				}
				player.getPackets().sendGlobalConfig(184, zoomId);
				return true;
			case "varbit":
				int globalConfigId = Integer.valueOf(cmd[1]);
				int value = Integer.valueOf(cmd[2]);
				player.getVarsManager().sendVarBit(globalConfigId, value);
				player.getPackets().sendGameMessage("Sent varbit: " + globalConfigId + "; " + value);
				player.getInventory().refresh();
				for (int i = 0; i < 9; i++)
					player.getEquipment().refresh(i);
				return true;
			case "var":
				int configValue = Integer.valueOf(cmd[2]);
				int configId = Integer.valueOf(cmd[1]);
				player.getVarsManager().sendVar(configId, configValue);
				// player.getVarsManager().sendVar(configId, configValue);
				player.getPackets().sendGameMessage("Sent var: " + configId + "; " + configValue);
				return true;
			case "varloop":
				int var1 = Integer.valueOf(cmd[1]);
				int var2 = Integer.valueOf(cmd[2]);
				int varValue = Integer.valueOf(cmd[3]);
				for (int i = var1; i < var2; i++)
					player.getVarsManager().sendVar(i, varValue);
				player.sm("send vars " + var1 + "-" + var2 + ":value:" + varValue);
				return true;
			case "varbitloop":
				var1 = Integer.valueOf(cmd[1]);
				var2 = Integer.valueOf(cmd[2]);
				varValue = Integer.valueOf(cmd[3]);
				for (int i = var1; i < var2; i++)
					player.getVarsManager().sendVarBit(i, varValue);
				player.sm("sent varbit " + var1 + "-" + var2 + ":value:" + varValue);
				return true;
			case "sendstring":
				int stringId = Integer.valueOf(cmd[1]);
				String stringText = cmd[2].substring(cmd[2].indexOf(" ") + 1);
				player.getPackets().sendGlobalString(stringId, stringText == null ? "Test" : stringText);
				player.getPackets().sendGameMessage("Sent global string: " + stringId + "; " + stringText);
				return true;
			case "resetzoom":
				player.getPackets().sendGlobalConfig(184, 250);
				return true;
			case "skull":
				player.skullDelay = 2000; // 20minutes
				player.skullId = 0;
				player.getAppearence().generateAppearenceData();
				return true;
			case "direction":
				player.getPackets()
						.sendGameMessage(
								player.getDirection() == 0 ? "South"
										: player.getDirection() == 2048 ? "South-west"
												: player.getDirection() == 4096 ? "West"
														: player.getDirection() == 6144 ? "North-west"
																: player.getDirection() == 8192 ? "North"
																		: player.getDirection() == 10240 ? "North-east"
																				: player.getDirection() == 12288
																						? "East"
																						: "South-east");
				player.getPackets().sendGameMessage("Id " + player.getDirection());
				return true;
			case "resettask":
				player.getSlayerManager().resetTask(false, false);
				player.getPackets().sendGameMessage("Your task have been reset.");
				player.refreshTask();
				return true;
			case "resettaskother":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				if (target != null) {
					target.getSlayerManager().resetTask(false, false);
				} else {
					name = Utils.formatPlayerNameForProtocol(name);
					if (!SerializableFilesManager.containsPlayer(name)) {
						player.getPackets().sendGameMessage(
								"Account name " + Utils.formatPlayerNameForDisplay(name) + " doesn't exist.");
						return true;
					}
					target = SerializableFilesManager.loadPlayer(name);
					target.setUsername(name);
					target.getSlayerManager().resetTask(false, false);
					SerializableFilesManager.savePlayer(target);
				}
				return true;
			case "completetask":
				player.getSlayerManager().completedTasks++;
				player.getSlayerManager().resetTask(true, true);
				player.refreshTask();
				return true;
			case "removebankitem":
				if (cmd.length == 3 || cmd.length == 4) {
					Player p = World.getPlayerByDisplayName(Utils.formatPlayerNameForDisplay(cmd[1]));
					int amount = 1;
					if (cmd.length == 4) {
						try {
							amount = Integer.parseInt(cmd[3]);
						} catch (NumberFormatException e) {
							amount = 1;
						}
					}
					if (p != null) {
						try {
							Item itemToRemove = new Item(Integer.parseInt(cmd[2]), amount);
							boolean multiple = itemToRemove.getAmount() > 1;
							p.getBank().removeItem(itemToRemove.getId());
							p.getPackets().sendGameMessage(player.getDisplayName() + " has removed "
									+ (multiple ? itemToRemove.getAmount() : "one") + " "
									+ itemToRemove.getDefinitions().getName() + (multiple ? "s" : "from your bank."));
							player.getPackets()
									.sendGameMessage("You have removed " + (multiple ? itemToRemove.getAmount() : "one")
											+ " " + itemToRemove.getDefinitions().getName() + (multiple ? "s" : "")
											+ " from " + p.getDisplayName() + " bank ");
							return true;
						} catch (NumberFormatException e) {
						}
					}
				}
				player.getPackets().sendGameMessage("Use: ::removebankitem player id (optional:amount)");
				return true;
			case "emptybank":
				player.getDialogueManager().startDialogue("EmptyBank");
				return true;
			case "kill":
				if (!isDeveloper(player))
					return true;
				if (!isDeveloper(player)) {
					player.getPackets().sendGameMessage("You don't have permission to use this command.");
					return true;
				}
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				if (target == null)
					return true;
				target.applyHit(new Hit(target, player.getHitpoints(), HitLook.REGULAR_DAMAGE));
				target.stopAll();
				return true;
			case "makemember":
				if (cmd.length == 2 || cmd.length == 3) {
					Player p = World.getPlayerByDisplayName(Utils.formatPlayerNameForDisplay(cmd[1]));
					int amount = 1;
					if (cmd.length == 3) {
						try {
							amount = Integer.parseInt(cmd[2]);
						} catch (NumberFormatException e) {
							amount = 1;
						}
					}
					if (p != null) {
						try {
							p.makeMember(amount);
							p.member = true;
							p.getPackets().sendGameMessage("You recieve " + amount + " days of member.");
							return true;
						} catch (NumberFormatException e) {
						}
					}
				}
				player.getPackets().sendGameMessage("Use: ::makemember username amount of days (optional:amount)");
				return true;
			case "removemember":
				player.member = false;
				player.memberTill = 0;
				return true;
			case "giveitem":
				if (cmd.length == 3 || cmd.length == 4) {
					Player p = World.getPlayerByDisplayName(Utils.formatPlayerNameForDisplay(cmd[1]));
					int amount = 1;
					if (cmd.length == 4) {
						try {
							amount = Integer.parseInt(cmd[3]);
						} catch (NumberFormatException e) {
							amount = 1;
						}
					}
					if (p != null) {
						try {
							Item itemToGive = new Item(Integer.parseInt(cmd[2]), amount);
							boolean multiple = itemToGive.getAmount() > 1;
							if (!p.getInventory().addItem(itemToGive)) {
								p.getBank().addItem(itemToGive.getId(), itemToGive.getAmount(), true);
							}
							p.getPackets()
									.sendGameMessage(player.getDisplayName() + " has given you "
											+ (multiple ? itemToGive.getAmount() : "one") + " "
											+ itemToGive.getDefinitions().getName() + (multiple ? "s" : ""));
							player.getPackets()
									.sendGameMessage("You have given " + (multiple ? itemToGive.getAmount() : "one")
											+ " " + itemToGive.getDefinitions().getName() + (multiple ? "s" : "")
											+ " to " + p.getDisplayName());
							return true;
						} catch (NumberFormatException e) {
						}
					}
				}
				player.getPackets().sendGameMessage("Use: ::giveitem player id (optional:amount)");
				return true;
			case "god":
				player.getPackets().sendGameMessage("Godmode is now "
						+ (player.getTemporaryAttributtes().get("GODMODE") != null ? "Inactive" : "Active."));
				if (player.getTemporaryAttributtes().get("GODMODE") != null)
					player.getTemporaryAttributtes().remove("GODMODE");
				else
					player.getTemporaryAttributtes().put("GODMODE", 0);
				return true;
			case "givepkp":
				if (!isDeveloper(player)) {
					player.getPackets().sendGameMessage("You don't have permission to use this command.");
					return true;
				}
				if (cmd.length == 2 || cmd.length == 3) {
					Player p = World.getPlayerByDisplayName(Utils.formatPlayerNameForDisplay(cmd[1]));
					int amount = 1;
					if (cmd.length == 3) {
						try {
							amount = Integer.parseInt(cmd[2]);
						} catch (NumberFormatException e) {
							amount = 1;
						}
					}
					if (p != null) {
						try {
							p.PKP += amount;
							p.getPackets().sendGameMessage("You recieve " + amount + " Pk points.");
							player.getPackets().sendGameMessage("You gave " + amount + " pkp to " + p.getDisplayName());
							player.getPackets().sendGameMessage("They now have " + p.PKP + "");

							return true;
						} catch (NumberFormatException e) {
						}
					}
				}
				player.getPackets().sendGameMessage("Use: ::giveitem player id (optional:amount)");
				return true;
			case "setks":
				if (cmd.length == 2 || cmd.length == 3) {
					Player p = World.getPlayerByDisplayName(Utils.formatPlayerNameForDisplay(cmd[1]));
					int amount = 1;
					if (cmd.length == 3) {
						try {
							amount = Integer.parseInt(cmd[2]);
						} catch (NumberFormatException e) {
							amount = 1;
						}
					}
					if (p != null) {
						try {
							p.killStreak += amount;
							p.getPackets().sendGameMessage("You recieve " + amount + " killstreaks.");
							return true;
						} catch (NumberFormatException e) {
						}
					}
				}
				player.getPackets().sendGameMessage("Use: ::killstreak player amount of kills (optional:amount)");
				return true;
			case "kills":
				if (cmd.length == 2 || cmd.length == 3) {
					Player p = World.getPlayerByDisplayName(Utils.formatPlayerNameForDisplay(cmd[1]));
					int amount = 1;
					if (cmd.length == 3) {
						try {
							amount = Integer.parseInt(cmd[2]);
						} catch (NumberFormatException e) {
							amount = 1;
						}
					}
					if (p != null) {
						try {
							p.killCount += amount;
							p.getPackets().sendGameMessage("You recieve " + amount + " kills.");
							return true;
						} catch (NumberFormatException e) {
						}
					}
				}
				player.getPackets().sendGameMessage("Use: ::kills player amount of kills (optional:amount)");
				return true;
			case "deaths":
				if (cmd.length == 2 || cmd.length == 3) {
					Player p = World.getPlayerByDisplayName(Utils.formatPlayerNameForDisplay(cmd[1]));
					int amount = 1;
					if (cmd.length == 3) {
						try {
							amount = Integer.parseInt(cmd[2]);
						} catch (NumberFormatException e) {
							amount = 1;
						}
					}
					if (p != null) {
						try {
							p.deathCount += amount;
							p.getPackets().sendGameMessage("You recieve " + amount + " kills.");
							return true;
						} catch (NumberFormatException e) {
						}
					}
				}
				player.getPackets().sendGameMessage("Use: ::kills player amount of kills (optional:amount)");
				return true;
			case "checkbank":
				String playername = "";
				for (int i = 1; i < cmd.length; i++) {
					playername += cmd[i] + ((i == cmd.length - 1 ? "" : " "));
				}
				playername = Utils.formatPlayerNameForProtocol(playername);
				if (!SerializableFilesManager.containsPlayer(playername)) {
					player.getPackets()
							.sendGameMessage("No such account named " + playername + " was found in the database.");
					return true;
				}

				Player p211 = World.getPlayerByDisplayName(playername);
				p211 = SerializableFilesManager.loadPlayer(playername);
				p211.setUsername(playername);
				player.getPackets().sendItems(95, p211.getBank().getContainerCopy());
				player.getBank().openPlayerBank(p211);
				return true;
			case "healother":
				if (cmd.length == 2 || cmd.length == 3) {
					Player p = World.getPlayerByDisplayName(Utils.formatPlayerNameForDisplay(cmd[1]));
					int amount = 1;
					if (cmd.length == 3) {
						try {
							amount = Integer.parseInt(cmd[2]);
						} catch (NumberFormatException e) {
							amount = 1;
						}
					}
					if (p != null) {
						try {
							p.getPrayer().restorePrayer(
									(int) ((int) (Math.floor(p.getSkills().getLevelForXp(Skills.PRAYER) * 2.5) + 990)
											* p.getAuraManager().getPrayerPotsRestoreMultiplier()));
							p.getPoison().makePoisoned(0);
							p.setRunEnergy(100);
							p.heal(p.getMaxHitpoints());
							p.getSkills().restoreSkills();
							p.getCombatDefinitions().resetSpecialAttack();
							p.getAppearence().generateAppearenceData();
							p.heal(amount);
							p.getPackets().sendGameMessage("You were healed.");
							return true;
						} catch (NumberFormatException e) {
						}
					}
				}
				player.getPackets().sendGameMessage("Use: ::healother player hp (optional:amount)");
				return true;
			case "removeotherdisplay":
				if (!isDeveloper(player)) {
					player.getPackets().sendGameMessage("You don't have permission to use this command.");
					return true;
				}
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				if (target != null) {
					target.setDisplayName(Utils.formatPlayerNameForDisplay(target.getUsername()));
					target.getPackets().sendGameMessage("Your display name was removed by "
							+ Utils.formatPlayerNameForDisplay(player.getUsername()) + ".");
					player.getPackets()
							.sendGameMessage("You have removed display name of " + target.getDisplayName() + ".");
					SerializableFilesManager.savePlayer(target);
				} else {
					File acc1 = new File(SerializableFilesManager.PATH + name.replace(" ", "_") + ".p");
					try {
						target = (Player) SerializableFilesManager.loadSerializedFile(acc1);
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
					target.setDisplayName(Utils.formatPlayerNameForDisplay(target.getUsername()));
					player.getPackets()
							.sendGameMessage("You have removed display name of " + target.getDisplayName() + ".");
					try {
						SerializableFilesManager.storeSerializableClass(target, acc1);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return true;
			case "removealloffers":
				player.getGeManager().cancelOffer();
				GrandExchange.removeAllOffers();
				player.getPackets().sendGameMessage("Removed all Grand Exchange offers.");
				return true;
			case "permban":
				if (!isDeveloper(player)) {
					player.getPackets().sendGameMessage("You don't have permission to use this command.");
					return true;
				}
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				if (target != null) {
					if (target.getRights() == 2) {
						target.getPackets()
								.sendGameMessage("<col=ff0000>" + player.getDisplayName() + " just tried to ban you!");
						return true;
					}
					if (target.getGeManager() == null) {
						target.setGeManager(new GrandExchangeManager());
					}
					target.getGeManager().setPlayer(target);
					target.getGeManager().init();
					GrandExchange.removeOffers(target);
					target.setPermBanned(true);
					target.getSession().getChannel().close();
					player.getPackets().sendGameMessage("You have perm banned: " + target.getDisplayName() + ".");
					player.getPackets()
							.sendGameMessage("Display: " + target.getDisplayName() + " User: " + target.getUsername());
				} else {
					name = Utils.formatPlayerNameForProtocol(name);
					if (!SerializableFilesManager.containsPlayer(name)) {
						player.getPackets().sendGameMessage(
								"Account name " + Utils.formatPlayerNameForDisplay(name) + " doesn't exist.");
						return true;
					}
					target = SerializableFilesManager.loadPlayer(name);
					if (target.getRights() == 2) {
						return true;
					}
					if (target.getGeManager() == null) {
						target.setGeManager(new GrandExchangeManager());
					}
					target.getGeManager().setPlayer(target);
					target.getGeManager().init();
					GrandExchange.removeOffers(target);
					target.setUsername(name);
					target.setPermBanned(true);
					player.getPackets()
							.sendGameMessage("You have perm banned: " + Utils.formatPlayerNameForDisplay(name) + ".");
					SerializableFilesManager.savePlayer(target);
				}
				return true;
			case "ipban":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				boolean loggedIn = true;
				if (target != null) {
					if (target.getRights() == 2)
						return true;
					IPBanL.ban(target, loggedIn);
					player.getPackets().sendGameMessage(
							"You've permanently ipbanned " + (loggedIn ? target.getDisplayName() : name) + ".");
				}
				return true;
			case "heal":
				player.getPrayer().restorePrayer(
						(int) ((int) (Math.floor(player.getSkills().getLevelForXp(Skills.PRAYER) * 2.5) + 990)
								* player.getAuraManager().getPrayerPotsRestoreMultiplier()));
				if (player.getPoison().isPoisoned())
					player.getPoison().makePoisoned(0);
				player.setRunEnergy(100);
				player.heal(player.getMaxHitpoints());
				player.getSkills().restoreSkills();
				player.getCombatDefinitions().resetSpecialAttack();
				player.getAppearence().generateAppearenceData();
				int hitpointsModification = (int) (player.getMaxHitpoints() * 0.15);
				player.heal(hitpointsModification + 20, hitpointsModification);
				return true;

			case "unipban":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				File acc11 = new File(SerializableFilesManager.PATH + name.replace(" ", "_") + ".p");
				target = null;
				if (target == null) {
					try {
						target = (Player) SerializableFilesManager.loadSerializedFile(acc11);
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
				}
				IPBanL.unban(target);
				player.getPackets().sendGameMessage("You've unipbanned " + target.getDisplayName() + ".");
				try {
					SerializableFilesManager.storeSerializableClass(target, acc11);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;

			case "masterallskills":
				if (cmd.length < 2) {
					for (int skill1 = 0; skill1 < 25; skill1++)
						player.getSkills().addXp(skill1, 150000000);
					return true;
				}
				try {
					player.getSkills().addXp(Integer.valueOf(cmd[1]), 150000000);
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: ::master skill");
				}
				return true;

			case "setptsother":
				if (cmd.length == 2 || cmd.length == 3) {
					Player p = World.getPlayerByDisplayName(Utils.formatPlayerNameForDisplay(cmd[1]));
					int amount = 1;
					if (cmd.length == 3) {
						try {
							amount = Integer.parseInt(cmd[2]);
						} catch (NumberFormatException e) {
							amount = 1;
						}
					}
					if (p != null) {
						try {
							player.getPackets().sendGameMessage("You gave " + p.getUsername() + " "
									+ Utils.getFormattedNumber(amount, ',') + " " + Settings.SERVER_NAME + " points.");
							p.getPackets().sendGameMessage("You recieved " + Utils.getFormattedNumber(amount, ',') + " "
									+ Settings.SERVER_NAME + " points.");
							p.setAvalonPoints(p.getAvalonPoints() + amount);
							return true;
						} catch (NumberFormatException e) {
						}
					}
				} else {
					player.getPackets().sendGameMessage("Use: ::setkillsother username amount");
				}
				return true;

			case "setkillsother":
				if (cmd.length == 2 || cmd.length == 3) {
					Player p = World.getPlayerByDisplayName(Utils.formatPlayerNameForDisplay(cmd[1]));
					int amount = 1;
					if (cmd.length == 3) {
						try {
							amount = Integer.parseInt(cmd[2]);
						} catch (NumberFormatException e) {
							amount = 1;
						}
					}
					if (p != null) {
						try {
							p.killCount = amount;
							return true;
						} catch (NumberFormatException e) {
						}
					}
				} else {
					player.getPackets().sendGameMessage("Use: ::setkillsother username amount");
				}
				return true;

			case "setduelkillsother":
				if (cmd.length == 2 || cmd.length == 3) {
					Player p = World.getPlayerByDisplayName(Utils.formatPlayerNameForDisplay(cmd[1]));
					int amount = 1;
					if (cmd.length == 3) {
						try {
							amount = Integer.parseInt(cmd[2]);
						} catch (NumberFormatException e) {
							amount = 1;
						}
					}
					if (p != null) {
						try {
							p.setDuelkillCount(amount);
							return true;
						} catch (NumberFormatException e) {
						}
					}
				} else {
					player.getPackets().sendGameMessage("Use: ::setduelkillsother username amount");
				}
				return true;

			case "settitlecolor":
			case "settitlecolour":
			case "changetitlecolor":
			case "changetitlecolour":
			case "titlecolor":
			case "titlecolour":
				player.getPackets().sendRunScript(109, new Object[] { "Please enter the title color in HEX format." });
				player.temporaryAttribute().put("titlecolor", Boolean.TRUE);
				return true;

			case "mb":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't do that command here.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2539, 4716, 0));
				player.getPackets().sendGameMessage("You have teleported to mage bank.");
				return true;

			case "clanwars":
			case "cw":
			case "clws":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't do that command here.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2970, 9679, 0));
				player.getPackets().sendGameMessage("You have teleported to clan wars.");
				return true;

			case "pvp":
				EdgevillePvPControler.enterPVP(player);
				return true;

			case "east":
			case "easts":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't do that command here.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3349, 3647, 0));
				player.getPackets().sendGameMessage("You have teleported to east dragons.");
				return true;

			case "edgeville":
			case "edge":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't use " + cmd[0] + " at this location.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3082, 3545, 0));
				player.getPackets().sendGameMessage("You have teleported to edgeville.");
				return true;

			case "gdz":
			case "gds":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't do that command here.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3289, 3886, 0));
				player.getPackets().sendGameMessage("You have teleported to greater demons.");
				return true;

			case "kbd":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't do that command here.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3032, 3836, 0));
				player.getPackets().sendGameMessage("You have teleported outside king black dragon lair.");
				return true;

			case "44ports":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't do that command here.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2980, 3867, 0));
				player.getPackets().sendGameMessage("You have teleported lvl 44 wilderness port.");
				return true;

			case "iceplatue":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't do that command here.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2962, 3918, 0));
				player.getPackets().sendGameMessage("You have teleported to ice platue.");
				return true;

			case "50ports":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't do that command here.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3308, 3916, 0));
				player.getPackets().sendGameMessage("You have teleported to lvl 50 wilderness portal.");
				return true;

			case "castle":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't do that command here.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3012, 3631, 0));
				player.getPackets().sendGameMessage("You have teleported to castle.");
				return true;

			case "bh":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't do that command here.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3153, 3709, 0));
				player.getPackets().sendGameMessage("You have teleported to bounty hunter crater.");
				return true;

			case "altar":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't do that command here.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2950, 3821, 0));
				player.getPackets().sendGameMessage("You have teleported to wilderness altar.");
				return true;

			case "wests":
			case "west":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't do that command here.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2978, 3598, 0));
				player.getPackets().sendGameMessage("You have teleported to west dragons.");
				return true;

			case "zerk":
			case "zerkspot":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't do that command here.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3043, 3552, 0));
				player.getPackets().sendGameMessage("You have teleported to zerker spot.");
				return true;

			case "bridspot":
			case "brid":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't do that command here.");
					return true;
				}
				OldMagicSystem.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3013, 3553, 0));
				player.getPackets().sendGameMessage("You have teleported to hybridding spot.");
				return true;

			case "teles":
				player.getInterfaceManager().sendInterface(275);
				player.getPackets().sendIComponentText(275, 1, "*Teleports*");
				player.getPackets().sendIComponentText(275, 10, "");
				player.getPackets().sendIComponentText(275, 11,
						"::zerk *Teleports to western side of edgeville. SINGLE");
				player.getPackets().sendIComponentText(275, 12, "::easts *Teleports to lvl 20 east dragons. SINGLE");
				player.getPackets().sendIComponentText(275, 13, "::wests *Teleports to lvl 13 west dragons. SINGLE");
				player.getPackets().sendIComponentText(275, 14, "::mb *Teleports inside the mage bank. NOT WILDY");
				player.getPackets().sendIComponentText(275, 15,
						"::brid *Teleports to west side of edgeville wilderness. SINGLE");
				player.getPackets().sendIComponentText(275, 16, "::gdz *Teleports to Greater demons in lvl 48. MULTI");
				player.getPackets().sendIComponentText(275, 17,
						"::44ports *Teleports to lvl 44 wilderness portal. SINGLE");
				player.getPackets().sendIComponentText(275, 18,
						"::iceplatue *Teleports to ice platue in lvl 50 wilderness. SINGLE");
				player.getPackets().sendIComponentText(275, 19,
						"::kbd *Teleports outside king black dragon lair. MULTI");
				player.getPackets().sendIComponentText(275, 20,
						"::50ports *Teleports to lvl 50 wilderness portal. MULTI");
				player.getPackets().sendIComponentText(275, 21,
						"::bh *Teleports inside the Bounty hunter crate. MULTI");
				player.getPackets().sendIComponentText(275, 22, "::revs *Teleports to rev cave. SINGLE & MULTI");
				player.getPackets().sendIComponentText(275, 23,
						"::altar *Teleports to an altar deep in west of wilderness.");
				player.getPackets().sendIComponentText(275, 24,
						"::castle *Teleports to castle near west dragons. MULTI");
				return true;

			case "showrisk":
				Integer[][] slots = ButtonHandler.getItemSlotsKeptOnDeath(player, player.isAtWild(), player.hasSkull(),
						player.getPrayer().usingPrayer(0, 10) || player.getPrayer().usingPrayer(1, 0));
				Item[][] riskitems = ButtonHandler.getItemsKeptOnDeath(player, slots);
				long riskedWealth = 0;
				long carriedWealth = 0;
				for (Item item11 : riskitems[1]) {
					if (item11 == null)
						continue;
					carriedWealth = riskedWealth += GrandExchange.getPrice(item11.getId()) * item11.getAmount();
				}
				player.getPackets()
						.sendGameMessage("My risk is: " + Utils.getFormattedNumber(carriedWealth, ',') + " coins.");
				player.setNextForceTalk(
						new ForceTalk("My risk is: " + Utils.getFormattedNumber(carriedWealth, ',') + " coins."));
				return true;

			case "score":
			case "kdr":
				double kill = player.getKillCount();
				double death = player.getDeathCount();
				double dr = kill / death;
				player.setNextForceTalk(new ForceTalk("Kills: " + player.getKillCount() + " Deaths: "
						+ player.getDeathCount() + " Ratio: " + new DecimalFormat("##.#").format(dr)));
				return true;

			case "playerslist":
			case "playerlist":
				player.getInterfaceManager().sendInterface(275);
				int number = 0;
				for (int i = 0; i < 100; i++) {
					player.getPackets().sendIComponentText(275, i, "");
				}
				for (Player p5 : World.getPlayers()) {
					if (p5 == null)
						continue;
					number++;
					String titles = "";
					if (p5.getRights() == 1) {
						titles = "<col=ff0000><shad=ff0050>Player Moderator</col></shad> <img=0> ";
					}
					if (p5.getRights() == 2) {
						titles = "<img=1> <col=00ffff><shad=0000ff>Developer</col></shad> <img=1> ";
					}
					if (p5.getRights() == 0 && p5.isSupporter()) {
						titles = "<col=58ACFA><shad=2E2EFE>" + Settings.SERVER_NAME + " Support</shad></col> <img=12> ";
					}
					if (p5.getUsername().equalsIgnoreCase("andreas")) {
						titles = "<img=1> <col=ff0000><shad=000000>The Real Viking</col></shad> <img=1> ";
					}
					player.getPackets().sendIComponentText(275, (13 + number), titles + "" + p5.getDisplayName());
				}
				player.getPackets().sendIComponentText(275, 1, Settings.SERVER_NAME);
				player.getPackets().sendIComponentText(275, 10, " ");
				player.getPackets().sendIComponentText(275, 11, "Players Online: " + number);
				player.getPackets().sendIComponentText(275, 12, " ");
				player.getPackets().sendGameMessage("There are currently " + World.getPlayers().size()
						+ " players playing " + Settings.SERVER_NAME + ".");
				return true;

			case "ks":
			case "killstreak":
				player.setNextForceTalk(new ForceTalk("My current killstreak: " + player.killStreak));
				return true;

			case "players":
				player.getPackets().sendGameMessage("There are currently " + World.getPlayers().size()
						+ " players playing " + Settings.SERVER_NAME + ".");
				return true;

			case "title":
				if (cmd.length < 2) {
					player.getPackets().sendGameMessage("Use: ::title id");
					return true;
				}
				try {
					player.getAppearence().setTitle(Integer.valueOf(cmd[1]));
					player.getPackets().sendGameMessage("Title set to: " + player.getAppearence().getTitleString());
				} catch (NumberFormatException e) {
					player.getPackets().sendGameMessage("Use: ::title id");
				}
				return true;
			case "customtitle":
				if (player.isMember()) {
					player.getPackets().sendGameMessage("You need to be at least a member to use ::customtitle.");
					return true;
				} else {
					player.temporaryAttribute().put("customtitle", Boolean.TRUE);
					player.getPackets().sendInputNameScript("Enter your custom title");
					player.getPackets().sendGameMessage("To get the title AFTER your name use commands ::customtitle.");
				}
				return true;

			case "customname":
				player.getPackets().sendRunScript(109,
						new Object[] { "Please enter the color you would like. (HEX FORMAT)" });
				player.temporaryAttribute().put("customname", Boolean.TRUE);
				return true;

			case "inters":
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage("Use: ::inter interfaceId");
					return true;
				}
				try {
					int interId1 = Integer.valueOf(cmd[1]);
					for (int componentId = 0; componentId < Utils
							.getInterfaceDefinitionsComponentsSize(interId1); componentId++) {
						player.getPackets().sendIComponentText(interId1, componentId, "cid: " + componentId);
					}
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: ::inter interfaceId");
				}
				return true;

			case "customtitle1":
			case "customtitle2":
				if (!player.isMember()) {
					player.getPackets().sendGameMessage("You need to be at least a member to use ::customtitle.");
					return true;
				} else {
					player.temporaryAttribute().put("setcustom_title2", Boolean.TRUE);
					player.getPackets().sendInputNameScript("Enter your custom title");
					player.getPackets().sendGameMessage("To get the title BEFORE your name use commands ::customtitle");
				}
				return true;

			case "checkoffers":
				GrandExchange.sendOfferTracker(player);
				return true;

			case "setdisplay":
			case "changedisplay":
				if (!player.isMember()) {
					player.getPackets().sendGameMessage("You need to be at least a member to use ::setdisplay.");
					return true;
				} else {
					player.temporaryAttribute().put("setdisplay", Boolean.TRUE);
					player.getPackets().sendInputNameScript("Enter the display name you wish:");
				}
				return true;
			case "website":
			case "forum":
			case "forums":
				player.getPackets().sendOpenURL(Settings.WEBSITE_LINK);
				return true;
			case "update":
			case "updates":
				player.getPackets().sendOpenURL(Settings.UPDATE_LINK);
				return true;
			case "client":
			case "newclient":
				player.getPackets().sendOpenURL(Settings.NEW_CLIENT_LINK);
				return true;
			case "lockxp":
				player.setXpLocked(player.isXpLocked() ? false : true);
				player.getPackets()
						.sendGameMessage("You have " + (player.isXpLocked() ? "unlocked" : "locked") + " your xp.");
				return true;
			case "hideyell":
			case "toggleyell":
				player.setYellOff(!player.isYellOff());
				player.getPackets()
						.sendGameMessage("You have turned " + (player.isYellOff() ? "off" : "on") + " yell.");
				return true;
			case "changepass":
				String message = "";
				for (int i = 1; i < cmd.length; i++)
					message += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				if (message.length() > 15 || message.length() < 5) {
					player.getPackets().sendGameMessage("You cannot set your password to over 15 chars.");
					return true;
				}
				player.setPassword(Encrypt.encryptSHA1(cmd[1]));
				player.getPackets().sendGameMessage("You changed your password! Your new password is " + cmd[1] + ".");
				return true;
			case "setlvl":
			case "setlevel":
				if (!player.canUseCommand()) {
					player.getPackets().sendGameMessage("You can't do that command here.");
					return true;
				}
				if (player.getEquipment().wearingArmour()) {
					player.getPackets().sendGameMessage("You can't wear any armour when using this command.");
					return true;
				}
				name = "";
				for (int i = 3; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				if (cmd.length < 3/* && player.getRights() == 0 */) {
					player.getPackets().sendGameMessage("Usage ::setlevel skillId level");
					return true;
				}
				try {
					int skill1 = Integer.parseInt(cmd[1]);
					int level = Integer.parseInt(cmd[2]);
					if (level < 0 || level > 99) {
						player.getPackets().sendGameMessage("Please choose a valid level.");
						return true;
					}
					if (skill1 < 0 || skill1 > 6) {
						player.getPackets().sendGameMessage("You can only set combat stats 0-6.");
						return true;
					}
					player.getSkills().set(skill1, level);
					player.getSkills().setXp(skill1, Skills.getXPForLevel(level));
					player.getDialogueManager().startDialogue("LevelUp", skill1);
					player.getAppearence().generateAppearenceData();
					player.getSkills().switchXPPopup(true);
					player.getSkills().switchXPPopup(true);
					return true;
				} catch (NumberFormatException e) {
					player.getPackets().sendGameMessage("Usage ::setlevel skillId level");
				}
				return true;
			case "setdeathsother":
				if (cmd.length == 2 || cmd.length == 3) {
					Player p = World.getPlayerByDisplayName(Utils.formatPlayerNameForDisplay(cmd[1]));
					int amount = 1;
					if (cmd.length == 3) {
						try {
							amount = Integer.parseInt(cmd[2]);
						} catch (NumberFormatException e) {
							amount = 1;
						}
					}
					if (p != null) {
						try {
							p.deathCount = amount;
							return true;
						} catch (NumberFormatException e) {
						}
					}
				} else {
					player.getPackets().sendGameMessage("Use: ::setdeathsother username amount");
				}
				return true;
			case "bitem":
				if (player.isAtWild()) {
					player.getPackets().sendGameMessage("You can't use ::" + cmd[0] + " in the wilderness.");
					return true;
				}
				try {
					int itemId1 = Integer.valueOf(cmd[1]);
					int amount = 1;
					if (cmd.length == 3)
						amount = Integer.parseInt(cmd[2]);
					player.getBank().addItem(itemId1, amount, true);
					player.getPackets().sendGameMessage("You spawn " + amount + " x "
							+ ItemDefinitions.getItemDefinitions(itemId1).getName() + " to your bank.");
				} catch (NumberFormatException e) {
					player.getPackets().sendGameMessage("Use: ::bankitem id (optional:amount)");
				}
				return true;
			case "item":
				if (player.isAtWild()) {
					player.getPackets().sendGameMessage("You can't use ::" + cmd[0] + " in the wilderness.");
					return true;
				}
				try {
					int itemId1 = Integer.valueOf(cmd[1]);
					int amount = 1;
					if (cmd.length == 3)
						amount = Integer.parseInt(cmd[2]);
					player.getInventory().addItem(itemId1, amount);
					player.getPackets().sendGameMessage("You spawn " + amount + " x "
							+ ItemDefinitions.getItemDefinitions(itemId1).getName() + ".");
				} catch (NumberFormatException e) {
					player.getPackets().sendGameMessage("Use: ::item id (optional:amount)");
				}
				return true;

			case "setlevelother":
				name = "";
				for (int i = 3; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				if (cmd.length < 4) {
					player.getPackets().sendGameMessage("Usage ::setlevel skillId level username");
					return true;
				}
				try {
					int skill = Integer.parseInt(cmd[1]);
					int level = Integer.parseInt(cmd[2]);
					if (level < 0 || level > 99) {
						player.getPackets().sendGameMessage("Please choose a valid level.");
						return true;
					}
					if (skill == 3 && level < 10) {
						player.getPackets().sendGameMessage("You cannot have lower than 10 hitpoints.");
						return true;
					}
					if (skill < 0 || skill > 24) {
						player.getPackets().sendGameMessage("You can only set combat stats 0-24");
						return true;
					}
					target.getPackets().sendGameMessage("You were given " + level + " levels in " + skill + ".");
					target.getSkills().set(skill, level);
					target.getSkills().setXp(skill, Skills.getXPForLevel(level));
					target.getDialogueManager().startDialogue("LevelUp", skill);
					target.getAppearence().generateAppearenceData();
					target.getSkills().switchXPPopup(true);
					target.getSkills().switchXPPopup(true);
					return true;
				} catch (NumberFormatException e) {
					player.getPackets().sendGameMessage("Usage ::setlevel skillId level");
				}
				return true;
			case "wildy":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				if (target == null)
					player.getPackets().sendGameMessage("Couldn't find player " + name + ".");
				else
					target.getControlerManager().startControler("WildernessControler");
				target.setNextWorldTile(player);
				return true;
			case "teletome":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				if (target == null)
					player.getPackets().sendGameMessage("Couldn't find player " + name + ".");
				else {
					target.setNextWorldTile(player);
				}
				return true;

			case "telealltome":
				for (Player players : World.getPlayers()) {
					players.setNextWorldTile(player);
				}
				return true;

			case "matchaccounts":// admin tool more specific
				String[] playerName = { Utils.formatPlayerNameForProtocol(cmd[1]),
						Utils.formatPlayerNameForProtocol(cmd[2]) };
				Player[] targets = new Player[2];
				byte count1 = 0;
				for (String pn : playerName) {
					if (!SerializableFilesManager.containsPlayer(pn)) {
						player.getPackets().sendGameMessage("No such account named %s was found in the database.", pn);
						return true;
					}
					targets[count1] = SerializableFilesManager.loadPlayer(pn);
					targets[count1].setUsername(pn);
					count1++;
				}

				if (targets[0] == null || targets[1] == null)
					return true;

				ArrayList<String> ip1 = targets[0].getIPList(), ip2 = targets[1].getIPList();
				boolean match = false;
				for (String ip : ip1)
					for (String ipx : ip2) {
						if (ip == null || ipx == null)
							continue;
						if (ip.equalsIgnoreCase(ipx)) {
							player.getPackets().sendPanelBoxMessage("IP link between " + playerName[0] + " and "
									+ playerName[1] + " found! [" + ip + "]");
							match = true;
						}
					}
				player.getPackets().sendGameMessage(
						match ? "One or more matches found between the accounts %s and %s"
								: "No matches were found between the accounts %s and %s",
						targets[0].getUsername(), targets[1].getUsername());
				return true;
			case "pos":
				player.getPackets()
						.sendPanelBoxMessage("Coords: " + player.getX() + ", " + player.getY() + ", "
								+ player.getPlane() + ", regionId: " + player.getRegionId() + ", rx: "
								+ player.getChunkX() + ", ry: " + player.getChunkY());
				return true;

			case "killnpc":
				for (NPC n : World.getNPCs()) {
					if (n == null || n.getId() != Integer.parseInt(cmd[1]))
						continue;
					n.sendDeath(n);
				}
				return true;
			case "clearchat":
				player.sm(" ");
				player.sm(" ");
				player.sm(" ");
				player.sm(" ");
				player.sm(" ");
				player.sm(" ");
				player.sm(" ");
				player.sm(" ");
				player.sm(" ");
				player.sm(" ");
				return true;
			case "master":
				for (int i = 0; i < 24; i++) {
					player.getSkills().set(i, 99);
					player.getSkills().setXp(i, Skills.getXPForLevel(99));
				}
				player.getSkills().set(24, 120);
				player.getSkills().setXp(24, Skills.getXPForLevel(120));
				for (int i = 0; i < 25; i++)
					player.getDialogueManager().startDialogue("LevelUp", i);
				player.getAppearence().generateAppearenceData();
				player.getSkills().switchXPPopup(true);
				player.getSkills().switchXPPopup(true);
				return true;
			case "milestonelevels":
				for (int i = 0; i < 24; i++) {
					player.getSkills().setXp(i, Skills.getXPForLevel(player.getSkills().getLevelForXp(i) + 10));
					player.getSkills().set(i, player.getSkills().getLevelForXp(i));
				}
				player.getAppearence().generateAppearenceData();
				player.getSkills().switchXPPopup(true);
				player.getSkills().switchXPPopup(true);
				return true;
			case "adnpc":
				try {
					World.spawnNPC(Integer.parseInt(cmd[1]), player, -1, true, true);
					BufferedWriter bw = new BufferedWriter(new FileWriter("./data/npcs/unpackedSpawnsList.txt", true));
					bw.write("//" + NPCDefinitions.getNPCDefinitions(Integer.parseInt(cmd[1])).name + " spawned by "
							+ player.getUsername());
					bw.newLine();
					bw.write(Integer.parseInt(cmd[1]) + " - " + player.getX() + " " + player.getY() + " "
							+ player.getPlane());
					bw.newLine();
					bw.close();
					player.sm("Added NPC: " + NPCDefinitions.getNPCDefinitions(Integer.parseInt(cmd[1])).name + " to X:"
							+ player.getX() + ", Y:" + player.getY() + ", H:" + player.getPlane());
				} catch (Throwable t) {
					t.printStackTrace();
				}
				return true;
			case "adobject":
				try {
					World.spawnObject(new WorldObject(Integer.parseInt(cmd[1]), 10, 0, player));
					BufferedWriter bw = new BufferedWriter(new FileWriter("./data/map/unpackedSpawnsList.txt", true));
					bw.write("//" + ObjectDefinitions.getObjectDefinitions(Integer.parseInt(cmd[1])).name
							+ " spawned by " + player.getUsername());
					bw.newLine();
					bw.write(Integer.parseInt(cmd[1]) + " 10 0 - " + player.getX() + " " + player.getY() + " "
							+ player.getPlane() + " true");
					bw.newLine();
					bw.close();
					player.sm("Added Object: " + ObjectDefinitions.getObjectDefinitions(Integer.parseInt(cmd[1])).name
							+ " to X:" + player.getX() + ", Y:" + player.getY() + ", H:" + player.getPlane());
				} catch (Throwable t) {
					t.printStackTrace();
				}
				return true;
			case "npc":
				try {
					World.spawnNPC(Integer.parseInt(cmd[1]), player, -1, true, true);
					player.sm(Integer.valueOf(cmd[1]) + " " + player.getX() + " " + player.getY() + " "
							+ player.getPlane());
					Logger.log("NPC", Integer.valueOf(cmd[1]) + " " + player.getX() + " " + player.getY() + " "
							+ player.getPlane());
					return true;
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: ::npc id(Integer)");
				}
				return true;
			case "object":
				try {
					type = cmd.length > 2 ? Integer.parseInt(cmd[2]) : 10;
					int rotation = Integer.parseInt(cmd[3]);
					if (type > 22 || type < 0) {
						type = 10;
					}
					World.spawnObject(new WorldObject(Integer.valueOf(cmd[1]), type, rotation, player.getX(),
							player.getY(), player.getPlane()));
					player.sm(Integer.valueOf(cmd[1]) + " 10 0 - " + player.getX() + " " + player.getY() + " "
							+ player.getPlane());
					Logger.log("Object", Integer.valueOf(cmd[1]) + " 10 0 - " + player.getX() + " " + player.getY()
							+ " " + player.getPlane());
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: setkills id");
				}
				return true;
			case "changepassother":
				name = cmd[1];
				File acc1 = new File(SerializableFilesManager.PATH + name.replace(" ", "_") + ".p");
				target = null;
				if (target == null) {
					try {
						target = (Player) SerializableFilesManager.loadSerializedFile(acc1);
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
				}
				target.setPassword(Encrypt.encryptSHA1(cmd[2]));
				player.getPackets().sendGameMessage("You changed their password!");
				try {
					SerializableFilesManager.storeSerializableClass(target, acc1);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			case "forcemovement":
				WorldTile toTile = player.transform(0, 5, 0);
				player.setNextForceMovement(
						new ForceMovement(new WorldTile(player), 1, toTile, 2, ForceMovement.NORTH));
				return true;
			case "gamble":
				player.getPackets().sendInputIntegerScript(true,
						"Enter the amount you wish to gamble (Max 100m, Min 1m):");
				player.temporaryAttribute().put("gambling", Boolean.TRUE);
				return true;

			case "getpts":
				player.setAvalonPoints(1000000);
				player.getPackets().sendGameMessage("You now have 1,000,000 " + Settings.SERVER_NAME + " points.");
				return true;
			case "getpkp":
				player.setPKP(1000000);
				player.getPackets().sendGameMessage("You now have 1,000,000 pk points.");
				return true;
			case "resetpkp":
				player.setPKP(0);
				return true;
			case "tonpc":
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage("Use: ::tonpc id(-1 for player)");
					return true;
				}
				try {
					player.getAppearence().transformIntoNPC(Integer.valueOf(cmd[1]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: ::tonpc id(-1 for player)");
				}
				return true;
			case "inter":
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage("Use: ::inter interfaceId");
					return true;
				}
				try {
					player.getInterfaceManager().sendInterface(Integer.valueOf(cmd[1]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: ::inter interfaceId");
				}
				return true;
			case "cinter":
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage("Use: ::inter interfaceId");
					return true;
				}
				try {
					player.getInterfaceManager().sendChatBoxInterface(Integer.valueOf(cmd[1]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: ::inter interfaceId");
				}
				return true;
			case "empty":
				player.getInventory().reset();
				return true;
			case "bank":
				player.getBank().openBank();
				return true;
			case "tele":
				if (cmd.length < 3) {
					player.getPackets().sendPanelBoxMessage("Use: ::tele coordX coordY");
					return true;
				}
				try {
					player.resetWalkSteps();
					player.setNextWorldTile(new WorldTile(Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]),
							cmd.length >= 4 ? Integer.valueOf(cmd[3]) : player.getPlane()));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: ::tele coordX coordY plane");
				}
				return true;
			case "emote":
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage("Use: ::emote id");
					return true;
				}
				player.animate(new Animation(-1));
				try {
					player.animate(new Animation(-1));
					player.animate(new Animation(Integer.valueOf(cmd[1])));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: ::emote id");
				}
				return true;
			case "remote":
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage("Use: ::emote id");
					return true;
				}
				try {
					player.getAppearence().setRenderEmote(Integer.valueOf(cmd[1]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: ::emote id");
				}
				return true;
			case "spec":
				player.getCombatDefinitions().resetSpecialAttack();
				return true;
			case "gfx":
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage("Use: ::gfx id");
					return true;
				}
				try {
					player.gfx(new Graphics(Integer.valueOf(cmd[1]), 0, 0));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: ::gfx id");
				}
				return true;
			case "anim":
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage("Use: ::anim id");
					return true;
				}
				try {
					player.animate(new Animation(Integer.valueOf(cmd[1])));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: ::anim id");
				}
				return true;
			case "sync":
				int animId = Integer.parseInt(cmd[1]);
				int gfxId = Integer.parseInt(cmd[2]);
				int height = cmd.length > 3 ? Integer.parseInt(cmd[3]) : 0;
				player.animate(new Animation(animId));
				player.gfx(new Graphics(gfxId, 0, height));
				return true;
			}
		}
		return false;
	}

	public static boolean processSupportCommands(Player player, String[] cmd, boolean console, boolean clientCommand) {
		String name;
		Player target;
		if (clientCommand) {
			;
		} else {
			switch (cmd[0]) {
			case "forcekick":
			case "kick":
				if (!isDeveloper(player))
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				target.forceLogout();
				player.getPackets().sendGameMessage("You have kicked: " + target.getDisplayName() + ".");
				return true;
			case "unnull":
			case "sendhome":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				target.unlock();
				target.getControlerManager().forceStop();
				if (target.getNextWorldTile() == null)
					target.setNextWorldTile(Settings.RESPAWN_PLAYER_LOCATION);
				target.getPackets().sendGameMessage("You have been sent home by: " + player.getDisplayName());
				player.getPackets().sendGameMessage("You have sent home: " + target.getDisplayName() + ".");
				return true;
			case "teleto":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				if (target == null)
					player.getPackets().sendGameMessage("Couldn't find player " + name + ".");
				if (target.getAppearence().isHidden()) {
					player.getPackets().sendGameMessage("Couldn't find player " + name + ".");
					return true;
				} else
					player.setNextWorldTile(target);
				return true;

			}
		}
		return clientCommand;
	}

	public static boolean processModCommand(Player player, String[] cmd, boolean console, boolean clientCommand) {
		if (clientCommand) {
		} else {
			String name;
			Player target;
			switch (cmd[0]) {
			case "answer":
			case "open":
			case "access":
			case "openticket":
				if (player.isInLiveChat) {
					player.sm("<col=ff000>You cannot handle more than one ticket at a time.");
					return true;
				}
				String username2 = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				Player requester = World.getPlayerByDisplayName(username2);
				if (requester.isInLiveChat) {
					player.sm(
							"<col=ff000>" + requester.getDisplayName() + " is currently already placed in a chatroom.");
					return true;
				}
				if (requester.isRequestingChat)
					TicketSystem.answerTicket(requester, player);
				else
					player.sm("<col=ff000>" + requester.getDisplayName() + " has no open tickets.");
				return true;
			case "mute":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				if (target != null) {
					target.mute(target.getUsername(), "-", 1);
					player.getPackets().sendGameMessage("You have muted 1 day: " + target.getDisplayName() + ".");
					player.getPackets()
							.sendGameMessage("Display: " + target.getDisplayName() + " User: " + target.getUsername());
				} else {
					name = Utils.formatPlayerNameForProtocol(name);
					if (!SerializableFilesManager.containsPlayer(name)) {
						player.getPackets().sendGameMessage(
								"Account name " + Utils.formatPlayerNameForDisplay(name) + " doesn't exist.");
						return true;
					}
					target = SerializableFilesManager.loadPlayer(name);
					target.setUsername(name);
					target.mute(target.getUsername(), "-", 1);
					player.getPackets()
							.sendGameMessage("You have muted 1 day: " + Utils.formatPlayerNameForDisplay(name) + ".");
					SerializableFilesManager.savePlayer(target);
				}
				return true;

			case "unmute":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				if (target != null) {
					target.liftMute(false, player);
					player.getPackets().sendGameMessage("You have unmuted: " + target.getDisplayName() + ".");
					player.getPackets()
							.sendGameMessage("Display: " + target.getDisplayName() + " User: " + target.getUsername());
				} else {
					name = Utils.formatPlayerNameForProtocol(name);
					if (!SerializableFilesManager.containsPlayer(name)) {
						player.getPackets().sendGameMessage(
								"Account name " + Utils.formatPlayerNameForDisplay(name) + " doesn't exist.");
						return true;
					}
					target = SerializableFilesManager.loadPlayer(name);
					target.setUsername(name);
					target.liftMute(false, player);
					player.getPackets()
							.sendGameMessage("You have unmuted: " + Utils.formatPlayerNameForDisplay(name) + ".");
					SerializableFilesManager.savePlayer(target);
				}
				return true;

			case "yell":
				String data = "";
				for (int i = 1; i < cmd.length; i++) {
					data += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				}
				ServerMessage.filterMessage(player, Utils.fixChatMessage(data), false);
				archiveYell(player, Utils.fixChatMessage(data));
				return true;

			case "permban":
				if (!isDeveloper(player)) {
					player.getPackets().sendGameMessage("You don't have permission to use this command.");
					return true;
				}
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				if (target != null) {
					if (target.getRights() == 2) {
						target.getPackets()
								.sendGameMessage("<col=ff0000>" + player.getDisplayName() + " just tried to ban you!");
						return true;
					}
					if (target.getGeManager() == null) {
						target.setGeManager(new GrandExchangeManager());
					}
					target.getGeManager().setPlayer(target);
					target.getGeManager().init();
					GrandExchange.removeOffers(target);
					target.setPermBanned(true);
					target.getSession().getChannel().close();
					player.getPackets().sendGameMessage("You have perm banned: " + target.getDisplayName() + ".");
					player.getPackets()
							.sendGameMessage("Display: " + target.getDisplayName() + " User: " + target.getUsername());
				} else {
					name = Utils.formatPlayerNameForProtocol(name);
					if (!SerializableFilesManager.containsPlayer(name)) {
						player.getPackets().sendGameMessage(
								"Account name " + Utils.formatPlayerNameForDisplay(name) + " doesn't exist.");
						return true;
					}
					target = SerializableFilesManager.loadPlayer(name);
					if (target.getRights() == 2)
						return true;
					if (target.getGeManager() == null)
						target.setGeManager(new GrandExchangeManager());
					target.getGeManager().setPlayer(target);
					target.getGeManager().init();
					GrandExchange.removeOffers(target);
					target.setUsername(name);
					target.setPermBanned(true);
					player.getPackets()
							.sendGameMessage("You have perm banned: " + Utils.formatPlayerNameForDisplay(name) + ".");
					SerializableFilesManager.savePlayer(target);
				}
				return true;
			case "ban":
				if (!isDeveloper(player))
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				if (target != null) {
					if (target.getRights() == 2) {
						target.getPackets()
								.sendGameMessage("<col=ff0000>" + player.getDisplayName() + " just tried to ban you!");
						return true;
					}
					if (target.getGeManager() == null) {
						target.setGeManager(new GrandExchangeManager());
					}
					target.getGeManager().setPlayer(target);
					target.getGeManager().init();
					GrandExchange.removeOffers(target);
					target.setBanned(Utils.currentTimeMillis() + (48 * 60 * 60 * 1000));
					target.getSession().getChannel().close();
					player.getPackets().sendGameMessage("You have banned 48 hours: " + target.getDisplayName() + ".");
					player.getPackets()
							.sendGameMessage("Display: " + target.getDisplayName() + " User: " + target.getUsername());
				} else {
					name = Utils.formatPlayerNameForProtocol(name);
					if (!SerializableFilesManager.containsPlayer(name)) {
						player.getPackets().sendGameMessage(
								"Account name " + Utils.formatPlayerNameForDisplay(name) + " doesn't exist.");
						return true;
					}
					target = SerializableFilesManager.loadPlayer(name);
					if (target.getRights() == 2) {
						return true;
					}
					if (target.getGeManager() == null) {
						target.setGeManager(new GrandExchangeManager());
					}
					target.getGeManager().setPlayer(target);
					target.getGeManager().init();
					GrandExchange.removeOffers(target);
					target.setUsername(name);
					target.setBanned(Utils.currentTimeMillis() + (48 * 60 * 60 * 1000));
					player.getPackets().sendGameMessage(
							"You have banned 48 hours: " + Utils.formatPlayerNameForDisplay(name) + ".");
					SerializableFilesManager.savePlayer(target);
				}
				return true;
			case "unban":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				if (target != null) {
					IPBanL.unban(target);
					player.getPackets().sendGameMessage("You have unbanned: " + target.getDisplayName() + ".");
				} else {
					name = Utils.formatPlayerNameForProtocol(name);
					if (!SerializableFilesManager.containsPlayer(name)) {
						player.getPackets().sendGameMessage(
								"Account name " + Utils.formatPlayerNameForDisplay(name) + " doesn't exist.");
						return true;
					}
					target = SerializableFilesManager.loadPlayer(name);
					target.setUsername(name);
					target.setPermBanned(false);
					IPBanL.unban(target);
					player.getPackets().sendGameMessage("You have unbanned: " + target.getDisplayName() + ".");
					SerializableFilesManager.savePlayer(target);
				}
				return true;

			case "forcekick":
			case "kick":
				if (player.isAtWild()) {
					player.getPackets().sendGameMessage("You cannot use this command in the wilderness.");
					return true;
				}
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				if (target == null) {
					player.getPackets().sendGameMessage("This player is offline");
				} else {
					target.forceLogout();
					player.getPackets().sendGameMessage("You have kicked: " + target.getDisplayName() + ".");
				}
				return true;
			case "setrank":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				if (target == null)
					target = SerializableFilesManager.loadPlayer(name);
				if (target != null) {
					if (target.getPlayerMode() >= 5) {
						player.getPackets().sendGameMessage("Already got highest rank");
						return true;
					}
					target.setPlayerMode(target.getPlayerMode() + 1);
					player.getPackets()
							.sendGameMessage("You have set rank: " + target.getPlayerMode() + " to player: " + name);
				}
				return true;
			case "derank":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				if (target == null)
					target = SerializableFilesManager.loadPlayer(name);
				if (target != null) {
					if (target.getPlayerMode() <= 0) {
						player.getPackets().sendGameMessage("Already got highest rank");
						return true;
					}
					target.setPlayerMode(target.getPlayerMode() - 1);
					player.getPackets()
							.sendGameMessage("You have set rank: " + target.getPlayerMode() + " to player: " + name);
				}
				return true;
			case "teleto":
				if (player.isAtWild()) {
					player.getPackets().sendGameMessage("You cannot use this command in the wilderness.");
					return true;
				}
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				if (target == null)
					player.getPackets().sendGameMessage("Couldn't find player " + name + ".");
				if (target.getAppearence().isHidden()) {
					player.getPackets().sendGameMessage("Couldn't find player " + name + ".");
					return true;
				} else
					player.setNextWorldTile(target);
				return true;
			case "teletome":
				if (player.isAtWild()) {
					player.getPackets().sendGameMessage("You cannot use this command in the wilderness.");
					return true;
				}
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				if (target.getRights() > 1) {
					player.getPackets().sendGameMessage("Unable to teleport a developer to you.");
					return true;
				}
				target.setNextWorldTile(player);
				return true;
			case "sendhome":
				if (player.isAtWild()) {
					player.getPackets().sendGameMessage("You cannot use this command in the wilderness.");
					return true;
				}
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				if (target == null) {
					player.getPackets().sendGameMessage("This player is offline.");
				} else {
					target.unlock();
					target.getControlerManager().forceStop();
					target.setNextWorldTile(Settings.START_PLAYER_LOCATION);
					player.getPackets().sendGameMessage("You have sent home " + target.getDisplayName() + ".");
				}
				return true;
			}
		}
		return false;
	}

	public static void archiveLogs(Player player, String[] cmd) {
		try {
			String location = "";
			if (player.getRights() == 2) {
				location = "data/logs/commands/admin/" + player.getUsername() + ".txt";
			} else if (player.getRights() == 1) {
				location = "data/logs/commands/mod/" + player.getUsername() + ".txt";
			} else {
				location = "data/logs/commands/player/" + player.getUsername() + ".txt";
			}
			String afterCMD = "";
			for (int i = 1; i < cmd.length; i++)
				afterCMD += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			BufferedWriter writer = new BufferedWriter(new FileWriter(location, true));
			writer.write("[" + currentTime("dd MMMMM yyyy 'at' hh:mm:ss z") + "] - ::" + cmd[0] + " " + afterCMD);
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String currentTime(String dateFormat) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(cal.getTime());
	}

	private static boolean isDeveloper(Player player) {
		for (String name : Settings.DEVELOPERS) {
			if (player.getUsername().equalsIgnoreCase(name))
				return true;
		}
		return false;
	}

	public static void archiveYell(Player player, String message) {
		try {
			String location = "";
			location = "data/logs/yell/" + player.getUsername() + ".txt";
			BufferedWriter writer = new BufferedWriter(new FileWriter(location, true));
			writer.write("[" + currentTime("dd MMMMM yyyy 'at' hh:mm:ss z") + "] - ::yell" + message);
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Doesn't let it be instanced
	 */

	private Commands() {

	}
}
