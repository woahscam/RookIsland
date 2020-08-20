package droptable;

import com.rs.game.npc.MobRewardNodeBuilder;
import com.rs.game.player.Player;

public final class BabyBlueDragon extends MobRewardNodeBuilder {

	public BabyBlueDragon() {
		super(new Object[] { "Baby blue dragon", -1 });
	}

	@Override
	public void populate(Player player) {
		addObj(BABYDRAGON_BONES, 1);// baby dragon bones
	}
}
