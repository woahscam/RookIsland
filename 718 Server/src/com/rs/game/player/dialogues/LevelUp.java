package com.rs.game.player.dialogues;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;

public final class LevelUp extends Dialogue {

	public enum Configs {
		ATTACK(1, 0), STRENGTH(2, 2), DEFENCE(5, 1), HITPOINTS(6, 3), RANGE(3, 4), MAGIC(4, 6), PRAYER(7, 5), AGILITY(8,
				16), HERBLORE(9, 15), THIEVING(10, 17), CRAFTING(11, 12), RUNECRAFTING(12, 20), MINING(13,
						14), SMITHING(14, 13), FISHING(15, 10), COOKING(16, 7), FIREMAKING(17, 11), WOODCUTTING(18,
								8), FLETCHING(19, 9), SLAYER(20, 18), FARMING(21, 19), CONSTRUCTION(22,
										22), HUNTER(23, 21), SUMMONING(24, 23), DUNGEONEERING(25, 24);

		private int id;
		private int skill;

		private Configs(int id, int skill) {
			this.id = id;
			this.skill = skill;
		}

		public int getId() {
			return id;
		}

		private static Map<Integer, Configs> configs = new HashMap<Integer, Configs>();

		public static Configs levelup(int skill) {
			return configs.get(skill);
		}

		static {
			for (Configs config : Configs.values()) {
				configs.put(config.skill, config);
			}
		}
	}

	/*
	 * Levelup sounds 1 - 99
	 */

	public enum Musics {
		ATTACK(29, 30, 0), STRENGTH(65, 66, 2), DEFENCE(37, 38, 1), HITPOINTS(47, 48, 3), RANGE(57, 58, 4), MAGIC(51,
				52, 6), PRAYER(55, 56, 5), AGILITY(28, 322, 16), HERBLORE(45, 46, 15), THIEVING(67, 68,
						17), CRAFTING(35, 36, 12), RUNECRAFTING(59, 60, 20), MINING(53, 54,
								14), SMITHING(63, 64, 13), FISHING(41, 42, 10), COOKING(33, 34, 7), FIREMAKING(39, 40,
										11), WOODCUTTING(69, 70, 8), FLETCHING(43, 44, 9), SLAYER(61, 62,
												18), FARMING(11, 10, 19), CONSTRUCTION(31, 32, 22), HUNTER(49, 50,
														21), SUMMONING(300, 301, 23), DUNGEONEERING(416, 417, 24);

		private int id;
		private int id2;
		private int skill;

		private Musics(int id, int id2, int skill) {
			this.id = id;
			this.id2 = id2;
			this.skill = skill;
		}

		public int getId() {
			return id;
		}

		public int getId2() {
			return id2;
		}

		private static Map<Integer, Musics> musics = new HashMap<Integer, Musics>();

		public static Musics levelup(int skill) {
			return musics.get(skill);
		}

		static {
			for (Musics music : Musics.values()) {
				musics.put(music.skill, music);
			}
		}
	}

	private int skill;

	@Override
	public void start() {
		skill = (Integer) parameters[0];
		int level = player.getSkills().getLevelForXp(skill);
		String name = Skills.SKILL_NAME[skill];
		totalMileStone(player);
		player.temporaryAttribute().put("leveledUp[" + skill + "]", Boolean.TRUE);
		if (level >= 90)
			player.getAdventureLog().addActivity("I levelled up my " + name + ". I am now level " + level + ".");
		Musics musicId = Musics.levelup(skill);
		player.getPackets().sendMusicEffect(level > 50 ? musicId.getId2() : musicId.getId());
		player.gfx(new Graphics(199));
		if (level == 99 || level == 120)
			player.gfx(new Graphics(1765));
		player.getPackets().sendGameMessage("You've just advanced a" + (name.startsWith("A") ? "n" : "") + " " + name
				+ " level! You have reached level " + level + ".");
		player.lastlevelUp.clear();
		player.lastSkill.clear();
		player.lastlevelUp.add(Integer.valueOf(level).toString());
		player.lastSkill.add(name);
		if (level > 70) {
			player.getInterfaceManager().sendFadingInterface(1216);
			player.getVarsManager().sendVarBit(4757, getIconValue(skill));
			Configs levelup = Configs.levelup(skill);
			player.getPackets().sendGlobalConfig(1756, levelup.getId());
		}
		switchFlash(player, skill, true);
		/*if ((level == 99 || level == 120)) {
			sendNews(player, skill, level);
		}*/
	}

	static int reachedMilestoneMax = 1;
	static int reachedMilestone = 1;
	int milestoneLevels[] = { 10, 20, 30, 40, 50, 60, 70, 80, 90, 99 };

	public static void sendNews(Player player, int skill, int level) {
		boolean reachedAll = true;
		for (int i = 0; i < Skills.SKILL_NAME.length; i++) {
			if (player.getSkills().getLevelForXp(i) < 99) {
				reachedAll = false;
				break;
			}
		}
		World.sendWorldMessage("<img=5><col=ff8c38>News: " + player.getDisplayName() + " has achieved " + level
				+ " " + Skills.SKILL_NAME[skill] + ".", false);
		player.sm("You have achieved highest possible level in " + Skills.SKILL_NAME[skill] + ".");
		player.getAdventureLog().addActivity("I have achieved " + level + " " + Skills.SKILL_NAME[skill] + ".");
		if (reachedAll) {
			World.sendWorldMessage("<img=6><col=ff0000>News: " + player.getDisplayName()
					+ " has just achieved at least level 99 in all skills!", false);
			player.sm("<col=990000>You have achieved highest possible level in all skills.");
			player.sm("Congratulations you have reached the maximum possible total level.");
			player.getAdventureLog().addActivity("I have achieved at least level 99 in all skills.");
		}
	}

	public static int getIconValue(int skill) {
		if (skill == Skills.ATTACK)
			return 1;
		if (skill == Skills.STRENGTH)
			return 2;
		if (skill == Skills.RANGE)
			return 3;
		if (skill == Skills.MAGIC)
			return 4;
		if (skill == Skills.DEFENCE)
			return 5;
		if (skill == Skills.HITPOINTS)
			return 6;
		if (skill == Skills.PRAYER)
			return 7;
		if (skill == Skills.AGILITY)
			return 8;
		if (skill == Skills.HERBLORE)
			return 9;
		if (skill == Skills.THIEVING)
			return 10;
		if (skill == Skills.CRAFTING)
			return 11;
		if (skill == Skills.RUNECRAFTING)
			return 12;
		if (skill == Skills.MINING)
			return 13;
		if (skill == Skills.SMITHING)
			return 14;
		if (skill == Skills.FISHING)
			return 15;
		if (skill == Skills.COOKING)
			return 16;
		if (skill == Skills.FIREMAKING)
			return 17;
		if (skill == Skills.WOODCUTTING)
			return 18;
		if (skill == Skills.FLETCHING)
			return 19;
		if (skill == Skills.SLAYER)
			return 20;
		if (skill == Skills.FARMING)
			return 21;
		if (skill == Skills.CONSTRUCTION)
			return 22;
		if (skill == Skills.HUNTER)
			return 23;
		if (skill == Skills.SUMMONING)
			return 24;
		if (skill == Skills.DUNGEONEERING)
			return 25;
		return 0;
	}

	public static void switchFlash(Player player, int skill, boolean on) {
		int id = 0;
		if (skill == Skills.ATTACK)
			id = 4732;
		if (skill == Skills.STRENGTH)
			id = 4733;
		if (skill == Skills.DEFENCE)
			id = 4734;
		if (skill == Skills.RANGE)
			id = 4735;
		if (skill == Skills.PRAYER)
			id = 4736;
		if (skill == Skills.MAGIC)
			id = 4737;
		if (skill == Skills.HITPOINTS)
			id = 4738;
		if (skill == Skills.AGILITY)
			id = 4739;
		if (skill == Skills.HERBLORE)
			id = 4740;
		if (skill == Skills.THIEVING)
			id = 4741;
		if (skill == Skills.CRAFTING)
			id = 4742;
		if (skill == Skills.FLETCHING)
			id = 4743;
		if (skill == Skills.MINING)
			id = 4744;
		if (skill == Skills.SMITHING)
			id = 4745;
		if (skill == Skills.FISHING)
			id = 4746;
		if (skill == Skills.COOKING)
			id = 4747;
		if (skill == Skills.FIREMAKING)
			id = 4748;
		if (skill == Skills.WOODCUTTING)
			id = 4749;
		if (skill == Skills.RUNECRAFTING)
			id = 4750;
		if (skill == Skills.SLAYER)
			id = 4751;
		if (skill == Skills.FARMING)
			id = 4752;
		if (skill == Skills.CONSTRUCTION)
			id = 4753;
		if (skill == Skills.HUNTER)
			id = 4754;
		if (skill == Skills.SUMMONING)
			id = 4755;
		if (skill == Skills.DUNGEONEERING)
			id = 7756;
		player.getVarsManager().sendVarBit(id, on ? 1 : 0);
	}

	public void totalMileStone(Player player) {
		final int TotalLevels[] = { 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1100, 1200, 1300, 1400, 1500,
				1600, 1700, 1800, 1900, 2000, 2100, 2200, 2300, 2400, 2496 };
		for (int levels : TotalLevels) {
			if (player.getSkills().getTotalLevel(player) == levels) {
				player.sm("<col=990000>Well done! You've reached the total level "
						+ player.getSkills().getTotalLevel(player) + " milestone!");
				player.gfx(new Graphics(199));
				player.setAvalonPoints(player.getAvalonPoints() + 5000);
			}
			return;
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
	}

	@Override
	public void finish() {
		// player.getPackets().sendConfig(1179, SKILL_ICON[skill]); //removes
		// random flash
	}
}
