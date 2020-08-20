package com.rs.net.decoders.handlers;

import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.ForceMovement;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.cityhandler.CityEventHandler;
import com.rs.game.item.Item;
import com.rs.game.minigames.castlewars.CastleWars;
import com.rs.game.minigames.crucible.Crucible;
import com.rs.game.minigames.fightpits.FightPits;
import com.rs.game.minigames.pest.Lander;
import com.rs.game.minigames.warriorguild.WarriorsGuild;
import com.rs.game.objects.ObjectScript;
import com.rs.game.objects.ObjectScriptsHandler;
import com.rs.game.player.CombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.RouteEvent;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Charter;
import com.rs.game.player.actions.CowMilkingAction;
import com.rs.game.player.actions.ObjectOption1;
import com.rs.game.player.actions.WaterFilling;
import com.rs.game.player.actions.WhirlPool;
import com.rs.game.player.actions.combat.OldMagicSystem;
import com.rs.game.player.actions.combat.PlayerCombat;
import com.rs.game.player.actions.construction.BoneOffering;
import com.rs.game.player.actions.runecrafting.SihponActionNodes;
import com.rs.game.player.actions.skills.agility.Agility;
import com.rs.game.player.actions.skills.agility.BarbarianOutpostAgility;
import com.rs.game.player.actions.skills.agility.GnomeAgility;
import com.rs.game.player.actions.skills.agility.WildyAgility;
import com.rs.game.player.actions.skills.construction.House;
import com.rs.game.player.actions.skills.construction.HouseConstants;
import com.rs.game.player.actions.skills.cooking.Cooking;
import com.rs.game.player.actions.skills.cooking.Cooking.Cookables;
import com.rs.game.player.actions.skills.firemaking.Bonfire;
import com.rs.game.player.actions.skills.mining.EssenceMining;
import com.rs.game.player.actions.skills.mining.EssenceMining.EssenceDefinitions;
import com.rs.game.player.actions.skills.mining.MiningBase;
import com.rs.game.player.actions.skills.newmining.Mining;
import com.rs.game.player.actions.skills.smithing.JewllerySmithing;
import com.rs.game.player.actions.skills.smithing.Smelting;
import com.rs.game.player.actions.skills.smithing.Smelting.SmeltingBar;
import com.rs.game.player.actions.skills.smithing.Smithing.ForgingBar;
import com.rs.game.player.actions.skills.smithing.Smithing.ForgingInterface;
import com.rs.game.player.actions.skills.summoning.Summoning;
import com.rs.game.player.actions.skills.woodcutting.Woodcutting;
import com.rs.game.player.actions.skills.woodcutting.Woodcutting.TreeDefinitions;
import com.rs.game.player.content.CrystalChest;
import com.rs.game.player.content.DwarfMultiCannon;
import com.rs.game.player.content.FadingScreen;
import com.rs.game.player.content.GrotwormLair;
import com.rs.game.player.content.ItemConstants;
import com.rs.game.player.content.KillScoreBoard;
import com.rs.game.player.content.SquealOfFortuneRewards;
import com.rs.game.player.content.WildernessKills;
import com.rs.game.player.content.WildernessObelisk;
import com.rs.game.player.actions.skills.prayer.Burying.Bone;
import com.rs.game.player.actions.skills.runecrafting.CombinationRunes;
import com.rs.game.player.actions.skills.runecrafting.CombinationRunes.CombinationRunesStore;
import com.rs.game.player.actions.skills.runecrafting.Tiaras;
import com.rs.game.player.actions.skills.runecrafting.Tiaras.RunecraftingTiaraStore;
import com.rs.game.player.controlers.Falconry;
import com.rs.game.player.controlers.FightCaves;
import com.rs.game.player.controlers.GodCapes;
import com.rs.game.player.controlers.RecipeForDisaster;
import com.rs.game.player.controlers.EdgevillePvPControler;
import com.rs.game.player.controlers.WildernessControler;
import com.rs.game.player.dialogues.npcs.MiningGuildDwarf;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.io.InputStream;
import com.rs.utils.Logger;
import com.rs.utils.Utils;

/**
 * 
 * @Improved Andreas
 * 
 */

public final class ObjectHandler {

	private ObjectHandler() {

	}

	private static int STAIRSUP = 834;
	private static int STAIRSDOWN = 833;

	public static void handleOption(final Player player, InputStream stream, int option) {
		if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead())
			return;
		if (player.isLocked() || player.getEmotesManager().getNextEmoteEnd() >= Utils.currentTimeMillis())
			return;

		boolean forceRun = stream.readUnsignedByte128() == 1;
		final int id = stream.readIntLE();
		int x = stream.readUnsignedShortLE();
		int y = stream.readUnsignedShortLE128();
		final WorldTile tile = new WorldTile(x, y, player.getPlane());
		final int regionId = tile.getRegionId();
		if (!player.getMapRegionsIds().contains(regionId))
			return;
		WorldObject mapObject = World.getObjectWithId(tile, id);
		if (mapObject == null || mapObject.getId() != id)
			return;
		final WorldObject object = mapObject;
		player.stopAll();
		if (forceRun)
			player.setRun(forceRun);
		switch (option) {
		case 1:
			handleOption1(player, object, stream);
			break;
		case 2:
			handleOption2(player, object);
			break;
		case 3:
			handleOption3(player, object);
			break;
		case 4:
			handleOption4(player, object);
			break;
		case 5:
			handleOption5(player, object);
			break;
		case -1:
			handleOptionExamine(player, object);
			break;
		}
	}

	public static void renewSummoningPoints(Player player) {
		int summonLevel = player.getSkills().getLevelForXp(Skills.SUMMONING);
		if (player.getSkills().getLevel(Skills.SUMMONING) < summonLevel) {
			player.lock(3);
			player.animate(new Animation(8502));
			player.gfx(new Graphics(1308));
			player.getSkills().set(Skills.SUMMONING, summonLevel);
			player.getPackets().sendGameMessage("You have recharged your Summoning points.", true);
		} else
			player.getPackets().sendGameMessage("You already have full Summoning points.");
	}

	public static void healFamiliar(Player owner) {
		if (owner.getFamiliar() != null && owner.getFamiliar().getHitpoints() < owner.getFamiliar().getMaxHitpoints()) {
			owner.getFamiliar().heal(owner.getFamiliar().getMaxHitpoints());
			owner.getPackets().sendGameMessage("Your follower was hurt, and is now healed.");
		} else {

		}
	}


	/**
	 * gnome agility
	 * 
	 * if (id == 43529 || id == 69514 || (id >= 4550 && id <= 4559)) {
	 * player.setRouteEvent(new RouteEvent(object, new Runnable() {
	 * 
	 * @Override public void run() { // unreachable agility objects exception
	 *           player.faceObject(object); if (id == 43529) {
	 *           GnomeAgility.PreSwing(player, object); } else if (id == 69514) {
	 *           GnomeAgility.RunGnomeBoard(player, object); } else if (id >= 4550
	 *           && id <= 4559) { if (!Agility.hasLevel(player, 35)) return; if
	 *           (object.withinDistance(player, 2)) { if (!Agility.hasLevel(player,
	 *           35)) return; player.setNextForceMovement(new ForceMovement(player,
	 *           1, object, 2, Utils .getFaceDirection(object.getX() -
	 *           player.getX(), object.getY() - player.getY())));
	 *           player.useStairs(-1, object, 1, 2); player.setNextAnimation(new
	 *           Animation(769)); player.getSkills().addXp(Skills.AGILITY, 2); } } }
	 *           }, true)); return; }
	 */

	private static void handleOption1(final Player player, final WorldObject object, InputStream stream) {
		final ObjectDefinitions objectDef = object.getDefinitions();
		final int id = object.getId();
		final int x = object.getX();
		final int y = object.getY();
		ObjectScript script = ObjectScriptsHandler.cachedObjectScripts.getOrDefault(object.getId(),
				ObjectScriptsHandler.cachedObjectScripts.get(objectDef.name));
		if (script != null) {
			if (script.getDistance() == 0) {
				player.setRouteEvent(new RouteEvent(object, new Runnable() {
					@Override
					public void run() {
						player.stopAll();
						player.faceObject(object);
						if (script.processObject(player, object))
							return;
					}
				}, true));
				return;
			} else {
				// TODO route to script.getDistane()
			}
		}
		if (SihponActionNodes.siphon(player, object))
			return;
		if (ObjectOption1.validObject(player, object))
			return;

		if (id >= 30555 && id <= 30557) {
			player.setRouteEvent(new RouteEvent(object, new Runnable() {
				@Override
				public void run() {
					if (!player.withinDistance(object, 2))
						return;
					boolean west = player.getX() <= 3545;
					player.animate(new Animation(west ? 753 : 752));
					player.lock(3);
					WorldTasksManager.schedule(new WorldTask() {

						int x;

						@Override
						public void run() {
							player.getAppearence().setRenderEmote(west ? 157 : 156);
							if (x == 1)
								player.addWalkSteps(west ? player.getX() + 2 : player.getX() - 2, player.getY(), 2,
										false);
							if (x == 2)
								player.addWalkSteps(west ? player.getX() + 2 : player.getX() - 2, player.getY(), 2,
										false);
							if (x == 3) {
								player.getAppearence().setRenderEmote(-1);
								player.setNextAnimationNoPriority(new Animation(west ? 759 : 758), player);
								stop();
								return;
							}
							x++;
						}
					}, 0, 1);
				}
			}, true));
			return;
		}
		if (object.getId() == 69514) {
			player.setRouteEvent(new RouteEvent(object, new Runnable() {
				@Override
				public void run() {
					if (!player.withinDistance(object, 3))
						return;
					GnomeAgility.RunGnomeBoard(player, object);
					return;
				}
			}, true));
			return;
		}
		if (object.getId() == 43529) {
			player.setRouteEvent(new RouteEvent(object, new Runnable() {
				@Override
				public void run() {
					if (!player.withinDistance(object, 4))
						return;
					GnomeAgility.PreSwing(player, object);
					return;
				}
			}, true));
			return;
		}
		player.setRouteEvent(new RouteEvent(object, new Runnable() {
			@Override
			public void run() {
				player.stopAll();
				player.faceObject(object);
				if (!player.getControlerManager().processObjectClick1(object))
					return;
				if (player.getDungManager().enterResourceDungeon(object))
					return;
				if (GrotwormLair.handleObject1(object, player))
					return;
				if (CastleWars.handleObjects(player, id))
					return;
				if (CityEventHandler.handleObjectClick(player, object, object.getId()))
					return;
				if (player.getFarmingManager().isFarming(id, null, 1))
					return;
				if (player.getTreasureTrailsManager().useObject(object))
					return;
				if (id >= 15477 && id <= 15482 && House.enterHousePortal(player))
					return;
				if (id == 23921) {
					if ((player.getCombatDefinitions().getSpellId() > 0
							&& player.getCombatDefinitions().getAutoCastSpell() > 0)) {
						player.getPackets().sendGameMessage("You can't use magic on a dummy.");
						return;
					} else if (PlayerCombat.isRanging(player) > 0) {
						player.getPackets().sendGameMessage("You can't use ranged on a dummy.");
						return;
					}
					int weaponId = player.getEquipment().getWeaponId();
					final ItemDefinitions defs = ItemDefinitions.getItemDefinitions(weaponId);
					if (defs == null || weaponId == -1)
						player.lock(weaponId == -1 ? 3 : 4);
					player.lock(defs.getAttackSpeed() - 1);
					player.animate(new Animation(PlayerCombat.getWeaponAttackEmote(player.getEquipment().getWeaponId(),
							player.getCombatDefinitions().getAttackStyle())));
					int xpStyle = player.getCombatDefinitions().getXpStyle(player.getEquipment().getWeaponId(),
							player.getCombatDefinitions().getAttackStyle());
					if (xpStyle != CombatDefinitions.SHARED) {
						player.getSkills().addXp(xpStyle, 3);
					} else {
						player.getSkills().addXp(Skills.ATTACK, 1);
						player.getSkills().addXp(Skills.STRENGTH, 1);
						player.getSkills().addXp(Skills.DEFENCE, 1);
					}
					player.getSkills().addXp(Skills.HITPOINTS, 1);
					return;
				}
				if (id == 2399) {
					handleDoorTemporary(player, object, 1200);
					return;
				}
				if (id == 54019 || id == 54020) {
					KillScoreBoard.showRanks(player);
					return;
				}
				if (id == 2339) {
					handleDoorTemporary(player, object, 1200);
					return;
				}
				if (id == 30560 && object.getX() == 3510 && object.getY() == 9811) {
					player.lock(5);
					final long time = FadingScreen.fade(player);
					CoresManager.slowExecutor.execute(new Runnable() {
						@Override
						public void run() {
							player.animate(new Animation(844));
							try {
								FadingScreen.unfade(player, time, new Runnable() {
									@Override
									public void run() {
										player.setNextWorldTile(new WorldTile(3492, 9809, 0));
									}
								});
							} catch (Throwable e) {
								Logger.handle(e);
							}
						}
					});
					return;
				}
				if (id == 5046 && object.getX() == 3492 && object.getY() == 9808) {
					player.lock(5);
					final long time = FadingScreen.fade(player);
					CoresManager.slowExecutor.execute(new Runnable() {
						@Override
						public void run() {
							player.animate(new Animation(844));
							try {
								FadingScreen.unfade(player, time, new Runnable() {
									@Override
									public void run() {
										player.setNextWorldTile(new WorldTile(3511, 9811, 0));
									}
								});
							} catch (Throwable e) {
								Logger.handle(e);
							}
						}
					});
					return;
				}
				if (id == 24360) {
					player.movePlayer(new WorldTile(3190, 9833, 0), 1, 2);
					return;
				}
				if (id == 24365) {
					player.movePlayer(new WorldTile(3188, 3432, 0), 1, 2);
					return;
				}
				if (id == 1804) {
					if (player.getY() == 3449 && !player.getInventory().containsItem(983, 1)) {
						player.getPackets().sendGameMessage("You need some sort of key to unlock this door.");
						return;
					}
					handleDoorTemporary(player, object, 1200);
					return;
				}
				if (id == 7272) {
					player.movePlayer(new WorldTile(3253, 3401, 0), 1, 2);
					return;
				}
				if (id == 11620 || id == 11621) {
					handleGateTemporary(player, object, 1200);
					return;
				}
				if (id == 11724) {
					if (object.getX() == 2968 && object.getY() == 3347)
						player.movePlayer(new WorldTile(player.getX() - 3, player.getY() + 1, player.getPlane() + 1), 1,
								2);
					else
						player.movePlayer(new WorldTile(player.getX() + 1, player.getY() + 1, player.getPlane() + 1), 1,
								2);
					return;
				}
				if (id == 11725) {
					if (object.getX() == 2968 && object.getY() == 3347)
						player.movePlayer(new WorldTile(player.getX() + 3, player.getY() - 1, player.getPlane() - 1), 1,
								2);
					else
						player.movePlayer(new WorldTile(player.getX() + 1, player.getY() + 1, player.getPlane() + 1), 1,
								2);
					return;
				}
				if (id == 2718 || id == 24072) {
					if (player.hopper) {
						player.getPackets().sendGameMessage("You operate the hopper. The grain slides down the chute.");
						player.lever = true;
						WorldObject mill = null;
						if (id == 24072)
							mill = new WorldObject(24070, 10, 1, 3140, 3449, 0);
						else
							mill = new WorldObject(36878, 10, 3, 3166, 3306, 0);
						World.spawnObject(player, mill);
						return;
					}
					player.getPackets().sendGameMessage("You pull the lever.. but nothing happens.");
					return;
				}
				if (id == 29355 && object.getX() == 3209 && object.getY() == 9616) {
					player.useStairs(828, new WorldTile(3210, 3216, 0), 1, 2);
					return;
				}
				if (id == 25213) {
					player.useStairs(828, new WorldTile(2834, 3259, 0), 1, 2);
					return;
				}
				if (id == 24074) {
					// player.movePlayer(new WorldTile(3144, 3446, 2), 1, 2);
				}
				if (id == 25154) {
					player.movePlayer(new WorldTile(2833, 9656, 0), 1, 2);
					return;
				}
				if (id == 36878 || id == 24070) {
					if (!player.getInventory().containsItem(1931, 1)) {
						player.getPackets().sendGameMessage("You need an empty pot to fill.");
						return;
					}
					WorldObject mill = null;
					if (id == 24070)
						mill = new WorldObject(954, 10, 1, 3140, 3449, 0);
					else
						mill = new WorldObject(36880, 10, 3, 3166, 3306, 0);
					World.spawnObject(player, mill);
					player.getInventory().deleteItem(1931, 1);
					player.getInventory().addItem(1933, 1);
					player.hopper = false;
					player.lever = false;
					return;
				}
				if (id == 16149) {
					player.useStairs(827, new WorldTile(2042, 5245, 0), 1, 2);
					return;
				}
				if (id == 16080) {
					player.useStairs(828, new WorldTile(1902, 5223, 0), 1, 2);
					return;
				}
				if (id == 16148) {
					player.useStairs(828, new WorldTile(3081, 3421, 0), 1, 2);
					return;
				}
				if (id == 16154) {
					player.useStairs(827, new WorldTile(1860, 5244, 0), 1, 2);
					return;
				}
				if (id == 16078) {
					player.useStairs(828, new WorldTile(1902, 5223, 0), 1, 2);
					return;
				}
				if (id == 16081) {
					player.useStairs(827, new WorldTile(2122, 5251, 0), 1, 2);
					return;
				}
				if (id == 16114) {
					player.useStairs(828, new WorldTile(2026, 5217, 0), 1, 2);
					return;
				}
				if (id == 16115) {
					player.useStairs(827, new WorldTile(2358, 5215, 0), 1, 2);
					return;
				}
				if (id == 16049) {
					player.useStairs(828, new WorldTile(2147, 5284, 0), 1, 2);
					return;
				}
				if (id == 16112) {
					player.useStairs(828, new WorldTile(2026, 5217, 0), 1, 2);
					return;
				}
				if (id == 16048) {
					player.useStairs(828, new WorldTile(2147, 5284, 0), 1, 2);
					return;
				}
				if (id == 16150) {
					if (!player.strongHoldSecurityFloor1) {
						player.getPackets().sendGameMessage("You must complete this floor before using this portal.");
						return;
					}
					player.useStairs(-1, new WorldTile(2042, 5245, 0), 1, 2);
					return;
				}
				if (id == 16082) {
					if (!player.strongHoldSecurityFloor2) {
						player.getPackets().sendGameMessage("You must complete this floor before using this portal.");
						return;
					}
					player.useStairs(-1, new WorldTile(2122, 5251, 0), 1, 2);
					return;
				}
				if (id == 16116) {
					if (!player.strongHoldSecurityFloor3) {
						player.getPackets().sendGameMessage("You must complete this floor before using this portal.");
						return;
					}
					player.useStairs(-1, new WorldTile(2358, 5215, 0), 1, 2);
					return;
				}
				if (id == 16050) {
					if (!player.strongHoldSecurityFloor4) {
						player.getPackets().sendGameMessage("You must complete this floor before using this portal.");
						return;
					}
					player.useStairs(-1, new WorldTile(2350, 5214, 0), 1, 2);
					return;
				}
				if (id >= 2073 && id <= 2078) {// banana tree
					if (id == 2078) {
						player.getPackets().sendGameMessage("You search the tree, but there is no more bananas.");
						return;
					}
					if (!player.getInventory().hasFreeSlots()) {
						player.getPackets().sendGameMessage("Not enough inventory space.");
						return;
					}
					WorldObject fullTree = new WorldObject(2073, object.getType(), object.getRotation(), object.getX(),
							object.getY(), object.getPlane());
					World.spawnObjectTemporary(new WorldObject(id + 1, fullTree.getType(), fullTree.getRotation(),
							fullTree.getX(), fullTree.getY(), fullTree.getPlane()), 60000);
					player.getInventory().addItem(1963, 1);
					player.getPackets().sendGameMessage("You pick a banana from the tree.");
					return;
				}
				if (id >= 23625 && id <= 23627) {// cadava bush
					if (!player.getInventory().hasFreeSlots()) {
						player.getPackets().sendGameMessage("Not enough inventory space.");
						return;
					}
					if (id == 23627) {
						player.getPackets().sendGameMessage("You search the bush, but there is no more berries.");
						return;
					}
					WorldObject fullBush = new WorldObject(23625, object.getType(), object.getRotation(), object.getX(),
							object.getY(), object.getPlane());
					World.spawnObjectTemporary(new WorldObject(id == 23625 ? 23626 : 23627, fullBush.getType(),
							fullBush.getRotation(), fullBush.getX(), fullBush.getY(), fullBush.getPlane()), 5000);
					player.getInventory().addItem(753, 1);
					player.getPackets().sendGameMessage("You pick some cadava berries from the bush.");

					return;
				}
				if (id >= 23628 && id <= 23630) {// red berry bush
					if (!player.getInventory().hasFreeSlots()) {
						player.getPackets().sendGameMessage("Not enough inventory space.");
						return;
					}
					if (id == 23630) {
						player.getPackets().sendGameMessage("You search the bush, but there is no more berries.");
						return;
					}
					WorldObject fullBush = new WorldObject(23628, object.getType(), object.getRotation(), object.getX(),
							object.getY(), object.getPlane());
					World.spawnObjectTemporary(new WorldObject(id == 23628 ? 23629 : 23630, fullBush.getType(),
							fullBush.getRotation(), fullBush.getX(), fullBush.getY(), fullBush.getPlane()), 5000);
					player.getInventory().addItem(1951, 1);
					player.getPackets().sendGameMessage("You pick some redberries from the bush.");
					return;
				}
				if (id == 16043 || id == 16044 || id == 16065 || id == 16066 || id == 16089 || id == 16090
						|| id == 16124 || id == 16123) {
					player.animate(new Animation(547));
					switch (object.getRotation()) {
					case 0:
						if (player.getX() < object.getX())
							player.movePlayer(new WorldTile(player.getX() + 2, player.getY(), player.getPlane()), 1, 2);
						if (player.getX() == object.getX())
							player.movePlayer(new WorldTile(player.getX() - 1, player.getY(), player.getPlane()), 1, 2);
						break;
					case 1:
						if (player.getY() > object.getY())
							player.movePlayer(new WorldTile(player.getX(), player.getY() - 2, player.getPlane()), 1, 2);
						if (player.getY() == object.getY())
							player.movePlayer(new WorldTile(player.getX(), player.getY() + 1, player.getPlane()), 1, 2);
						break;
					case 2:
						if (player.getX() > object.getX())
							player.movePlayer(new WorldTile(player.getX() - 2, player.getY(), player.getPlane()), 1, 2);
						if (player.getX() == object.getX())
							player.movePlayer(new WorldTile(player.getX() + 1, player.getY(), player.getPlane()), 1, 2);
						break;
					case 3:
						if (player.getY() < object.getY())
							player.movePlayer(new WorldTile(player.getX(), player.getY() + 2, player.getPlane()), 1, 2);
						if (player.getY() == object.getY())
							player.movePlayer(new WorldTile(player.getX(), player.getY() - 1, player.getPlane()), 1, 2);
						break;
					}
					return;
				}
				if (id == 16135) {
					player.getDialogueManager().startDialogue("StrongholdSecurity", 1);
					return;
				}
				if (id == 16077) {
					player.getDialogueManager().startDialogue("StrongholdSecurity", 2);
					return;
				}
				if (id == 16118) {
					player.getDialogueManager().startDialogue("StrongholdSecurity", 3);
					return;
				}
				if (id == 16047) {
					player.getDialogueManager().startDialogue("StrongholdSecurity", 4);
					return;
				}
				if (id == 33060) {
					player.setNextWorldTile(new WorldTile(3107, 9570, 0));
					return;
				}
				if (id == 2147) {
					player.setNextWorldTile(new WorldTile(3104, 9576, 0));
					return;
				}
				if (id == 5085) {
					WildernessKills.DisplayKills(player);
					return;
				}

				if (id == 28296) {
					if (player.getInventory().containsOneItem(10501)) {
						player.getInventory().addItem(10501, 10);
						player.animate(new Animation(7529));
						player.sm("You make some snowballs and put them in your inventory");
					} else if (player.getInventory().hasFreeSlots()) {
						player.getInventory().addItem(10501, 10);
						player.animate(new Animation(7529));
						player.sm("You make some snowballs and put them in your inventory");
					} else {
						player.sm("Try to free up some inventory space before making snowballs!");
					}
					return;
				}
				if (id == 1816) {
					if (!player.KBDEntrance) {
						player.getDialogueManager().startDialogue("KBDEntrance");
						return;
					}
				}
				if (id == 3409) {
					if (player.getSpins() == 0) {
						player.getPackets().sendGameMessage("You don't have any spins.");
						return;
					}
					if (!player.getInventory().hasFreeSlots()) {
						player.getPackets().sendGameMessage("You need atleast 1 inventory space.");
						return;
					}
					player.animate(new Animation(2140));
					WorldObject spinningWheel = new WorldObject(30085, object.getType(), object.getRotation(),
							object.getX(), object.getY() - 2, object.getPlane());
					WorldObject stoppedWheel = new WorldObject(30084, object.getType(), object.getRotation(),
							object.getX(), object.getY() - 2, object.getPlane());
					if (World.removeObjectTemporary(spinningWheel, 1200, false))
						World.spawnObjectTemporary(stoppedWheel, 1200);
					WorldObject pulledLever = new WorldObject(3417, object.getType(), object.getRotation(),
							object.getX(), object.getY(), object.getPlane());
					if (World.removeObjectTemporary(object, 1200, false))
						World.spawnObjectTemporary(pulledLever, 1200);
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							if (player.dropTesting) {
								for (int i = 0; i < player.dropTestingAmount; i++)
									SquealOfFortuneRewards.sendBankRewards(player);
							} else
								SquealOfFortuneRewards.sendRewards(player);
						}
					}, 1);
					return;
				}
				if (id == 36974) {
					WorldObject regularLog = new WorldObject(36975, object.getType(), object.getRotation(),
							object.getX(), object.getY(), object.getPlane());
					World.spawnObjectTemporary(regularLog, 60000);
					player.getInventory().addItem(1351, 1);
					return;
				}
				if (id == 36974) {
					WorldObject regularLog = new WorldObject(36975, object.getType(), object.getRotation(),
							object.getX(), object.getY(), object.getPlane());
					World.spawnObjectTemporary(regularLog, 60000);
					player.getInventory().addItem(1351, 1);
					return;
				}
				if (id == 2623) {
					if (!player.getInventory().containsOneItem(1590) && player.getX() >= object.getX()) {
						player.getPackets().sendGameMessage("You need a dusty key to enter this gate.");
						return;
					}
					handleDoorTemporary2(player, object, 1200);
					return;
				}
				if (id == 5090) {
					if (player.getX() == 2687)
						player.addWalkSteps(player.getX() - 5, player.getY(), 5, false);
					return;
				}
				if (id == 5088) {
					if (player.getX() == 2682)
						player.addWalkSteps(player.getX() + 5, player.getY(), 5, false);
					return;
				}
				if (id == 9294) {
					if (player.getX() == 2880)
						player.addWalkSteps(player.getX() - 2, player.getY(), 2, false);
					else
						player.addWalkSteps(player.getX() + 2, player.getY(), 2, false);
					return;
				}
				if (id == 4495 && object.getX() == 3413 && object.getY() == 3540) {
					player.useStairs(-1, new WorldTile(player.getX() + 5, player.getY(), 2), 1, 2);
					return;
				}
				if (id == 4496 && object.getX() == 3415 && object.getY() == 3540) {
					player.useStairs(-1, new WorldTile(player.getX() - 5, player.getY(), 1), 1, 2);
					return;
				}
				if (id == 29375) {
					boolean north = player.getY() == 9963 ? true : false;
					player.lock();
					player.animate(new Animation(742));
					final WorldTile toTile = new WorldTile(3120, north ? 9964 : 9969, player.getPlane());
					player.setNextForceMovement(
							new ForceMovement(toTile, 1, north ? ForceMovement.NORTH : ForceMovement.SOUTH));
					player.setNextWorldTile(toTile);
					WorldTasksManager.schedule(new WorldTask() {

						int x;

						@Override
						public void run() {
							if (x++ == 7) {
								stop();
								return;
							}
							if (x == 1) {
								player.setNextAnimationNoPriority(new Animation(744), player);
								final WorldTile toTile = new WorldTile(3120, north ? 9964 + x : 9969 - x,
										player.getPlane());
								player.setNextForceMovement(new ForceMovement(toTile, 1,
										north ? ForceMovement.NORTH : ForceMovement.SOUTH));
								WorldTasksManager.schedule(new WorldTask() {

									@Override
									public void run() {
										player.setNextAnimationNoPriority(new Animation(745), player);
										player.setNextWorldTile(toTile);
									}
								}, 0);
							} else if (x == 2) {
								player.setNextAnimationNoPriority(new Animation(744), player);
								final WorldTile toTile = new WorldTile(3120, north ? 9964 + x : 9969 - x,
										player.getPlane());
								player.setNextForceMovement(new ForceMovement(toTile, 1,
										north ? ForceMovement.NORTH : ForceMovement.SOUTH));
								WorldTasksManager.schedule(new WorldTask() {

									@Override
									public void run() {
										player.setNextAnimationNoPriority(new Animation(745), player);
										player.setNextWorldTile(toTile);
									}
								}, 0);
							} else if (x == 3) {
								player.setNextAnimationNoPriority(new Animation(744), player);
								final WorldTile toTile = new WorldTile(3120, north ? 9964 + x : 9969 - x,
										player.getPlane());
								player.setNextForceMovement(new ForceMovement(toTile, 1,
										north ? ForceMovement.NORTH : ForceMovement.SOUTH));
								WorldTasksManager.schedule(new WorldTask() {

									@Override
									public void run() {
										player.setNextAnimationNoPriority(new Animation(745), player);
										player.setNextWorldTile(toTile);
									}
								}, 0);
							} else if (x == 4) {
								player.setNextAnimationNoPriority(new Animation(744), player);
								final WorldTile toTile = new WorldTile(3120, north ? 9964 + x : 9969 - x,
										player.getPlane());
								player.setNextForceMovement(new ForceMovement(toTile, 1,
										north ? ForceMovement.NORTH : ForceMovement.SOUTH));
								WorldTasksManager.schedule(new WorldTask() {

									@Override
									public void run() {
										player.setNextAnimationNoPriority(new Animation(745), player);
										player.setNextWorldTile(toTile);
									}
								}, 0);
							} else if (x == 5) {
								player.setNextAnimationNoPriority(new Animation(744), player);
								final WorldTile toTile = new WorldTile(3120, north ? 9964 + x : 9969 - x,
										player.getPlane());
								player.setNextForceMovement(new ForceMovement(toTile, 1,
										north ? ForceMovement.NORTH : ForceMovement.SOUTH));
								WorldTasksManager.schedule(new WorldTask() {

									@Override
									public void run() {
										player.setNextAnimationNoPriority(new Animation(745), player);
										player.setNextWorldTile(toTile);
									}
								}, 0);
							} else if (x == 6) {
								player.setNextAnimationNoPriority(new Animation(743), player);
								final WorldTile toTile = new WorldTile(3120, north ? 9964 + x : 9969 - x,
										player.getPlane());
								player.setNextForceMovement(new ForceMovement(toTile, 1, ForceMovement.NORTH));
								WorldTasksManager.schedule(new WorldTask() {

									@Override
									public void run() {
										player.setNextWorldTile(toTile);
										player.unlock();
									}
								}, 0);
							}
						}
					}, 1, 1);
				}

				if (object.getId() == 9472 && object.getX() == 3008 && object.getY() == 3150) {
					player.useStairs(STAIRSDOWN, new WorldTile(3009, 9550, 0), 1, 2);
					return;
				}
				if (object.getId() == 32015 && object.getX() == 3008 && object.getY() == 9550) {
					player.useStairs(STAIRSUP, new WorldTile(3009, 3150, 0), 1, 2);
					return;
				}
				if (object.getId() == 9320) {
					if (object.getX() == 3447 && object.getY() == 3576) {
						if (player.getSkills().getLevel(Skills.AGILITY) < 72) {
							player.getPackets()
									.sendGameMessage("You need at least an agility level of 72 to use this shortcut.");
							return;
						}
						player.useStairs(STAIRSDOWN, new WorldTile(player.getX(), player.getY(), 1), 1, 2);
						return;
					} else if (object.getX() == 3422 && object.getY() == 3550) {
						if (player.getSkills().getLevel(Skills.AGILITY) < 61) {
							player.getPackets()
									.sendGameMessage("You need at least an agility level of 61 to use this shortcut.");
							return;
						}
						player.useStairs(STAIRSDOWN, new WorldTile(player.getX(), player.getY(), 0), 1, 2);
						return;
					}
				}

				if (object.getId() == 9319) {
					if (object.getX() == 3447 && object.getY() == 3576) {
						if (player.getSkills().getLevel(Skills.AGILITY) < 72) {
							player.getPackets()
									.sendGameMessage("You need at least an agility level of 72 to use this shortcut.");
							return;
						}
						player.useStairs(STAIRSUP, new WorldTile(player.getX(), player.getY(), 2), 1, 2);
						return;
					} else if (object.getX() == 3422 && object.getY() == 3550) {
						if (player.getSkills().getLevel(Skills.AGILITY) < 61) {
							player.getPackets()
									.sendGameMessage("You need at least an agility level of 61 to use this shortcut.");
							return;
						}
						player.useStairs(STAIRSUP, new WorldTile(player.getX(), player.getY(), 1), 1, 2);
						return;
					}
				}
				if (object.getId() == 2562) {
					player.getDialogueManager().startDialogue("CompletionistStand", 3373);
					return;
				}
				if (object.getId() == 12356
						&& !(player.getControlerManager().getControler() instanceof RecipeForDisaster)) {
					RecipeForDisaster.enterRfd(player);
					return;
				}
				if ((object.getId() == 8958 || object.getId() == 8959 || object.getId() == 8960)
						&& object.getX() == 2491) {
					if (player.getX() == 2490)
						player.addWalkSteps(player.getX() + 2, player.getY(), 2, false);
					else
						player.addWalkSteps(player.getX() - 2, player.getY(), 2, false);
				}

				if (object.getId() == 2566) {
					World.sendObjectAnimation(player, object, new Animation(555));
					return;
				}
				if (object.getId() == 26342 && object.getX() == 2917 && object.getY() == 3745) {
					player.setNextWorldTile(new WorldTile(2882, 5311, 2));
					player.getControlerManager().startControler("GodWars");
					return;
				}
				if (object.getId() == 35390 && object.getX() == 2907 && object.getY() == 3709) {
					player.sm("It's way too heavy to lift, perhaps I can squeeze by that gap...");
					return;
				}
				if (object.getId() == 26323 && object.getX() == 2928 && object.getY() == 3758) {
					player.sm("That look's too dangerous...");
					return;
				}
				if (object.getId() == 26293 && object.getX() == 2881 && object.getY() == 5311) {
					player.getControlerManager().forceStop();
					player.setNextWorldTile(new WorldTile(2916, 3746, 0));
					return;
				}
				if (object.getId() == 10177 && object.getX() == 2546 && object.getY() == 10143) {
					player.useStairs(833, new WorldTile(2900, 4449, 0), 1, 2);
					return;
				}
				if (object.getId() == 10229 && object.getX() == 2899 && object.getY() == 4449) {
					player.useStairs(833, new WorldTile(2545, 10143, 0), 1, 2);
					return;
				} else if (id == 2468 && object.getX() == 3089 && object.getY() == 3493) {
					EdgevillePvPControler.enterPVP(player);
					return;
				} else if (id == 2465 && object.getX() == 83 && object.getY() == 80) {
					EdgevillePvPControler.leavePVP(player);
					return;
					// Start of Runecrafting Portal Exit
				} else if (id == 2465) {// air
					player.movePlayer(new WorldTile(3127, 3408, 0), 1, 1);
					return;
				} else if (id == 2466 && object.getX() == 2793 && object.getY() == 4827) { // mind
					player.movePlayer(new WorldTile(2980, 3512, 0), 1, 1);
					return;
				} else if (id == 2467) {// water
					player.movePlayer(new WorldTile(3185, 3163, 0), 1, 1);
					return;
				} else if (id == 2468) {// earth
					player.movePlayer(new WorldTile(3304, 3474, 0), 1, 1);
					return;
				} else if (id == 2469) {// fire
					player.movePlayer(new WorldTile(3312, 3253, 0), 1, 1);
					return;
				} else if (id == 2470) { // body
					player.movePlayer(new WorldTile(3055, 3444, 0), 1, 1);
					return;
				} else if (id == 2471) {// cosmic
					player.movePlayer(new WorldTile(2408, 4379, 0), 1, 1);
					return;
				} else if (id == 2472) {// chaos
					player.movePlayer(new WorldTile(2857, 3379, 0), 1, 1);
					return;
				} else if (id == 2473) {// nature
					player.movePlayer(new WorldTile(2869, 3021, 0), 1, 1);
					return;
				} else if (id == 2474) {// law
					player.movePlayer(new WorldTile(3060, 3588, 0), 1, 1);
					return;
				} else if (id == 2475) {// death
					player.movePlayer(new WorldTile(1863, 4639, 0), 1, 1);
					return;
				} else if (id == 2477) {// blood
					player.movePlayer(new WorldTile(3561, 9779, 0), 1, 1);
					return;
				} else if (id >= 2465 && id <= 2477) { // portal
					player.setNextWorldTile(new WorldTile(3087, 3491, 0));
				} else if (id == 7133) { // nature rift
					player.setNextWorldTile(new WorldTile(2398, 4841, 0));
				} else if (id == 7132) { // cosmic rift
					player.setNextWorldTile(new WorldTile(2162, 4833, 0));
				} else if (id == 7141) { // blood rift
					player.setNextWorldTile(new WorldTile(2462, 4891, 1));
				} else if (id == 7129) { // fire rift
					player.setNextWorldTile(new WorldTile(2584, 4836, 0));
				} else if (id == 7130) { // earth rift
					player.setNextWorldTile(new WorldTile(2660, 4839, 0));
				} else if (id == 7131) { // body rift
					player.setNextWorldTile(new WorldTile(2527, 4833, 0));
				} else if (id == 7140) { // mind rift
					player.setNextWorldTile(new WorldTile(2794, 4830, 0));
				} else if (id == 7139) { // air rift
					player.setNextWorldTile(new WorldTile(2845, 4832, 0));
				} else if (id == 7137) { // water rift
					player.setNextWorldTile(new WorldTile(3482, 4836, 0));
				} else if (id == 7136) { // death rift
					player.setNextWorldTile(new WorldTile(2207, 4836, 0));
				} else if (id == 7135) { // law rift
					player.setNextWorldTile(new WorldTile(2464, 4834, 0));
				} else if (id == 7134) { // chaotic rift
					player.setNextWorldTile(new WorldTile(2269, 4843, 0));
					// End of Runecrafting Abyss Exits
				}
				if (id == 172) {
					if (player.getInventory().containsItem(3597, 1)) {
						player.animate(new Animation(536));
						player.lock(2);
						player.getPackets().sendGameMessage("You attempt to unlock the chest...");
						CrystalChest.sendRewards(true, player);
						return;
					} else if (player.getInventory().containsItem(989, 1)) {
						player.animate(new Animation(536));
						player.lock();
						player.getPackets().sendGameMessage("You attempt to unlock the chest...");
						World.removeObjectTemporary(player, object, 1000, false);
						WorldObject openedChest = new WorldObject(173, object.getType(), object.getRotation(), object);
						World.spawnObjectTemporary(player, openedChest, 1000);
						WorldTasksManager.schedule(new WorldTask() {
							@Override
							public void run() {
								player.getInventory().deleteItem(989, 1);
								CrystalChest.sendRewards(false, player);
								player.unlock();
							}
						}, 1);
						return;
					} else {
						player.getPackets().sendGameMessage("You need a crystal key to open this chest.");
						return;
					}
				}
				if (object.getId() == 25340 && object.getX() == 1778 && object.getY() == 5344) {
					player.setNextWorldTile(new WorldTile(1778, 5346, 0));
					return;
				}

				if (object.getId() == 25339 && object.getX() == 1778 && object.getY() == 5344) {
					player.setNextWorldTile(new WorldTile(1778, 5343, 1));
					return;
				}

				/**
				 */

				if (id == 15653) {
					if (World.isSpawnedObject(object) || !WarriorsGuild.canEnter(player))
						return;
					player.lock(2);
					player.addWalkSteps(object.getX() - 1, player.getY(), 2, false);
					player.getControlerManager().startControler("WarriorsGuild");
					return;
				}

				/** Chaos Tunnels added by Phillip **/

				/**
				 * Note: Chaos Tunnels on RuneScape wait 10 seconds until end of combat before
				 * allowing the player to access rift. TODO Add a combat timer
				 */

				/** Start of Rifts */

				// Entrance from Wilderness Rift
				if (object.getId() == 65203 && object.getX() == 3118)
					player.setNextWorldTile(new WorldTile(3247, 5491, 0));

				if (object.getId() == 65203 && object.getX() == 3129)
					player.setNextWorldTile(new WorldTile(3235, 5560, 0));

				if (object.getId() == 65203 && object.getX() == 3164)
					player.setNextWorldTile(new WorldTile(3291, 5480, 0));

				if (object.getId() == 65203 && object.getX() == 3176)
					player.setNextWorldTile(new WorldTile(3291, 5538, 0));

				if (object.getId() == 65203 && object.getX() == 3058)
					player.setNextWorldTile(new WorldTile(3184, 5469, 0));

				// Entrance to wilderness from chaos tunnel ropes
				if (object.getId() == 28782 && object.getX() == 3248) {// level
																		// 7
					player.setNextWorldTile(new WorldTile(3118, 3569, 0));
					player.getControlerManager().startControler("WildernessControler");
				}

				if (object.getId() == 28782 && object.getX() == 3234) {// level
																		// 9
					player.setNextWorldTile(new WorldTile(3129, 3586, 0));
					player.getControlerManager().startControler("WildernessControler");
				}

				if (object.getId() == 28782 && object.getX() == 3292) {// to lvl
																		// 5-6
																		// (bot
																		// tunnel
																		// from
																		// rs)
					player.setNextWorldTile(new WorldTile(3165, 3562, 0));
					player.getControlerManager().startControler("WildernessControler");
				}

				if (object.getId() == 28782 && object.getX() == 3291) {// to lvl
																		// 9
					player.setNextWorldTile(new WorldTile(3176, 3584, 0));
					player.getControlerManager().startControler("WildernessControler");
				}

				if (object.getId() == 28782 && object.getX() == 3183) {// to lvl
																		// 4
					player.setNextWorldTile(new WorldTile(3057, 3551, 0));
					player.getControlerManager().startControler("WildernessControler");
				}
				if (object.getId() == 20602) {
					player.movePlayer(new WorldTile(2954, 9675, 0), 1, 2);
					return;
				}
				if (object.getId() == 20604) {
					player.movePlayer(new WorldTile(3018, 3404, 0), 1, 2);
					return;
				}

				/** End Rifts */

				if (object.getId() == 77745 || object.getId() == 28779 || object.getId() == 29537) {

					if (x == 3254 && y == 5451) {
						player.setNextWorldTile(new WorldTile(3250, 5448, 0));
					}
					if (x == 3250 && y == 5448) {
						player.setNextWorldTile(new WorldTile(3254, 5451, 0));
					}
					if (x == 3241 && y == 5445) {
						player.setNextWorldTile(new WorldTile(3233, 5445, 0));
					}
					if (x == 3233 && y == 5445) {
						player.setNextWorldTile(new WorldTile(3241, 5445, 0));
					}
					if (x == 3259 && y == 5446) {
						player.setNextWorldTile(new WorldTile(3265, 5491, 0));
					}
					if (x == 3265 && y == 5491) {
						player.setNextWorldTile(new WorldTile(3259, 5446, 0));
					}
					if (x == 3260 && y == 5491) {
						player.setNextWorldTile(new WorldTile(3266, 5446, 0));
					}
					if (x == 3266 && y == 5446) {
						player.setNextWorldTile(new WorldTile(3260, 5491, 0));
					}
					if (x == 3241 && y == 5469) {
						player.setNextWorldTile(new WorldTile(3233, 5470, 0));
					}
					if (x == 3233 && y == 5470) {
						player.setNextWorldTile(new WorldTile(3241, 5469, 0));
					}
					if (x == 3235 && y == 5457) {
						player.setNextWorldTile(new WorldTile(3229, 5454, 0));
					}
					if (x == 3229 && y == 5454) {
						player.setNextWorldTile(new WorldTile(3235, 5457, 0));
					}
					if (x == 3280 && y == 5460) {
						player.setNextWorldTile(new WorldTile(3273, 5460, 0));
					}
					if (x == 3273 && y == 5460) {
						player.setNextWorldTile(new WorldTile(3280, 5460, 0));
					}
					if (x == 3283 && y == 5448) {
						player.setNextWorldTile(new WorldTile(3287, 5448, 0));
					}
					if (x == 3287 && y == 5448) {
						player.setNextWorldTile(new WorldTile(3283, 5448, 0));
					}
					if (x == 3244 && y == 5495) {
						player.setNextWorldTile(new WorldTile(3239, 5498, 0));
					}
					if (x == 3239 && y == 5498) {
						player.setNextWorldTile(new WorldTile(3244, 5495, 0));
					}
					if (x == 3232 && y == 5501) {
						player.setNextWorldTile(new WorldTile(3238, 5507, 0));
					}
					if (x == 3238 && y == 5507) {
						player.setNextWorldTile(new WorldTile(3232, 5501, 0));
					}
					if (x == 3218 && y == 5497) {
						player.setNextWorldTile(new WorldTile(3222, 5488, 0));
					}
					if (x == 3222 && y == 5488) {
						player.setNextWorldTile(new WorldTile(3218, 5497, 0));
					}
					if (x == 3218 && y == 5478) {
						player.setNextWorldTile(new WorldTile(3215, 5475, 0));
					}
					if (x == 3215 && y == 5475) {
						player.setNextWorldTile(new WorldTile(3218, 5478, 0));
					}
					if (x == 3224 && y == 5479) {
						player.setNextWorldTile(new WorldTile(3222, 5474, 0));
					}
					if (x == 3222 && y == 5474) {
						player.setNextWorldTile(new WorldTile(3224, 5479, 0));
					}
					if (x == 3208 && y == 5471) {
						player.setNextWorldTile(new WorldTile(3210, 5477, 0));
					}
					if (x == 3210 && y == 5477) {
						player.setNextWorldTile(new WorldTile(3208, 5471, 0));
					}
					if (x == 3214 && y == 5456) {
						player.setNextWorldTile(new WorldTile(3212, 5452, 0));
					}
					if (x == 3212 && y == 5452) {
						player.setNextWorldTile(new WorldTile(3214, 5456, 0));
					}
					if (x == 3204 && y == 5445) {
						player.setNextWorldTile(new WorldTile(3197, 5448, 0));
					}
					if (x == 3197 && y == 5448) {
						player.setNextWorldTile(new WorldTile(3204, 5445, 0));
					}
					if (x == 3189 && y == 5444) {
						player.setNextWorldTile(new WorldTile(3187, 5460, 0));
					}
					if (x == 3187 && y == 5460) {
						player.setNextWorldTile(new WorldTile(3189, 5444, 0));
					}
					if (x == 3192 && y == 5472) {
						player.setNextWorldTile(new WorldTile(3186, 5472, 0));
					}
					if (x == 3186 && y == 5472) {
						player.setNextWorldTile(new WorldTile(3192, 5472, 0));
					}
					if (x == 3185 && y == 5478) {
						player.setNextWorldTile(new WorldTile(3191, 5482, 0));
					}
					if (x == 3191 && y == 5482) {
						player.setNextWorldTile(new WorldTile(3185, 5478, 0));
					}
					if (x == 3171 && y == 5473) {
						player.setNextWorldTile(new WorldTile(3167, 5471, 0));
					}
					if (x == 3167 && y == 5471) {
						player.setNextWorldTile(new WorldTile(3171, 5473, 0));
					}
					if (x == 3171 && y == 5478) {
						player.setNextWorldTile(new WorldTile(3167, 5478, 0));
					}
					if (x == 3167 && y == 5478) {
						player.setNextWorldTile(new WorldTile(3171, 5478, 0));
					}
					if (x == 3168 && y == 5456) {
						player.setNextWorldTile(new WorldTile(3178, 5460, 0));
					}
					if (x == 3178 && y == 5460) {
						player.setNextWorldTile(new WorldTile(3168, 5456, 0));
					}
					if (x == 3191 && y == 5495) {
						player.setNextWorldTile(new WorldTile(3194, 5490, 0));
					}
					if (x == 3194 && y == 5490) {
						player.setNextWorldTile(new WorldTile(3191, 5495, 0));
					}
					if (x == 3141 && y == 5480) {
						player.setNextWorldTile(new WorldTile(3142, 5489, 0));
					}
					if (x == 3142 && y == 5489) {
						player.setNextWorldTile(new WorldTile(3141, 5480, 0));
					}
					if (x == 3142 && y == 5462) {
						player.setNextWorldTile(new WorldTile(3154, 5462, 0));
					}
					if (x == 3154 && y == 5462) {
						player.setNextWorldTile(new WorldTile(3142, 5462, 0));
					}
					if (x == 3143 && y == 5443) {
						player.setNextWorldTile(new WorldTile(3155, 5449, 0));
					}
					if (x == 3155 && y == 5449) {
						player.setNextWorldTile(new WorldTile(3143, 5443, 0));
					}
					if (x == 3307 && y == 5496) {
						player.setNextWorldTile(new WorldTile(3317, 5496, 0));
					}
					if (x == 3317 && y == 5496) {
						player.setNextWorldTile(new WorldTile(3307, 5496, 0));
					}
					if (x == 3318 && y == 5481) {
						player.setNextWorldTile(new WorldTile(3322, 5480, 0));
					}
					if (x == 3322 && y == 5480) {
						player.setNextWorldTile(new WorldTile(3318, 5481, 0));
					}
					if (x == 3299 && y == 5484) {
						player.setNextWorldTile(new WorldTile(3303, 5477, 0));
					}
					if (x == 3303 && y == 5477) {
						player.setNextWorldTile(new WorldTile(3299, 5484, 0));
					}
					if (x == 3286 && y == 5470) {
						player.setNextWorldTile(new WorldTile(3285, 5474, 0));
					}
					if (x == 3285 && y == 5474) {
						player.setNextWorldTile(new WorldTile(3286, 5470, 0));
					}
					if (x == 3290 && y == 5463) {
						player.setNextWorldTile(new WorldTile(3302, 5469, 0));
					}
					if (x == 3302 && y == 5469) {
						player.setNextWorldTile(new WorldTile(3290, 5463, 0));
					}
					if (x == 3296 && y == 5455) {
						player.setNextWorldTile(new WorldTile(3299, 5450, 0));
					}
					if (x == 3299 && y == 5450) {
						player.setNextWorldTile(new WorldTile(3296, 5455, 0));
					}
					if (x == 3280 && y == 5501) {
						player.setNextWorldTile(new WorldTile(3285, 5508, 0));
					}
					if (x == 3285 && y == 5508) {
						player.setNextWorldTile(new WorldTile(3280, 5501, 0));
					}
					if (x == 3300 && y == 5514) {
						player.setNextWorldTile(new WorldTile(3297, 5510, 0));
					}
					if (x == 3297 && y == 5510) {
						player.setNextWorldTile(new WorldTile(3300, 5514, 0));
					}
					if (x == 3289 && y == 5533) {
						player.setNextWorldTile(new WorldTile(3288, 5536, 0));
					}
					if (x == 3288 && y == 5536) {
						player.setNextWorldTile(new WorldTile(3289, 5533, 0));
					}
					if (x == 3285 && y == 5527) {
						player.setNextWorldTile(new WorldTile(3282, 5531, 0));
					}
					if (x == 3282 && y == 5531) {
						player.setNextWorldTile(new WorldTile(3285, 5527, 0));
					}
					if (x == 3325 && y == 5518) {
						player.setNextWorldTile(new WorldTile(3323, 5531, 0));
					}
					if (x == 3323 && y == 5531) {
						player.setNextWorldTile(new WorldTile(3325, 5518, 0));
					}
					if (x == 3299 && y == 5533) {
						player.setNextWorldTile(new WorldTile(3297, 5536, 0));
					}
					if (x == 3297 && y == 5538) {
						player.setNextWorldTile(new WorldTile(3299, 5533, 0));
					}
					if (x == 3321 && y == 5554) {
						player.setNextWorldTile(new WorldTile(3315, 5552, 0));
					}
					if (x == 3315 && y == 5552) {
						player.setNextWorldTile(new WorldTile(3321, 5554, 0));
					}
					if (x == 3291 && y == 5555) {
						player.setNextWorldTile(new WorldTile(3285, 5556, 0));
					}
					if (x == 3285 && y == 5556) {
						player.setNextWorldTile(new WorldTile(3291, 5555, 0));
					}
					if (x == 3266 && y == 5552) {
						player.setNextWorldTile(new WorldTile(3262, 5552, 0));
					}
					if (x == 3262 && y == 5552) {
						player.setNextWorldTile(new WorldTile(3266, 5552, 0));
					}
					if (x == 3256 && y == 5561) {
						player.setNextWorldTile(new WorldTile(3253, 5561, 0));
					}
					if (x == 3253 && y == 5561) {
						player.setNextWorldTile(new WorldTile(3256, 5561, 0));
					}
					if (x == 3249 && y == 5546) {
						player.setNextWorldTile(new WorldTile(3252, 5543, 0));
					}
					if (x == 3252 && y == 5543) {
						player.setNextWorldTile(new WorldTile(3249, 5546, 0));
					}
					if (x == 3261 && y == 5536) {
						player.setNextWorldTile(new WorldTile(3268, 5534, 0));
					}
					if (x == 3268 && y == 5534) {
						player.setNextWorldTile(new WorldTile(3261, 5536, 0));
					}
					if (x == 3243 && y == 5526) {
						player.setNextWorldTile(new WorldTile(3241, 5529, 0));
					}
					if (x == 3241 && y == 5529) {
						player.setNextWorldTile(new WorldTile(3243, 5526, 0));
					}
					if (x == 3230 && y == 5547) {
						player.setNextWorldTile(new WorldTile(3226, 5553, 0));
					}
					if (x == 3226 && y == 5553) {
						player.setNextWorldTile(new WorldTile(3230, 5547, 0));
					}
					if (x == 3206 && y == 5553) {
						player.setNextWorldTile(new WorldTile(3204, 5546, 0));
					}
					if (x == 3204 && y == 5546) {
						player.setNextWorldTile(new WorldTile(3206, 5553, 0));
					}
					if (x == 3211 && y == 5533) {
						player.setNextWorldTile(new WorldTile(3214, 5533, 0));
					}
					if (x == 3214 && y == 5533) {
						player.setNextWorldTile(new WorldTile(3211, 5533, 0));
					}
					if (x == 3208 && y == 5527) {
						player.setNextWorldTile(new WorldTile(3211, 5523, 0));
					}
					if (x == 3211 && y == 5523) {
						player.setNextWorldTile(new WorldTile(3208, 5527, 0));
					}
					if (x == 3201 && y == 5531) {
						player.setNextWorldTile(new WorldTile(3197, 5529, 0));
					}
					if (x == 3197 && y == 5529) {
						player.setNextWorldTile(new WorldTile(3201, 5531, 0));
					}
					if (x == 3202 && y == 5515) {
						player.setNextWorldTile(new WorldTile(3196, 5512, 0));
					}
					if (x == 3196 && y == 5512) {
						player.setNextWorldTile(new WorldTile(3202, 5515, 0));
					}
					if (x == 3190 && y == 5515) {
						player.setNextWorldTile(new WorldTile(3190, 5519, 0));
					}
					if (x == 3190 && y == 5519) {
						player.setNextWorldTile(new WorldTile(3190, 5515, 0));
					}
					if (x == 3185 && y == 5518) {
						player.setNextWorldTile(new WorldTile(3181, 5517, 0));
					}
					if (x == 3181 && y == 5517) {
						player.setNextWorldTile(new WorldTile(3185, 5518, 0));
					}
					if (x == 3187 && y == 5531) {
						player.setNextWorldTile(new WorldTile(3182, 5530, 0));
					}
					if (x == 3182 && y == 5530) {
						player.setNextWorldTile(new WorldTile(3187, 5531, 0));
					}
					if (x == 3169 && y == 5510) {
						player.setNextWorldTile(new WorldTile(3159, 5501, 0));
					}
					if (x == 3159 && y == 5501) {
						player.setNextWorldTile(new WorldTile(3169, 5510, 0));
					}
					if (x == 3165 && y == 5515) {
						player.setNextWorldTile(new WorldTile(3173, 5530, 0));
					}
					if (x == 3173 && y == 5530) {
						player.setNextWorldTile(new WorldTile(3165, 5515, 0));
					}
					if (x == 3156 && y == 5523) {
						player.setNextWorldTile(new WorldTile(3152, 5520, 0));
					}
					if (x == 3152 && y == 5520) {
						player.setNextWorldTile(new WorldTile(3156, 5523, 0));
					}
					if (x == 3148 && y == 5533) {
						player.setNextWorldTile(new WorldTile(3153, 5537, 0));
					}
					if (x == 3153 && y == 5537) {
						player.setNextWorldTile(new WorldTile(3148, 5533, 0));
					}
					if (x == 3143 && y == 5535) {
						player.setNextWorldTile(new WorldTile(3147, 5541, 0));
					}
					if (x == 3147 && y == 5541) {
						player.setNextWorldTile(new WorldTile(3143, 5535, 0));
					}
					if (x == 3168 && y == 5541) {
						player.setNextWorldTile(new WorldTile(3171, 5542, 0));
					}
					if (x == 3171 && y == 5542) {
						player.setNextWorldTile(new WorldTile(3168, 5541, 0));
					}
					if (x == 3190 && y == 5549) {
						player.setNextWorldTile(new WorldTile(3190, 5554, 0));
					}
					if (x == 3190 && y == 5554) {
						player.setNextWorldTile(new WorldTile(3190, 5549, 0));
					}
					if (x == 3180 && y == 5557) {
						player.setNextWorldTile(new WorldTile(3174, 5558, 0));
					}
					if (x == 3174 && y == 5558) {
						player.setNextWorldTile(new WorldTile(3180, 5557, 0));
					}
					if (x == 3162 && y == 5557) {
						player.setNextWorldTile(new WorldTile(3158, 5561, 0));
					}
					if (x == 3158 && y == 5561) {
						player.setNextWorldTile(new WorldTile(3162, 5557, 0));
					}
					if (x == 3166 && y == 5553) {
						player.setNextWorldTile(new WorldTile(3162, 5545, 0));
					}
					if (x == 3162 && y == 5545) {
						player.setNextWorldTile(new WorldTile(3166, 5553, 0));
					}
					if (x == 3142 && y == 5545) {
						if (player.getRights() == 2) {
							player.setNextWorldTile(new WorldTile(3115, 5528, 0));
						} else {
							player.sm("Sorry this zone is unavalible for players at the moment.");// bork
							return;
						}
					}
					if (x == 3115 && y == 5528) {
						player.setNextWorldTile(new WorldTile(3142, 5545, 0));
						// player.setNextGraphics(new Graphics(6));
					}
					player.gfx(new Graphics(2646));
					return;
				}

				if (object.getId() == 2081 && object.getX() == 2956 && object.getY() == 3145) {
					player.getActionManager().setAction(new Charter(Charter.KARAMJA_GANGPLANK));
					return;
				}
				if (object.getId() == 492) {
					player.useStairs(827, new WorldTile(2857, 9569, 0), 1, 2);
					return;
				}
				if (object.getId() == 1764) {
					player.useStairs(828, new WorldTile(2856, 3167, 0), 1, 2);
					return;
				}
				if (object.getId() == 29728) {
					player.useStairs(827, new WorldTile(3159, 4279, 3), 1, 2);
					return;
				}
				if (object.getId() == 29729) {
					player.useStairs(828, new WorldTile(3078, 3463, 0), 1, 2);
					return;
				}
				if (object.getId() == 29671) {
					player.useStairs(-1, new WorldTile(3174, 4273, 2), 1, 2);
					return;
				}
				if (object.getId() == 29672) {
					player.useStairs(-1, new WorldTile(3171, 4271, 3), 1, 2);
					return;
				}
				if (object.getId() == 29667) {
					player.useStairs(-1, new WorldTile(3160, 4249, 1), 1, 2);
					return;
				}
				if (object.getId() == 29668) {
					player.useStairs(-1, new WorldTile(3157, 4251, 2), 1, 2);
					return;
				}
				if (object.getId() == 29664) {
					player.useStairs(-1, new WorldTile(3157, 4244, 2), 1, 2);
					return;
				}
				if (object.getId() == 29663) {
					player.useStairs(-1, new WorldTile(3160, 4246, 1), 1, 2);
					return;
				}
				if (object.getId() == 29660) {
					player.useStairs(-1, new WorldTile(3149, 4251, 2), 1, 2);
					return;
				}
				if (object.getId() == 29659) {
					player.useStairs(-1, new WorldTile(3146, 4249, 1), 1, 2);
					return;
				}
				if (object.getId() == 29656) {
					player.useStairs(-1, new WorldTile(3149, 4244, 2), 1, 2);
					return;
				}
				if (object.getId() == 29655) {
					player.useStairs(-1, new WorldTile(3146, 4246, 1), 1, 2);
					return;
				}
				if (object.getId() == 29623) {
					player.useStairs(-1, new WorldTile(3077, 4235, 0), 1, 2);
					return;
				}
				if (object.getId() == 29589) {
					player.useStairs(-1, new WorldTile(3083, 3452, 0), 1, 2);
					return;
				}
				if (object.getId() == 29592) {
					player.useStairs(-1, new WorldTile(3086, 4247, 0), 1, 2);
					return;
				}
				if (object.getId() == 29602) {
					player.useStairs(-1, new WorldTile(3074, 3456, 0), 1, 2);
					return;
				}
				if (object.getId() == 29603) {
					player.useStairs(-1, new WorldTile(3082, 4229, 0), 1, 2);
					return;
				}
				if (object.getId() == 29732) {
					if (player.getX() == 3085)
						player.getPackets().sendGameMessage("This door is locked.");
					else
						player.getPackets()
								.sendGameMessage("This door is locked, maybe i can find another way to get in.");
					return;
				}
				if (object.getId() == 29736) {// leverhere
					WorldObject pulledLever = new WorldObject(32282, object.getType(), object.getRotation(),
							object.getX(), object.getY(), object.getPlane());
					if (World.removeObjectTemporary(object, 1200, false))
						World.spawnObjectTemporary(pulledLever, 1200);
					player.getPackets().sendGameMessage("You pull the lever.. wonder what it does..");
					player.safetyLever = true;
					return;
				}
				if (object.getId() == 29624) {
					if (!player.safetyLever) {
						player.getPackets().sendGameMessage("This gate is locked.");
						return;
					}
					if (object.getX() == 3178 && object.getY() == 4269)
						player.useStairs(-1, new WorldTile(3177, 4266, 0), 1, 2);
					if (object.getX() == 3178 && object.getY() == 4266)
						player.useStairs(-1, new WorldTile(3177, 4269, 2), 1, 2);
					if (object.getX() == 3142 && object.getY() == 4270)
						player.useStairs(-1, new WorldTile(3142, 4272, 1), 1, 2);
					if (object.getX() == 3141 && object.getY() == 4272)
						player.useStairs(-1, new WorldTile(3143, 4270, 0), 1, 2);
					return;
				}

				if (object.getId() == 29734) {
					if (!player.completedSafety) {
						if (!player.getInventory().hasFreeSlots()) {
							player.getPackets().sendGameMessage("You need one inventory space to open this chest.");
							return;
						}
						player.getInventory().addItem(12629, 1);
						player.getMoneyPouch().addMoney(10000, false);
						player.getDialogueManager().startDialogue("SimpleItemDialogue", 12629, 1,
								"You open the chest to find a large pile of gold, along with a pair of safety gloves.");
						player.completedSafety = true;
					} else {
						player.getPackets().sendGameMessage("You have already opened this chest.");
						return;
					}
					return;
				}

				/**
				 * Start of Ancient Cavern Stairs, Shortcuts, and Barriers added
				 *
				 * @author Phillip
				 */

				/** Whirlpool */

				if (object.getId() == 67966) {
					player.getActionManager().setAction(new WhirlPool(WhirlPool.WHIRLPOOL_LOC));
					return;
				}

				if (object.getId() == 25216) {
					player.setNextWorldTile(new WorldTile(Settings.HOME_PLAYER_LOCATION));
				}

				if (object.getId() == 25336 && object.getX() == 1770 && object.getY() == 5365) {
					player.setNextWorldTile(new WorldTile(1768, 5366, 1));
					return;
				}

				if (object.getId() == 25338 && object.getX() == 1769 && object.getY() == 5365) {
					player.setNextWorldTile(new WorldTile(1772, 5366, 0));
					return;
				}

				if (object.getId() == 25337 && object.getX() == 1744 && object.getY() == 5323) {
					player.setNextWorldTile(new WorldTile(1744, 5321, 1));
					return;
				}

				if (object.getId() == 39468 && object.getX() == 1744 && object.getY() == 5322) {
					player.setNextWorldTile(new WorldTile(1745, 5325, 0));
					return;
				}

				if (object.getId() == 47232) {
					player.setNextWorldTile(new WorldTile(1661, 5257, 0));
					return;
				}

				if (object.getId() == 47231) {
					player.setNextWorldTile(new WorldTile(1735, 5313, 1));
					return;
				}

				// barriers
				if (object.getId() == 47236 && object.getX() == 1634) {
					if (player.getX() == 1635) {
						player.setNextWorldTile(new WorldTile(player.getX() - 1, player.getY(), 0));
					} else if (player.getX() == 1634) {
						player.setNextWorldTile(new WorldTile(player.getX() + 1, player.getY(), 0));
					}
					return;
				}

				if (object.getId() == 47236 && object.getY() == 5265) {
					if (player.getY() == 5264) {
						player.setNextWorldTile(new WorldTile(player.getX(), player.getY() + 1, 0));
					} else if (player.getY() == 5265) {
						player.setNextWorldTile(new WorldTile(player.getX(), player.getY() - 1, 0));
					}
					return;
				}

				if (object.getId() == 47236 && object.getY() == 5289) {
					if (player.getY() == 5288) {
						player.setNextWorldTile(new WorldTile(player.getX(), player.getY() + 1, 0));
					} else if (player.getY() == 5289) {
						player.setNextWorldTile(new WorldTile(player.getX(), player.getY() - 1, 0));
					}
					return;
				}

				if (object.getId() == 47236 && object.getX() == 1625) {
					if (player.getX() == 1625) {
						player.setNextWorldTile(new WorldTile(player.getX() + 1, player.getY(), 0));
					} else if (player.getX() == 1626) {
						player.setNextWorldTile(new WorldTile(player.getX() - 1, player.getY(), 0));
					}
					return;
				}

				if (object.getId() == 47236 && object.getX() == 1649) {
					if (player.getX() == 1649) {
						player.setNextWorldTile(new WorldTile(player.getX() + 1, player.getY(), 0));
					} else if (player.getX() == 1650) {
						player.setNextWorldTile(new WorldTile(player.getX() - 1, player.getY(), 0));
					}
					return;
				}

				if (object.getId() == 47236 && object.getY() == 5281) {
					if (player.getY() == 5281) {
						player.setNextWorldTile(new WorldTile(player.getX(), player.getY() - 1, 0));
					} else if (player.getY() == 5280) {
						player.setNextWorldTile(new WorldTile(player.getX(), player.getY() + 1, 0));
					}
					return;
				}

				// shortcuts

				if (object.getId() == 47233 && player.getY() == 5294) {
					if (!Agility.hasLevel(player, 86)) {
						player.sm("You must have an Agility level of 86 or higher to use this shortcut.");
						return;
					}
					player.getPackets().sendGameMessage("You climb the low wall...", true);
					player.lock(3);
					player.animate(new Animation(4853));
					final WorldTile toTile = new WorldTile(object.getX(), object.getY() - 1, object.getPlane());
					player.setNextForceMovement(new ForceMovement(player, 0, toTile, 2, ForceMovement.SOUTH));

					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							player.setNextWorldTile(toTile);
						}
					}, 1);
					return;
				}

				if (object.getId() == 47233 && player.getY() == 5292) {
					if (!Agility.hasLevel(player, 86)) {
						player.sm("You must have an Agility level of 86 or higher to use this shortcut.");
						return;
					}
					player.getPackets().sendGameMessage("You climb the low wall...", true);
					player.lock(3);
					player.animate(new Animation(4853));
					final WorldTile toTile = new WorldTile(object.getX(), object.getY() + 1, object.getPlane());
					player.setNextForceMovement(new ForceMovement(player, 0, toTile, 2, ForceMovement.NORTH));

					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							player.setNextWorldTile(toTile);
							player.getSkills().addXp(Skills.AGILITY, 13.7);
						}
					}, 1);
					return;
				}

				if (object.getId() == 47237 && player.getY() == 5268) {
					player.getRun();
					player.lock(4);
					player.animate(new Animation(2922));
					final WorldTile toTile = new WorldTile(object.getX(), object.getY() - 7, object.getPlane());
					player.setNextForceMovement(new ForceMovement(player, 1, toTile, 3, ForceMovement.SOUTH));
					player.getPackets().sendGameMessage("You skilfully run across the Gap.", true);
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							player.setNextWorldTile(toTile);
						}

					}, 1);
					return;
				}

				if (object.getId() == 47237 && player.getY() == 5260) {
					player.getRun();
					player.lock(4);
					player.animate(new Animation(2922));
					final WorldTile toTile = new WorldTile(object.getX(), object.getY() + 7, object.getPlane());
					player.setNextForceMovement(new ForceMovement(player, 1, toTile, 3, ForceMovement.NORTH));
					player.getPackets().sendGameMessage("You skilfully run across the Gap.", true);
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							player.setNextWorldTile(toTile);
						}

					}, 1);
					return;
				} else if (id == 11554 || id == 11552)
					player.getPackets().sendGameMessage("That rock is currently unavailable.");
				else if (id == 38279)
					player.getDialogueManager().startDialogue("RunespanPortalD");
				else if (id == 2491)
					player.getActionManager()
							.setAction(new EssenceMining(object,
									player.getSkills().getLevel(Skills.MINING) < 30 ? EssenceDefinitions.Rune_Essence
											: EssenceDefinitions.Pure_Essence));
				// } else if (id == 65371) {

				/*
				 * if (player.getTeleBlockDelay() > Utils.currentTimeMillis()) {
				 * player.getPackets().sendGameMessage (
				 * "A magical force interrupts your focus..."); return; } else {
				 * //player.getActionManager().setAction(new
				 * ObjectActionTeleport(Settings.HOME_PLAYER_LOCATION, 0));
				 * 
				 * return; }
				 */
				else if (id == 47120) {
					if (player.getPrayer().getPrayerpoints() < player.getSkills().getLevelForXp(Skills.PRAYER) * 10) {
						player.lock(12);
						player.animate(new Animation(12563));
						player.getPrayer()
								.setPrayerpoints((int) ((player.getSkills().getLevelForXp(Skills.PRAYER) * 10) * 1.15));
						player.getPrayer().refreshPrayerPoints();
					}
					player.getDialogueManager().startDialogue("SwitchPrayers");
				} else if (id == 19222)
					Falconry.beginFalconry(player);
				else if (id == 36786)
					player.getDialogueManager().startDialogue("Banker", 4907);
				else if (id == 42377 || id == 42378)
					player.getDialogueManager().startDialogue("Banker", 2759);
				else if (id == 42217 || id == 782 || id == 34752)
					player.getDialogueManager().startDialogue("Banker", 553);
				else if (id == 57437)
					player.getBank().openBank();
				else if (id == 42425 && object.getX() == 3220 && object.getY() == 3222) { // zaros
																							// portal
					player.useStairs(10256, new WorldTile(3353, 3416, 0), 4, 5,
							"And you find yourself into a digsite.");
					player.addWalkSteps(3222, 3223, -1, false);
					player.getPackets().sendGameMessage("You examine portal and it aborves you...");
				} else if (id == 9356)
					FightCaves.enterFightCaves(player);
				else if (id == 65365)
					WildyAgility.DoorStart(player, object);
				else if (id == 65367)
					WildyAgility.DoorStart2(player, object);
				else if (id == 69514)
					GnomeAgility.RunGnomeBoard(player, object);
				else if (id == 69389)
					GnomeAgility.JumpDown(player, object);
				else if (id == 68223)
					FightPits.enterLobby(player, false);
				else if (id == 46500 && object.getX() == 3351 && object.getY() == 3415) { // zaros
																							// portal
					player.useStairs(-1, new WorldTile(Settings.RESPAWN_PLAYER_LOCATION.getX(),
							Settings.RESPAWN_PLAYER_LOCATION.getY(), Settings.RESPAWN_PLAYER_LOCATION.getPlane()), 2, 3,
							"You found your way back to home.");
					player.addWalkSteps(3351, 3415, -1, false);
				} else if (id == 9293) {
					if (player.getSkills().getLevel(Skills.AGILITY) < 70) {
						player.getPackets().sendGameMessage("You need an agility level of 70 to use this obstacle.",
								true);
						return;
					}
					player.animate(new Animation(844));
					int x = player.getX() == 2886 ? 2892 : 2886;
					WorldTasksManager.schedule(new WorldTask() {
						int count = 0;

						@Override
						public void run() {
							player.animate(new Animation(844));
							if (count++ == 1)
								stop();
						}

					}, 0, 0);
					player.setNextForceMovement(
							new ForceMovement(new WorldTile(x, 9799, 0), 3, player.getX() == 2886 ? 1 : 3));
					player.useStairs(-1, new WorldTile(x, 9799, 0), 3, 4);
				} else if (id == 29370 && (object.getX() == 3150 || object.getX() == 3153) && object.getY() == 9906) { // edgeville
					if (Settings.FREE_TO_PLAY) {
						player.getPackets().sendGameMessage("Nothing interesting happens.");
						return;
					}
					// cut
					if (player.getSkills().getLevel(Skills.AGILITY) < 53) {
						player.getPackets().sendGameMessage("You need an agility level of 53 to use this obstacle.");
						return;
					}
					final boolean running = player.getRun();
					player.setRunHidden(false);
					player.lock(8);
					player.addWalkSteps(x == 3150 ? 3155 : 3149, 9906, -1, false);
					player.getPackets().sendGameMessage("You pulled yourself through the pipes.", true);
					WorldTasksManager.schedule(new WorldTask() {
						boolean secondloop;

						@Override
						public void run() {
							if (!secondloop) {
								secondloop = true;
								player.getAppearence().setRenderEmote(295);
							} else {
								player.getAppearence().setRenderEmote(-1);
								player.setRunHidden(running);
								player.getSkills().addXp(Skills.AGILITY, 7);
								stop();
							}
						}
					}, 0, 5);
				}
				// start forinthry dungeon
				else if (id == 18341 && object.getX() == 3036 && object.getY() == 10172)
					player.useStairs(-1, new WorldTile(3039, 3765, 0), 0, 1);
				else if (id == 20599 && object.getX() == 3038 && object.getY() == 3761)
					player.useStairs(-1, new WorldTile(3037, 10171, 0), 0, 1);
				else if (id == 18342 && object.getX() == 3075 && object.getY() == 10057)
					player.useStairs(-1, new WorldTile(3071, 3649, 0), 0, 1);
				else if (id == 20600 && object.getX() == 3072 && object.getY() == 3648)
					player.useStairs(-1, new WorldTile(3077, 10058, 0), 0, 1);

				// nomads requiem
				/*
				 * else if (id == 18425 && !player.getQuestManager().completedQuest(Quests.
				 * NOMADS_REQUIEM)) NomadsRequiem.enterNomadsRequiem(player); else if (id ==
				 * 42219) { player.useStairs(-1, new WorldTile(1886, 3178, 0), 0, 1); if
				 * (player.getQuestManager().getQuestStage(Quests. NOMADS_REQUIEM) == -2)
				 * player.getQuestManager().setQuestStageAndRefresh(Quests. NOMADS_REQUIEM, 0);
				 */
				else if (id == 8689)
					player.getActionManager().setAction(new CowMilkingAction());
				else if (id == 42220)
					player.useStairs(-1, new WorldTile(3082, 3475, 0), 0, 1);
				// start falador mininig
				else if (id == 30942 && object.getX() == 3019 && object.getY() == 3450)
					player.useStairs(828, new WorldTile(3020, 9850, 0), 1, 2);
				else if (id == 6226 && object.getX() == 3019 && object.getY() == 9850)
					player.useStairs(833, new WorldTile(3018, 3450, 0), 1, 2);
				else if (id == 30943 && object.getX() == 3059 && object.getY() == 9776)
					player.useStairs(-1, new WorldTile(3061, 3376, 0), 0, 1);
				else if (id == 30944 && object.getX() == 3059 && object.getY() == 3376)
					player.useStairs(-1, new WorldTile(3058, 9776, 0), 0, 1);
				else if (id == 2112 && object.getX() == 3046 && object.getY() == 9756) {
					if (player.getSkills().getLevelForXp(Skills.MINING) < 60) {
						player.getDialogueManager().startDialogue("SimpleNPCMessage",
								MiningGuildDwarf.getClosestDwarfID(player),
								"Sorry, but you need level 60 Mining to go in there.");
						return;
					}
					/*
					 * WorldObject openedDoor = new WorldObject(object.getId(), object.getType(),
					 * object.getRotation() - 1, object .getX(), object.getY() + 1, object
					 * .getPlane()); if (World.removeObjectTemporary(object, 1200)) {
					 * World.spawnObjectTemporary(openedDoor, 1200);
					 */
					player.lock(2);
					player.stopAll();
					player.addWalkSteps(3046, player.getY() > object.getY() ? object.getY() : object.getY() + 1, -1,
							false);
					World.sendObjectAnimation(player, object, new Animation(1999));
				} else if (id == 2113) {
					if (player.getSkills().getLevelForXp(Skills.MINING) < 60) {
						player.getDialogueManager().startDialogue("SimpleNPCMessage",
								MiningGuildDwarf.getClosestDwarfID(player),
								"Sorry, but you need level 60 Mining to go in there.");
						return;
					}
					player.useStairs(-1, new WorldTile(3021, 9739, 0), 0, 1);
				} else if (id == 6226 && object.getX() == 3019 && object.getY() == 9740)
					player.useStairs(828, new WorldTile(3019, 3341, 0), 1, 2);
				else if (id == 5097 && object.getX() == 2635 && object.getY() == 9514) // brimhaven
																						// stairs
					player.useStairs(-1, new WorldTile(2636, 9510, 2), 1, 2);
				else if (id == 5098 && object.getX() == 2635 && object.getY() == 9511) // brimhaven
																						// stairs
					player.useStairs(-1, new WorldTile(2636, 9517, 0), 1, 2);
				else if (id == 5096 && object.getX() == 2644 && object.getY() == 9593) // brimhaven
																						// stairs
					player.useStairs(-1, new WorldTile(2649, 9591, 0), 1, 2);
				else if (id == 5094 && object.getX() == 2648 && object.getY() == 9592) { // brimhaven
																							// stairs
					player.useStairs(-1, new WorldTile(2643, 9594, 2), 1, 2);
				} else if (id == 5111 && object.getX() == 2647 && object.getY() == 9558) { // Steps
																							// brimhaven
					player.lock(14);
					WorldTasksManager.schedule(new WorldTask() {
						int y;

						@Override
						public void run() {
							if (y++ == 7) {
								stop();
								return;
							}
							if (y < 4) {
								final WorldTile toTile = new WorldTile(player.getX(), player.getY() + 1,
										player.getPlane());
								player.setNextForceMovement(new ForceMovement(toTile, 1, ForceMovement.NORTH));
								player.setNextWorldTile(toTile);
							}
							if (y > 3 && y < 7) {
								final WorldTile toTile = new WorldTile(player.getX() + 1, player.getY(),
										player.getPlane());
								player.setNextForceMovement(new ForceMovement(toTile, 1, ForceMovement.EAST));
								player.setNextWorldTile(toTile);
							}
							if (y > 5) {
								final WorldTile toTile = new WorldTile(player.getX(), player.getY() + 1,
										player.getPlane());
								player.setNextForceMovement(new ForceMovement(toTile, 1, ForceMovement.NORTH));
								player.setNextWorldTile(toTile);
							}
							player.animate(new Animation(741));
						}
					}, 0, 1);
				} else if (id == 5110 && object.getX() == 2649 && object.getY() == 9561) { // Steps
																							// brimhaven
					player.lock(14);
					WorldTasksManager.schedule(new WorldTask() {
						int x;

						@Override
						public void run() {
							if (x++ == 7) {
								stop();
								return;
							}
							if (x == 1 || x == 2) {
								final WorldTile toTile = new WorldTile(player.getX(), player.getY() - 1,
										player.getPlane());
								player.setNextForceMovement(new ForceMovement(toTile, 1, ForceMovement.SOUTH));
								player.setNextWorldTile(toTile);
							}
							if (x == 3 || x == 4) {
								final WorldTile toTile = new WorldTile(player.getX() - 1, player.getY(),
										player.getPlane());
								player.setNextForceMovement(new ForceMovement(toTile, 1, ForceMovement.WEST));
								player.setNextWorldTile(toTile);
							}
							if (x > 4) {
								final WorldTile toTile = new WorldTile(player.getX(), player.getY() - 1,
										player.getPlane());
								player.setNextForceMovement(new ForceMovement(toTile, 1, ForceMovement.SOUTH));
								player.setNextWorldTile(toTile);
							}
							player.animate(new Animation(741));
						}
					}, 0, 1);
				} else if (id == 6226 && object.getX() == 3019 && object.getY() == 9738)
					player.useStairs(828, new WorldTile(3019, 3337, 0), 1, 2);
				else if (id == 6226 && object.getX() == 3018 && object.getY() == 9739)
					player.useStairs(828, new WorldTile(3017, 3339, 0), 1, 2);
				else if (id == 6226 && object.getX() == 3020 && object.getY() == 9739)
					player.useStairs(828, new WorldTile(3021, 3339, 0), 1, 2);
				else if (id == 30963)
					player.getBank().openBank();
				else if (id == 6045)
					player.getPackets().sendGameMessage("You search the cart but find nothing.");
				else if (id == 5906) {
					if (player.getSkills().getLevel(Skills.AGILITY) < 42) {
						player.getPackets().sendGameMessage("You need an agility level of 42 to use this obstacle.");
						return;
					}
					player.lock();
					WorldTasksManager.schedule(new WorldTask() {
						int count = 0;

						@Override
						public void run() {
							if (count == 0) {
								player.animate(new Animation(2594));
								WorldTile tile = new WorldTile(object.getX() + (object.getRotation() == 2 ? -2 : +2),
										object.getY(), 0);
								player.setNextForceMovement(new ForceMovement(tile, 4, Utils
										.getMoveDirection(tile.getX() - player.getX(), tile.getY() - player.getY())));
							} else if (count == 2) {
								WorldTile tile = new WorldTile(object.getX() + (object.getRotation() == 2 ? -2 : +2),
										object.getY(), 0);
								player.setNextWorldTile(tile);
							} else if (count == 5) {
								player.animate(new Animation(2590));
								WorldTile tile = new WorldTile(object.getX() + (object.getRotation() == 2 ? -5 : +5),
										object.getY(), 0);
								player.setNextForceMovement(new ForceMovement(tile, 4, Utils
										.getMoveDirection(tile.getX() - player.getX(), tile.getY() - player.getY())));
							} else if (count == 7) {
								WorldTile tile = new WorldTile(object.getX() + (object.getRotation() == 2 ? -5 : +5),
										object.getY(), 0);
								player.setNextWorldTile(tile);
							} else if (count == 10) {
								player.animate(new Animation(2595));
								WorldTile tile = new WorldTile(object.getX() + (object.getRotation() == 2 ? -6 : +6),
										object.getY(), 0);
								player.setNextForceMovement(new ForceMovement(tile, 4, Utils
										.getMoveDirection(tile.getX() - player.getX(), tile.getY() - player.getY())));
							} else if (count == 12) {
								WorldTile tile = new WorldTile(object.getX() + (object.getRotation() == 2 ? -6 : +6),
										object.getY(), 0);
								player.setNextWorldTile(tile);
							} else if (count == 14) {
								stop();
								player.unlock();
							}
							count++;
						}

					}, 0, 0);
					// BarbarianOutpostAgility start
					// BarbarianOutpostAgility start
				} else if (id == 20210)
					BarbarianOutpostAgility.enterObstaclePipe(player, object);
				else if (id == 43526)
					BarbarianOutpostAgility.swingOnRopeSwing(player, object);
				else if (id == 43595 && x == 2550 && y == 3546)
					BarbarianOutpostAgility.walkAcrossLogBalance(player, object);
				else if (id == 20211 && x == 2538 && y == 3545)
					BarbarianOutpostAgility.climbObstacleNet(player, object);
				else if (id == 2302 && x == 2535 && y == 3547)
					BarbarianOutpostAgility.walkAcrossBalancingLedge(player, object);
				else if (id == 1948)
					BarbarianOutpostAgility.climbOverCrumblingWall(player, object);
				else if (id == 43533)
					BarbarianOutpostAgility.runUpWall(player, object);
				else if (id == 43597)
					BarbarianOutpostAgility.climbUpWall(player, object);
				else if (id == 43587)
					BarbarianOutpostAgility.fireSpringDevice(player, object);
				else if (id == 43527)
					BarbarianOutpostAgility.crossBalanceBeam(player, object);
				else if (id == 43531)
					BarbarianOutpostAgility.jumpOverGap(player, object);
				else if (id == 43532)
					BarbarianOutpostAgility.slideDownRoof(player, object);
				else if (id == 23610 && object.getX() == 3509 && object.getY() == 9497)
					player.useStairs(STAIRSDOWN, new WorldTile(3507, 9493, 0), 1, 2);
				else if (id == 9295) {// nonprod
					player.getControlerManager().forceStop();
					player.setNextWorldTile(new WorldTile(3219, 3219, 0));
					player.reset();
				}

				// rock living caverns
				else if (id == 45077) {
					player.lock();
					if (player.getX() != object.getX() || player.getY() != object.getY())
						player.addWalkSteps(object.getX(), object.getY(), -1, false);
					WorldTasksManager.schedule(new WorldTask() {

						private int count;

						@Override
						public void run() {
							if (count == 0) {
								player.setNextFaceWorldTile(new WorldTile(object.getX() - 1, object.getY(), 0));
								player.animate(new Animation(12216));
								player.unlock();
							} else if (count == 2) {
								player.setNextWorldTile(new WorldTile(3651, 5122, 0));
								player.setNextFaceWorldTile(new WorldTile(3651, 5121, 0));
								player.animate(new Animation(12217));
							} else if (count == 3) {
								// TODO find emote
								// player.getPackets().sendObjectAnimation(new
								// WorldObject(45078, 0, 3, 3651, 5123, 0), new
								// Animation(12220));
							} else if (count == 5) {
								player.unlock();
								stop();
							}
							count++;
						}

					}, 1, 0);

					/**
					 * Mage Bank objects *
					 */

					/**
					 * } else if (id == 2878 || id == 2879) { player.getDialogueManager
					 * ().startDialogue("SimpleMessage", "You step into the pool of sparkling water.
					 * You feel the energy rush through your veins." ); final boolean isLeaving = id
					 * == 2879; final WorldTile tile = isLeaving ? new WorldTile(2509, 4687, 0) :
					 * new WorldTile(2542, 4720, 0); player.setNextForceMovement(new
					 * ForceMovement(player, 1, tile, 2, isLeaving ? ForceMovement.SOUTH :
					 * ForceMovement.NORTH)); WorldTasksManager.schedule(new WorldTask() {
					 *
					 * @Override public void run() { player.setNextAnimation(new Animation(13842));
					 *           WorldTasksManager.schedule(new WorldTask() {
					 * @Override public void run() { player.setNextAnimation(new Animation(-1));
					 *           player.setNextWorldTile(isLeaving ? new WorldTile(2542, 4718, 0) :
					 *           new WorldTile(2509, 4689, 0)); } }, 2); } });
					 */

				} else if (id == 2878) {
					player.sm("You jump into the spring and fall into a dark cavern...");
					player.setNextWorldTile(new WorldTile(2509, 4689, 0));// mb
																			// fountain

				} else if (id == 2879) {
					player.sm("You jump into the spring...");
					player.setNextWorldTile(new WorldTile(2542, 4718, 0));// god
																			// cape
																			// tunnel
																			// fountain
				} else if (id >= 2873 && id <= 2875)
					GodCapes.handleStatue(object, player);
				else if (id == 45078)
					player.useStairs(2413, new WorldTile(3012, 9832, 0), 2, 2);
				else if (id == 45079)
					player.getBank().openDepositBox();
				else if (id == 2606) {
					WorldObject openedDoor = new WorldObject(object.getId(), object.getType(), object.getRotation() - 1,
							object.getX(), object.getY() - 1, object.getPlane());
					if (World.removeObjectTemporary(object, 1200, false))
						World.spawnObjectTemporary(openedDoor, 1200);
					player.lock(2);
					player.stopAll();
					player.addWalkSteps(2836, player.getY() >= object.getY() ? object.getY() - 1 : object.getY(), -1,
							false);
				}
				// champion guild
				else if (id == 24357 && object.getX() == 3188 && object.getY() == 3355)
					player.useStairs(-1, new WorldTile(3189, 3354, 1), 0, 1);
				else if (id == 24359 && object.getX() == 3188 && object.getY() == 3355)
					player.useStairs(-1, new WorldTile(3189, 3358, 0), 0, 1);
				else if (id == 1805 && object.getX() == 3191 && object.getY() == 3363) {
					// if (player.getQuestManager().get(Quests.DRAGON_SLAYER).getState() ==
					// QuestState.NOT_STARTED)
				}
				// start of varrock dungeon
				else if (id == 29355 && object.getX() == 3230 && object.getY() == 9904) // varrock
																						// dungeon
																						// climb
																						// to
																						// bear
					player.useStairs(828, new WorldTile(3229, 3503, 0), 1, 2);
				else if (id == 29355 && object.getX() == 3097 && object.getY() == 9868) // Phillip
																						// -
																						// Fixed
																						// Manhole
					player.useStairs(828, new WorldTile(3096, 3468, 0), 1, 2);
				else if (id == 24264)
					player.useStairs(833, new WorldTile(3229, 9904, 0), 1, 2);
				else if (id == 24366)
					player.useStairs(828, new WorldTile(3237, 3459, 0), 1, 2);
				else if (id == 882 && object.getX() == 3237 && object.getY() == 3458)
					player.useStairs(833, new WorldTile(3237, 9858, 0), 1, 2);
				else if (id == 29355 && object.getX() == 3097 && object.getY() == 9867) // edge
																						// dungeon
																						// climb
					player.useStairs(828, new WorldTile(3096, 3468, 0), 1, 2);
				else if (id == 26934)
					player.useStairs(833, new WorldTile(3096, 9868, 0), 1, 2);
				else if (id == 29355 && object.getX() == 3088 && object.getY() == 9971)
					player.useStairs(828, new WorldTile(3087, 3571, 0), 1, 2);
				else if (id == 65453)
					player.useStairs(833, new WorldTile(3089, 9971, 0), 1, 2);
				else if (id == 12389 && object.getX() == 3116 && object.getY() == 3452)
					player.useStairs(833, new WorldTile(3117, 9852, 0), 1, 2);
				else if (id == 29355 && object.getX() == 3116 && object.getY() == 9852)
					player.useStairs(828, new WorldTile(3115, 3452, 0), 1, 2);
				else if (id == 69526)
					GnomeAgility.walkGnomeLog(player);
				else if (id == 69383)
					GnomeAgility.climbGnomeObstacleNet(player);
				else if (id == 69508)
					GnomeAgility.climbUpGnomeTreeBranch(player);
				else if (id == 69506)
					GnomeAgility.climbUpGnomeTreeBranch2(player);
				else if (id == 2312)
					GnomeAgility.walkGnomeRope(player);
				else if (id == 4059)
					GnomeAgility.walkBackGnomeRope(player);
				else if (id == 69507)
					GnomeAgility.climbDownGnomeTreeBranch(player);
				else if (id == 69384)
					GnomeAgility.climbGnomeObstacleNet2(player);
				else if (id == 65362)
					WildyAgility.PipeStart(player, object);
				else if (id == 64696)
					WildyAgility.swingOnRopeSwing(player, object);
				else if (id == 64698)
					WildyAgility.walkLog(player);
				else if (id == 64699)
					WildyAgility.crossSteppingPalletes(player, object);
				else if (id == 65734)
					WildyAgility.climbCliff(player, object);
				else if (id == 69377 || id == 69378)
					GnomeAgility.enterGnomePipe(player, object.getX(), object.getY());
				else if (WildernessControler.isDitch(id)) {// wild ditch
					
					if (((object.getRotation() == 0 || object.getRotation() == 2) && player.getY() < object.getY())
						|| (object.getRotation() == 1 || object.getRotation() == 3) && player.getX() > object.getX()) {
						player.getDialogueManager().startDialogue("WildernessDitch", object);
						return;
					} else {
						player.lock();
						player.animate(new Animation(6132));
						final WorldTile toTile = new WorldTile(
								object.getRotation() == 1 || object.getRotation() == 3 ? object.getX() + 1
										: player.getX(),
								object.getRotation() == 0 || object.getRotation() == 2 ? object.getY() - 1
										: player.getY(),
								object.getPlane());
						player.setNextForceMovement(new ForceMovement(new WorldTile(player), 1, toTile, 2,
								object.getRotation() == 0 || object.getRotation() == 2 ? ForceMovement.SOUTH
										: ForceMovement.EAST));
						WorldTasksManager.schedule(new WorldTask() {
							@Override
							public void run() {
								player.setNextWorldTile(toTile);
								player.faceObject(object);
								player.unlock();
							}
						}, 2);
						return;
					}
				} else if (id == 42611) {// Magic Portal
					player.getDialogueManager().startDialogue("MagicPortal");
					// } else if
					// (object.getDefinitions().name.equalsIgnoreCase("Obelisk")
					// && object.getY() > 3525) {
					// WildernessObelisk.handleObject(object, player);
				} else if (id >= 65616 && id <= 65622) {
					WildernessObelisk.activateObelisk(id, player);
				} else if (id == 27254) {// Edgeville portal
					player.getPackets().sendGameMessage("You enter the portal...");
					player.useStairs(10584, new WorldTile(3087, 3488, 0), 2, 3, "..and are transported to Edgeville.");
					player.addWalkSteps(1598, 4506, -1, false);
				} else if (id == 12202) {// mole entrance
					if (!player.getInventory().containsItem(952, 1)) {
						player.getPackets().sendGameMessage("You need a spade to dig this.");
						return;
					}
					if (player.getX() != object.getX() || player.getY() != object.getY()) {
						player.lock();
						player.addWalkSteps(object.getX(), object.getY());
						WorldTasksManager.schedule(new WorldTask() {
							@Override
							public void run() {
								InventoryOptionsHandler.dig(player);
							}

						}, 1);
					} else
						InventoryOptionsHandler.dig(player);
				} else if (id == 12230 && object.getX() == 1752 && object.getY() == 5136) {// mole
																							// exit
					player.setNextWorldTile(new WorldTile(2986, 3316, 0));
				} else if (id == 15522) {// portal sign
					if (player.withinDistance(new WorldTile(1598, 4504, 0), 1)) {// PORTAL
						// 1
						player.getInterfaceManager().sendInterface(327);
						player.getPackets().sendIComponentText(327, 13, "Edgeville");
						player.getPackets().sendIComponentText(327, 14, "This portal will take you to edgeville. There "
								+ "you can multi pk once past the wilderness ditch.");
					}
					if (player.withinDistance(new WorldTile(1598, 4508, 0), 1)) {// PORTAL
						// 2
						player.getInterfaceManager().sendInterface(327);
						player.getPackets().sendIComponentText(327, 13, "Mage Bank");
						player.getPackets().sendIComponentText(327, 14, "This portal will take you to the mage bank. "
								+ "The mage bank is a 1v1 deep wilderness area.");
					}
					if (player.withinDistance(new WorldTile(1598, 4513, 0), 1)) {// PORTAL
						// 3
						player.getInterfaceManager().sendInterface(327);
						player.getPackets().sendIComponentText(327, 13, "Magic's Portal");
						player.getPackets().sendIComponentText(327, 14,
								"This portal will allow you to teleport to areas that "
										+ "will allow you to change your magic spell book.");
					}
				} else if (id == 38811 || id == 37929) {// corp beast
					if (object.getX() == 2971 && object.getY() == 4382)
						player.getInterfaceManager().sendInterface(650);
					else if (object.getX() == 2918 && object.getY() == 4382) {
						player.stopAll();
						player.setNextWorldTile(
								new WorldTile(player.getX() == 2921 ? 2917 : 2921, player.getY(), player.getPlane()));
					}
				} else if (id == 37928 && object.getX() == 2883 && object.getY() == 4370) {
					player.stopAll();
					player.setNextWorldTile(new WorldTile(3214, 3782, 0));
					player.getControlerManager().startControler("WildernessControler");
				} else if (id == 38815 && object.getX() == 3209 && object.getY() == 3780 && object.getPlane() == 0) {
					if (player.getSkills().getLevelForXp(Skills.WOODCUTTING) < 37
							|| player.getSkills().getLevelForXp(Skills.MINING) < 45
							|| player.getSkills().getLevelForXp(Skills.SUMMONING) < 23
							|| player.getSkills().getLevelForXp(Skills.FIREMAKING) < 47
							|| player.getSkills().getLevelForXp(Skills.PRAYER) < 55) {
						player.getPackets().sendGameMessage(
								"You need 23 Summoning, 37 Woodcutting, 45 Mining, 47 Firemaking and 55 Prayer to enter this dungeon.");
						return;
					}
					player.stopAll();
					player.setNextWorldTile(new WorldTile(2885, 4372, 2));
					player.getControlerManager().forceStop();
					// TODO all reqs, skills not added
				} else if (id == 48803 && player.isKalphiteLairSetted()) {
					player.setNextWorldTile(new WorldTile(3508, 9494, 0));
				} else if (id == 48802 && player.isKalphiteLairEntranceSetted()) {
					player.setNextWorldTile(new WorldTile(3483, 9510, 2));
				} else if (id == 3829) {
					if (object.getX() == 3483 && object.getY() == 9510) {
						player.useStairs(828, new WorldTile(3226, 3108, 0), 1, 2);
					}
				} else if (id == 3832) {
					if (object.getX() == 3508 && object.getY() == 9494) {
						player.useStairs(828, new WorldTile(3509, 9496, 2), 1, 2);
					}
				} else if (id == 9369)
					player.getControlerManager().startControler("FightPits");
				else if (id == 1817 && object.getX() == 2273 && object.getY() == 4680) // kbd
																						// lever
					OldMagicSystem.pushLeverTeleport(player, new WorldTile(3067, 10254, 0));
				else if (id == 1816 && object.getX() == 3067 && object.getY() == 10252) // kbd
																						// out
																						// lever
					OldMagicSystem.pushLeverTeleport(player, new WorldTile(2273, 4681, 0));
				else if (id == 32015 && object.getX() == 3069 && object.getY() == 10256) { // kbd
																							// stairs
					player.useStairs(828, new WorldTile(3017, 3848, 0), 1, 2);
					player.getControlerManager().startControler("WildernessControler");
				} else if (id == 1765 && object.getX() == 3017 && object.getY() == 3849) { // kbd
																							// out
																							// stairs
					player.stopAll();
					player.setNextWorldTile(new WorldTile(3069, 10255, 0));
					player.getControlerManager().startControler("WildernessControler");
				} else if (id == 14315) {
					if (Lander.canEnter(player, 0))
						return;
				} else if (id == 25631) {
					if (Lander.canEnter(player, 1))
						return;
				} else if (id == 25632) {
					if (Lander.canEnter(player, 2))
						return;
				} else if (id == 5959) {
					OldMagicSystem.pushLeverTeleport(player, new WorldTile(2539, 4712, 0));
				} else if (id == 5960) {
					OldMagicSystem.pushLeverTeleport(player, new WorldTile(3089, 3957, 0));
				} else if (id == 1814) {
					player.getDialogueManager().startDialogue("WildyLever");
				} else if (id == 1815) {
					OldMagicSystem.pushLeverTeleport(player, new WorldTile(2561, 3311, 0));
				} else if (id == 62675)
					player.getCutscenesManager().play("DTPreview");
				/*
				 * else if (id == 62681) player.getDominionTower().viewScoreBoard();
				 */
				/*
				 * else if (id == 62678 || id == 62679) player.getDominionTower().openModes();
				 */
				else if (id == 62688)
					player.getDialogueManager().startDialogue("DTClaimRewards");
				/*
				 * else if (id == 62677) player.getDominionTower().talkToFace(); else if (id ==
				 * 62680) player.getDominionTower().openBankChest();
				 */
				else if (id == 48797)
					player.useStairs(-1, new WorldTile(3877, 5526, 1), 0, 1);
				else if (object.getId() >= 11552 && object.getId() <= 11557 || object.getId() == 4030) {
					player.getPackets().sendGameMessage("That rock is currently unavailable.");
					return;
				} else if (id == 48798)
					player.useStairs(-1, new WorldTile(3246, 3198, 0), 0, 1);
				else if (id == 48678 && x == 3858 && y == 5533)
					player.useStairs(-1, new WorldTile(3861, 5533, 0), 0, 1);
				else if (id == 48678 && x == 3858 && y == 5543)
					player.useStairs(-1, new WorldTile(3861, 5543, 0), 0, 1);
				else if (id == 48678 && x == 3858 && y == 5533)
					player.useStairs(-1, new WorldTile(3861, 5533, 0), 0, 1);
				else if (id == 48677 && x == 3858 && y == 5543)
					player.useStairs(-1, new WorldTile(3856, 5543, 1), 0, 1);
				else if (id == 48677 && x == 3858 && y == 5533)
					player.useStairs(-1, new WorldTile(3856, 5533, 1), 0, 1);
				else if (id == 48679)
					player.useStairs(-1, new WorldTile(3875, 5527, 1), 0, 1);
				else if (id == 48688)
					player.useStairs(-1, new WorldTile(3972, 5565, 0), 0, 1);
				else if (id == 48683)
					player.useStairs(-1, new WorldTile(3868, 5524, 0), 0, 1);
				else if (id == 48682)
					player.useStairs(-1, new WorldTile(3869, 5524, 0), 0, 1);
				else if (id == 62676) { // dominion exit
					player.useStairs(-1, new WorldTile(3374, 3093, 0), 0, 1);
					/*
					 * } else if (id == 62674) { // dominion entrance player.useStairs(-1, new
					 * WorldTile(3744, 6405, 0), 0, 1);
					 */
				} else if (id == 65349) {
					player.useStairs(-1, new WorldTile(3044, 10325, 0), 0, 1);
				} else if (id == 32048 && object.getX() == 3043 && object.getY() == 10328) {
					player.useStairs(-1, new WorldTile(3045, 3927, 0), 0, 1);
				} else if (id == 26194) {
					player.getDialogueManager().startDialogue("PartyRoomLever");
				} else if (id == 61190 || id == 61191 || id == 61192 || id == 61193) {
					if (objectDef.containsOption(0, "Chop down"))
						player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.NORMAL));
				} else if (id == 5103 || id == 5104 || id == 5105 || id == 5106 || id == 5107) {
					player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.VINES));
				} else if (id == 20573)
					player.getControlerManager().startControler("RefugeOfFear");
				else if (id == 67050)
					player.useStairs(-1, new WorldTile(3359, 6110, 0), 0, 1);
				else if (id == 67053)
					player.useStairs(-1, new WorldTile(3120, 3519, 0), 0, 1);
				else if (id == 67051)
					player.getDialogueManager().startDialogue("Marv", false);
				else if (id == 67052)
					Crucible.enterCrucibleEntrance(player);
				else if (id == 12266) {// trapdoor
					player.faceObject(object);
					player.setNextWorldTile(new WorldTile(3077, 9893, 0));
				} else if (id == 12265) // stairs
					player.setNextWorldTile(new WorldTile(3078, 3493, 0));
				else if (id == 29362)
					player.sm("Eew it's all slimey...");
				else if (id == 21600)
					handleDoor(player, object);
				else if (id == 26721 || id == 1317) {
					player.getDialogueManager().startDialogue("SpiritTree", id);
				} else if (id == 6435) {
					if (object.getX() == 3118)
						player.useStairs(827, new WorldTile(3118, 9643, 0), 1, 2);
					else
						player.useStairs(827, new WorldTile(3085, 9672, 0), 1, 2);
				} else if (id == 32015) {
					player.useStairs(828, new WorldTile(3084, 3273, 0), 1, 2);
				} else if (id == 26518) {
					player.useStairs(828, new WorldTile(3118, 3243, 0), 1, 2);
				} else if (objectDef.name.contains("rocks") || objectDef.name.equalsIgnoreCase("Rocks")) {
					if (object.getDefinitions().containsOption("Mine")) {
						player.getActionManager().setAction(new Mining(object));
					}
				} else if (id == 69829 || id == 69834 || id == 69835 || id == 69837 || id == 69840 || id == 69833) {
					player.activateLodeStone(object, player);

				} else if (objectDef.name.toLowerCase().contains("hay bale")) {
					if (!player.getInventory().hasFreeSlots() && player.getInventory().containsItem(1733, 1)) {
						player.getPackets().sendGameMessage("You need inventory space to search this.");
						return;
					}
					switch (Utils.getRandom(2)) {
					case 0:
						player.lock(3);
						player.getInventory().addItem(1733, 1);
						player.getPackets().sendGameMessage("Wow! A needle! Now what are the chances of finding that?");
						break;
					case 1:
						player.lock(3);
						player.getPackets().sendGameMessage("You find nothing of interest.");
						break;
					case 2:
						player.applyHit(new Hit(player, 10, HitLook.REGULAR_DAMAGE));
						player.getPackets().sendGameMessage("You hurt your finger by something pointy.");
						break;
					}
				} else {
					switch (objectDef.name.toLowerCase()) {
					case "crate":
					case "crates":
					case "boxes":
					case "bookcase":
						if (objectDef.containsOption(0, "Search"))
							player.getPackets().sendGameMessage(
									"You search the " + objectDef.name.toLowerCase() + " but find nothing.");
						break;
					case "trapdoor":
					case "manhole":
						if (objectDef.containsOption(0, "Open")) {
							WorldObject openedHole = new WorldObject(object.getId() + 1, object.getType(),
									object.getRotation(), object.getX(), object.getY(), object.getPlane());
							player.faceObject(openedHole);
							World.spawnObjectTemporary(openedHole, 60000);
							player.animate(new Animation(536));
						} else {
							player.sm("It won't budge!");
						}
						break;
					case "drawers":
						if (objectDef.containsOption(0, "Open")) {
							WorldObject openedDrawer = new WorldObject(getOpenId(object.getId()), object.getType(),
									object.getRotation(), object.getX(), object.getY(), object.getPlane());
							player.faceObject(openedDrawer);
							World.spawnObjectTemporary(openedDrawer, 60000);
							player.animate(new Animation(536));
						}
						if (objectDef.containsOption(0, "Search")) {
							player.getPackets().sendGameMessage("You search the drawers but find nothing.");
							return;
						}
						break;
					case "closed chest":
						if (objectDef.containsOption(0, "Open")) {
							player.animate(new Animation(536));
							player.lock(2);
							WorldObject openedChest = new WorldObject(object.getId() + 1, object.getType(),
									object.getRotation(), object.getX(), object.getY(), object.getPlane());
							player.faceObject(openedChest);
							World.spawnObjectTemporary(openedChest, 60000);
						}
						break;
					case "open chest":
						if (objectDef.containsOption(0, "Search"))
							player.getPackets().sendGameMessage("You search the chest but find nothing.");
						break;
					case "spiderweb":
						if (object.getRotation() == 2) {
							player.lock(2);
							if (Utils.getRandom(1) == 0) {
								player.addWalkSteps(player.getX(),
										player.getY() < y ? object.getY() + 2 : object.getY() - 1, -1, false);
								player.getPackets().sendGameMessage("You squeeze though the web.");
							} else
								player.getPackets().sendGameMessage(
										"You fail to squeeze though the web; perhaps you should try again.");
						}
						break;
					case "web":
						if (objectDef.containsOption(0, "Slash")) {
							slashWeb(player, object);
						}
						break;
					case "anvil":
						player.getDialogueManager().startDialogue("SimpleMessage",
								"Use a metal on the anvil in order to begin working with the metal.");
						break;
					case "bank deposit box":
						if (objectDef.containsOption(0, "Deposit"))
							player.getBank().openDepositBox();
						break;
					case "bank":
					case "bank chest":
					case "bank booth":
					case "counter":
						if (objectDef.containsOption(0, "Bank") || objectDef.containsOption(0, "Use"))
							player.getBank().openBank();
						break;
					// Woodcutting start
					case "vines":
						if (objectDef.containsOption(0, "Chop down"))
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.VINES));
						break;
					case "tree":
						if (objectDef.containsOption(0, "Chop down"))
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.NORMAL));
						break;
					case "evergreen":
						if (objectDef.containsOption(0, "Chop down"))
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.EVERGREEN));
						break;
					case "dead tree":
						if (objectDef.containsOption(0, "Chop down"))
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.DEAD));
						break;
					case "swamp tree":
						if (objectDef.containsOption(0, "Chop down"))
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.DEAD));
						break;
					case "oak":
						if (objectDef.containsOption(0, "Chop down"))
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.OAK));
						break;
					case "willow":
						if (objectDef.containsOption(0, "Chop down"))
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.WILLOW));
						break;
					case "raple tree":
					case "maple tree":
						if (objectDef.containsOption(0, "Chop down"))
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.MAPLE));
						break;
					case "ivy":
						if (objectDef.containsOption(0, "Chop"))
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.IVY));
						break;
					case "yew":
						if (objectDef.containsOption(0, "Chop down"))
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.YEW));
						break;
					case "magic tree":
						if (objectDef.containsOption(0, "Chop down"))
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.MAGIC));
						break;
					case "cursed magic tree":
						if (objectDef.containsOption(0, "Chop down"))
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.CURSED_MAGIC));
						break;
					case "bloodwood tree":
						if (objectDef.containsOption(0, "Chop down"))
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.BLOODWOOD));
						break;
					case "achey tree":
						if (objectDef.containsOption(0, "Chop"))
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.ACHEY));
						break;
					// Woodcutting end
					case "gate":
					case "large door":
					case "castle door":
					case "metal door":
					case "long hall door":
						if ((object.getId() == 29320 || object.getId() == 29319 || object.getId() == 2051
								|| object.getId() == 45856 || object.getId() == 45857 || object.getId() == 24561
								|| object.getId() == 24370 || object.getId() == 28691 || object.getId() == 24369)
								&& Settings.FREE_TO_PLAY) {
							player.getPackets().sendGameMessage("You can't acess this member area.");
							return;
						}
						if (objectDef.containsOption(0, "Open"))
							handleGate(player, object);
						break;
					case "door":
						if (object.getType() == 0
								&& (objectDef.containsOption(0, "Open") || objectDef.containsOption(0, "Unlock")))
							handleDoor(player, object);
						else if (object.getType() == 0 && objectDef.containsOption(0, "Close"))
							handleCloseDoor(player, object);
						break;
					case "ladder":
						handleLadder(player, object, 1);
						break;
					case "staircase":
					case "steps":
						handleStaircases(player, object, 1);
						break;
					case "stairs":
						handleStairs(player, object, 1);
						break;
					case "small obelisk":
						if (objectDef.containsOption(0, "Renew-points"))
							renewSummoningPoints(player);
						healFamiliar(player);
						break;
					case "obelisk":
						if (objectDef.containsOption(0, "Infuse-pouch"))
							Summoning.openInfusionInterface(player);
						break;
					case "altar":
					case "gorilla statue":
					case "chaos altar":
						if (objectDef.containsOption(0, "Pray") || objectDef.containsOption(0, "Pray-at")) {
							final int maxPrayer = player.getSkills().getLevelForXp(Skills.PRAYER) * 10;
							if (player.getPrayer().getPrayerpoints() < maxPrayer) {
								player.lock(1);
								player.getPackets().sendGameMessage("You pray to the gods...", true);
								player.getPrayer().restorePrayer(maxPrayer);
								player.getPackets().sendGameMessage("...and recharged your prayer.", true);
								player.animate(new Animation(645));
							} else
								player.getPackets().sendGameMessage("You already have full prayer.");
							if (id == 6552)
								player.getDialogueManager().startDialogue("AncientAltar");
						}
						break;

					case "locked door":
						player.sm("The door appears to be locked!");
						break;

					default:
						player.getPackets().sendGameMessage("Nothing interesting happens.");
						break;
					}
				}
				if (Settings.DEBUG)
					Logger.log("ObjectHandler",
							"clicked 1 at object id : " + id + ", " + object.getX() + ", " + object.getY() + ", "
									+ object.getPlane() + ", " + object.getType() + ", " + object.getRotation() + ", "
									+ object.getDefinitions().name);
			}
		}));
	}

	private static void handleOption2(final Player player, final WorldObject object) {
		final ObjectDefinitions objectDef = object.getDefinitions();
		final int id = object.getId();
		ObjectScript script = ObjectScriptsHandler.cachedObjectScripts.getOrDefault(object.getId(),
				ObjectScriptsHandler.cachedObjectScripts.get(objectDef.name));
		if (script != null) {
			if (script.getDistance() == 0) {
				player.setRouteEvent(new RouteEvent(object, new Runnable() {
					@Override
					public void run() {
						player.stopAll();
						player.faceObject(object);
						if (script.processObject2(player, object))
							return;
					}
				}, true));
				return;
			} else {
				// TODO route to script.getDistane()
			}
		}
		player.setRouteEvent(new RouteEvent(object, new Runnable() {
			@Override
			public void run() {
				player.stopAll();
				player.faceObject(object);

				if (!player.getControlerManager().processObjectClick2(object))
					return;

				if (id == 6) {
					DwarfMultiCannon.pickupCannon(player, 4, object);
					return;
				}
				if (GrotwormLair.handleObject2(object, player))
					return;
				if (player.getFarmingManager().isFarming(id, null, 2))
					return;
				if (object.getDefinitions().name.equalsIgnoreCase("furnace"))
					player.getDialogueManager().startDialogue("SmeltingD", object);
				else if (object.getDefinitions().name.toLowerCase().contains("spinning wheel"))
					player.getDialogueManager().startDialogue("SpinningWheelD", object);
				else if (object.getDefinitions().name.toLowerCase().contains("loom"))
					player.getDialogueManager().startDialogue("LoomD", object);
				else if (id == 17010)
					player.getDialogueManager().startDialogue("LunarAltar");
				else {
					switch (objectDef.name.toLowerCase()) {
					case "drawers":
						if (objectDef.containsOption(1, "Search"))
							player.getPackets().sendGameMessage("You search the drawers but find nothing.");
						if (objectDef.containsOption(1, "Close") || objectDef.containsOption(1, "Shut")) {
							player.faceObject(object);
							World.removeObject(object);
							player.animate(new Animation(537));
							return;
						}
						break;
					case "open chest":
						if (objectDef.containsOption(1, "Search"))
							player.getPackets().sendGameMessage("You search the chest but find nothing.");
						break;
					case "trapdoor":
					case "manhole":
						if (objectDef.containsOption(1, "Close")) {
							player.faceObject(object);
							World.removeObject(object);
							player.animate(new Animation(535));
						} else {
							player.sm("It won't budge!");
						}
						break;
					case "cabbage":
						if (objectDef.containsOption(1, "Pick") && player.getInventory().addItem(1965, 1)) {
							player.addWalkSteps(object.getX(), object.getY());
							player.animate(new Animation(827));
							player.lock(3);
							World.removeObjectTemporary(object, 30000, true);
						}
						break;
					case "wheat":
						if (objectDef.containsOption(1, "Pick") && player.getInventory().addItem(1947, 1)) {
							player.addWalkSteps(object.getX(), object.getY());
							player.animate(new Animation(827));
							player.lock(3);
							World.removeObjectTemporary(object, 30000, true);
						}
						break;
					case "potato":
						if (objectDef.containsOption(1, "Pick") && player.getInventory().addItem(1942, 1)) {
							player.addWalkSteps(object.getX(), object.getY());
							player.animate(new Animation(827));
							player.lock(3);
							World.removeObjectTemporary(object, 30000, true);
						}
						break;
					case "onion":
						if (objectDef.containsOption(1, "Pick") && player.getInventory().addItem(1957, 1)) {
							player.addWalkSteps(object.getX(), object.getY());
							player.animate(new Animation(827));
							player.lock(3);
							World.removeObjectTemporary(object, 30000, true);
						}
						break;
					case "flax":
						if (objectDef.containsOption(1, "Pick") && player.getInventory().addItem(1779, 1)) {
							player.animate(new Animation(827));
							player.lock(2);
							if (Utils.getRandom(3) == 0)
								World.removeObjectTemporary(object, 30000, true);
						}
						break;
					case "bank":
					case "bank booth":
					case "counter":
					case "bank chest":
						if (objectDef.containsOption(1, "Bank"))
							player.getBank().openBank();
						break;
					case "gates":
					case "gate":
					case "metal door":
					case "castle door":
					case "long hall door":
						if (object.getId() == 24369 && Settings.FREE_TO_PLAY) {
							player.getPackets().sendGameMessage("You can't acess this member area.");
							return;
						}
						if (object.getType() == 0 && objectDef.containsOption(1, "Open"))
							handleGate(player, object);
						break;
					case "door":
						if (object.getType() == 0 && objectDef.containsOption(1, "Open"))
							handleDoor(player, object);
						break;
					case "ladder":
						handleLadder(player, object, 2);
						break;
					case "staircase":
						handleStaircases(player, object, 2);
						break;
					default:
						player.getPackets().sendGameMessage("Nothing interesting happens.");
						break;
					}
				}
				if (Settings.DEBUG)
					Logger.log("ObjectHandler", "clicked 2 at object id : " + id + ", " + object.getX() + ", "
							+ object.getY() + ", " + object.getPlane());
			}
		}));
	}

	private static void handleOption3(final Player player, final WorldObject object) {
		final ObjectDefinitions objectDef = object.getDefinitions();
		final int id = object.getId();
		ObjectScript script = ObjectScriptsHandler.cachedObjectScripts.getOrDefault(object.getId(),
				ObjectScriptsHandler.cachedObjectScripts.get(objectDef.name));
		if (script != null) {
			if (script.getDistance() == 0) {
				player.setRouteEvent(new RouteEvent(object, new Runnable() {
					@Override
					public void run() {
						player.stopAll();
						player.faceObject(object);
						if (script.processObject3(player, object))
							return;
					}
				}, true));
				return;
			} else {
				// TODO route to script.getDistane()
			}
		}
		player.setRouteEvent(new RouteEvent(object, new Runnable() {
			@Override
			public void run() {
				player.stopAll();
				player.faceObject(object);
				if (!player.getControlerManager().processObjectClick3(object))
					return;
				if (GrotwormLair.handleObject3(object, player))
					return;
				if (player.getFarmingManager().isFarming(id, null, 3))
					return;
				switch (objectDef.name.toLowerCase()) {
				case "bank":
				case "bank chest":
				case "bank booth":
				case "counter":
					if (objectDef.containsOption(1, "Bank"))
						player.getGeManager().openCollectionBox();
					break;
				case "open chest":
					if (objectDef.containsOption(2, "Shut") && object.getX() != 3185 && object.getY() != 3274) {
						player.faceObject(object);
						World.removeObject(object);
						player.animate(new Animation(537));
					} else {
						player.sm("It won't budge!");
					}
					break;
				case "drawers":
					if (objectDef.containsOption(2, "Close") || objectDef.containsOption(2, "Shut")) {
						player.faceObject(object);
						World.removeObject(object);
						player.animate(new Animation(537));
					} else {
						player.sm("It won't budge!");
					}
					break;
				case "gate":
				case "metal door":
				case "castle door":
				case "long hall door":
					if (object.getType() == 0 && objectDef.containsOption(2, "Open"))
						handleGate(player, object);
					else if (object.getType() == 0 && objectDef.containsOption(2, "Close"))
						handleCloseGate(player, object);
					break;

				case "door":
					if (object.getType() == 0 && objectDef.containsOption(2, "Open"))
						handleDoor(player, object);
					break;
				case "ladder":
					handleLadder(player, object, 3);
					break;
				case "staircase":
					handleStaircases(player, object, 3);
					break;
				default:
					player.getPackets().sendGameMessage("Nothing interesting happens.");
					break;
				}
				if (Settings.DEBUG)
					Logger.log("ObjectHandler", "cliked 3 at object id : " + id + ", " + object.getX() + ", "
							+ object.getY() + ", " + object.getPlane() + ", ");
			}
		}));
	}

	private static void handleOption4(final Player player, final WorldObject object) {
		final ObjectDefinitions objectDef = object.getDefinitions();
		final int id = object.getId();
		ObjectScript script = ObjectScriptsHandler.cachedObjectScripts.getOrDefault(object.getId(),
				ObjectScriptsHandler.cachedObjectScripts.get(objectDef.name));
		if (script != null) {
			if (script.getDistance() == 0) {
				player.setRouteEvent(new RouteEvent(object, new Runnable() {
					@Override
					public void run() {
						player.stopAll();
						player.faceObject(object);
						if (script.processObject4(player, object))
							return;
					}
				}, true));
				return;
			} else {
				// TODO route to script.getDistane()
			}
		}
		player.setRouteEvent(new RouteEvent(object, new Runnable() {
			@Override
			public void run() {
				player.stopAll();
				player.faceObject(object);
				if (!player.getControlerManager().processObjectClick4(object))
					return;
				if (GrotwormLair.handleObject4(object, player))
					return;
				if (player.getFarmingManager().isFarming(id, null, 4))
					return;
				// living rock Caverns
				if (id == 45076)
					MiningBase.propect(player, "This rock contains a large concentration of gold.");
				else if (id == 5999)
					MiningBase.propect(player, "This rock contains a large concentration of coal.");
				else {
					switch (objectDef.name.toLowerCase()) {
					default:
						player.getPackets().sendGameMessage("Nothing interesting happens.");
						break;
					}
				}
				if (Settings.DEBUG)
					Logger.log("ObjectHandler", "cliked 4 at object id : " + id + ", " + object.getX() + ", "
							+ object.getY() + ", " + object.getPlane() + ", ");
			}
		}));
	}

	private static void handleOption5(final Player player, final WorldObject object) {
		final ObjectDefinitions objectDef = object.getDefinitions();
		final int id = object.getId();
		ObjectScript script = ObjectScriptsHandler.cachedObjectScripts.getOrDefault(object.getId(),
				ObjectScriptsHandler.cachedObjectScripts.get(objectDef.name));
		if (script != null) {
			if (script.getDistance() == 0) {
				player.setRouteEvent(new RouteEvent(object, new Runnable() {
					@Override
					public void run() {
						player.stopAll();
						player.faceObject(object);
						if (script.processObject5(player, object))
							return;
					}
				}, true));
				return;
			} else {
				// TODO route to script.getDistane()
			}
		}
		if (object.getId() >= HouseConstants.HObject.WOOD_BENCH.getId()
				&& object.getId() <= HouseConstants.HObject.GILDED_BENCH.getId()) {
			player.setRouteEvent(new RouteEvent(object, new Runnable() {
				@Override
				public void run() {
					player.sm("test");
					player.getControlerManager().processObjectClick5(object);
				}
			}, true));
			return;
		}
		player.setRouteEvent(new RouteEvent(object, new Runnable() {
			@Override
			public void run() {
				player.stopAll();
				player.faceObject(object);
				if (!player.getControlerManager().processObjectClick5(object)) {
					return;
				}
				switch (objectDef.name.toLowerCase()) {
				case "fire":
					if (objectDef.containsOption(4, "Add-logs"))
						Bonfire.addLogs(player, object);
					break;
				default:
					player.getPackets().sendGameMessage("Nothing interesting happens.");
					break;
				}
				if (Settings.DEBUG)
					Logger.log("ObjectHandler", "cliked 5 at object id : " + id + ", " + object.getX() + ", "
							+ object.getY() + ", " + object.getPlane() + ", ");
			}
		}));
	}

	private static void handleOptionExamine(final Player player, final WorldObject object) {
		if (Settings.DEBUG) {
			int offsetX = object.getX() - player.getX();
			int offsetY = object.getY() - player.getY();
			System.out.println("Offsets" + offsetX + " , " + offsetY);
		}
		if (object.getId() == 29735) {
			player.useStairs(-1, new WorldTile(3140, 4230, 2), 1, 2);
			player.getPackets().sendGameMessage("You find a secret passage.");
			return;
		}
		player.getPackets().sendObjectMessage(0, object,
				"It's " + player.grammar(object) + " " + object.getDefinitions().name + ".");

		if (Settings.DEBUG)
			Logger.log("ObjectHandler",
					"examined object id : " + object.getId() + ", x" + object.getX() + ", y" + object.getY() + ", z"
							+ object.getPlane() + ", t" + object.getType() + ", r" + object.getRotation() + ", "
							+ object.getDefinitions().name);
	}

	private static void slashWeb(Player player, WorldObject object) {
		boolean usingKnife = false;
		int defs = CombatDefinitions.getMeleeBonusStyle(player.getEquipment().getWeaponId(), 0);
		int defs2 = CombatDefinitions.getMeleeBonusStyle(player.getEquipment().getWeaponId(), 1);
		int defs3 = CombatDefinitions.getMeleeBonusStyle(player.getEquipment().getWeaponId(), 2);

		if (defs != CombatDefinitions.SLASH_ATTACK && defs2 != CombatDefinitions.SLASH_ATTACK
				&& defs3 != CombatDefinitions.SLASH_ATTACK) {
			if (!player.getInventory().containsItem(946, 1)) {
				player.getPackets().sendGameMessage("You need something sharp to cut this with.");
				return;
			}
			usingKnife = true;
		}

		int weaponEmote = PlayerCombat.getWeaponAttackEmote(player.getEquipment().getWeaponId(),
				player.getCombatDefinitions().getAttackStyle());
		int knifeEmote = -1;

		player.animate(new Animation(usingKnife ? knifeEmote : weaponEmote));

		if (Utils.getRandom(1) == 0) {
			World.spawnObjectTemporary(new WorldObject(object.getId() + 1, object.getType(), object.getRotation(),
					object.getX(), object.getY(), object.getPlane()), 60000);
			player.getPackets().sendGameMessage("You slash through the web!");
		} else
			player.getPackets().sendGameMessage("You fail to cut through the web.");
	}

	private static boolean handleGateTemporary(Player player, WorldObject object, int timer) {
		if (object.getRotation() == 0) {
			boolean south = true;
			WorldObject otherDoor = World.getObjectWithType(
					new WorldTile(object.getX(), object.getY() + 1, object.getPlane()), object.getType());
			if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
					|| otherDoor.getType() != object.getType()
					|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name)) {
				otherDoor = World.getObjectWithType(new WorldTile(object.getX(), object.getY() - 1, object.getPlane()),
						object.getType());
				if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
						|| otherDoor.getType() != object.getType()
						|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name))
					return false;
				south = false;
			}
			WorldObject openedDoor1 = new WorldObject(object.getId(), object.getType(), object.getRotation(),
					object.getX(), object.getY(), object.getPlane());
			WorldObject openedDoor2 = new WorldObject(otherDoor.getId(), otherDoor.getType(), otherDoor.getRotation(),
					otherDoor.getX(), otherDoor.getY(), otherDoor.getPlane());
			if (south) {
				openedDoor1.moveLocation(-1, 0, 0);
				openedDoor2.moveLocation(-1, 0, 0);
				openedDoor1.setRotation(3);
				openedDoor2.setRotation(1);
				openedDoor1.setId(getClosedGateId(openedDoor1.getId()));
				openedDoor2.setId(getClosedGateId(openedDoor2.getId()));
			} else {
				openedDoor1.moveLocation(-1, 0, 0);
				openedDoor2.moveLocation(-1, 0, 0);
				openedDoor1.setRotation(1);
				openedDoor2.setRotation(3);
				openedDoor1.setId(getClosedGateId(openedDoor1.getId()));
				openedDoor2.setId(getClosedGateId(openedDoor2.getId()));
			}
			if (World.removeObjectTemporary(object, timer, false)
					&& World.removeObjectTemporary(otherDoor, timer, false)) {
				player.faceObject(openedDoor1);
				World.spawnObjectTemporary(openedDoor1, timer);
				World.spawnObjectTemporary(openedDoor2, timer);
				return true;
			}
		} else if (object.getRotation() == 2) {

			boolean south = true;
			WorldObject otherDoor = World.getObjectWithType(
					new WorldTile(object.getX(), object.getY() + 1, object.getPlane()), object.getType());
			if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
					|| otherDoor.getType() != object.getType()
					|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name)) {
				otherDoor = World.getObjectWithType(new WorldTile(object.getX(), object.getY() - 1, object.getPlane()),
						object.getType());
				if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
						|| otherDoor.getType() != object.getType()
						|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name))
					return false;
				south = false;
			}
			WorldObject openedDoor1 = new WorldObject(object.getId(), object.getType(), object.getRotation(),
					object.getX(), object.getY(), object.getPlane());
			WorldObject openedDoor2 = new WorldObject(otherDoor.getId(), otherDoor.getType(), otherDoor.getRotation(),
					otherDoor.getX(), otherDoor.getY(), otherDoor.getPlane());
			if (south) {
				openedDoor1.moveLocation(1, 0, 0);
				openedDoor2.moveLocation(1, 0, 0);
				openedDoor1.setRotation(3);
				openedDoor2.setRotation(1);
				openedDoor1.setId(getClosedGateId(openedDoor1.getId()));
				openedDoor2.setId(getClosedGateId(openedDoor2.getId()));
			} else {
				openedDoor1.moveLocation(1, 0, 0);
				openedDoor2.moveLocation(1, 0, 0);
				openedDoor1.setRotation(1);
				openedDoor2.setRotation(3);
				openedDoor1.setId(getClosedGateId(openedDoor1.getId()));
				openedDoor2.setId(getClosedGateId(openedDoor2.getId()));
			}
			if (World.removeObjectTemporary(object, timer, false)
					&& World.removeObjectTemporary(otherDoor, timer, false)) {
				player.faceObject(openedDoor1);
				World.spawnObjectTemporary(openedDoor1, timer);
				World.spawnObjectTemporary(openedDoor2, timer);
				return true;
			}
		} else if (object.getRotation() == 3) {

			boolean right = true;
			WorldObject otherDoor = World.getObjectWithType(
					new WorldTile(object.getX() - 1, object.getY(), object.getPlane()), object.getType());
			if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
					|| otherDoor.getType() != object.getType()
					|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name)) {
				otherDoor = World.getObjectWithType(new WorldTile(object.getX() + 1, object.getY(), object.getPlane()),
						object.getType());
				if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
						|| otherDoor.getType() != object.getType()
						|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name))
					return false;
				right = false;
			}
			WorldObject openedDoor1 = new WorldObject(object.getId(), object.getType(), object.getRotation(),
					object.getX(), object.getY(), object.getPlane());
			WorldObject openedDoor2 = new WorldObject(otherDoor.getId(), otherDoor.getType(), otherDoor.getRotation(),
					otherDoor.getX(), otherDoor.getY(), otherDoor.getPlane());
			if (right) {
				openedDoor1.moveLocation(0, -1, 0);
				openedDoor2.moveLocation(0, -1, 0);
				openedDoor1.setRotation(2);
				openedDoor2.setRotation(0);
				openedDoor1.setId(getClosedGateId(openedDoor1.getId()));
				openedDoor2.setId(getClosedGateId(openedDoor2.getId()));
			} else {
				openedDoor1.moveLocation(0, -1, 0);
				openedDoor2.moveLocation(0, -1, 0);
				openedDoor1.setRotation(0);
				openedDoor2.setRotation(2);
				openedDoor1.setId(getClosedGateId(openedDoor1.getId()));
				openedDoor2.setId(getClosedGateId(openedDoor2.getId()));
			}
			if (World.removeObjectTemporary(object, timer, false)
					&& World.removeObjectTemporary(otherDoor, timer, false)) {
				player.faceObject(openedDoor1);
				World.spawnObjectTemporary(openedDoor1, timer);
				World.spawnObjectTemporary(openedDoor2, timer);
				return true;
			}
		} else if (object.getRotation() == 1) {

			boolean right = true;
			WorldObject otherDoor = World.getObjectWithType(
					new WorldTile(object.getX() - 1, object.getY(), object.getPlane()), object.getType());
			if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
					|| otherDoor.getType() != object.getType()
					|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name)) {
				otherDoor = World.getObjectWithType(new WorldTile(object.getX() + 1, object.getY(), object.getPlane()),
						object.getType());
				if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
						|| otherDoor.getType() != object.getType()
						|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name))
					return false;
				right = false;
			}
			WorldObject openedDoor1 = new WorldObject(object.getId(), object.getType(), object.getRotation(),
					object.getX(), object.getY(), object.getPlane());
			WorldObject openedDoor2 = new WorldObject(otherDoor.getId(), otherDoor.getType(), otherDoor.getRotation(),
					otherDoor.getX(), otherDoor.getY(), otherDoor.getPlane());
			if (right) {
				openedDoor1.moveLocation(0, 1, 0);
				openedDoor2.moveLocation(0, 1, 0);
				openedDoor1.setRotation(2);
				openedDoor2.setRotation(0);
				openedDoor1.setId(getClosedGateId(openedDoor1.getId()));
				openedDoor2.setId(getClosedGateId(openedDoor2.getId()));
			} else {
				openedDoor1.moveLocation(0, 1, 0);
				openedDoor2.moveLocation(0, 1, 0);
				openedDoor1.setRotation(0);
				openedDoor2.setRotation(2);
				openedDoor1.setId(getClosedGateId(openedDoor1.getId()));
				openedDoor2.setId(getClosedGateId(openedDoor2.getId()));
			}
			if (World.removeObjectTemporary(object, timer, false)
					&& World.removeObjectTemporary(otherDoor, timer, false)) {
				player.faceObject(openedDoor1);
				World.spawnObjectTemporary(openedDoor1, timer);
				World.spawnObjectTemporary(openedDoor2, timer);
				if (player.getY() == object.getY())
					player.addWalkSteps(player.getX(), player.getY() + 1, 1, false);
				if (player.getY() > object.getY())
					player.addWalkSteps(player.getX(), player.getY() - 1, 1, false);
				return true;
			}
		}
		return false;
	}

	public static int getClosedGateId(int id) {
		switch (id) {
		case 11718:
			return 11717;

		case 11716:
		case 11717:
			return 11722;
		case 11719:
		case 11721:
			return 11723;
		case 24369:
			return 24373;
		case 24370:
			return 24374;
		}
		return id;
	}

	public static int getOpenedGateId(int id) {
		switch (id) {
		case 11722:
			return 11716;
		case 11723:
			return 11721;
		case 24369:
			return 24373;
		case 24370:
			return 24374;
		}
		return id;
	}

	private static boolean handleGate(Player player, WorldObject object) {
		if (World.isSpawnedObject(object))
			return false;
		int doorDelay = 60000;
		if (object.getId() == 65386) {
			WorldObject openedDoor1 = new WorldObject(object.getId(), object.getType(), object.getRotation(),
					object.getX(), object.getY(), object.getPlane());
			openedDoor1.moveLocation(-1, 0, 0);
			openedDoor1.setRotation(3);
			openedDoor1.setId(getClosedGateId(openedDoor1.getId()));
			if (World.removeObjectTemporary(object, doorDelay, true)) {
				player.faceObject(openedDoor1);
				World.spawnObjectTemporary(openedDoor1, doorDelay);
				return true;
			}
			return true;
		}
		if (object.getRotation() == 0) {
			boolean south = true;
			WorldObject otherDoor = World.getObjectWithType(
					new WorldTile(object.getX(), object.getY() + 1, object.getPlane()), object.getType());
			if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
					|| otherDoor.getType() != object.getType()
					|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name)) {
				otherDoor = World.getObjectWithType(new WorldTile(object.getX(), object.getY() - 1, object.getPlane()),
						object.getType());
				if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
						|| otherDoor.getType() != object.getType()
						|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name))
					return false;
				south = false;
			}
			WorldObject openedDoor1 = new WorldObject(object.getId(), object.getType(), object.getRotation(),
					object.getX(), object.getY(), object.getPlane());
			WorldObject openedDoor2 = new WorldObject(otherDoor.getId(), otherDoor.getType(), otherDoor.getRotation(),
					otherDoor.getX(), otherDoor.getY(), otherDoor.getPlane());
			if (south) {
				openedDoor1.moveLocation(-1, 0, 0);
				openedDoor2.moveLocation(-1, 0, 0);
				openedDoor1.setRotation(3);
				openedDoor2.setRotation(1);
				openedDoor1.setId(getClosedGateId(openedDoor1.getId()));
				openedDoor2.setId(getClosedGateId(openedDoor2.getId()));
			} else {
				openedDoor1.moveLocation(-1, 0, 0);
				openedDoor2.moveLocation(-1, 0, 0);
				openedDoor1.setRotation(1);
				openedDoor2.setRotation(3);
				openedDoor1.setId(getClosedGateId(openedDoor1.getId()));
				openedDoor2.setId(getClosedGateId(openedDoor2.getId()));
			}
			if (World.removeObjectTemporary(object, doorDelay, true)
					&& World.removeObjectTemporary(otherDoor, doorDelay, true)) {
				player.faceObject(openedDoor1);
				World.spawnObjectTemporary(openedDoor1, doorDelay);
				World.spawnObjectTemporary(openedDoor2, doorDelay);
				return true;
			}
		} else if (object.getRotation() == 2) {
			boolean south = true;
			WorldObject otherDoor = World.getObjectWithType(
					new WorldTile(object.getX(), object.getY() + 1, object.getPlane()), object.getType());
			if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
					|| otherDoor.getType() != object.getType()
					|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name)) {
				otherDoor = World.getObjectWithType(new WorldTile(object.getX(), object.getY() - 1, object.getPlane()),
						object.getType());
				if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
						|| otherDoor.getType() != object.getType()
						|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name))
					return false;
				south = false;
			}
			WorldObject openedDoor1 = new WorldObject(object.getId(), object.getType(), object.getRotation(),
					object.getX(), object.getY(), object.getPlane());
			WorldObject openedDoor2 = new WorldObject(otherDoor.getId(), otherDoor.getType(), otherDoor.getRotation(),
					otherDoor.getX(), otherDoor.getY(), otherDoor.getPlane());
			if (south) {
				openedDoor1.moveLocation(1, 0, 0);
				openedDoor2.moveLocation(1, 0, 0);
				openedDoor1.setRotation(3);
				openedDoor2.setRotation(1);
				openedDoor1.setId(getClosedGateId(openedDoor1.getId()));
				openedDoor2.setId(getClosedGateId(openedDoor2.getId()));
			} else {
				openedDoor1.moveLocation(1, 0, 0);
				openedDoor2.moveLocation(1, 0, 0);
				openedDoor1.setRotation(1);
				openedDoor2.setRotation(3);
				openedDoor1.setId(getClosedGateId(openedDoor1.getId()));
				openedDoor2.setId(getClosedGateId(openedDoor2.getId()));
			}
			if (World.removeObjectTemporary(object, doorDelay, true)
					&& World.removeObjectTemporary(otherDoor, doorDelay, true)) {
				player.faceObject(openedDoor1);
				World.spawnObjectTemporary(openedDoor1, doorDelay);
				World.spawnObjectTemporary(openedDoor2, doorDelay);
				return true;
			}
		} else if (object.getRotation() == 3) {

			boolean right = true;
			WorldObject otherDoor = World.getObjectWithType(
					new WorldTile(object.getX() - 1, object.getY(), object.getPlane()), object.getType());
			if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
					|| otherDoor.getType() != object.getType()
					|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name)) {
				otherDoor = World.getObjectWithType(new WorldTile(object.getX() + 1, object.getY(), object.getPlane()),
						object.getType());
				if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
						|| otherDoor.getType() != object.getType()
						|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name))
					return false;
				right = false;
			}
			WorldObject openedDoor1 = new WorldObject(object.getId(), object.getType(), object.getRotation(),
					object.getX(), object.getY(), object.getPlane());
			WorldObject openedDoor2 = new WorldObject(otherDoor.getId(), otherDoor.getType(), otherDoor.getRotation(),
					otherDoor.getX(), otherDoor.getY(), otherDoor.getPlane());
			if (right) {
				openedDoor1.moveLocation(0, -1, 0);
				openedDoor2.moveLocation(0, -1, 0);
				openedDoor1.setRotation(2);
				openedDoor2.setRotation(0);
				openedDoor1.setId(getClosedGateId(openedDoor1.getId()));
				openedDoor2.setId(getClosedGateId(openedDoor2.getId()));
			} else {
				openedDoor1.moveLocation(0, -1, 0);
				openedDoor2.moveLocation(0, -1, 0);
				openedDoor1.setRotation(0);
				openedDoor2.setRotation(2);
				openedDoor1.setId(getClosedGateId(openedDoor1.getId()));
				openedDoor2.setId(getClosedGateId(openedDoor2.getId()));
			}
			if (World.removeObjectTemporary(object, doorDelay, true)
					&& World.removeObjectTemporary(otherDoor, doorDelay, true)) {
				player.faceObject(openedDoor1);
				World.spawnObjectTemporary(openedDoor1, doorDelay);
				World.spawnObjectTemporary(openedDoor2, doorDelay);
				return true;
			}
		} else if (object.getRotation() == 1) {

			boolean right = true;
			WorldObject otherDoor = World.getObjectWithType(
					new WorldTile(object.getX() - 1, object.getY(), object.getPlane()), object.getType());
			if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
					|| otherDoor.getType() != object.getType()
					|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name)) {
				otherDoor = World.getObjectWithType(new WorldTile(object.getX() + 1, object.getY(), object.getPlane()),
						object.getType());
				if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
						|| otherDoor.getType() != object.getType()
						|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name))
					return false;
				right = false;
			}
			WorldObject openedDoor1 = new WorldObject(object.getId(), object.getType(), object.getRotation(),
					object.getX(), object.getY(), object.getPlane());
			WorldObject openedDoor2 = new WorldObject(otherDoor.getId(), otherDoor.getType(), otherDoor.getRotation(),
					otherDoor.getX(), otherDoor.getY(), otherDoor.getPlane());
			if (right) {
				openedDoor1.moveLocation(0, 1, 0);
				openedDoor2.moveLocation(0, 1, 0);
				openedDoor1.setRotation(2);
				openedDoor2.setRotation(0);
				openedDoor1.setId(getClosedGateId(openedDoor1.getId()));
				openedDoor2.setId(getClosedGateId(openedDoor2.getId()));
			} else {
				openedDoor1.moveLocation(0, 1, 0);
				openedDoor2.moveLocation(0, 1, 0);
				openedDoor1.setRotation(0);
				openedDoor2.setRotation(2);
				openedDoor1.setId(getClosedGateId(openedDoor1.getId()));
				openedDoor2.setId(getClosedGateId(openedDoor2.getId()));
			}
			if (World.removeObjectTemporary(object, doorDelay, true)
					&& World.removeObjectTemporary(otherDoor, doorDelay, true)) {
				player.faceObject(openedDoor1);
				World.spawnObjectTemporary(openedDoor1, doorDelay);
				World.spawnObjectTemporary(openedDoor2, doorDelay);
				return true;
			}
		}
		return false;
	}

	private static boolean handleCloseGate(Player player, WorldObject object) {
		if (object.getRotation() == 1) {
			WorldObject otherDoor = World.getObjectWithType(
					new WorldTile(object.getX(), object.getY() - 1, object.getPlane()), object.getType());
			WorldObject closedDoor1 = new WorldObject(object.getId(), object.getType(), object.getRotation(),
					object.getX(), object.getY(), object.getPlane());
			WorldObject closedDoor2 = new WorldObject(otherDoor.getId(), otherDoor.getType(), otherDoor.getRotation(),
					otherDoor.getX(), otherDoor.getY(), otherDoor.getPlane());
			closedDoor1.moveLocation(-1, 0, 0);
			closedDoor1.setRotation(0);
			closedDoor1.setId(getOpenedGateId(closedDoor1.getId()));
			player.faceObject(object);
			World.removeObject(object);
			World.removeObject(otherDoor);
			World.spawnObject(closedDoor1);
			World.spawnObject(closedDoor2);
			return true;
		}
		if (object.getRotation() == 3) {
			WorldObject otherDoor = World.getObjectWithType(
					new WorldTile(object.getX(), object.getY() + 1, object.getPlane()), object.getType());
			WorldObject closedDoor1 = new WorldObject(object.getId(), object.getType(), object.getRotation(),
					object.getX(), object.getY(), object.getPlane());
			WorldObject closedDoor2 = new WorldObject(otherDoor.getId(), otherDoor.getType(), otherDoor.getRotation(),
					otherDoor.getX(), otherDoor.getY(), otherDoor.getPlane());
			if (player.getX() < closedDoor1.getX()) {
				closedDoor1.moveLocation(-1, 0, 0);
				closedDoor2.moveLocation(-1, 0, 0);
				closedDoor1.setRotation(0);
				closedDoor2.setRotation(0);
				closedDoor1.setId(getOpenedGateId(closedDoor1.getId()));
				closedDoor2.setId(getOpenedGateId(closedDoor2.getId()));
			} else if (player.getX() > closedDoor1.getX()) {
				closedDoor1.moveLocation(1, 0, 0);
				closedDoor2.moveLocation(1, 0, 0);
				closedDoor1.setRotation(0);
				closedDoor2.setRotation(0);
				closedDoor1.setId(getOpenedGateId(closedDoor1.getId()));
				closedDoor2.setId(getOpenedGateId(closedDoor2.getId()));
			}
			player.faceObject(object);
			World.removeObject(object);
			World.removeObject(otherDoor);
			World.spawnObject(closedDoor1);
			World.spawnObject(closedDoor2);
			return true;
		}
		return false;
	}

	public static boolean handleCloseDoor(Player player, WorldObject object) {
		WorldObject closedDoor = new WorldObject(object.getId() - 1, object.getType(), object.getRotation() - 1,
				object.getX(), object.getY(), object.getPlane());
		if (object.getId() == 11715 && (object.getX() == 2959 && object.getY() == 3334 && object.getPlane() == 0)
				|| (object.getX() == 2960 && object.getY() == 3343 && object.getPlane() == 0)) {
			player.getPackets().sendGameMessage("It won't budge!");
			return false;
		}
		if (object.getId() == 24375 || object.getId() == 15535 || (object.getX() == 2966 && object.getY() == 3328)
				|| (object.getX() == 2978 && object.getY() == 3330) || (object.getX() == 2988 && object.getY() == 3334)
				|| (object.getX() == 2980 && object.getY() == 3316) || (object.getX() == 3027 && object.getY() == 3379)
				|| (object.getX() == 2977 && object.getY() == 3373) || (object.getX() == 2971 && object.getY() == 3376)
				|| (object.getX() == 2961 && object.getY() == 3372) || (object.getX() == 2956 && object.getY() == 3378)
				|| (object.getX() == 2958 && object.getY() == 3385) || (object.getX() == 2950 && object.getY() == 3385)
				|| (object.getX() == 2945 && object.getY() == 3337) || (object.getX() == 2972 && object.getY() == 3314)
				|| (object.getX() == 3034 && object.getY() == 3290)
				|| (object.getX() == 3092 && object.getY() == 3287)) {
			if (World.removeObjectTemporary(object, 60000, true)) {
				if (object.getRotation() == 0) {
					closedDoor.setRotation(3);
					closedDoor.moveLocation(0, 1, 0);
				} else if (object.getRotation() == 1) {
					closedDoor.moveLocation(1, 0, 0);
				} else if (object.getRotation() == 2) {
					closedDoor.moveLocation(0, -1, 0);
				} else if (object.getRotation() == 3) {
					closedDoor.moveLocation(-1, 0, 0);
				} else if (object.getRotation() == 4) {
					closedDoor.moveLocation(0, 1, 0);
				}
				if (object.getId() == 15535)
					closedDoor.setId(15536);
				else if (object.getId() == 24375)
					closedDoor.setId(24376);
				player.faceObject(closedDoor);
				World.spawnObjectTemporary(closedDoor, 60000);
				return false;
			}
		}
		if (object.getRotation() == 0) {
			closedDoor.moveLocation(0, 1, 0);
		} else if (object.getRotation() == 1) {
			closedDoor.moveLocation(1, 0, 0);
		} else if (object.getRotation() == 2) {
			closedDoor.moveLocation(0, -1, 0);
		} else if (object.getRotation() == 3) {
			closedDoor.moveLocation(-1, 0, 0);
		} else if (object.getRotation() == 4) {
			closedDoor.moveLocation(0, 1, 0);
		}
		World.removeObject(object);
		player.faceObject(closedDoor);
		World.spawnObject(closedDoor);
		return true;
	}

	public static boolean handle2DoorTemporary(Player player, WorldObject object, long timer) {
		if (World.isSpawnedObject(object))
			return false;
		WorldObject otherDoor = World.getObjectWithType(
				new WorldTile(object.getX(), object.getY() - 1, object.getPlane()), object.getType());
		WorldObject otherDoor2 = World.getObjectWithType(
				new WorldTile(object.getX(), object.getY() + 1, object.getPlane()), object.getType());
		WorldObject openedDoor = new WorldObject(object.getId(), object.getType(), object.getRotation() + 1,
				object.getX(), object.getY(), object.getPlane());
		WorldObject openedDoor2 = new WorldObject(otherDoor.getId(), otherDoor.getType(), otherDoor.getRotation() + 1,
				otherDoor.getX(), otherDoor.getY(), otherDoor.getPlane());
		WorldObject openedDoor3 = new WorldObject(otherDoor2.getId(), otherDoor2.getType(),
				otherDoor2.getRotation() + 1, otherDoor2.getX(), otherDoor2.getY(), otherDoor2.getPlane());
		if (object.getRotation() == 0) {
			if (object.getId() == 35549)
				openedDoor.setRotation(3);
			openedDoor.moveLocation(-1, 0, 0);
			if (player.getX() == object.getX())
				player.addWalkSteps(player.getX() - 1, player.getY(), 1, false);
			if (player.getX() < object.getX())
				player.addWalkSteps(player.getX() + 1, player.getY(), 1, false);
		} else if (object.getRotation() == 1) {
			if (player.getY() == object.getY())
				player.addWalkSteps(player.getX(), player.getY() + 1, 1, false);
			if (player.getY() > object.getY())
				player.addWalkSteps(player.getX(), player.getY() - 1, 1, false);
			openedDoor.moveLocation(0, 1, 0);
		} else if (object.getRotation() == 2) {
			openedDoor.moveLocation(1, 0, 0);
		} else if (object.getRotation() == 3) {
			openedDoor.moveLocation(0, -1, 0);
		}
		if (object.getId() == 35551 && player.getY() < object.getY()) {
			openedDoor2.moveLocation(-1, 0, 0);
			openedDoor2.setRotation(3);
			if (World.removeObjectTemporary(otherDoor, timer, false)) {
				player.faceObject(openedDoor2);
				World.spawnObjectTemporary(openedDoor2, timer);
				return true;
			}
		}
		if (object.getId() == 35549 && player.getY() > object.getY()) {
			openedDoor3.moveLocation(-1, 0, 0);
			openedDoor3.setRotation(1);
			if (World.removeObjectTemporary(otherDoor2, timer, false)) {
				player.faceObject(openedDoor3);
				World.spawnObjectTemporary(openedDoor3, timer);
				return true;
			}
		}
		if (World.removeObjectTemporary(object, timer, false)) {
			player.faceObject(openedDoor);
			World.spawnObjectTemporary(openedDoor, timer);
			return true;
		}
		return false;
	}

	public static boolean handleDoorTemporary(Player player, WorldObject object, long timer) {
		if (World.isSpawnedObject(object))
			return false;
		WorldObject openedDoor = new WorldObject(object.getId(), object.getType(), object.getRotation() + 1,
				object.getX(), object.getY(), object.getPlane());
		if (object.getRotation() == 0) {
			openedDoor.moveLocation(-1, 0, 0);
			if (player.getX() == object.getX())
				player.addWalkSteps(object.getX() - 1, object.getY(), 1, false);
			if (player.getX() < object.getX())
				player.addWalkSteps(object.getX() + 1, object.getY(), 1, false);
		} else if (object.getRotation() == 1) {
			if (player.getY() == object.getY())
				player.addWalkSteps(object.getX(), object.getY() + 1, 1, false);
			if (player.getY() > object.getY())
				player.addWalkSteps(object.getX(), object.getY() - 1, 1, false);
			openedDoor.moveLocation(0, 1, 0);
		} else if (object.getRotation() == 2) {
			if (player.getX() == object.getX())
				player.addWalkSteps(object.getX() + 1, object.getY(), 1, false);
			if (player.getX() > object.getX())
				player.addWalkSteps(object.getX() - 1, object.getY(), 1, false);
			openedDoor.moveLocation(1, 0, 0);
		} else if (object.getRotation() == 3) {
			if (player.getY() == object.getY())
				player.addWalkSteps(object.getX(), object.getY() - 1, 1, false);
			if (player.getY() < object.getY())
				player.addWalkSteps(object.getX(), object.getY() + 1, 1, false);
			openedDoor.moveLocation(0, -1, 0);
		}
		if (World.removeObjectTemporary(object, timer, false)) {
			player.faceObject(openedDoor);
			World.spawnObjectTemporary(openedDoor, timer);
			return true;
		}
		return false;
	}

	public static boolean handleDoorTemporary2(Player player, WorldObject object, long timer) {
		if (World.isSpawnedObject(object))
			return false;
		WorldObject openedDoor = new WorldObject(object.getId(), object.getType(), object.getRotation() + 1,
				object.getX(), object.getY(), object.getPlane());
		if (object.getRotation() == 0) {
			openedDoor.moveLocation(-1, 0, 0);
			if (player.getX() == object.getX())
				player.addWalkSteps(object.getX() - 1, object.getY(), 1, false);
			if (player.getX() < object.getX())
				player.addWalkSteps(object.getX() + 1, object.getY(), 1, false);
		} else if (object.getRotation() == 1) {
			if (player.getY() == object.getY())
				player.addWalkSteps(object.getX(), object.getY() + 1, 1, false);
			if (player.getY() > object.getY())
				player.addWalkSteps(object.getX(), object.getY() - 1, 1, false);
			openedDoor.moveLocation(0, 1, 0);
		} else if (object.getRotation() == 2) {
			if (player.getX() == object.getX())
				player.addWalkSteps(object.getX() + 1, object.getY(), 1, false);
			if (player.getX() > object.getX())
				player.addWalkSteps(object.getX() - 1, object.getY(), 1, false);
			openedDoor.moveLocation(1, 0, 0);
		} else if (object.getRotation() == 3) {
			if (player.getY() == object.getY())
				player.addWalkSteps(object.getX(), object.getY() - 1, 1, false);
			if (player.getY() < object.getY())
				player.addWalkSteps(object.getX(), object.getY() + 1, 1, false);
			openedDoor.moveLocation(0, -1, 0);
		}
		if (World.removeObjectTemporary(object, timer, false)) {
			player.faceObject(openedDoor);
			World.spawnObjectTemporary(openedDoor, timer);
			return true;
		}
		return false;
	}

	public static int getOpenedDoorId(WorldObject object) {
		switch (object.getId()) {
		case 36846:
			return object.getId() + 2;
		case 37123:
			return 37130;
		case 15536:
			return 15535;
		case 24381:
		case 24384:
			return 24383;
		case 24376:
			return 24375;
		case 24378:
			return 24377;
		}
		return object.getId() + 1;
	}

	public static boolean handleDoor(Player player, WorldObject object, long timer) {
		WorldObject openedDoor = new WorldObject(getOpenedDoorId(object), object.getType(), object.getRotation() + 1,
				object.getX(), object.getY(), object.getPlane());
		if (object.getRotation() == 0) {
			openedDoor.moveLocation(-1, 0, 0);
		} else if (object.getRotation() == 1) {
			openedDoor.moveLocation(0, 1, 0);
		} else if (object.getRotation() == 2) {
			openedDoor.moveLocation(1, 0, 0);
		} else if (object.getRotation() == 3) {
			openedDoor.moveLocation(0, -1, 0);
		}
		if (World.removeObjectTemporary(object, timer, true)) {
			player.faceObject(openedDoor);
			World.spawnObjectTemporary(openedDoor, timer);
			return true;
		}
		return false;
	}

	public static boolean handleDoor(Player player, WorldObject object) {
		return handleDoor(player, object, 60000);
	}

	private static WorldTile stairsUp(WorldObject object, Player player) {
		WorldTile tile = new WorldTile(player.getX(), player.getY(), player.getPlane() + 1);
		switch (object.getId()) {
		case 2347:// draynor stairs
			if (object.getRotation() == 1) {
				tile.setX(object.getX() - 1);
				tile.setY(object.getY());
			}
			if (object.getRotation() == 3) {
				tile.setX(object.getX() + 2);
				tile.setY(object.getY());
			}
			return tile;
		case 35646:// alkharid stairs
			if (object.getRotation() == 0) {
				tile.setY(object.getY() + 3);
				tile.setX(object.getX());
			}
			return tile;
		case 45483:// lumbridge stairs
			if (object.getRotation() == 1) {
				tile.setY(object.getY());
				tile.setX(object.getX() - 1);
			}
			if (object.getRotation() == 2) {
				tile.setY(object.getY() + 2);
				tile.setX(object.getX());
			}
			if (object.getRotation() == 3) {
				tile.setY(object.getY());
				tile.setX(object.getX() + 2);
			}
			return tile;
		case 45481:
			if (object.getRotation() == 1) {
				tile.setY(object.getY());
				tile.setX(object.getX() + 2);
			}
			if (object.getRotation() == 2) {
				tile.setY(object.getY() - 1);
				tile.setX(object.getX());
			}
			if (object.getRotation() == 3) {
				tile.setY(object.getY());
				tile.setX(object.getX() - 1);
			}
			return tile;
		}
		return tile;
	}

	private static WorldTile stairsDown(WorldObject object, Player player) {
		WorldTile tile = new WorldTile(player.getX(), player.getY(), player.getPlane() - 1);
		switch (object.getId()) {
		case 2348:
			if (object.getRotation() == 1) {
				tile.setX(object.getX() + 2);
				tile.setY(object.getY());
			}
			if (object.getRotation() == 3) {
				tile.setX(object.getX() - 1);
				tile.setY(object.getY());
			}
			return tile;
		case 35645:// alkharid stairs
			if (object.getRotation() == 0) {
				tile.setY(object.getY() - 4);
				tile.setX(object.getX());
			}
			return tile;
		case 45482:// lumbridge stairs
			if (object.getRotation() == 1) {
				tile.setY(object.getY());
				tile.setX(object.getX() - 1);
			}
			if (object.getRotation() == 2) {
				tile.setY(object.getY() + 2);
				tile.setX(object.getX());
			}
			if (object.getRotation() == 3) {
				tile.setY(object.getY());
				tile.setX(object.getX() + 2);
			}
			return tile;
		case 45484:// lumbridge stairs
			if (object.getRotation() == 1) {
				tile.setY(object.getY());
				tile.setX(object.getX() + 2);
			}
			if (object.getRotation() == 2) {
				tile.setY(object.getY() - 1);
				tile.setX(object.getX());
			}
			if (object.getRotation() == 3) {
				tile.setY(object.getY());
				tile.setX(object.getX() - 1);
			}
			return tile;
		}
		return tile;
	}

	private static boolean handleStairs(Player player, WorldObject object, int optionId) {
		String option = object.getDefinitions().getOption(optionId);
		if (option.equalsIgnoreCase("Climb-up")) {
			if (player.getPlane() == 3)
				return false;
			player.useStairs(-1, stairsUp(object, player), 0, 1);
		} else if (option.equalsIgnoreCase("Climb-down")) {
			if (player.getPlane() == 0)
				return false;
			player.useStairs(-1, stairsDown(object, player), 0, 1);
		} else if (option.equalsIgnoreCase("Climb")) {
			if (player.getPlane() == 3 || player.getPlane() == 0)
				return false;
			player.getDialogueManager().startDialogue("ClimbNoEmoteStairs", stairsUp(object, player),
					stairsDown(object, player), "Go up the stairs.", "Go down the stairs.");
		} else
			return false;
		return false;
	}

	private static WorldTile stairCaseUp(WorldObject object, Player player) {
		WorldTile tile = new WorldTile(player.getX(), player.getY(), player.getPlane() + 1);
		switch (object.getId()) {
		case 34548:
			if (object.getRotation() == 3) {
				tile.setX(object.getX() + 1);
				tile.setY(object.getY() + 2);
			}
			return tile;
		case 26197:
			if (object.getRotation() == 0) {
				tile.setX(object.getX() + 2);
				tile.setY(object.getY());
			}
			if (object.getRotation() == 1) {
				tile.setX(object.getX());
				tile.setY(object.getY() - 1);
			}
			return tile;
		case 47364:
			tile.setX(object.getX() + 2);
			tile.setY(object.getY() + 4);
			return tile;
		case 24074:// cooking guild
			if (object.getRotation() == 1) {
				tile.setX(tile.getX());
				tile.setY(tile.getY() - 3);
			}
			return tile;
		case 35533:
			if (object.getRotation() == 1)
				tile.setX(object.getX() + 1);
			return tile;
		case 35516:
			tile.setY(object.getY() - 1);
			return tile;
		case 11729:// spiral staircases
		case 35781:
		case 11732:
		case 24349:
		case 31615:
		case 24350:
		case 40057:
		case 9582:
		case 11888:
			if (object.getRotation() == 0) {
				tile.setX(tile.getX() + 1);
				tile.setY(tile.getY() + 1);
			}
			if (object.getRotation() == 1) {
				tile.setX(tile.getX() + 1);
				tile.setY(tile.getY() - 1);
			}
			if (object.getRotation() == 2) {
				tile.setX(object.getX() - 1);
				tile.setY(tile.getY() - 1);
			}
			if (object.getRotation() == 3) {
				tile.setX(tile.getX() - 1);
				tile.setY(tile.getY() + 1);
			}
			return tile;
		case 26145:// falador party room
			if (object.getRotation() == 1) {
				tile.setX(tile.getX() - 1);
				tile.setY(tile.getY() + 1);
			}
			return tile;
		case 26144:// falador party room
			if (object.getRotation() == 1) {
				tile.setX(tile.getX() + 1);
				tile.setY(tile.getY() + 1);
			}
			return tile;
		case 26146:// falador party room
			if (object.getRotation() == 0) {
				tile.setX(tile.getX() - 1);
				tile.setY(tile.getY() + 1);
			}
			return tile;
		case 26147:// falador party room
			if (object.getRotation() == 2) {
				tile.setX(tile.getX() + 1);
				tile.setY(tile.getY() + 1);
			}
			return tile;
		case 11736:// long staircases
		case 11734:
		case 24356:
		case 24357:
		case 24358:
		case 24367:
			if (object.getRotation() == 0) {
				tile.setX(tile.getX());
				tile.setY(tile.getY() + 4);
			}
			if (object.getRotation() == 1) {
				tile.setX(tile.getX() + 4);
				tile.setY(tile.getY());
			}
			if (object.getRotation() == 2) {
				tile.setX(tile.getX());
				tile.setY(tile.getY() - 4);
			}
			if (object.getRotation() == 3) {
				tile.setX(tile.getX() - 4);
				tile.setY(tile.getY());
			}
			return tile;
		}
		return tile;
	}

	private static WorldTile stairCaseDown(WorldObject object, Player player) {
		WorldTile tile = new WorldTile(player.getX(), player.getY(), player.getPlane() - 1);
		switch (object.getId()) {
		case 34550:
			tile.setX(tile.getX());
			tile.setY(tile.getY() - 3);
			return tile;
		case 47657:
			tile.setX(tile.getX());
			tile.setY(tile.getY() - 5);
			return tile;
		case 35518:
			tile.setY(object.getY() + 1);
			return tile;
		case 11737:
		case 35783:
		case 24359:
		case 37117:
			if (object.getRotation() == 0) {
				tile.setX(tile.getX());
				tile.setY(tile.getY() - 4);
			}
			if (object.getRotation() == 1) {
				tile.setX(tile.getX() - 4);
				tile.setY(tile.getY());
			}
			if (object.getRotation() == 2) {
				tile.setX(tile.getX());
				tile.setY(tile.getY() + 4);
			}
			if (object.getRotation() == 3) {
				tile.setX(tile.getX() + 4);
				tile.setY(tile.getY());
			}
			return tile;
		case 24075:
			tile.setY(tile.getY() + 3);
			return tile;
		}
		return tile;
	}

	private static boolean handleStaircases(Player player, WorldObject object, int optionId) {
		String option = object.getDefinitions().getOption(optionId);
		if (option.equalsIgnoreCase("Climb-up")) {
			if (player.getPlane() == 3)
				return false;
			WorldTile tile = stairCaseUp(object, player);
			player.useStairs(-1, tile, 0, 1);
		} else if (option.equalsIgnoreCase("Climb-down")) {
			if (player.getPlane() == 0)
				return false;
			WorldTile tile = stairCaseDown(object, player);
			player.useStairs(-1, tile, 0, 1);
		} else if (option.equalsIgnoreCase("Climb")) {
			if (player.getPlane() == 3 || player.getPlane() == 0)
				return false;
			WorldTile upTile = stairCaseUp(object, player);
			WorldTile downTile = stairCaseDown(object, player);
			player.getDialogueManager().startDialogue("ClimbNoEmoteStairs", upTile, downTile, "Go up the stairs.",
					"Go down the stairs.");
		} else
			return false;
		return false;
	}

	private static boolean handleLadder(Player player, WorldObject object, int optionId) {

		// Edits
		if (object.getId() == 39191 && object.getX() == 3241 && object.getY() == 9990) {
			player.animate(new Animation(828));
			OldMagicSystem.sendObjectTeleportSpell(player, true, Settings.HOME_PLAYER_LOCATION);
			return true;
		}

		String option = object.getDefinitions().getOption(optionId);
		if (option.equalsIgnoreCase("Climb-up")) {
			if (player.getPlane() == 3)
				return false;
			WorldTile tile = new WorldTile(player.getX(), player.getY(), player.getPlane() + 1);
			if (object.getId() == 16450)
				tile.setPlane(2);
			if (object.getId() == 36771 && object.getX() == 3207 && object.getY() == 3223)
				tile.setY(object.getY() - 1);
			player.useStairs(828, tile, 1, 2);
		} else if (option.equalsIgnoreCase("Climb-down")) {
			if (player.getPlane() == 0)
				return false;
			WorldTile tile = new WorldTile(player.getX(), player.getY(), player.getPlane() - 1);
			if (object.getId() == 16556)
				tile.setPlane(0);
			if (object.getId() == 36772 && object.getX() == 3207 && object.getY() == 3223)
				tile.setY(object.getY() + 1);
			player.useStairs(827, tile, 1, 2);
		} else if (option.equalsIgnoreCase("Climb")) {
			if (player.getPlane() == 3 || player.getPlane() == 0)
				return false;
			player.getDialogueManager().startDialogue("ClimbEmoteStairs",
					new WorldTile(player.getX(), player.getY(), player.getPlane() + 1),
					new WorldTile(player.getX(), player.getY(), player.getPlane() - 1), "Climb up the ladder.",
					"Climb down the ladder.", 828);
		} else
			return false;
		return true;
	}

	public static void handleItemOnObject(final Player player, final WorldObject object, final int interfaceId,
			final Item item) {
		final int itemId = item.getId();
		final ObjectDefinitions objectDef = object.getDefinitions();
		player.setRouteEvent(new RouteEvent(object, new Runnable() {
			@Override
			public void run() {
				if (!player.getControlerManager().handleItemOnObject(object, item)) {
					return;
				}
				player.faceObject(object);
				for (CombinationRunesStore data : CombinationRunesStore.values()) {
					if (data != null) {
						if ((item.getId() == data.itemId || item.getId() == data.talisman)
								&& object.getId() == data.objectId2) {
							CombinationRunes.craftComboRune(player, data.itemId, data.level, data.exp, data.finalItem,
									data.talisman);
							return;
						} else if ((item.getId() == data.itemId2 || item.getId() == data.talisman2)
								&& object.getId() == data.objectId) {
							CombinationRunes.craftComboRune(player, data.itemId2, data.level, data.exp2, data.finalItem,
									data.talisman2);
							return;
						}
					}
				}
				for (RunecraftingTiaraStore data : RunecraftingTiaraStore.values()) {
					if (data != null) {
						if (item.getId() == data.talismanId && object.getId() == data.altarId) {
							Tiaras.enchantTiara(player, data.talismanId, data.tiaraId, data.level, data.exp);
							return;
						}
					}
				}
				if (HouseConstants.Builds.ALTAR.containsObject(object)) {
					Bone bone = Bone.forId(item.getId());
					if (bone != null) {
						player.getActionManager().setAction(new BoneOffering(object, bone, 2));
						return;
					}
				}
				if (CityEventHandler.handleItemOnObject(player, object, item))
					return;
				if (player.getFarmingManager().isFarming(object.getId(), item, 0)) {
					return;
				} else if (itemId == 1947 && (object.getId() == 70034 || object.getId() == 24071)) {
					if (player.hopper) {
						player.getPackets().sendGameMessage("You already put some wheat in the hopper.");
						return;
					}
					player.hopper = true;
					player.getInventory().deleteItem(itemId, 1);
					player.getPackets().sendGameMessage("You put the wheat in the hopper.");
					return;
					/*
					 * } else if (itemId == 536 && object.getId() == 172) {
					 * GildedAltar.addBones(player, object);
					 */
				} else if (itemId == 983 && object.getId() == 1804 && player.getY() == 3449) {
					handleDoorTemporary(player, object, 1200);
				} else if (itemId == 14472 && object.getId() == 2783 || itemId == 14474 && object.getId() == 2783
						|| itemId == 14476 && object.getId() == 2783) {
					if (!player.getInventory().containsItem(2347, 1)) {
						player.getPackets().sendGameMessage("You don't have any hammer to smith this.");
						return;
					}
					if (player.getSkills().getLevel(Skills.SMITHING) >= 92) {
						if (player.getInventory().containsItem(14472, 1) && player.getInventory().containsItem(14474, 1)
								&& player.getInventory().containsItem(14476, 1)) {
							player.animate(new Animation(898));
							player.getSkills().addXp(Skills.SMITHING, 5000);
							player.getInventory().deleteItem(14472, 1);
							player.getInventory().deleteItem(14474, 1);
							player.getInventory().deleteItem(14476, 1);
							player.getInventory().addItem(14479, 1);
						} else {
							player.getPackets().sendGameMessage(
									"You need ruined dragon lump, slice and shard in order to smith this.");
						}
					} else {
						player.getPackets().sendGameMessage("You need an level of at least 92 smithing to smith this.");
					}
				} else if (itemId == 11286 && object.getId() == 2783) {
					if (!player.getInventory().containsItem(2347, 1)) {
						player.getPackets().sendGameMessage("You don't have any hammer to smith this.");
						return;
					}
					if (player.getSkills().getLevel(Skills.SMITHING) >= 90) {
						if (player.getInventory().containsItem(1540, 1)) {
							player.animate(new Animation(898));
							player.getSkills().addXp(Skills.SMITHING, 2000);
							player.getInventory().deleteItem(1540, 1);
							player.getInventory().deleteItem(11286, 1);
							player.getInventory().addItem(11284, 1);
						} else {
							player.getPackets()
									.sendGameMessage("You need an anti-dragon shield in order to smith this.");
						}
					} else {
						player.getPackets().sendGameMessage("You need an level of at least 90 smithing to smith this.");
					}
				} else if (itemId == 989 && object.getId() == 172) {
					player.animate(new Animation(536));
					player.lock(2);
					player.getPackets().sendGameMessage("You attemp to unlock the chest...");
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							player.getInventory().deleteItem(989, 1);
							CrystalChest.sendRewards(false, player);
						}
					}, 1);
				} else if (object.getId() == 733 || object.getId() == 64729) {
					player.animate(new Animation(PlayerCombat.getWeaponAttackEmote(-1, 0)));
					slashWeb(player, object);
				} else if (object.getId() == 48803 && itemId == 954) {
					if (player.isKalphiteLairSetted())
						return;
					player.getInventory().deleteItem(954, 1);
					player.setKalphiteLair();
				} else if (itemId == 2355) {
					if (player.getSkills().getLevel(Skills.CRAFTING) < 23) {
						player.getPackets().sendGameMessage("You need a crafting level of 23 to craft tiara.");
						return;
					}
					if (!player.getInventory().containsItem(2355, 1) && player.getToolbelt().contains(2355)) {
						player.getPackets().sendGameMessage("You need a tiara mould.");
						return;
					}
					player.lock(4);
					player.animate(new Animation(3243));
					player.getSkills().addXp(Skills.CRAFTING, 52.5);
					player.getInventory().deleteItem(2355, 1);
					player.getInventory().addItem(5525, 1);
					player.getPackets().sendGameMessage("You craft a tiara.");
				} else if (object.getId() == 48802 && itemId == 954) {
					if (player.isKalphiteLairEntranceSetted())
						return;
					player.getInventory().deleteItem(954, 1);
					player.setKalphiteLairEntrance();
				} else {
					switch (objectDef.name.toLowerCase()) {
					// case "altar":

					// GildedAltar.addBone(player, object, item);
					// break;
					case "small table":
					case "large table":
					case "crate":
					case "counter":
					case "table":
						if (objectDef.containsOption("bank")) {
							player.getPackets().sendGameMessage("Nothing interesting happens.");
							return;
						}
						if (!ItemConstants.isTradeable(item)) {
							player.getPackets().sendGameMessage("You can't put that on the " + objectDef.name + ".");
							return;
						}
						player.animate(new Animation(833));
						player.getInventory().deleteItem(new Item(item.getId(), item.getAmount()));
						World.updateGroundItem(item, new WorldTile(object), player, 60, 0);
						break;
					case "anvil":
						ForgingBar bar = ForgingBar.forId(itemId);
						if (bar != null)
							ForgingInterface.sendSmithingInterface(player, bar);
						break;
					case "spinning wheel":

						break;
					case "furnace":
					case "lava furnace":
						if (item.getId() == 2357) {
							JewllerySmithing.openInterface(player);
							return;
						} else if (item.getId() == 2353 || item.getId() == 4) {
							if (player.getInventory().containsItem(4, 1))
								player.getActionManager()
										.setAction(new Smelting(SmeltingBar.CANNONBALL.getButtonId(), object, player.getInventory().getNumberOf(2353)));
							else
								player.sm("You need a cannonball mould.");
								return;
						} else if (item.getId() == 4155) {
							if (!player.getSlayerManager().hasLearnedRing()) {
								player.getPackets().sendGameMessage("You haven't learnt to do this yet.");
								return;
							}
							if (player.getSkills().getLevel(Skills.SMITHING) < 75) {
								player.getPackets()
										.sendGameMessage("You need at least a level of 75 to smith ring of slayings.");
								return;
							}
							if (!player.getInventory().containsItem(2357, 1)) {
								player.getPackets().sendGameMessage("You need a gold bar to smith a ring of slaying.");
								return;
							}
							player.animate(new Animation(3243));
							player.getSkills().addXp(Skills.SMITHING, 15);
							player.getInventory().deleteItem(4155, 1);
							player.getInventory().deleteItem(2357, 1);
							player.getInventory().addItem(13281, 1);
							return;
						}
						player.sm("Nothing interesting happens.");
						break;
					case "sink":
					case "fountain":
					case "well":
					case "waterpump":
						if (WaterFilling.isFilling(player, itemId, false))
							return;
						break;
					case "range":
					case "cooking range":
					case "stove":
					case "fire":
						if (objectDef.containsOption(4, "Add-logs") && Bonfire.addLog(player, object, item))
							return;
						Cookables cook = Cooking.isCookingSkill(item);
						if (cook != null) {
							player.getDialogueManager().startDialogue("CookingD", cook, object);
							return;
						}
						player.getPackets().sendGameMessage(
								"You can't cook that on a " + (objectDef.name.equals("Fire") ? "fire" : "range") + ".");
						break;
					default:
						player.getPackets().sendGameMessage("Nothing interesting happens.");
						break;
					}
					if (Settings.DEBUG)
						System.out.println("Item on object: " + object.getId());
				}
			}
		}));
	}

	private static int getOpenId(int objectId) {
		if (objectId == 44293)
			return 44305;
		return objectId + 1;
	}
}
