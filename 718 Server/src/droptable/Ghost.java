package droptable;

import com.rs.game.npc.MobRewardNodeBuilder;
import com.rs.game.player.Player;

public final class Ghost extends MobRewardNodeBuilder {

	public Ghost() {
		super(new Object[] { "Ghost", 491 });
	}

	@Override
	public void populate(Player player) {
		addObj(BONES, 1);
	}
}
