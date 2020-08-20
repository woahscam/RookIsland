package com.rs.game.player.teleportation;

import com.rs.game.WorldTile;

public class TeleportsData {

	public enum TeleportStore {
		
		ALKHARID(new WorldTile(3293, 3184, 0)),

		ARDOUGNE(new WorldTile(2663, 3305, 0)),

		BURTHORPE(new WorldTile(2895, 3546, 0)),

		CAMELOT(new WorldTile(2725, 3485, 0)),

		CATHERBY(new WorldTile(2809, 3435, 0)),

		CANIFIS(new WorldTile(3496, 3488, 0)),

		DRAYNOR(new WorldTile(3103, 3249, 0)),

		EDGEVILLE(new WorldTile(3086, 3495, 0)),

		FALADOR(new WorldTile(2964, 3379, 0)),

		KARAMJA(new WorldTile(2945, 3146, 0)),

		LUMBRIDGE(new WorldTile(3222, 3218, 0)),

		LUNAR(new WorldTile(2100, 3914, 0)),

		NEITIZNOT(new WorldTile(2310, 3781, 0)),

		PISCATORIS(new WorldTile(2335, 3689, 0)),

		RIMMINGTON(new WorldTile(2955, 3224, 0)),

		SHILOVILLAGE(new WorldTile(2852, 2960, 0)),

		TREEGNOME(new WorldTile(2462, 3435, 0)),

		VARROCK(new WorldTile(3212, 3424, 0)),

		YANILLE(new WorldTile(2605, 3093, 0)),
		
		
		// MINING LOCATIONS
		ALKHARID_MINING(new WorldTile(3424, 3537, 0)),

		// SMITHING LOCATIONS
		ALKHARID_FURNACE(new WorldTile(3424, 3537, 0)),

		// FISHING LOCATIONS
		DRAYNOR_FISHING(new WorldTile(3424, 3537, 0)),

		// COOKING LOCATIONS
		CATHERBY_RANGE(new WorldTile(3424, 3537, 0)),

		// WOODCUTTING LOCATIONS
		CAMELOT_WOODCUTTING(new WorldTile(3424, 3537, 0)),

		// FARMING LOCATIONS
		CATHERBY_PATCHES(new WorldTile(3424, 3537, 0)),

		// AGILITY LOCATIONS
		GNOME_COURSE(new WorldTile(3424, 3537, 0)),

		// THIEVING LOCATIONS
		ARDOUGNE_STALLS(new WorldTile(3424, 3537, 0)),

		// RUNECRAFTING LOCATIONS
		AIR_ALTAR(new WorldTile(3128, 3408, 0)),

		MIND_ALTAR(new WorldTile(2980, 3512, 0)),

		WATER_ALTAR(new WorldTile(3185, 3163, 0)),

		EARTH_ALTAR(new WorldTile(3304, 3474, 0)),

		FIRE_ALTAR(new WorldTile(3312, 3253, 0)),

		BODY_ALTAR(new WorldTile(3055, 3444, 0)),

		COSMIC_ALTAR(new WorldTile(2408, 4379, 0)),

		CHAOS_ALTAR(new WorldTile(3060, 3588, 0)),

		ASTRAL_ALTAR(new WorldTile(2151, 3864, 0)),

		NATURE_ALTAR(new WorldTile(2869, 3021, 0)),

		LAW_ALTAR(new WorldTile(2857, 3379, 0)),

		DEATH_ALTAR(new WorldTile(1863, 4639, 0)),

		BLOOD_ALTAR(new WorldTile(3561, 9779, 0)),

		// MONSTER TELEPORTS
		ROCK_CRABS(new WorldTile(2673, 3709, 0)),

		YAKS(new WorldTile(2323, 3804, 0)),

		OGRES(new WorldTile(2492, 3097, 0)),

		// DUNGEON/SLAYER TELEPORTS
		SLAYER_TOWER(new WorldTile(3424, 3537, 0)),

		TAVERLY(new WorldTile(2884, 9799, 0)),

		BRIMHAVEN(new WorldTile(2708, 9564, 0)),

		WATERBIRTH(new WorldTile(2443, 10147, 0)),

		SECURITYDUNGEON(new WorldTile(3080, 3422, 0)),

		STRONGHOLD(new WorldTile(3074, 3458, 0)),

		ANCIENTCAVERN(new WorldTile(2512, 3515, 0)),

		FREMENNIK_SLAYER(new WorldTile(2806, 10002, 0)),

		ASGARNIA_ICE(new WorldTile(3003, 9548, 0)),

		KALPHITE(new WorldTile(3485, 9510, 0)),

		TZHAAR(new WorldTile(4672, 5156, 0)),

		JADINKO(new WorldTile(3012, 9273, 0)),

		LIVINGROCK(new WorldTile(3013, 9832, 0)),

		// BOSS TELEPORTS
		GODWARS(new WorldTile(2915, 3746, 0)),
		
		KALPHITE_QUEEN(new WorldTile(3506, 9493, 0)),
		
		CORPOREAL_BEAST(new WorldTile(2966, 4383, 2)),
		
		QUEEN_BLACK_DRAGON(new WorldTile(1197, 6499, 0)),

		// MINIGAME TELEPORTS
		FIGHTCAVES(new WorldTile(4612, 5129, 0)),

		FIGHTKILN(new WorldTile(4744, 5168, 0)),

		PESTCONTROL(new WorldTile(2662, 2649, 0)),

		WARRIORGUILD(new WorldTile(2879, 3542, 0)),

		DUELARENA(new WorldTile(3369, 3266, 0)),

		CLANWARS(new WorldTile(2993, 9679, 0)),

		DOMINION(new WorldTile(3744, 6425, 0)),

		// WILDERNESS TELEPORTS
		WEST_DRAGONS(new WorldTile(2982, 3597, 0)),

		EAST_DRAGONS(new WorldTile(3346, 3650, 0)),

		CHAOS_WILDY_ALTAR(new WorldTile(3238, 3622, 0)),

		PORTS1(new WorldTile(3155, 3615, 0)),

		PORTS2(new WorldTile(3219, 3649, 0)),

		PORTS3(new WorldTile(3035, 3727, 0)),

		PORTS4(new WorldTile(3106, 3788, 0)),

		PORTS5(new WorldTile(2974, 3871, 0)),

		PORTS6(new WorldTile(3307, 3916, 0)),

		MAGEBANK(new WorldTile(2539, 4716, 0)),

		WILDYAGILITY(new WorldTile(2998, 3908, 0)),
		
		EDGEVILLE_PVP_INSTANCE(new WorldTile(85, 79, 0)),
		
		;
		

		private WorldTile tile;

		private TeleportStore(WorldTile tile) {
			this.setTile(tile);
		}

		public WorldTile getTile() {
			return tile;
		}

		public void setTile(WorldTile tile) {
			this.tile = tile;
		}

		public static TeleportStore getTele(String name) {
			for (TeleportStore tele : TeleportStore.values()) {
				if (tele.name().toLowerCase().replace("_", " ").contains(name.toLowerCase().replace("_", " ")))
					return tele;
			}
			return null;
		}
	}

}
