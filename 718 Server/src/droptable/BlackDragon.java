package droptable;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.npc.MobRewardNodeBuilder;
import com.rs.game.player.Player;

public final class BlackDragon extends MobRewardNodeBuilder {

	public BlackDragon() {
		super(new Object[] { "Black dragon", -1 });
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
			dissectNodeBatch(1, node("draconic visage", 1));
		} else
		switch (rarityNode(player)) {
		case -1:
		case COMMON:
			dissectNodeBatch(1, node("adamant dart (p)", 16), 
					node("adamant javelin", 30), 
					node("coins", 129, 3000));
			break;
		case VERY_RARE:
			dissectNodeBatch(1, node("rune battleaxe", 1), 
					node("rune 2h sword", 1), 
					node("steel bar", 1, 2));
			break;
		case RARE:
			dissectNodeBatch(1, node("rune longsword", 1), 
					node("rune sq shield", 1));
			break;
		case UNCOMMON:
			dissectNodeBatch(1, node("mithril hatchet", 1), 
					node("adamant javelin", 30), 
					node("black hatchet", 1), 
					node("mithril battleaxe", 1), 
					node("mithril kiteshield", 1), 
					node("mithril 2h sword", 1), 
					node("adamant platebody", 1), 
					node("rune knife", 2), 
					node("rune dart", 10), 
					node("air rune", 75), 
					node("blood rune", 15), 
					node("death rune", 5, 50), 
					node("fire rune", 50), 
					node("law rune", 10, 12), 
					node("law rune", 45), 
					node("nature rune", 67, 79), 
					node("chaos rune", 90), 
					node("adamant bar", 1), 
					node("runite limbs", 1), 
					node("chocolate cake", 1));
			break;
		}
		addObj("dragon bones", 1);// bones
		addObj("black dragonhide", 1);// hide
		shakeTreasureTrail(player, HARD_CLUE);
		shakeSummoningCharm(3, 8.5, 26.5, 7, 1);

	}
}
