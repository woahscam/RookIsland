package droptable;

import com.rs.game.npc.MobRewardNodeBuilder;
import com.rs.game.player.Player;

public final class FlightKilisa extends MobRewardNodeBuilder {

	public FlightKilisa() {
		super(new Object[] { "Flight Kilisa", 6227 });
	}

	@Override
	public void populate(Player player) {

		int shake = shake(1620);
		if (shake == 1) {
			dissectNodeBatch(1, 
					node(11718, 1),
					node(11720, 1),
					node(11722, 1));
			return;
		}

		switch (rarityNode(player)) {
		case -1:
		case COMMON:
			dissectNodeBatch(1, node(995, 1006, 1500));
			break;
		case VERY_RARE:
		case RARE:
			dissectNodeBatch(1, node(11710, 1), 
					node(11712, 1), 
					node(11714, 1));
			break;
		case UNCOMMON:
			dissectNodeBatch(1, node(391, 2), 
					node(7058, 3), 
					node(6693, 2));
			break;
		}
		addObj(526, 1);// bones
		shakeTreasureTrail(player, HARD_CLUE);
	}
}
