package droptable;

import com.rs.game.npc.MobRewardNodeBuilder;
import com.rs.game.player.Player;

public final class GiantRat extends MobRewardNodeBuilder {

	public GiantRat() {
		super(new Object[] { "Giant rat", 12348 });
	}

	@Override
	public void populate(Player player) {
		addObj(526, 1);// bones
		addObj(2134, 1);// raw rat meat
	}
}
