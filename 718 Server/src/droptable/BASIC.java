package droptable;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.npc.MobRewardNodeBuilder;
import com.rs.game.player.Player;

public final class BASIC extends MobRewardNodeBuilder {

	public BASIC() {
		super(new Object[] { "nameofnpc", -1 });
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
			dissectNodeBatch(1, node(000, 1));
		}
		switch (rarityNode(player)) {
		case -1:
		case COMMON:
			dissectNodeBatch(1, node(000, 1), 
					node(000, 1));
			break;
		case UNCOMMON:
			dissectNodeBatch(1, node(000, 1), 
					node(000, 1));
			break;
		case RARE:
			dissectNodeBatch(1, node(000, 1), 
					node(000, 1));
			break;
		case VERY_RARE:
			dissectNodeBatch(1, node(000, 1), 
					node(000, 1));
			break;
		}
		addObj(BONES, 1);
		shakeTreasureTrail(player, HARD_CLUE);
		shakeTreasureTrail(player, ELITE_CLUE);
		shakeSummoningCharm(1, 16, 18, 22, 9);

	}
}
