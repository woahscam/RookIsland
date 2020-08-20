package com.rs.game.npc.combat;

import com.rs.Settings;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceMovement;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.minigames.godwars.zaros.Nex;
import com.rs.game.npc.NPC;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.npc.fightcaves.FightCavesNPC;
import com.rs.game.npc.fightkiln.HarAkenTentacle;
import com.rs.game.npc.pest.PestPortal;
import com.rs.game.player.Player;
import com.rs.game.player.actions.combat.Combat;
import com.rs.utils.MapAreas;
import com.rs.utils.Utils;

/**
 * 
 * @Improved Andreas - AvalonPK
 * 
 */

public final class NPCCombat {

	private NPC npc;
	private int combatDelay;
	private Entity target;

	public NPCCombat(NPC npc) {
		this.npc = npc;
	}

	public int getCombatDelay() {
		return combatDelay;
	}

	/*
	 * returns if under combat
	 */
	public boolean process() {
		if (combatDelay > 0)
			combatDelay--;
		if (target != null) {
			if (!checkAll()) {
				removeTarget();
				return false;
			}
			if (combatDelay <= 0)
				combatDelay = combatAttack();
			return true;
		}
		return false;
	}

	/*
	 * return combatDelay
	 */
	private int combatAttack() {
		Entity target = this.target;
		if (target == null) {
			return 0;
		}
		if (npc.getId() == 4474 || npc.getId() == 7891 || npc.isDead() || npc.hasFinished() || target.isDead()
				|| target.hasFinished() || npc.getPlane() != target.getPlane())
			return 0;
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int attackStyle = defs.getAttackStyle();
		if (target instanceof Familiar) {
			Familiar familiar = (Familiar) target;
			Player player = familiar.getOwner();
			if (player != null) {
				target = player;
				npc.setTarget(target);
			}
			if (target == familiar.getOwner()) {
				npc.setTarget(target);
			}

		}
		int maxDistance = attackStyle == NPCCombatDefinitions.MELEE || attackStyle == NPCCombatDefinitions.SPECIAL2 ? 0
				: npc instanceof HarAkenTentacle ? 12
						: npc instanceof FightCavesNPC && attackStyle == NPCCombatDefinitions.SPECIAL ? 12 : 7;
		if ((!(npc instanceof Nex))
				&& !npc.clipedProjectile(target, maxDistance == 0 && !forceCheckClipAsRange(target))) {
			return 0;
		}
		int size = 1;
		int distanceX = target.getX() - npc.getX();
		int distanceY = target.getY() - npc.getY();
		if (distanceX > size + maxDistance || distanceX < -1 - maxDistance || distanceY > size + maxDistance
				|| distanceY < -1 - maxDistance) {
			return 0;
		}
		addAttackedByDelay(target);
		return CombatScriptsHandler.specialAttack(npc, target);
	}

	protected void doDefenceEmote(Entity target) {
		target.setNextAnimationNoPriority(new Animation(Combat.getDefenceEmote(target)), target);
	}

	public Entity getTarget() {
		return target;
	}

	public void addAttackedByDelay(Entity target) {
		target.setAttackedBy(npc);
		target.setAttackedByDelay(Utils.currentTimeMillis() + 5000); // 8seconds
	}

	public void setTarget(Entity target) {
		this.target = target;
		npc.setNextFaceEntity(target);
		if (!checkAll()) {
			removeTarget();
			return;
		}
	}

	// maxDistance = npc.getForceAgressiveDistance() > 0 ? npc
	// .getForceAgressiveDistance() : 4;

	// maxDistance = npc.getForceTargetDistance() > 0 ? npc
	// .getForceTargetDistance() * 2 : 8;
	public boolean checkAll() {
		Entity target = this.target; // prevents multithread issues
		if (target == null)
			return false;
		if (npc.isDead() || npc.hasFinished() || npc.isForceWalking() || target.isDead() || target.hasFinished()
				|| npc.getPlane() != target.getPlane())
			return false;
		if (npc instanceof Familiar && target instanceof NPC && ((NPC) target).isCantInteract())
			return false;
		if (npc.getFreezeDelay() >= Utils.currentTimeMillis())
			return true; // if freeze cant move ofc
		int distanceX = npc.getX() - npc.getRespawnTile().getX();
		int distanceY = npc.getY() - npc.getRespawnTile().getY();
		int size = npc.getSize();
		int maxDistance;
		int agroRatio = npc.getForceAgressiveDistance() > 0 ? npc.getForceAgressiveDistance() : npc.getSize() * 2;
		if (!npc.isNoDistanceCheck() && !npc.isCantFollowUnderCombat()) {
			maxDistance = agroRatio > 12 ? agroRatio : 12; // before 32, but its too much
			if (!(npc instanceof Familiar)) {
				if (npc.getMapAreaNameHash() != -1) {
					// if out his area
					if (!MapAreas.isAtArea(npc.getMapAreaNameHash(), npc) || (!npc.canBeAttackFromOutOfArea()
							&& !MapAreas.isAtArea(npc.getMapAreaNameHash(), target))) {
						npc.forceWalkRespawnTile();
						return false;
					}
				} else if (distanceX > size + maxDistance || distanceX < -1 - maxDistance
						|| distanceY > size + maxDistance || distanceY < -1 - maxDistance) {
					// if more than 32 distance from respawn place
					npc.forceWalkRespawnTile();
					return false;
				}
			}
			maxDistance = agroRatio > 16 ? agroRatio : 16;
			distanceX = target.getX() - npc.getX();
			distanceY = target.getY() - npc.getY();
			if (distanceX > size + maxDistance || distanceX < -1 - maxDistance || distanceY > size + maxDistance
					|| distanceY < -1 - maxDistance) {
				return false; // if target distance higher 16
			}
		} else {
			distanceX = target.getX() - npc.getX();
			distanceY = target.getY() - npc.getY();
		}
		// checks for no multi area :)
		if (npc instanceof Familiar) {
			Familiar familiar = (Familiar) npc;
			if (!familiar.canAttack(target)) {
				System.out.println("cant attack.");
				return false;
			}
		} else {
			if (!npc.isForceMultiAttacked()) {
				if (!target.isAtMultiArea() || !npc.isAtMultiArea()) {
					if (npc.getAttackedBy() != target && npc.getAttackedByDelay() > Utils.currentTimeMillis()) {
						return false;
					}
					if (target.getAttackedBy() != npc && target.getAttackedByDelay() > Utils.currentTimeMillis()) {
						return false;
					}
				}
			}
		}
		if (!npc.isCantFollowUnderCombat()) {
			// if is under
			int targetSize = target.getSize();
			/*
			 * if (distanceX < size && distanceX > -targetSize && distanceY < size &&
			 * distanceY > -targetSize && !target.hasWalkSteps()) {
			 */
			if (Utils.colides(npc.getX(), npc.getY(), size, target.getX(), target.getY(), target.getSize())
					&& !target.hasWalkSteps()) {
				npc.resetWalkSteps();
				if (!npc.addWalkSteps(target.getX() + target.getSize(), npc.getY())) {
					npc.resetWalkSteps();
					if (!npc.addWalkSteps(target.getX() - size, npc.getY())) {
						npc.resetWalkSteps();
						if (!npc.addWalkSteps(npc.getX(), target.getY() + target.getSize())) {
							npc.resetWalkSteps();
							if (!npc.addWalkSteps(npc.getX(), target.getY() - size)) {
								return false;
							}
						}
					}
				}
				return true;
			} else if (npc.getCombatDefinitions().getAttackStyle() == NPCCombatDefinitions.MELEE && targetSize == 1
					&& size == 1 && Math.abs(npc.getX() - target.getX()) == 1
					&& Math.abs(npc.getY() - target.getY()) == 1 && !target.hasWalkSteps()) {
				if (!npc.addWalkSteps(target.getX(), npc.getY(), 1))
					npc.addWalkSteps(npc.getX(), target.getY(), 1);
				if (npc.getX() == target.getX() + npc.getSize() && npc.getY() == target.getY() + npc.getSize()
						|| npc.getX() == target.getX() - npc.getSize() && npc.getY() == target.getY() - npc.getSize()
						|| npc.getX() == target.getX() - npc.getSize() && npc.getY() == target.getY() + npc.getSize()
						|| npc.getX() == target.getX() + npc.getSize() && npc.getY() == target.getY() - npc.getSize()) {
					if (Settings.DEBUG)
						System.out.println(npc.getName() + " combatDelay: " + npc.getCombat().getCombatDelay());
					if (npc.getCombat().getCombatDelay() != 0) {
						combatDelay = 1;
					} else {
						combatDelay = npc.getCombatDefinitions().getAttackDelay();
					}
				}
				return true;
			}

			int attackStyle = npc.getCombatDefinitions().getAttackStyle();
			if (npc instanceof Nex) {
				Nex nex = (Nex) npc;
				maxDistance = nex.isForceFollowClose() ? 0 : 7;
				if ((!npc.clipedProjectile(target, maxDistance == 0 && !forceCheckClipAsRange(target)))
						|| !Utils.isOnRange(npc.getX(), npc.getY(), size, target.getX(), target.getY(), targetSize,
								maxDistance)) {
					npc.resetWalkSteps();
					if (!Utils.isOnRange(npc.getX(), npc.getY(), size, target.getX(), target.getY(), targetSize, 10)) {
						int[][] dirs = Utils.getCoordOffsetsNear(size);
						for (int dir = 0; dir < dirs[0].length; dir++) {
							final WorldTile tile = new WorldTile(new WorldTile(target.getX() + dirs[0][dir],
									target.getY() + dirs[1][dir], target.getPlane()));
							if (World.isTileFree(tile.getPlane(), tile.getX(), tile.getY(), size)) { // if
								// found
								// done
								npc.setNextForceMovement(new ForceMovement(new WorldTile(npc), 0, tile, 1,
										Utils.getMoveDirection(tile.getX() - npc.getX(), tile.getY() - npc.getY())));
								npc.animate(new Animation(17408));
								npc.setNextWorldTile(tile);
								return true;
							}
						}
					} else
						npc.calcFollow(target, 2, true, npc.isIntelligentRouteFinder());
					return true;
				} else
					// if doesnt need to move more stop moving
					npc.resetWalkSteps();
			} else {
				// MAGE_FOLLOW and RANGE_FOLLOW follow close but can attack far
				// unlike melee
				maxDistance = npc.isForceFollowClose() ? 0
						: (attackStyle == NPCCombatDefinitions.MELEE || attackStyle == NPCCombatDefinitions.SPECIAL2)
								? 0
								: 7;
				npc.resetWalkSteps();
				// is far from target, moves to it till can attack
				if ((!npc.clipedProjectile(target, maxDistance == 0 && !forceCheckClipAsRange(target)))
						|| !Utils.isOnRange(npc.getX(), npc.getY(), size, target.getX(), target.getY(),
								target.getSize(), maxDistance)) {
					if (npc.isIntelligentRouteFinder()) {
						if (!npc.calcFollow(target, npc.getRun() ? 2 : 1, true, npc.isIntelligentRouteFinder())
								&& combatDelay < 3 && attackStyle == NPCCombatDefinitions.MELEE) {
							if (npc.getCombat().getCombatDelay() > Utils.currentTimeMillis())
								combatDelay = npc.getCombatDefinitions().getAttackDelay();
							return true;
						}
					} else {
						if (!npc.addWalkStepsInteract(target.getX(), target.getY(), npc.getRun() ? 2 : 1, size, true)) {
							if (!(npc instanceof Familiar)) {
								if (npc.getCombat().getCombatDelay() > Utils.currentTimeMillis()
										|| npc.getAttackedByDelay() < Utils.currentTimeMillis()) {
									combatDelay = npc.getCombatDefinitions().getAttackDelay();
								}
							}
							return true;
						}
					}
				}
				// if under target, moves

			}
		}
		return true;
	}

	private boolean forceCheckClipAsRange(Entity target) {
		return target instanceof PestPortal;
	}

	public void addCombatDelay(int delay) {
		combatDelay += delay;
	}

	public void setCombatDelay(int delay) {
		combatDelay = delay;
	}

	public boolean underCombat() {
		return target != null;
	}

	public void removeTarget() {
		this.target = null;
		npc.setNextFaceEntity(null);
	}

	public void reset() {
		combatDelay = 0;
		target = null;
	}

}
