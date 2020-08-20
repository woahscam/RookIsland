package com.rs.game.player;

import java.io.Serializable;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.utils.Utils;

public final class Skills implements Serializable {

	private static final long serialVersionUID = -7086829989489745985L;

	public static final double MAXIMUM_EXP = 200000000;

	public static final int ATTACK = 0, DEFENCE = 1, STRENGTH = 2, HITPOINTS = 3, RANGE = 4, PRAYER = 5, MAGIC = 6,
			COOKING = 7, WOODCUTTING = 8, FLETCHING = 9, FISHING = 10, FIREMAKING = 11, CRAFTING = 12, SMITHING = 13,
			MINING = 14, HERBLORE = 15, AGILITY = 16, THIEVING = 17, SLAYER = 18, FARMING = 19, RUNECRAFTING = 20,
			CONSTRUCTION = 22, HUNTER = 21, SUMMONING = 23, DUNGEONEERING = 24;

	public static final String[] SKILL_NAME = { "Attack", "Defence", "Strength", "Constitution", "Ranged", "Prayer",
			"Magic", "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining",
			"Herblore", "Agility", "Thieving", "Slayer", "Farming", "Runecrafting", "Hunter", "Construction",
			"Summoning", "Dungeoneering" };

	public short level[];
	private double xp[];
	private double[] xpTracks;
	private boolean[] trackSkills;
	private byte[] trackSkillsIds;
	private boolean[] enabledSkillsTargets;
	private boolean[] skillsTargetsUsingLevelMode;
	private int[] skillsTargetsValues;
	private boolean xpDisplay, xpPopup;

	private transient int currentCounter;
	private transient Player player;

	public void passLevels(Player p) {
		this.level = p.getSkills().level;
		this.xp = p.getSkills().xp;
	}

	public Skills() {
		level = new short[25];
		xp = new double[25];
		for (int i = 0; i < level.length; i++) {
			level[i] = 1;
			xp[i] = 0;
		}
		level[3] = 10;
		xp[3] = 1184;
		level[HERBLORE] = 3;
		xp[HERBLORE] = 250;
		xpPopup = true;
		xpTracks = new double[3];
		trackSkills = new boolean[3];
		trackSkillsIds = new byte[3];
		trackSkills[0] = true;
		for (int i = 0; i < trackSkillsIds.length; i++)
			trackSkillsIds[i] = 30;
		enabledSkillsTargets = new boolean[25];
		skillsTargetsUsingLevelMode = new boolean[25];
		skillsTargetsValues = new int[25];

	}

	public void sendXPDisplay() {
		for (int i = 0; i < trackSkills.length; i++) {
			player.getVarsManager().sendVarBit(10444 + i, trackSkills[i] ? 1 : 0);
			player.getVarsManager().sendVarBit(10440 + i, trackSkillsIds[i] + 1);
			refreshCounterXp(i);
		}
	}

	public void setupXPCounter() {
		player.getInterfaceManager().sendXPDisplay(1214);
	}

	public void refreshCurrentCounter() {
		player.getVarsManager().sendVar(2478, currentCounter + 1);
	}

	public void setCurrentCounter(int counter) {
		if (counter != currentCounter) {
			currentCounter = counter;
			refreshCurrentCounter();
		}
	}

	public void switchTrackCounter() {
		trackSkills[currentCounter] = !trackSkills[currentCounter];
		player.getVarsManager().sendVarBit(10444 + currentCounter, trackSkills[currentCounter] ? 1 : 0);
	}

	public void resetCounterXP() {
		xpTracks[currentCounter] = 0;
		refreshCounterXp(currentCounter);
	}

	public void setCounterSkill(int skill) {
		xpTracks[currentCounter] = 0;
		trackSkillsIds[currentCounter] = (byte) skill;
		player.getVarsManager().sendVarBit(10440 + currentCounter, trackSkillsIds[currentCounter] + 1);
		refreshCounterXp(currentCounter);
	}

	public void refreshCounterXp(int counter) {
		player.getVarsManager().sendVar(counter == 0 ? 1801 : 2474 + counter, (int) (xpTracks[counter] * 10));
	}

	public void handleSetupXPCounter(int componentId) {
		if (componentId == 18)
			player.getInterfaceManager().sendXPDisplay();
		else if (componentId >= 22 && componentId <= 24)
			setCurrentCounter(componentId - 22);
		else if (componentId == 27)
			switchTrackCounter();
		else if (componentId == 61)
			resetCounterXP();
		else if (componentId >= 31 && componentId <= 57)
			if (componentId == 33)
				setCounterSkill(4);
			else if (componentId == 34)
				setCounterSkill(2);
			else if (componentId == 35)
				setCounterSkill(3);
			else if (componentId == 42)
				setCounterSkill(18);
			else if (componentId == 49)
				setCounterSkill(11);
			else
				setCounterSkill(componentId >= 56 ? componentId - 27 : componentId - 31);

	}

	public void sendInterfaces() {
		if (xpDisplay)
			player.getInterfaceManager().sendXPDisplay();
		if (xpPopup)
			player.getInterfaceManager().sendXPPopup();
	}

	public void resetXPDisplay() {
		xpDisplay = false;
	}

	public void switchXPDisplay() {
		xpDisplay = !xpDisplay;
		if (xpDisplay)
			player.getInterfaceManager().sendXPDisplay();
		else
			player.getInterfaceManager().closeXPDisplay();
	}

	public void switchXPPopup(boolean silent) {
		xpPopup = !xpPopup ? true : false;
		if (!silent)
			player.getPackets().sendGameMessage("XP pop-ups are now " + (xpPopup ? "en" : "dis") + "abled.");
		if (xpPopup)
			player.getInterfaceManager().sendXPPopup();
		else
			player.getInterfaceManager().closeXPPopup();
	}

	public void restoreSkills() {
		for (int skill = 0; skill < level.length; skill++) {
			level[skill] = (short) getLevelForXp(skill);
			refresh(skill);
		}
	}

	public void setPlayer(Player player) {
		this.player = player;
		// temporary
		if (xpTracks == null) {
			xpPopup = true;
			xpTracks = new double[3];
			trackSkills = new boolean[3];
			trackSkillsIds = new byte[3];
			trackSkills[0] = true;
			for (int i = 0; i < trackSkillsIds.length; i++)
				trackSkillsIds[i] = 30;
		}
	}

	public short[] getLevels() {
		return level;
	}

	public double[] getXp() {
		return xp;
	}

	public int getLevel(int skill) {
		return level[skill];
	}

	public double getXp(int skill) {
		return xp[skill];
	}

	public int getTotalLevel(Player player) {
		int totallevel = 0;
		for (int i = 0; i <= 24; i++) {
			totallevel += player.getSkills().getLevelForXp(i);
		}
		return totallevel;
	}

	public int getTotalXP(Player player) {
		int totalXP = 0;
		for (int skill = 0; skill <= 24; skill++) {
			totalXP += player.getSkills().getXp(skill);
		}
		return totalXP;
	}

	public int getXPLampFormula(int skill) {
		int currentLevel = getLevel(skill);
		int formula = (int) Math
				.floor((Math.pow(currentLevel, 3) - 2 * Math.pow(currentLevel, 2) + 100 * currentLevel) / 20);
		player.getSkills().addXp(skill, formula);
		return formula;
	}

	public int getXPForSkill(int skill) {
		skill += player.getSkills().getXp(skill);
		return skill;
	}

	public int getTargetIdByComponentId(int componentId) {
		switch (componentId) {
		case 150: // Attack
			return 0;
		case 9: // Strength
			return 2;
		case 40: // Range
			return 4;
		case 71: // Magic
			return 6;
		case 22: // Defence
			return 1;
		case 145: // Constitution
			return 3;
		case 58: // Prayer
			return 5;
		case 15: // Agility
			return 16;
		case 28: // Herblore
			return 15;
		case 46: // Theiving
			return 17;
		case 64: // Crafting
			return 12;
		case 84: // Runecrafting
			return 20;
		case 140: // Mining
			return 14;
		case 135: // Smithing
			return 13;
		case 34: // Fishing
			return 10;
		case 52: // Cooking
			return 7;
		case 130: // Firemaking
			return 11;
		case 125: // Woodcutting
			return 8;
		case 77: // Fletching
			return 9;
		case 90: // Slayer
			return 18;
		case 96: // Farming
			return 19;
		case 102: // Construction
			return 22;
		case 108: // Hunter
			return 21;
		case 114: // Summoning
			return 23;
		case 120: // Dungeoneering
			return 24;
		default:
			return -1;
		}
	}

	public int getSkillIdByTargetId(int targetId) {
		switch (targetId) {
		case 0: // Attack
			return ATTACK;
		case 1: // Strength
			return STRENGTH;
		case 2: // Range
			return RANGE;
		case 3: // Magic
			return MAGIC;
		case 4: // Defence
			return DEFENCE;
		case 5: // Constitution
			return HITPOINTS;
		case 6: // Prayer
			return PRAYER;
		case 7: // Agility
			return AGILITY;
		case 8: // Herblore
			return HERBLORE;
		case 9: // Thieving
			return THIEVING;
		case 10: // Crafting
			return CRAFTING;
		case 11: // Runecrafting
			return RUNECRAFTING;
		case 12: // Mining
			return MINING;
		case 13: // Smithing
			return SMITHING;
		case 14: // Fishing
			return FISHING;
		case 15: // Cooking
			return COOKING;
		case 16: // Firemaking
			return FIREMAKING;
		case 17: // Woodcutting
			return WOODCUTTING;
		case 18: // Fletching
			return FLETCHING;
		case 19: // Slayer
			return SLAYER;
		case 20: // Farming
			return FARMING;
		case 21: // Construction
			return CONSTRUCTION;
		case 22: // Hunter
			return HUNTER;
		case 23: // Summoning
			return SUMMONING;
		case 24: // Dungeoneering
			return DUNGEONEERING;
		default:
			return -1;
		}
	}

	public void refreshEnabledSkillsTargets() {
		int value = Utils.get32BitValue(enabledSkillsTargets, true);
		player.getPackets().sendConfig(1966, value);
	}

	public void refreshUsingLevelTargets() {
		int value = Utils.get32BitValue(skillsTargetsUsingLevelMode, true);
		player.getPackets().sendConfig(1968, value);
	}

	public void refreshSkillsTargetsValues() {
		for (int i = 0; i < 25; i++) {
			player.getPackets().sendConfig(1969 + i, skillsTargetsValues[i]);
		}
	}

	public void setSkillTargetEnabled(int id, boolean enabled) {
		enabledSkillsTargets[id] = enabled;
		refreshEnabledSkillsTargets();
	}

	public void setSkillTargetUsingLevelMode(int id, boolean using) {
		skillsTargetsUsingLevelMode[id] = using;
		refreshUsingLevelTargets();
	}

	public void setSkillTargetValue(int skillId, int value) {
		skillsTargetsValues[skillId] = value;
		refreshSkillsTargetsValues();
	}

	public void setSkillTarget(boolean usingLevel, int skillId, int target) {
		setSkillTargetEnabled(skillId, true);
		setSkillTargetUsingLevelMode(skillId, usingLevel);
		setSkillTargetValue(skillId, target);

	}

	public boolean hasRequirements(int... skills) {
		for (int i = 0; i < skills.length; i += 2) {
			int skillId = skills[i];
			int skillLevel = skills[i + 1];
			if (skillId > 24)
				continue;
			if (getLevelForXp(skillId) < skillLevel)
				return false;
		}
		return true;
	}

	public int getCombatLevel() {
		double attack = getLevelForXp(0);
		double defence = getLevelForXp(1);
		double strength = getLevelForXp(2);
		double hp = getLevelForXp(3);
		double prayer = getLevelForXp(5);
		double ranged = getLevelForXp(4);
		double magic = getLevelForXp(6);
		double combatLevel = 0;
		double melee = Math.floor(0.25 * (defence + hp + Math.floor(prayer / 2)) + 0.325 * (attack + strength));
		double ranger = Math
				.floor(0.25 * (defence + hp + Math.floor(prayer / 2)) + 0.325 * (Math.floor(ranged / 2) + ranged));
		double mage = Math
				.floor(0.25 * (defence + hp + Math.floor(prayer / 2)) + 0.325 * (Math.floor(magic / 2) + magic));
		if (melee >= ranger && melee >= mage) {
			combatLevel = melee;
		} else if (ranger >= melee && ranger >= mage) {
			combatLevel += ranger;
		} else if (mage >= melee && mage >= ranger) {
			combatLevel += mage;
		}
		return (int) combatLevel;
	}

	public void set(int skill, int newLevel) {
		level[skill] = (short) newLevel;
		refresh(skill);
		player.getMaxHit();
	}

	public int drainLevel(int skill, int drain) {
		int drainLeft = drain - level[skill];
		if (drainLeft < 0) {
			drainLeft = 0;
		}
		level[skill] -= drain;
		if (level[skill] < 0) {
			level[skill] = 0;
		}
		refresh(skill);
		return drainLeft;
	}

	public int getCombatLevelWithSummoning() {
		return getCombatLevel() + getSummoningCombatLevel();
	}

	public int getSummoningCombatLevel() {
		return getLevelForXp(Skills.SUMMONING) / 8;
	}

	public void drainSummoning(int amt) {
		int level = getLevel(Skills.SUMMONING);
		if (level == 0)
			return;
		set(Skills.SUMMONING, amt > level ? 0 : level - amt);
	}

	public static int getXPForLevel(int level) {
		int points = 0;
		int output = 0;
		for (int lvl = 1; lvl <= level; lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			if (lvl >= level) {
				return output;
			}
			output = (int) Math.floor(points / 4);
		}
		return 0;
	}

	public static int getLevelForXp(double exp, int max) {
		int points = 0;
		int output = 0;
		for (int lvl = 1; lvl <= max; lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			output = (int) Math.floor(points / 4);
			if ((output - 1) >= exp) {
				return lvl;
			}
		}
		return max;
	}

	public int getLevelForXp(int skill) {
		double exp = xp[skill];
		int points = 0;
		int output = 0;
		for (int lvl = 1; lvl <= (skill == DUNGEONEERING ? 120 : 99); lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			output = (int) Math.floor(points / 4);
			if ((output - 1) >= exp) {
				return lvl;
			}
		}
		return skill == DUNGEONEERING ? 120 : 99;
	}

	public void init() {
		for (int skill = 0; skill < level.length; skill++)
			refresh(skill);
		sendXPDisplay();
		if (enabledSkillsTargets == null)
			enabledSkillsTargets = new boolean[25];
		if (skillsTargetsUsingLevelMode == null)
			skillsTargetsUsingLevelMode = new boolean[25];
		if (skillsTargetsValues == null)
			skillsTargetsValues = new int[25];
		refreshEnabledSkillsTargets();
		refreshUsingLevelTargets();
		refreshSkillsTargetsValues();
		if (enabledSkillsTargets == null)
			enabledSkillsTargets = new boolean[25];
		if (skillsTargetsUsingLevelMode == null)
			skillsTargetsUsingLevelMode = new boolean[25];
		if (skillsTargetsValues == null)
			skillsTargetsValues = new int[25];
		refreshEnabledSkillsTargets();
		refreshUsingLevelTargets();
		refreshSkillsTargetsValues();
	}

	public void refresh(int skill) {
		player.getPackets().sendSkillLevel(skill);
	}

	public void sendMilestoneNews(int oldTotal, double oldExp, int oldLevel, int skill) {
		boolean maxed = true;
		int milestoneLevel = 0;
		int[] levelMilestones = { 10, 20, 30, 40, 50, 60, 70, 80, 90 };
		for (int a : levelMilestones) {
			if (oldLevel < a && getLevelForXp(0) >= a && getLevelForXp(1) >= a && getLevelForXp(2) >= a
					&& getLevelForXp(3) >= a && getLevelForXp(4) >= a && getLevelForXp(5) >= a && getLevelForXp(6) >= a
					&& getLevelForXp(7) >= a && getLevelForXp(8) >= a && getLevelForXp(9) >= a && getLevelForXp(10) >= a
					&& getLevelForXp(11) >= a && getLevelForXp(12) >= a && getLevelForXp(13) >= a
					&& getLevelForXp(14) >= a && getLevelForXp(15) >= a && getLevelForXp(16) >= a
					&& getLevelForXp(17) >= a && getLevelForXp(18) >= a && getLevelForXp(19) >= a
					&& getLevelForXp(20) >= a && getLevelForXp(21) >= a && getLevelForXp(22) >= a
					&& getLevelForXp(23) >= a && getLevelForXp(24) >= a) {
				milestoneLevel = a;
				break;
			}
		}
		if (milestoneLevel != 0) {
			World.sendWorldMessage("<img=6><col=ff0000>News: " + player.getDisplayName()
					+ " has just achieved at least level " + milestoneLevel + " in all skills!", false);
		}
		int[] totalMilestones = { 500, 750, 1000, 1500, 2000 };
		int[] xpMilestones = { 5000000, 7500000, 10000000, 20000000, 30000000, 40000000, 50000000, 60000000, 70000000,
				80000000, 90000000, 100000000, 150000000, 200000000 };

		for (int i = 0; i < Skills.SKILL_NAME.length; i++) {
			if (player.getSkills().getLevelForXp(i) < 99) {
				maxed = false;
				break;
			}
		}
		if (oldLevel < 99 && getLevelForXp(skill) == 99 || oldLevel < 120 && getLevelForXp(skill) == 120) {
			if (maxed) {
				player.getPackets()
						.sendGameMessage("<col=bd7200>You have achieved highest possible level in all skills.");
				World.sendWorldMessage("<img=6><col=ff0000>News: " + player.getDisplayName()
						+ " has just achieved at least level 99 in all skills!", false);
			} else {
				World.sendWorldMessage("<img=5><col=ff8c38>News: " + player.getDisplayName() + " has achieved level "
						+ getLevel(skill) + " " + getSkillName(skill) + ".", false);
				player.getPackets()
						.sendGameMessage("You have achieved highest possible level in " + getSkillName(skill) + ".");
			}
		}
		for (int i : totalMilestones) {
			if (oldTotal < i && getTotalLevel(player) >= i) {
				World.sendWorldMessage(
						"<img=5><col=bd7200>News: " + player.getDisplayName() + " has achieved " + i + " total level.",
						false);
			}
		}
		for (int i : xpMilestones) {
			if (oldExp < i && xp[skill] >= i) {
				World.sendWorldMessage("<img=5><col=bd7200>News: " + player.getDisplayName() + " has achieved "
						+ Utils.getFormattedNumber(i, ',') + " xp in " + getSkillName(skill) + ".", false);
			}
		}
	}

	/*
	 * if(componentId == 33) setCounterSkill(4); else if(componentId == 34)
	 * setCounterSkill(2); else if(componentId == 35) setCounterSkill(3); else
	 * if(componentId == 42) setCounterSkill(18); else if(componentId == 49)
	 * setCounterSkill(11);
	 */

	public int getCounterSkill(int skill) {
		switch (skill) {
		case ATTACK:
			return 0;
		case STRENGTH:
			return 1;
		case DEFENCE:
			return 4;
		case RANGE:
			return 2;
		case HITPOINTS:
			return 5;
		case PRAYER:
			return 6;
		case AGILITY:
			return 7;
		case HERBLORE:
			return 8;
		case THIEVING:
			return 9;
		case CRAFTING:
			return 10;
		case MINING:
			return 12;
		case SMITHING:
			return 13;
		case FISHING:
			return 14;
		case COOKING:
			return 15;
		case FIREMAKING:
			return 16;
		case WOODCUTTING:
			return 17;
		case SLAYER:
			return 19;
		case FARMING:
			return 20;
		case CONSTRUCTION:
			return 21;
		case HUNTER:
			return 22;
		case SUMMONING:
			return 23;
		case DUNGEONEERING:
			return 24;
		case MAGIC:
			return 3;
		case FLETCHING:
			return 18;
		case RUNECRAFTING:
			return 11;
		default:
			return -1;
		}

	}

	public String getSkillName(int skill) {
		String skillName = null;
		switch (skill) {
		case 0:
			return "Attack";
		case 1:
			return "Defence";
		case 2:
			return "Strength";
		case 3:
			return "Constitution";
		case 4:
			return "Ranged";
		case 5:
			return "Prayer";
		case 6:
			return "Magic";
		case 7:
			return "Cooking";
		case 8:
			return "Woodcutting";
		case 9:
			return "Fletching";
		case 10:
			return "Fishing";
		case 11:
			return "Firemaking";
		case 12:
			return "Crafting";
		case 13:
			return "Smithing";
		case 14:
			return "Mining";
		case 15:
			return "Herblore";
		case 16:
			return "Agility";
		case 17:
			return "Thieving";
		case 18:
			return "Slayer";
		case 19:
			return "Farming";
		case 20:
			return "Runecrafting";
		case 21:
			return "Hunter";
		case 22:
			return "Construction";
		case 23:
			return "Summoning";
		case 24:
			return "Dungeoneering";
		}
		return skillName;
	}

	public double addLampXP(int skill, double exp) {
		int oldTotal = getTotalLevel(player);
		double oldExp = xp[skill];
		int Lamp_XP = 5;
		player.getControlerManager().trackXP(skill, (int) exp);
		if (player.isXpLocked())
			return 0;
		exp *= Lamp_XP;
		int oldLevel = getLevelForXp(skill);
		xp[skill] += exp;
		if (xp[skill] > MAXIMUM_EXP)
			xp[skill] = MAXIMUM_EXP;
		for (int i = 0; i < trackSkills.length; i++) {
			if (trackSkills[i]) {
				if (trackSkillsIds[i] == 30
						|| (trackSkillsIds[i] == 29
								&& (skill == Skills.ATTACK || skill == Skills.DEFENCE || skill == Skills.STRENGTH
										|| skill == Skills.MAGIC || skill == Skills.RANGE || skill == Skills.HITPOINTS))
						|| trackSkillsIds[i] == getCounterSkill(skill)) {
					xpTracks[i] += exp;
					refreshCounterXp(i);
				}
			}
		}
		if (xp[skill] >= MAXIMUM_EXP) {
			xp[skill] = MAXIMUM_EXP;
		}
		int newLevel = getLevelForXp(skill);
		int levelDifference = newLevel - oldLevel;
		if (newLevel > oldLevel) {
			level[skill] += levelDifference;
			player.getDialogueManager().startDialogue("LevelUp", skill);
			if (skill == SUMMONING || (skill >= ATTACK && skill <= MAGIC)) {
				player.getAppearence().generateAppearenceData();
				if (skill == HITPOINTS)
					player.heal(levelDifference * 10);
				else if (skill == PRAYER)
					player.getPrayer().restorePrayer(levelDifference * 10);
			}
			sendMilestoneNews(oldTotal, oldExp, oldLevel, skill);
			refresh(skill);
		}
		return exp;
	}

	public void addXpNoBonus(int skill, double exp) {
		if (exp < 1)
			return;
		int oldTotal = getTotalLevel(player);
		double oldExp = xp[skill];
		player.getControlerManager().trackXP(skill, (int) exp);
		int oldLevel = getLevelForXp(skill);
		xp[skill] += exp;
		if (xp[skill] > MAXIMUM_EXP)
			xp[skill] = MAXIMUM_EXP;
		for (int i = 0; i < trackSkills.length; i++) {
			if (trackSkills[i]) {
				if (trackSkillsIds[i] == 30
						|| (trackSkillsIds[i] == 29
								&& (skill == Skills.ATTACK || skill == Skills.DEFENCE || skill == Skills.STRENGTH
										|| skill == Skills.MAGIC || skill == Skills.RANGE || skill == Skills.HITPOINTS))
						|| trackSkillsIds[i] == getCounterSkill(skill)) {
					xpTracks[i] += exp;
					refreshCounterXp(i);
				}
			}
		}

		if (xp[skill] >= MAXIMUM_EXP) {
			xp[skill] = MAXIMUM_EXP;
			return;
		}
		int newLevel = getLevelForXp(skill);
		int levelDiff = newLevel - oldLevel;
		if (newLevel > oldLevel) {
			level[skill] += levelDiff;
			player.getDialogueManager().startDialogue("LevelUp", skill);
			if (skill == SUMMONING || (skill >= ATTACK && skill <= MAGIC)) {
				player.getAppearence().generateAppearenceData();
				if (skill == HITPOINTS)
					player.heal(levelDiff * 10);
				else if (skill == PRAYER)
					player.getPrayer().restorePrayer(levelDiff * 10);
			}
			// player.getQuestManager().checkCompleted();
		}
		sendMilestoneNews(oldTotal, oldExp, oldLevel, skill);
		refresh(skill);
	}

	public void addXp(int skill, double exp) {
		if (exp < 1)
			return;
		int oldTotal = getTotalLevel(player);
		double oldExp = xp[skill];
		if (getXp(skill) >= 200000000) {
			exp *= 1;
		} else {
			if ((skill >= 0 && skill <= 4) || skill == 6) {
				if (player.getTemporaryTarget() != null && player.isAtWild()
						&& !(player.getTemporaryTarget() instanceof NPC))
					exp *= 1;
				else if (player.getTemporaryTarget() != null && player.isAtWild()
						&& (player.getTemporaryTarget() instanceof NPC))
					exp *= Settings.COMBAT_XP_RATE;
				else
					exp *= Settings.COMBAT_XP_RATE;
			} else if (skill == 23) {
				exp *= Settings.SUMMONING_XP_RATE;
			} else if (skill == 24) {
				exp *= 50;
			} else if (skill == Skills.PRAYER) {
				exp *= 10;
			} else {
				exp *= Settings.SKILLING_XP_RATE;
			}
			exp *= 1;
		}
		double normalExp = exp;
		double bonusExp = (normalExp) * player.getBonusExp() - normalExp;
		exp *= player.getBonusExp();
		if (player.getBonusExp() <= 1)
			player.getVarsManager().sendVar(2044, 0);
//	player.getPackets().sendGameMessage("Normal Exp: " + normalExp + " (Bonus Exp: " + bonusExp +")");
		if (player.getBonusExp() > 1) {
			if (skill >= 0 && skill <= 6 && skill != 5)
				player.getVarsManager().sendVar(2044, (int) (bonusExp * 40));
			else
				player.getVarsManager().sendVar(2044, (int) (bonusExp * 10));
		}
		if (player.getAssist().isAssisted) {
			AssistManager.GiveEXP(player, skill, exp);
			return;
		}
		player.getControlerManager().trackXP(skill, (int) exp);
		int oldLevel = getLevelForXp(skill);
		xp[skill] += exp;
		for (int i = 0; i < trackSkills.length; i++) {
			if (trackSkills[i]) {
				if (trackSkillsIds[i] == 30
						|| (trackSkillsIds[i] == 29
								&& (skill == Skills.ATTACK || skill == Skills.DEFENCE || skill == Skills.STRENGTH
										|| skill == Skills.MAGIC || skill == Skills.RANGE || skill == Skills.HITPOINTS))
						|| trackSkillsIds[i] == getCounterSkill(skill)) {
					xpTracks[i] += exp;
					refreshCounterXp(i);
				}
			}
		}
		int newLevel = getLevelForXp(skill);
		int levelDiff = newLevel - oldLevel;
		if (newLevel > oldLevel) {
			level[skill] += levelDiff;
			player.getDialogueManager().startDialogue("LevelUp", skill);
			if (skill == SUMMONING || (skill >= ATTACK && skill <= MAGIC)) {
				player.getAppearence().generateAppearenceData();
				if (skill == HITPOINTS)
					player.heal(levelDiff * 10);
				else if (skill == PRAYER)
					player.getPrayer().restorePrayer(levelDiff * 10);
			}
		}
		sendMilestoneNews(oldTotal, oldExp, oldLevel, skill);
		refresh(skill);
	}

	public int time = 3600;

	public void setTime(int time) {
		this.time = time;
	}

	public void addSkillXpRefresh(int skill, double xp) {
		this.xp[skill] += xp;
		level[skill] = (short) getLevelForXp(skill);
	}

	public void resetSkillNoRefresh(int skill) {
		xp[skill] = 0;
		level[skill] = 1;
	}

	public void setXp(int skill, double exp) {
		xp[skill] = exp;
		refresh(skill);
	}

	public boolean hasTwo99s() {
		int count = 0;
		for (int i = 0; i < 25; i++) {
			if (player.getSkills().getLevelForXp(i) < 99) {
				count++;
				if (count >= 24)
					return false;
			}
		}
		return true;
	}
}
