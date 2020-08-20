package droptable;

import com.rs.game.npc.MobRewardNodeBuilder;
import com.rs.game.player.Player;

public final class MagicAxe extends MobRewardNodeBuilder {

	public MagicAxe() {
		super(new Object[] { "Magic axe", 127 });
	}

	@Override
	public void populate(Player player) {
		addObj(IRON_BATTLEAXE, 1);
	}
}
