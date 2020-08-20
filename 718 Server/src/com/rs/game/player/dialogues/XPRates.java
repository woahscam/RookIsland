package com.rs.game.player.dialogues;

public class XPRates extends Dialogue {

	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, 9827,
				"Hello " + player.getDisplayName() + ", what XP rate would you like to change to?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 1;
			sendOptionsDialogue("Choose a CombatXP rate", player.NormalXP ? "Runescape XP (x1)" : "Normal XP (x500)",
					player.isXpLocked() ? "Unlock Combat XP" : "Lock Combat XP");
		} else if (stage == 1) {
			if (componentId == OPTION_1) {
				if (player.NormalXP)
					stage = 50;
				else
					stage = 25;
				sendPlayerDialogue(9827, player.NormalXP ? "Runescape XP (x1), please." : "Normal XP (x500), please.");
			} else if (componentId == OPTION_2) {
				stage = 75;
				sendPlayerDialogue(9827,
						player.isXpLocked() ? "I want to Unlock my Combat XP" : "I want to Lock my Combat XP");
			}
		} else if (stage == 25) {
			stage = 100;
			player.NormalXP = true;
			sendNPCDialogue(npcId, 9827, "Your XP rate are now set to: Normal XP");
			player.getPackets().sendGameMessage("Your XP rate are now set to: Normal XP");
		} else if (stage == 50) {
			stage = 100;
			player.NormalXP = false;
			sendNPCDialogue(npcId, 9827, "Your XP rate are now set to: Runescape XP");
			player.getPackets().sendGameMessage("Your XP rate are now set to: Runescape XP");
		} else if (stage == 75) {
			stage = 100;
			player.xpLocked = player.xpLocked ? false : true;
			player.getPackets().sendGameMessage(
					player.isXpLocked() ? "Your Combat XP are now Locked." : "Your Combat XP are now Unlocked.");
			sendNPCDialogue(npcId, 9827,
					player.isXpLocked() ? "Your Combat XP are now Locked." : "Your Combat XP are now Unlocked.");
		} else if (stage == 100) {
			end();
		}
	}

	@Override
	public void finish() {

	}

}
