package com.rs.game.npc;

/**
 * A class representing a single drop's blue-print skeleton.
 * 
 * @author Savions Sw
 *
 */
public class DropBluePrint {

	/**
	 * This represents the drop's id.
	 */
	private final int id;

	/**
	 * This represents the minimum amount of the drop.
	 */
	private final int minAmount,

			/**
			 * This represents the maximum amount of the drop.
			 */
			maxAmount;

	/**
	 * This represents the rarity.
	 */
	private final int rarity;

	/**
	 * Constructs a new {@code DropBluePrint} {@code Object}.
	 * 
	 * @param id
	 *            The id.
	 * @param amount
	 *            The amount.
	 * @param rarity
	 *            The rarity.
	 */
	public DropBluePrint(int id, int amount, int rarity) {
		this(id, amount, amount, rarity);
	}

	/**
	 * Constructs a new {@code DropBluePrint} {@code Object}.
	 * 
	 * @param id
	 *            The id.
	 * @param minAmount
	 *            The minimum amount.
	 * @param maxAmount
	 *            The maximum amount.
	 * @param rarity
	 *            The rarity.
	 */
	public DropBluePrint(int id, int minAmount, int maxAmount, int rarity) {
		this.id = id;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.rarity = rarity;
	}

	/**
	 * Get the id of this drop.
	 * 
	 * @return The {@code id}.
	 */
	public final int getId() {
		return id;
	}

	/**
	 * Get the minimum amount of this drop.
	 * 
	 * @return The {@code minAmount}.
	 */
	public final int getMinimumAmount() {
		return minAmount;
	}

	/**
	 * Get the maximum amount of this drop.
	 * 
	 * @return The {@code maxAmount}.
	 */
	public final int getMaximumAmount() {
		return maxAmount;
	}

	/**
	 * Get the rarity index of this drop.
	 * 
	 * @return The {@code rarity}.
	 */
	public final int getRarityIndex() {
		return rarity;
	}
}