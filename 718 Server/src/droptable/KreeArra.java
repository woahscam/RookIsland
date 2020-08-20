package droptable;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.npc.MobRewardNodeBuilder;
import com.rs.game.player.Player;

public final class KreeArra extends MobRewardNodeBuilder {

	public KreeArra() {
		super(new Object[] { "Kree'arra", 6222 });
	}

	@Override
	public void populate(Player player) {
		boolean row = ItemDefinitions.getItemDefinitions(player.getEquipment().getRingId()).getName().toLowerCase()
				.contains("ring of wealth");
		int shake = shake(128);
		if (shake == 1 || row && shake <= 2) {
			if (row && shake <= 2) {
				player.getPackets().sendGameMessage("<col=ff7000>Your ring of wealth shines more brightly!</col>");
			}
			player.setRareDrop(true);
			dissectNodeBatch(1, 
					node(11718, 1),
					node(11720, 1),
					node(11722, 1),
					node(11702, 1));
		} else
		switch (rarityNode(player)) {
		case -1:
		case COMMON:
			dissectNodeBatch(1, node(995, 19501, 21000), 
					node(892, 100, 105), 
					node(9144, 18, 25), 
					node(2503, 1), 
					node(9185, 1));
			break;
		case RARE:
		case VERY_RARE:
			dissectNodeBatch(1, node(995, 19501, 21000), 
					node(995, 20500, 21000), 
					node(989, 1), 
					node(5315, 1), 
					node(11710, 1), 
					node(11712, 1), 
					node(11714, 1));
			break;
		case UNCOMMON:
			dissectNodeBatch(1, node(6694, 15), 
					node(9244, 2, 15), 
					node(218, 5, 22), 
					node(163, 3), 
					node(170, 3), 
					node(5303, 3), 
					node(1303, 1));
			break;
		}
		addObj(526, 1);// bones
		addObj(314, 1, 15);
		shakeTreasureTrail(player, HARD_CLUE);
		shakeTreasureTrail(player, ELITE_CLUE);
		shakeSummoningCharm(1, 23, 12, 14, 6);

	}
}
