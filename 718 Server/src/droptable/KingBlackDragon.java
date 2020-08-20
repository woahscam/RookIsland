package droptable;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.npc.MobRewardNodeBuilder;
import com.rs.game.player.Player;

public final class KingBlackDragon extends MobRewardNodeBuilder {

	public KingBlackDragon() {
		super(new Object[] { "King black dragon", 50 });
	}

	@Override
	public void populate(Player player) {
		double row = ItemDefinitions.getItemDefinitions(player.getEquipment().getRingId()).getName().toLowerCase()
				.contains("ring of wealth") ? 0.75 : 0;
		double shake = shake(256);
		if (shake <= 1 + row) {
			if (row > 0) {
				player.getPackets().sendGameMessage("<col=ff7000>Your ring of wealth shines more brightly!</col>");
			}
			player.setRareDrop(true);
			dissectNodeBatch(1, node(DRACONIC_VISAGE, 1));
		} else
		switch (rarityNode(player)) {
		case -1:
		case COMMON:
			dissectNodeBatch(1, 
					node(1620, 6), 
					node(1514, 10), 
					nodeB(1516, 50, 80), 
					node(565, 30, 50), 
					node(560, 50));
			break;
		case VERY_RARE:
			dissectNodeBatch(1,
					node(25312, 1), 
					node(25314, 1),
					node(7980, 1));
			break;
		case RARE:
			dissectNodeBatch(1,
					node(1149, 1));
			break;
		case UNCOMMON:
			dissectNodeBatch(1, 
					node(888, 250), 
					nodeB(892, 50, 75, 100), 
					node(1127, 1), 
					node(1185, 1), 
					node(2362, 8), 
					node(2364, 2), 
					node(445, 50), 
					node(452, 3), 
					node(443, 100), 
					node(563, 50), 
					node(1373, 1), 
					node(1303, 1));
			break;
		}
		addObj(536, 1);// dragon bones
		addObj(1747, 1);// dragonhide
		shakeTreasureTrail(player, HARD_CLUE);
		shakeTreasureTrail(player, ELITE_CLUE);
		shakeSummoningCharm(4, 4, 3, 59, 0.9);

	}
}
