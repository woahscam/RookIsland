package droptable;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.npc.MobRewardNodeBuilder;
import com.rs.game.player.Player;

public final class Banshee extends MobRewardNodeBuilder {

	public Banshee() {
		super(new Object[] { "Banshee", 1612 });
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
			dissectNodeBatch(1, node(15355, 1));
		}
		switch (rarityNode(player)) {
		case -1:
		case COMMON:
			dissectNodeBatch(1, node("air rune", 3), 
					nodeB("chaos rune", 3, 6,7, 17), 
					node("grimy guam", 1), 
					node("grimy tarromin", 1), 
					node("grimy marrentill", 1), 
					node("grimy harralander", 1), 
					node("grimy kwuarm", 1), 
					nodeB("pure essence#noted", 13, 23), 
					nodeB("fishing bait", 7, 15));
			break;
		case UNCOMMON:
			dissectNodeBatch(1, node("iron mace", 1), 
					node("cosmic rune", 2), 
					nodeB("fire rune", 6, 7), 
					node("grimy ranarr", 1), 
					node("grimy irit", 1), 
					node("grimy avantoe", 1), 
					node("grimy lantadyme", 1), 
					node("grimy dwarf weed", 1), 
					node("eye of newt", 1), 
					node("iron ore", 1));
			break;
		case RARE:
			dissectNodeBatch(1, node("iron dagger", 1), 
					node("iron kiteshield", 1), 
					node("grimy cadantine", 1));
			break;
		case VERY_RARE:
			dissectNodeBatch(1, node("adamant kiteshield", 1), 
					node(4105, 1));
			break;
		}
		shakeTreasureTrail(player, EASY_CLUE);
		shakeSummoningCharm(1, 2.5, 7, 1, 0.35);

	}
}
