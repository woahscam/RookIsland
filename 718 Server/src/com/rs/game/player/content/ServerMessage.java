package com.rs.game.player.content;

import com.rs.game.World;
import com.rs.game.player.Player;

public class ServerMessage {

	public static void filterMessage(Player player, String data, boolean staff) {
		// Moderator/Administrator accounts cannot be ignored, regular accounts
		// can just turn game to "filter"
		// Regular accounts can also do ::hideyell if they want filter off
		// ingame...
		String[] invalid = { "<euro", "<img", "<img=", "<col", "<col=", "<shad", "<shad=", "<str>", "<u>" };

		/*
		 * if (player.isYellMuted) { unused.
		 * player.getPackets().sendGameMessage(
		 * "%s has removed your ability to use the yell system.",
		 * player.yellMutedBy); return; }
		 */

		if (player.getRights() < 2) {
			for (String s : invalid) {
				if (data.contains(s)) {
					player.getPackets().sendGameMessage("Your message failed to send due to illegal charachters.");
					return;
				}
			}
		}

		sendGlobalMessage(player, data, staff);
	}

	public static void sendGlobalMessage(Player player, String data, boolean staff) {
		if (player.isAdministrator()) {
			String edits = String.format("[<shad=000000><col=ffce00>Server Message</col></shad>] <img=1>%s: %s", player.getDisplayName(), data);
			sendNews(true, edits, staff, false);
			return;
		}

		if (player.getRights() == 1 && player.isHeadMod()) {
			String edits = String.format("[Head Moderator] <img=0>%s: <col=007FB5>%s", player.getDisplayName(), data);
			sendNews(true, edits, staff, false);
			return;
		} else if (player.getRights() == 1 && !player.isHeadMod()) {
			String edits = String.format("[<shad=000000><col=007FB5>Player Moderator</col></shad>] <img=0>%s: <col=007FB5>%s", player.getDisplayName(), data);
			sendNews(true, edits, staff, false);
			return;
		} else if (player.getPlayerMode() == 4) {//ff005a
			String edits = String.format("[<shad=000000><col=ff0000>Youtuber</col></shad>] <img=8>%s: %s", player.getDisplayName(), data);
			sendNews(false, edits, false, false);
			return;
		} else {
			String edits = String.format("[Player] %s: %s", player.getDisplayName(), data);
			sendNews(false, edits, false, false);
			return;
		}
	}

	public static void sendNews(boolean important, String args, boolean staffOnly, boolean autoColor) {
		for (Player user : World.getPlayers()) {
			if (user == null || !user.isRunning()
			// if the user has yell off and it's not a staff msg
					|| (user.isYellOff() && important == false)
					// if the user is not a staff
					|| (staffOnly && user.getRights() == 0)) {

				continue;
			}
			if (autoColor) {
				user.getPackets().sendColoredMessage(args, important);
			} else {
				user.getPackets().sendGameMessage(args, true);
			}
		}
	}
}
