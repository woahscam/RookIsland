package com.rs.game.player.content;

import java.io.Serializable;

import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.controlers.CorpBeastControler;
import com.rs.game.player.controlers.CrucibleControler;
import com.rs.game.player.controlers.WildernessControler;
import com.rs.utils.Utils;

/**
 *
 * @Author Tristam <Hassan>
 * @Project - 1. Rain
 * @Date - 15 Mar 2016
 * 
 */

public class Ironman implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7849063233657093838L;

	/** The player. */
	private Player player;

	/** The HC death check. */
	public boolean HCDeath;

	/** The meaning of life. @Kappa */
	public int Life;

	/** The modes. */
	public int Ironman_Mode;

	/** The Titles. */
	public String[] Titles = { "<col=540037>Ironman</col> ", "<col=630019>Hardcore Ironman</col> " };

	/** Badge ID for the represented rank. */
	public int[] Badge = { 23, 24 };

	/** The Gameplay modifier for ironmen. (Ex: XP / Gameplay Modifier) */
	public final static int Gameplay_Modifier = 2;

	/**
	 * Instantiates a new ironman.
	 */
	public Ironman() {

	}

	/**
	 * Sets the player.
	 *
	 * @param ironman the new player
	 */
	public void setPlayer(Player ironman) {
		player = ironman;
	}

	/**
	 * Sets the mode.
	 *
	 * @param mode the mode
	 * @return the int
	 */
	public int setMode(int mode) {
		return player.getIronman().Ironman_Mode = mode;
	}

	/**
	 * Gets the mode.
	 *
	 * @param player the player
	 * @param mode   the mode
	 * @return the mode
	 */
	public boolean getMode(Player player, int mode) {
		return (mode == 1 ? player.getIronman().Ironman_Mode == 1 : player.getIronman().Ironman_Mode == 2);
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return Titles[Ironman_Mode - 1];
	}

	/**
	 * Gets the badge
	 * 
	 * @return the badge
	 */
	public int getBadge() {
		return Badge[Ironman_Mode - 1];
	}

	/**
	 * Checks if is ironman.
	 *
	 * @return true, if is ironman
	 */
	public boolean isIronman() {
		return (player.getIronman().Ironman_Mode == 2 || player.getIronman().Ironman_Mode == 1);
	}
	
	public boolean isRegularIronman() {
		return player.getIronman().Ironman_Mode == 1;
	}
	
	public boolean isHardcoreIronman() {
		return player.getIronman().Ironman_Mode == 2;
	}

	/**
	 * Adds the life.
	 *
	 * @param life the l
	 * @return the int
	 */
	public int addLife(int l) {
		return Life += l;
	}

	/**
	 * The price handler for each life.
	 */
	public int getLifePrices(double multiplier) {
		return (int) ((Life * 2500000) * multiplier);
	}

	/**
	 * Exchange items used on bank.
	 *
	 * @param player the player
	 * @param item   the item
	 */
	public static void ExchangeItems(Player player, Item item) {
		int amount = player.getInventory().getAmountOf(item.getId());
		int freeSlots = player.getInventory().getFreeSlots();
		amount = amount > freeSlots && amount != freeSlots + 1 ? freeSlots : amount;
		if (!item.getDefinitions().canBeNoted()) {
			player.sm("<u><col=99000>" + item.getName()
					+ " can not be exchanged, this means that it can not be noted or un noted.");
			return;
		}
		if (item.getDefinitions().isNoted()) {
			player.getInventory().deleteItem(item.getId(), amount);
			player.getInventory().addItem(item.getId() - 1, amount);
		} else {
			player.getInventory().deleteItem(item.getId(), amount);
			player.getInventory().addItem(item.getId() + 1, amount);
		}
	}

	/**
	 * Handles deaths in wilderness.
	 *
	 * @param player the player
	 * @param killer the killer
	 */
	public void WildernessDeath(Player player, Player killer) {
		if (getMode(player, 2)) {
			if (player.getControlerManager().getControler() instanceof WildernessControler
					|| player.getControlerManager().getControler() instanceof CrucibleControler) {
				takeLife(player, killer);
			}
		}
	}

	/**
	 * Handles the death.
	 *
	 * @param player the player
	 * @param source the source
	 */
	public void Death(Player player, Entity source) {
		NPC npc = (NPC) source;
		if (getMode(player, 2)) {
			if (player.getControlerManager().getControler() instanceof CorpBeastControler) {
				takeLife(npc);
				return;
			}
			takeLife(npc);
		}
	}

	/**
	 * Take life life from HC ironman.
	 *
	 * @param killer the killer
	 */
	public void takeLife(Player player, Player killer) {
		final int PlayerTotal = player.getSkills().getTotalLevel(player),
				PlayerTotalXP = player.getSkills().getTotalXP(player);
		if (killer != null) {
			if (player.getIronman().Life > 1) {
				player.getIronman().Life--;
				player.sm("<col=990000>You lost one life! You have " + player.getIronman().Life + " left.");
			} else {
				World.sendWorldMessage("<img=24>News: <col=990000>" + player.getDisplayName()
						+ " just died with a total level of " + PlayerTotal + " ("
						+ Utils.getFormattedNumber(PlayerTotalXP, ',') + " XP)" + " in a battle against "
						+ killer.getDisplayName() + ".", false);
				player.getPackets()
						.sendGameMessage("You have fallen as a Hardcore Iron "
								+ (player.getAppearence().isMale() ? "Man" : "Woman")
								+ ", your Hardcore status has been revoked.");
				player.getIronman().setMode(1);
			}
		} else {
			if (player.getIronman().Life > 1) {
				player.getIronman().Life--;
				player.sm("<col=990000>You lost one life! You have " + player.getIronman().Life + " left.");
			} else {
				World.sendWorldMessage("<img=24>News: <col=990000>" + player.getDisplayName()
						+ "</col> just died with a total level of <col=990000>" + PlayerTotal + "</col> ("
						+ Utils.getFormattedNumber(PlayerTotalXP, ',') + " XP)" + ".", false);
				player.getPackets()
						.sendGameMessage("You have fallen as a Hardcore Iron"
								+ (player.getAppearence().isMale() ? "man" : "woman")
								+ ", your Hardcore status has been revoked.");
				player.getIronman().setMode(1);
			}
		}
	}

	/**
	 * Takes life from HC ironman.
	 *
	 * @param npc the npc
	 */
	public void takeLife(NPC npc) {
		final int PlayerTotal = player.getSkills().getTotalLevel(player),
				PlayerTotalXP = player.getSkills().getTotalXP(player);
		if (Life > 0) {
			Life--;
			player.sm("<col=990000>You lost one life! You have " + Life + " left.");
			if (Life == 0) {
				finish();
				World.sendWorldMessage("<img=12><col=990000>News: " + player.getDisplayName()
						+ " just died in Hardcore ironman mode with a skill total of " + PlayerTotal + " ("
						+ Utils.getFormattedNumber(PlayerTotalXP, ',') + " XP) fighting against " + npc.getName() + "!",
						false);
			}
		}
	}

	/**
	 * Finishes the life of the HC ironman.
	 */
	private void finish() {
		HCDeath = true;
		Life = 0;
		player.getSession().getChannel().disconnect();
	}

	/**
	 * Spawns death and portal.
	 */
	public void SpawnDeath() {
		NPC death = new NPC(8977, new WorldTile(1888, 5130, 0), -1, false);
		WorldObject portal = new WorldObject(11369, 10, 0, 3077, 3484, 0);
		ObjectDefinitions defs = ObjectDefinitions.getObjectDefinitions(11369);
		defs.setName("Death's Portal");
		World.spawnObject(portal);
		death.setName("Death");
	}
}
