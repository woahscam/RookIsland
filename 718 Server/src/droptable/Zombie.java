package droptable;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.npc.MobRewardNodeBuilder;
import com.rs.game.player.Player;

public final class Zombie extends MobRewardNodeBuilder {

	public Zombie() {
		super(new Object[] { "Zombie", 75 });
	}

	@Override
	public void populate(Player player) {
		boolean row = ItemDefinitions.getItemDefinitions(player.getEquipment().getRingId()).getName().toLowerCase()
				.contains("ring of wealth");
		int shake = shake(512);
		if (shake == 1 || row && shake <= 2) {
			if (row && shake <= 2) {
				player.getPackets().sendGameMessage("<col=ff7000>Your ring of wealth shines more brightly!</col>");
			}
			player.setRareDrop(true);
			dissectNodeBatch(1, node(ZOMBIE_CHAMPION_SCROLL, 1));
		}
		switch (rarityNode(player)) {
		case -1:
		case COMMON:
			dissectNodeBatch(1, node(GRIMY_GUAM, 1), 
					node(GRIMY_MARRENTILL, 1), 
					node(GRIMY_TARROMIN, 1), 
					node(STEEL_ARROW, 5, 32), 
					node(BODY_RUNE, 3, 11), 
					node(COINS, 1, 82), 
					nodeB(FISHING_BAIT, 1, 5, 7, 9), 
					node(LIMESTONE_BRICK, 2));
			break;
		case UNCOMMON:
			dissectNodeBatch(1, node(GRIMY_HARRALANDER, 1), 
					node(GRIMY_RANARR, 1), 
					node(GRIMY_IRIT, 1), 
					node(GRIMY_AVANTOE, 1), 
					node(BRONZE_AXE, 1), 
					node(BRONZE_LONGSWORD, 1), 
					node(BRONZE_MED_HELM, 1), 
					node(BRONZE_KITESHIELD, 1), 
					node(IRON_DAGGER, 1), 
					node(IRON_AXE, 1), 
					node(IRON_MACE, 1), 
					nodeB(IRON_ARROW, 5, 8), 
					nodeB(MITHRIL_ARROW, 1, 2), 
					node(SLING, 1), 
					node(CHAOS_RUNE, 4), 
					node(AIR_RUNE, 3), 
					node(FIRE_RUNE, 7, 84), 
					node(MIND_RUNE, 5, 7), 
					node(NATURE_RUNE, 5), 
					node(LAW_RUNE, 2), 
					node(COSMIC_RUNE, 2), 
					node(TINDERBOX, 1), 
					node(TILE, 1), 
					node(BEER, 1), 
					node(COPPER_ORE, 1), 
					node(TIN_ORE, 1), 
					node(ASHES, 1), 
					node(EYE_OF_NEWT, 1), 
					node(UNHOLY_MOULD, 1));
			break;
		case RARE:
			dissectNodeBatch(1, node(GRIMY_KWUARM, 1));
			break;
		case VERY_RARE:
			dissectNodeBatch(1, node(GRIMY_CADANTINE, 1), 
					node(GRIMY_LANTADYME, 1), 
					node(GRIMY_DWARF_WEED, 1), 
					node(HALF_A_MEAT_PIE, 1));
			break;
		}
		addObj(BONES, 1);
	}
}
