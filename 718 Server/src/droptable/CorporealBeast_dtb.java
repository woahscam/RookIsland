package droptable;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.npc.MobRewardNodeBuilder;
import com.rs.game.player.Player;
import com.rs.utils.Utils;

public final class CorporealBeast_dtb extends MobRewardNodeBuilder {

	public CorporealBeast_dtb() {
		super(new Object[] { "Corporeal beast", 8133 });
	}
	
	@Override
	public void populate(Player player) {
		double row = ItemDefinitions.getItemDefinitions(player.getEquipment().getRingId()).getName().toLowerCase()
				.contains("ring of wealth") ? 0.75 : 0;
		double shake = shake(526);
		if (shake <= 1 + row) {
			if (row > 0) {
				player.getPackets().sendGameMessage("<col=ff7000>Your ring of wealth shines more brightly!</col>");
			}
			player.setRareDrop(true);
			int random = 2 + Utils.getRandom(3) * 2;
			dissectNodeBatch(1, node(13744 + (random), 1));
		} else
		switch (rarityNode(player)) {
		case -1:
		case COMMON:
			dissectNodeBatch(1,
					node(995, 250, 3000),
					node(1623, 1),
					node(1621, 1),
					node(5321, 24));
			break;
		case RARE:
		case VERY_RARE:
			dissectNodeBatch(1,
					node(5295, 10),
					node(13754, 1));
			break;
		case UNCOMMON:
			dissectNodeBatch(1,
					node(1617, 1),
					node(1619, 1),
					node(1631, 1),
					node(1462, 1),
					node(890, 750),
					node(9144, 250),
					node(1401, 1),
					node(1403, 1),
					node(1405, 1),
					node(1407, 1),
					node(4091, 1),
					node(4093, 1),
					node(9245, 175),
					node(2, 2000),
					node(13734, 1),
					node(7937, 2500),
					node(563, 250),
					node(11133, 1),
					node(564, 500),
					node(560, 300),
					node(566, 250),
					node(450, 125),
					node(452, 20),
					node(2362, 35),
					node(8781, 100),
					node(6333, 150),
					node(1514, 75),
					node(1754, 100),
					node(384, 70),
					node(240, 120),
					node(9736, 120),
					node(7060, 30),
					node(5953, 40));
			break;
		}
		shakeSummoningCharm(13, 22, 12, 22, 41.5);
		shakeTreasureTrail(player, ELITE_CLUE);
		shakeTreasureTrail(player, HARD_CLUE);

	}

}
