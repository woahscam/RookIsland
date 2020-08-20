package com.rs.game.player.dialogues;

public class MemberXPRates extends Dialogue {

	private short xpSelection = 0;

	@Override
	public void start() {
		sendOptionsDialogue("Select your combat EXP rate",
				player.isXpLocked() ? "Unlock Combat XP." : "Lock Combat XP.", "(RuneScape) x1.", "x50.", "x500.");
	}

	@Override
	public void run(int interfaceId, int componentId) {

		switch (stage) {
		case -1:
			switch (componentId) {
			case OPTION_1:
				player.setXpLocked(!player.isXpLocked());
				xpSelection = -1;
				break;
			case OPTION_2:
				xpSelection = 1;
				break;
			case OPTION_3:
				xpSelection = 50;
				break;
			case OPTION_4:
				xpSelection = 500;
				break;
			}
			player.expRate = (xpSelection > 0 ? xpSelection : player.expRate);
			sendDialogue("You have "
					+ (xpSelection == -1 ? (player.isXpLocked() ? "locked" : "unlocked") + " your combat XP."
							: "set your XP rate to x" + xpSelection + "."));
			stage++;
			break;
		case 0:
			end();
			break;
		}
	}

	@Override
	public void finish() {

	}

}
