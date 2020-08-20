package com.rs.game.player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.item.Item;
import com.rs.game.player.Player.Limits;
import com.rs.game.player.content.friendschat.FriendChatsManager;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.Utils;

/**
 * 
 * @author Savions Sw
 * @author Andreas Fixed by Tristam <Hassan>. Issue: Not saving IP when server
 *         restarted
 */

public class StarterProtection {

	private static List<String> StarterIPS = new ArrayList<String>();

	private static final String Path = "data/starter/starterIPS.txt";
	
	public static void addStarter(Player player) {
		player.recievedStarter = true;
	}

	public static void addStarterIP(String IP) {
		if (IP == null) {
			return;
		}
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			reader = new BufferedReader(new FileReader(Path));
			String line;
			while ((line = reader.readLine()) != null)
				StarterIPS.add(line);
			reader.close();
			StarterIPS.add(0, IP);
			writer = new BufferedWriter(new FileWriter(Path));
			for (String list : StarterIPS)
				writer.write(list + "\r\n");
			System.err.print(IP + " has just been added to the log. \n");
		} catch (Exception e) {
			System.err.print(IP + " was not added to starter list.");
		} finally {
			assert reader != null;
			assert writer != null;
			try {
				reader.close();
				writer.close();
			} catch (IOException e) {

			}
		}
	}

	public static void loadIPS() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(Path));
		String line;
		StarterIPS.clear();
		while ((line = br.readLine()) != null) {
			StarterIPS.add(line);
		}
		br.close();
	}

	public static final void sendStarterPack(final Player player) {
		addStarter(player);
		for (int skills = 0; skills < 7; skills++) {
			Limits i = Limits.forId(skills);
			if (i != null) {
				player.getSkills().set(skills, i.getLevel());
				player.getSkills().setXp(skills, Skills.getXPForLevel(i.getLevel()));
			}
		}
		player.reset();
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
		player.getPackets().sendIComponentText(275, 26, "::emptybank - Resets your whole bank.");
		for (int i = 39; i <= 150; i++)
			player.getPackets().sendIComponentText(275, i, "");
		player.sm("Experience rate: " + Settings.SKILLING_XP_RATE + "x for skilling, 1x in wilderness for combat.");
		World.sendWorldMessage(
				"<img=5><col=b25200>News: " + player.getDisplayName() + " has joined " + Settings.SERVER_NAME + "!",
				false);
		if (Settings.ECONOMY_MODE > 0) {
			String otherName = Utils.formatPlayerNameForDisplay("Bank");
			Player p2 = World.getPlayerByDisplayName(otherName);
			if (p2 == null)
				p2 = SerializableFilesManager.loadPlayer(otherName);
			if (p2 != null) {
				player.getBank().generateContainer();
				player.getBank().setBankTabs(p2.getBank().bankTabs);
				player.getPresetManager().PRESET_SETUPS = p2.getPresetManager().PRESET_SETUPS;
			}
			player.getAppearence().generateAppearenceData();
			player.getPresetManager().loadPreset("hybrid", null);
		}
		if (player.getCurrentFriendChat() == null) {
			FriendChatsManager.joinChat(Settings.HELP_CC_NAME, player, true);
			FriendChatsManager.refreshChat(player);
		}
		player.toggles.put("ONEXHITS", false);
		player.toggles.put("ONEXPPERHIT", false);
		player.toggles.put("HEALTHBAR", true);
		player.toggles.put("DROPVALUE", 10000);
		player.toggles.put("LOOTBEAMS", true);
		player.switchShiftDrop();
		player.switchZoom();
		player.switchItemsLook();
		if (Settings.ECONOMY_MODE > 0) {
			player.getControlerManager().startControler("EdgevillePvPControler");
			player.getControlerManager().moved();
		}
		if (StarterIPS.contains(player.getSession().getIP())) {
			player.sm(
					"<col=99000><u>You have already received the max amount of starter packs registered on your IP address.");
			return;
		}
		addStarterIP(player.getSession().getIP());
		if (Settings.ECONOMY_MODE == 1) {
			Item[] inventory = { new Item(1351, 1), new Item(590, 1), new Item(303, 1), new Item(315, 1),
					new Item(1925, 1), new Item(1931, 1), new Item(2309, 1), new Item(1265, 1), new Item(1205, 1),
					new Item(1277, 1), new Item(1171, 1), new Item(841, 1), new Item(882, 25), new Item(556, 25),
					new Item(558, 15), new Item(555, 6), new Item(557, 4), new Item(559, 2) };
			for (Item items : inventory)
				player.getInventory().addItem(items);
			player.getMoneyPouch().addMoney(2000000, false);
		}
	}

	public static final boolean containsIP(String ip) {
		return StarterIPS.contains(ip);
	}

}