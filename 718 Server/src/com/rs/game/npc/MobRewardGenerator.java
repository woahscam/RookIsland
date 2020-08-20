package com.rs.game.npc;

import java.util.HashMap;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.utils.Iterate;
import com.rs.utils.Utils;

/**
 * Contains the spoils for a {@link Player} when they defeat a {@link Mob}.
 *
 * @author Andreas
 * @version 1.0
 * @since 10/07/2018
 */
public final class MobRewardGenerator {
	
	/**
	 * The path of the drop table.
	 */
	private static final String DROP_TABLE_PATH = "bin/droptable";
	
	/**
	 * The logger for this specific class.
	 */
	//private static final Logger logger = Logger.getLogger(MobRewardGenerator.class.getSimpleName());
	
	/**
	 * The rewards map holding all mob drops.
	 */
	private final HashMap<Object, Class<? extends MobRewardNodeBuilder>> rewards = new HashMap<>();
	
	/**
	 * The current generator.
	 */
	private static final MobRewardGenerator mobRewardGenerator = new MobRewardGenerator();
	
	
	/**
	 * Get the current generator for the server.
	 * 
	 * @return the mob reward generator.
	 */
	public static MobRewardGenerator getGenerator() {
		return mobRewardGenerator;
	}
	
	/**
	 * Get the rewards map.
	 * 
	 * @return the rewards map.
	 */
	public HashMap<Object, Class<? extends MobRewardNodeBuilder>> getRewards() {
		return rewards;
	}
	
	/**
	 * Populate the rewards map.
	 */
	public void populateNodeBuilders() {
		Iterate.classes(DROP_TABLE_PATH, node -> {
			((MobRewardNodeBuilder) node).populateAll();
		});
	}
	
	/**
	 * Generate the reward for killing a mob.
	 * 
	 * @param mob The mob that was killed.
	 * @param player The player(s) who dealt the most damage.
	 * @return an {@link Item} array containing the reward.
	 */
	public Item[] generateReward(final NPC mob, final Player player) {
		if (getGenerator().getRewards().size() == 0) {
			MobRewardGenerator.getGenerator().populateNodeBuilders();
			System.out.println("generating builder, now size:" +getGenerator().getRewards().size());
		}
		Class<? extends MobRewardNodeBuilder> clazz = getGenerator().getRewards().get(mob.getId());
		if (clazz == null)
			clazz = getGenerator().getRewards().get(mob.getName());
		if (clazz == null)
			return null;
		try {
			MobRewardNodeBuilder node = clazz.getConstructor().newInstance();
			node.populate(player);
			return node.generateReward(player, mob);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private int VERY_RARE = 994;//994 0.6%
	private int RARE = 976;//976 2.4%
	private int UNCOMMON = 685;//685 31.5%
	private int COMMON = 445;//445 55.5%

	
	/**
	 * Get a generated rarity node.
	 * @param player 
	 * 
	 * 
	 * @return a randomly generated rarity node.
	 */
	public int generateRarityNode(Player player) {
		int shake = Utils.random(1000);
		final int row = ItemDefinitions.getItemDefinitions(player.getEquipment().getRingId()).getName().toLowerCase().contains("ring of wealth") ? 5 : 0;
		final int[] rarities = {  VERY_RARE, RARE, UNCOMMON, COMMON };
		for (int index = 0; index < rarities.length; index++) {
			if (shake > (rarities[index] - row)) {
				player.setVeryRareDrop(index == 0);
				player.setRareDrop(index == 1);
				if ((index == 0 || index == 1) && row > 0) 
					player.getPackets().sendGameMessage("<col=ff7000>Your ring of wealth shines more brightly!</col>");
					return 3 - index;
			}
		}
		return -1;
	}
	
	public void sendLootBeam(NPC mob, Item item, Player player, NPC npc) {
		int x = mob.getCoordFaceX(mob.getSize());
		int y = mob.getCoordFaceY(mob.getSize());
		int z =mob.getPlane();
		player.getPackets().sendGameMessage("<col=ff8c38>A loot beam appears on your rare drop.");
		World.sendGraphics(player, new Graphics(7, 0, 0), new WorldTile(x, y, z));
		player.setBeam(new WorldTile(x, y, z));
		player.setBeamItem(item);
	}

	/**
	 * Default private constructor to prevent instantiation by other classes.
	 */
	private MobRewardGenerator() {
		
	}

}