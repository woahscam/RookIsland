package com.rs;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import com.alex.store.Index;
import com.rs.cache.Cache;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.ItemsEquipIds;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cores.CoresManager;
import com.rs.game.Region;
import com.rs.game.World;
import com.rs.game.area.AreaManager;
import com.rs.game.cityhandler.CityEventHandler;
import com.rs.game.item.ground.AutomaticGroundItem;
import com.rs.game.map.MapBuilder;
import com.rs.game.npc.MobRewardRDT;
import com.rs.game.npc.combat.CombatScriptsHandler;
import com.rs.game.objects.ObjectScriptsHandler;
import com.rs.game.player.Player;
import com.rs.game.player.StarterProtection;
import com.rs.game.player.actions.skills.fishing.FishingSpotsHandler;
import com.rs.game.player.content.EdgevillePvPInstance;
import com.rs.game.player.content.KillScoreBoard;
import com.rs.game.player.content.clans.ClansManager;
import com.rs.game.player.content.customshops.CustomStoreData;
import com.rs.game.player.content.friendschat.FriendChatsManager;
import com.rs.game.player.content.grandexchange.GrandExchange;
import com.rs.game.player.content.grandexchange.LimitedGEReader;
import com.rs.game.player.content.grandexchange.UnlimitedGEReader;
import com.rs.game.player.controlers.ControlerHandler;
import com.rs.game.player.cutscenes.CutscenesHandler;
import com.rs.game.player.dialogues.DialogueHandler;
import com.rs.game.worldlist.WorldList;
import com.rs.net.ServerChannelHandler;
import com.rs.utils.Credentials;
import com.rs.utils.DisplayNames;
import com.rs.utils.ForumIntegration;
import com.rs.utils.IPBanL;
import com.rs.utils.ItemBonuses;
import com.rs.utils.ItemExamines;
import com.rs.utils.Logger;
import com.rs.utils.MapArchiveKeys;
import com.rs.utils.MapAreas;
import com.rs.utils.MusicHints;
import com.rs.utils.NPCBonuses;
import com.rs.utils.NPCCombatDefinitionsL;
import com.rs.utils.NPCDrops;
import com.rs.utils.NPCExamines;
import com.rs.utils.NPCSpawns;
import com.rs.utils.ObjectSpawns;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.ShopsHandler;
import com.rs.utils.Utils;
import com.rs.utils.WeaponTypesLoader;
import com.rs.utils.Weights;
import com.rs.utils.huffman.Huffman;

public final class Launcher {

	public static void main(String[] args) throws Exception {
		/*if (args.length < 4) {
			System.err.print(
					"Args, boolean boolean boolean port");
			return;
		}*/
		//Settings.OWNER_IP = String.valueOf(args[4]);
		Settings.VPS_HOSTED = Boolean.parseBoolean(args[3]);
		Settings.PORT_ID = Integer.valueOf(args[2]);
		Settings.HOSTED = Boolean.parseBoolean(args[1]);
		Settings.DEBUG = Boolean.parseBoolean(args[0]);
		long Time = Utils.currentTimeMillis();
		//Logger.log("Launcher", "Initiating Cache...");
		Cache.init();
		AreaManager.init();
		ItemsEquipIds.init();
		Huffman.init();
		DisplayNames.init();
		MapArchiveKeys.init();
		MapAreas.init();
		IPBanL.init();
		ObjectSpawns.init();
		NPCSpawns.init();
		NPCCombatDefinitionsL.init();
		NPCBonuses.init();
		NPCDrops.init();
		ItemExamines.init();
		ItemBonuses.init();
		MusicHints.init();
		//Logger.log("INFO", "Loading content...");
		ShopsHandler.init();
		FishingSpotsHandler.init();
		CombatScriptsHandler.init();
		ObjectScriptsHandler.init();
		DialogueHandler.init();
		ControlerHandler.init();
		CutscenesHandler.init();
		FriendChatsManager.init();
		ClansManager.init();
		CoresManager.init();
		World.init();
		MapBuilder.init();
		Weights.init();
		GrandExchange.init();
		LimitedGEReader.init();
		UnlimitedGEReader.init();
		StarterProtection.loadIPS();
		NPCExamines.loadPackedExamines();
		Credentials.init();
		WeaponTypesLoader.loadDefinitions();
		AutomaticGroundItem.initialize();
		MobRewardRDT.getInstance().structureNode();
		WorldList.init();
		KillScoreBoard.init();
		EdgevillePvPInstance.buildInstance();
		CityEventHandler.registerCitys();
		CustomStoreData.init();
		if (Settings.FORUM_INTEGRATION) {
			//Logger.log("Launcher", "Initiating Web Integration...");
			ForumIntegration.init();
		}
		try {
			ServerChannelHandler.init();
		} catch (Throwable e) {
			Logger.handle(e);
			Logger.log("Launcher", "Failed initing Server Channel Handler. Shutting down...");
			System.exit(1);
			return;
		}
		Logger.log(Settings.SERVER_NAME, "Took " + (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - Time))
				+ (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - Time) > 1 ? " seconds" : " second")
				+ " to launch.");
		Logger.log("Additional information", Settings.SERVER_NAME + " is running on a " + System.getProperty("os.name")
				+ " platform." + " PORT: " + Settings.PORT_ID + " | " + "JDK: " + System.getProperty("java.version"));
		Logger.log("Online", "Host: " + Utils.GrabCountryDayTimeMonth(false));
		Logger.log("Ecomode", Settings.ECONOMY_MODE == 2 ? "Full Spawn" : Settings.ECONOMY_MODE == 1 ? "Half Economy" : "Economy");
		Logger.log("Debug", Settings.DEBUG ? "Debug is activated." : "Debug is inactived.");
		addAccountsSavingTask();
		addCleanMemoryTask();
	}

	private static void addCleanMemoryTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					cleanMemory(Runtime.getRuntime().freeMemory() < Settings.MIN_FREE_MEM_ALLOWED);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 10, TimeUnit.SECONDS);
	}

	private static void addAccountsSavingTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					saveFiles();
				} catch (Throwable e) {
					Logger.handle(e);
				}

			}
		}, 0, 1, TimeUnit.MINUTES);
	}

	public static String Time(String dateFormat) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(cal.getTime());
	}

	public static String Time;

	public static void saveFiles() {
		int times = 0;
		for (Player player : World.getPlayers()) {
			if (player == null || !player.hasStarted() || player.hasFinished())
				continue;
			SerializableFilesManager.savePlayer(player);
		}
		times++;
		IPBanL.save();
		GrandExchange.save();
		Time = Time("dd MMMMM yyyy 'at' hh:mm:ss z");
		if (times == 0) {
			times = 0;
			System.out.println(Time + ", " + World.getPlayers().size() + " players online.");
		}
	}

	public static void cleanMemory(boolean force) {
		if (force) {
			ItemDefinitions.clearItemsDefinitions();
			NPCDefinitions.clearNPCDefinitions();
			ObjectDefinitions.clearObjectDefinitions();
			skip: for (Region region : World.getRegions().values()) {
				for (int regionId : MapBuilder.FORCE_LOAD_REGIONS)
					if (regionId == region.getRegionId())
						continue skip;
				region.unloadMap();
			}
		}
		for (Index index : Cache.STORE.getIndexes())
			index.resetCachedFiles();
		CoresManager.fastExecutor.purge();
		System.gc();
	}

	public static void shutdown() {
		try {
			closeServices();
		} finally {
			System.exit(0);
		}
	}

	public static void closeServices() {
		ServerChannelHandler.shutdown();
		CoresManager.shutdown();
		if (Settings.HOSTED) {
			try {
				// setWebsitePlayersOnline(0);
			} catch (Throwable e) {
				Logger.handle(e);
			}
		}
	}

	public static void restart() {
		closeServices();
		System.gc();
		try {
			Runtime.getRuntime().exec(
					"java -XX:-OmitStackTraceInFastThrow -Xms1024m -cp bin;data/libs/netty-3.5.2.Final.jar;data/libs/RuneTopListV2.jar;data/libs/FileStore.jar;data/lib/GTLVote.jar;data/lib/mysql2.jar com.rs.Launcher false false false");
			System.exit(0);
		} catch (Throwable e) {
			Logger.handle(e);
		}

	}

	private Launcher() {

	}

}
