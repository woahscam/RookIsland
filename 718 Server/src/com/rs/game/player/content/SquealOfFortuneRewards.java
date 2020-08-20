package com.rs.game.player.content;

import com.rs.game.World;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.utils.EconomyPrices;
import com.rs.utils.Utils;

/**
 * 
 * @author Andreas - AvalonPK
 * 
 */

public class SquealOfFortuneRewards {

	public static final Rewards[] items = Rewards.values();
	public static final double COMMON = 75.0;
	public static final double UNCOMMON = 35.0;
	public static final double RARE = 3.250;
	public static final double VERY_RARE = 0.125;

	public enum Rewards {
		
		/*
		 * Supplies
		 */
		
		PURE_ESSENCE(7937, COMMON), MAPLE_LOGS(1518, COMMON), YEW_LOGS(1516, COMMON), MAGIC_LOGS(1514, COMMON), COAL(454, COMMON), GOLD_ORE(445, COMMON),
		RUNITE_ORE(452, COMMON), UNCUT_DIAMOND(1618, COMMON), UNCUT_RUBY(1620, COMMON), UNCUT_EMERALD(1622, COMMON), GREEN_DRAGONHIDE(1754, COMMON), 
		RED_DRAGONHIDE(1750, COMMON), 

		/*
		 * Auras
		 */

		POISON_PURGE(20958, COMMON), RUNIC_ACCURACY(20962, COMMON), SHARPSHOOTER(20967, COMMON), QUARRYMASTER(22284,
				COMMON), SALVATION(22899, COMMON), CALLOFTHESEA(20966, COMMON), CORRUPTION(22905,
						COMMON), REVERENCE(20965, COMMON), FIVEFINGER(22288, COMMON), HARMONY(23848, COMMON),

		GREATER_HARMONY(23850, UNCOMMON), GREATER_SALVATION(22901, UNCOMMON), GREATER_CORRUPTION(22907, UNCOMMON),
		VAMPYRISM(22298, UNCOMMON),

		MASTER_HARMONY(23852, RARE), MASTER_SALVATION(22903, RARE), MASTER_CORRUPTION(22909, RARE),
		

		/*
		 * Lucky Items
		 */

		ARMADYL_GODSWORD(23679, VERY_RARE), BANDOS_GODSWORD(23680, VERY_RARE), SARADOMIN_GODSWORD(23681,
				VERY_RARE), ZAMORAK_GODSWORD(23682, VERY_RARE), ZAMORAKIAN_SPEAR(23683, VERY_RARE), ARMADYL_HELMET(
						23684, RARE), ARMADYL_CHESTPLATE(23685, VERY_RARE), ARMADYL_CHAINSKIRT(23686,
								VERY_RARE), BANDOS_CHESTPLATE(23687, VERY_RARE), BANDOS_TASSETS(23688,
										VERY_RARE), BANDOS_BOOTS(23689, RARE), SARADOMIN_SWORD(23690,
												RARE), ABYSSAL_WHIP(23691, RARE), DRAGON_FULLHELM(23692,
														VERY_RARE), DRAGON_PLATEBODY(23693,
																VERY_RARE), DRAGON_CHAINBODY(23694, RARE), DRAGON_CLAWS(
																		23695, VERY_RARE), DRAGON_2H_SWORD(23696,
																				RARE), ARCANE_SPIRIT_SHIELD(23697,
																						VERY_RARE), DIVINE_SPIRIT_SHIELD(
																								23698,
																								VERY_RARE), ELYSIAN_SPIRIT_SHIELD(
																										23699,
																										VERY_RARE), SPECTRAL_SPIRIT_SHIELD(
																												23700,
																												VERY_RARE),
		
		/*
		 * Special
		 */
		
		TRIBAL_TATTOO(23665, RARE), MARAUDER_TATTOO(23666, RARE), ARCANE_TATTOO(23667, RARE), BOLD_TATTOO(23668, RARE), MYSTIC_TATTOO(23668, RARE),
		INTRICATE_TATTOO(23670, RARE), FLYING_GOBLIN_HAT(23673, RARE), CURLED_HORNS(23675, RARE), SWAG_BAG(23672, RARE), TWISTED_HORNS(23678, RARE),
		LONG_HORNS(23677, RARE), SWAGGER_STICK(23671, RARE), VILE_HORNS(23676, RARE), FISH_MASK(24431, RARE), DRAGON_CEREMONIAL_HAT(24329, RARE),
		DRAGON_CEREMONIAL_BREASTPLATE(24330, RARE), DRAGON_CEREMONIAL_GREAVES(24331, RARE), DRAGON_CEREMONIAL_BOOTS(24332, RARE), DRAGON_CEREMONIAL_CAPE(24333, RARE),
		QUEENS_GUARD_HAT(24324, RARE), QUEENS_GUARD_SHIRT(24325, RARE), QUEENS_GUARD_TROUSERS(24326, RARE), QUEENS_GUARD_SHOES(24327, RARE), QUEENS_GUARD_STAFF(24328, RARE),
		MONKEY_MACE(24294, RARE), DOUBLE_SPIN_TICKET(24155, UNCOMMON), SPIN_TICKET(24154, COMMON),
		
		

		/*
		 * Misc
		 */

		COINS(995, COMMON), COINS2(995, UNCOMMON), COINS3(995, RARE), COINS4(995, VERY_RARE), THIRD_AGE_DRUIDIC_STAFF(
				19308, VERY_RARE), THIRD_AGE_DRUIDIC_CLOAK(19311, VERY_RARE), THIRD_AGE_DRUIDIC_WREATH(19314,
						VERY_RARE), THIRD_AGE_DRUIDIC_ROBE_TOP(19317, VERY_RARE), THIRD_AGE_DRUIDIC_ROBE(19320,
								VERY_RARE), THIRD_AGE_RANGE_TOP(10330, VERY_RARE), THIRD_AGE_RANGE_LEGS(10332,
										VERY_RARE), THIRD_AGE_RANGE_COIF(10334, VERY_RARE), THIRD_AGE_VAMBRACES(10336,
												VERY_RARE), THIRD_AGE_ROBE_TOP(10338, VERY_RARE), THIRD_AGE_ROBE(10340,
														VERY_RARE), THIRD_AGE_MAGE_HAT(10342,
																VERY_RARE), THIRD_AGE_AMULET(10344,
																		VERY_RARE), THIRD_AGE_PLATELEGS(10346,
																				VERY_RARE), THIRD_AGE_PLATEBODY(10348,
																						VERY_RARE), THIRD_AGE_FULL_HELMET(
																								10350,
																								VERY_RARE), THIRD_AGE_KITESHIELD(
																										10352,
																										VERY_RARE), RANGER_BOOTS(
																												2577,
																												RARE), ROBIN_HOOD_HAT(
																														2581,
																														RARE),

		;

		private int itemId;

		private double chance;

		private Rewards(int itemId, double chance) {
			this.itemId = itemId;
			this.chance = chance;
		}

		public int getItemId() {
			return itemId;
		}

		public void setItemId(int itemId) {
			this.itemId = itemId;
		}

		public double getChance() {
			return chance;
		}

		public void setChance(double chance) {
			this.chance = chance;
		}

	}

	public static void sendRewards(Player player) {
		player.setSpins(player.getSpins() - 1);
		Rewards reward = calculateRewards();
		Item item = new Item(reward.getItemId());
		int amount = getRewardAmount(reward);
		if (reward.getChance() != RARE && reward.getChance() != VERY_RARE)
		player.getPackets().sendGameMessage("<shad=3e85ba><col=3e85ba>You won " + (amount > 1 ? Utils.getFormattedNumber(amount, ',')  + " " + item.getName() : item.getName()) + " in squeal of fortune!" + (ItemConstants.isTradeable(item) && reward.getItemId() != 995 ? "<br><shad=3e85ba><col=3e85ba>Value of " + Utils.getFormattedNumber(EconomyPrices.getPrice(item.getId()) * amount,',') + " coins!" : ""));
		if (reward.getItemId() == 995)
		player.getMoneyPouch().addMoney(amount, false);
		else
		player.getInventory().addItem(reward.getItemId(), amount);
		if (reward.getChance() == RARE || reward.getChance() == VERY_RARE) {
			World.sendWorldMessage("<img=7><col=36648b>News: " + player.getDisplayName() + " won a " + (reward.getChance() == VERY_RARE ? "very rare " : "rare ") + (amount > 1 ? Utils.getFormattedNumber(amount, ',')  + " " + item.getName() : item.getName())
					+ " from a squeal of fortune." + (ItemConstants.isTradeable(item) && reward.getItemId() != 995 ? "<br><img=7><col=36648b>News: Value of " + Utils.getFormattedNumber(EconomyPrices.getPrice(item.getId()) * amount,',') + " coins!" : ""), false);
		}
		player.getPackets().sendGameMessage("You have " + (player.getSpins() == 0 ? "You're out of spins!" : player.getSpins() == 1 ? "One spin left." : player.getSpins() + " spins left."));

	}
	
	public static void sendBankRewards(Player player) {
		Rewards reward = calculateRewards();
		Item item = new Item(reward.getItemId());
		int amount = getRewardAmount(reward);
		if (reward.getChance() != RARE && reward.getChance() != VERY_RARE)
		player.getPackets().sendGameMessage("<shad=3e85ba><col=3e85ba>You won " + (amount > 1 ? Utils.getFormattedNumber(amount, ',')  + " " + item.getName() : item.getName()) + " in squeal of fortune!" + (ItemConstants.isTradeable(item) && reward.getItemId() != 995 ? "<br><shad=3e85ba><col=3e85ba>Value of " + Utils.getFormattedNumber(EconomyPrices.getPrice(item.getId()) * amount,',') + " coins!" : ""));
		if (reward.getItemId() == 995)
		player.getMoneyPouch().addMoney(amount, false);
		else
		player.getBank().addItem(reward.getItemId(), amount, true);
		if (reward.getChance() == RARE || reward.getChance() == VERY_RARE) {
			World.sendWorldMessage("<img=7><col=36648b>News: " + player.getDisplayName() + " won a " + (reward.getChance() == VERY_RARE ? "very rare " : "rare ") + (amount > 1 ? Utils.getFormattedNumber(amount, ',')  + " " + item.getName() : item.getName())
					+ " from a squeal of fortune." + (ItemConstants.isTradeable(item) && reward.getItemId() != 995 ? "<br><img=7><col=36648b>News: Value of " + Utils.getFormattedNumber(EconomyPrices.getPrice(item.getId()) * amount,',') + " coins!" : ""), false);
		}
	}

	private static Rewards calculateRewards() {
		while (true) {
			double chance = Utils.getRandomDouble(100);
			Rewards reward = Rewards.values()[Utils.getRandom(Rewards.values().length - 1)];
			if ((reward.getChance()) > chance)
				return reward;
			else
				continue;
		}
	}

	private static int getRewardAmount(Rewards reward) {
		switch (reward) {
		case GREEN_DRAGONHIDE:
			return 100;
		case RED_DRAGONHIDE:
			return 100;
		case PURE_ESSENCE:
			return 400;
		case MAPLE_LOGS:
			return 500;
		case YEW_LOGS:
			return 250;
		case MAGIC_LOGS:
			return 100;
		case COAL:
			return 1000;
		case GOLD_ORE:
			return 500;
		case RUNITE_ORE:
			return 20;
		case UNCUT_DIAMOND:
			return 10;
		case UNCUT_EMERALD:
			return 40;
		case UNCUT_RUBY:
			return 20;
		case COINS:
			return Utils.random(50000, 500000);
		case COINS2:
			return Utils.random(500000, 2000000);
		case COINS3:
			return Utils.random(3000000, 10000000);
		case COINS4:
			return Utils.random(50000000, 200000000);
		default:
			return 1;
		}
	}
}
