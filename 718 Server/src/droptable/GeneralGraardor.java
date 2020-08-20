package droptable;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.npc.MobRewardNodeBuilder;
import com.rs.game.player.Player;

public final class GeneralGraardor extends MobRewardNodeBuilder {

	public GeneralGraardor() {
		super(new Object[] { "General graardor", 6260 });
	}

	@Override
	public void populate(Player player) {
		double row = ItemDefinitions.getItemDefinitions(player.getEquipment().getRingId()).getName().toLowerCase()
				.contains("ring of wealth") ? 0.75 : 0;
		double shake = shake(128);
		if (shake <= 1 + row) {
			if (row > 0) {
				player.getPackets().sendGameMessage("<col=ff7000>Your ring of wealth shines more brightly!</col>");
			}
			player.setRareDrop(true);
			dissectNodeBatch(1, 
					node(11724, 1),
					node(11726, 1),
					node(11728, 1),
					node(11704, 1));
		} else
		switch (rarityNode(player)) {
		case -1:
		case COMMON:
			dissectNodeBatch(1, node(1319, 1), node(1303, 1), node(1275, 1), node(1127, 1), node(450, 16, 20),
					node(454, 115, 120), node(995, 19), 
					node(5300, 1), node(995, 19501, 21000));
			break;
		case VERY_RARE:
			dissectNodeBatch(1, node(560, 40, 50), node(892, 38, 43), node(886, 135, 155));
			break;
		case RARE:
			dissectNodeBatch(1, node(563, 41, 45), node(1373, 1), node(1289, 1), node(1185, 1), node(995, 19501, 21000), node(995, 20500, 21000),
					node(11710, 1), node(11712, 1), node(11714, 1));
			break;
		case UNCOMMON:
			dissectNodeBatch(1, node(561, 60, 70), node(3052, 3), node(1514, 15, 20), node(3024, 3), node(452, 2));
			break;
		}
		addObj(4834, 1);// bones
		shakeTreasureTrail(player, HARD_CLUE);
		shakeTreasureTrail(player, ELITE_CLUE);
		shakeSummoningCharm(1, 18, 13, 14, 9);

	}

}
