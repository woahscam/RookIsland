package droptable;

import com.rs.game.npc.MobRewardNodeBuilder;
import com.rs.game.player.Player;

public final class PoisonScorpion extends MobRewardNodeBuilder {

	public PoisonScorpion() {
		super(new Object[] { "Poison Scorpion", 108 });
	}

	@Override
	public void populate(Player player) {
	}
}
