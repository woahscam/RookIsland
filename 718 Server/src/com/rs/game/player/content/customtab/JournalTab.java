package com.rs.game.player.content.customtab;

import java.text.DecimalFormat;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.player.Player;

public class JournalTab extends CustomTab {
	
	/**
	 * @author Andreas
	 *
	 */

	public enum JournalStore {

		TITLE(25) {
			@Override
			public void usage(Player p) {
			}
			@Override
			public String text(Player p) {
				return "Journal";
			}
		},

		INFORMATION(3) {
			@Override
			public void usage(Player p) {
			}
			@Override
			public String text(Player p) {
				return "<u>Server Information";
			}
		},

		PLAYERCOUNT(4) {
			@Override
			public void usage(Player p) {
				p.sendPlayersList();
				p.getPackets().sendGameMessage("Players online: " + World.getPlayers().size() + ".");
				
			}
			@Override
			public String text(Player p) {
				return "Players Online: <col=04BB3B>" + World.getPlayers().size();
			}
		},

		WILDYCOUNT(5) {
			@Override
			public void usage(Player p) {
				p.getPackets().sendGameMessage("Players in the Wilderness: " + p.wildyCount() + ".");
			}
			@Override
			public String text(Player p) {
				return "Players in Wilderness: <col=04BB3B>" + p.wildyCount();
			}
		},

		FFACOUNT(6) {
			@Override
			public void usage(Player p) {
				p.getPackets().sendGameMessage("Players in Clan Wars (FFA): " + p.clanwarsCount() + ".");
			}
			@Override
			public String text(Player p) {
				return "Clan Wars (FFA): <col=04BB3B>" + p.clanwarsCount();
			}
		},

		DOUBLEDROPS(7) {
			@Override
			public void usage(Player p) {
				p.getPackets()
				.sendGameMessage("Double drops is " + (Settings.DOUBLE_DROP ? " activated." : "inactivated."));
			}
			@Override
			public String text(Player p) {
				return "Double Drops: " + (Settings.DOUBLE_DROP ? "<col=04BB3B>Active" : "<col=BB0404>Inactive");
			}
		},

		
		PLAYERINFO(9) {
			@Override
			public void usage(Player p) {
			}
			@Override
			public String text(Player p) {
				return "<u>Player Information";
			}
		},
		PLAYERRANK(10) {
			@Override
			public void usage(Player p) {
				p.getPackets().sendGameMessage("My rank is: " + "<img=" + (p.getRights() == 2 ? 1 : p.getRights() == 1 ? 0 : p.getMessageIcon()) + ">" + (p.getIronman().isIronman() && p.getIronman().Ironman_Mode == 2 ? "Hardcore Ironman" : p.getIronman().isIronman() && p.getIronman().Ironman_Mode == 1 ? "Ironman" : p.getRights() == 2 ? "Developer" : p.getRights() == 1 ? "Player Moderator" : p.getPlayerMode() == 5 ? "PK King" : p.getPlayerMode() == 4 ? "Youtuber" : p.getPlayerMode() == 3 ? "Gold Member" : p.getPlayerMode() == 2 ? "Silver Member" : p.getPlayerMode() == 1 ? "Bronze Member" : "Player") + ".");
			}

			@Override
			public String text(Player p) {
				return "Rank: " + "<img=" + (p.getRights() == 2 ? 1 : p.getRights() == 1 ? 0 : p.getMessageIcon()) + "><col=04BB3B>" + (p.getIronman().isIronman() && p.getIronman().Ironman_Mode == 2 ? ("Hardcore Iron" + (p.getAppearence().isMale() ? "man" : "woman")) : p.getIronman().isIronman() && p.getIronman().Ironman_Mode == 1 ? "Ironman" : p.getRights() == 2 ? "Developer" : p.getRights() == 1 ? "Moderator" : p.getPlayerMode() == 5 ? "PK King" : p.getPlayerMode() == 4 ? "Youtuber" : p.getPlayerMode() == 3 ? "Gold Member" : p.getPlayerMode() == 2 ? "Silver Member" : p.getPlayerMode() == 1 ? "Bronze Member" : "Player");
			}
		},
		PLAYERTITLE(11) {
			@Override
			public void usage(Player p) {
				p.setCustomTitle(null);
				p.getTemporaryAttributtes().put("SET_TITLE", Boolean.TRUE);
				p.getPackets().sendRunScript(108, new Object[] { "Enter title id, 0-58, 0 = none:" });
			}

			@Override
			public String text(Player p) {
				return "Title: " + (p.getAppearence().getTitle() != -1 && p.getCustomTitle() == null ? p.getAppearence().getTitleString() : "<col=BB0404>None - Click here");
			}
		},
		KILLS(12) {
			@Override
			public void usage(Player p) {
				p.getPackets().sendGameMessage("My killcount is: " + p.getKillCount() + ".");
			}
			@Override
			public String text(Player p) {
				return "Kills: <col=04BB3B>" + p.getKillCount();
			}
		},
		DEATHS(13) {
			@Override
			public void usage(Player p) {
				p.getPackets().sendGameMessage("My deathcount is: " + p.getDeathCount() + ".");
			}
			@Override
			public String text(Player p) {
				return "Deaths: <col=04BB3B>" + p.getDeathCount();
			}
		},
		KDR(14) {
			@Override
			public void usage(Player p) {
				double kill = p.getKillCount();
				double death = p.getDeathCount();
				double dr = kill / death;
				p.getPackets().sendGameMessage("My kill/death ratio is: " + new DecimalFormat("##.#").format(dr) + ".");
			}
			@Override
			public String text(Player p) {
				double kill = p.getKillCount();
				double death = p.getDeathCount();
				double dr = kill / death;
				return "K/D Ratio: <col=04BB3B>" + new DecimalFormat("##.#").format(dr);
			}
		},
		PLAYERXP(15) {
			@Override
			public void usage(Player p) {
				p.getPackets().sendGameMessage("Your bonus experience is " + p.getBonusExp() + ".");
			}
			@Override
			public String text(Player p) {
				return "Bonus Experience: " + (p.getBonusExp() > 1 ? "<col=04BB3B>" + p.getBonusExp() + "x"
						: "<col=BB0404>" + p.getBonusExp() + "x");
			}
		},
		
		EP(16) {
			@Override
			public void usage(Player p) {
				p.getPackets().sendGameMessage("Your Ep is: " + p.getEp() + ".");
			}
			@Override
			public String text(Player p) {
				return "Ep: " + (p.getEp() == 100 ? "<col=04BB3B>" : p.getEp() > 0 ? "<col=FFF300>" : "<col=BB0404>") + p.getEp() + "%";
			}
		},
		
		SLAYERTASK(17) {
			@Override
			public void usage(Player p) {
				p.getPackets().sendGameMessage(p.getSlayerTask() == null ? "I don't have a slayer task."
						: "I have " + p.getSlayerTask() + " to hunt.");
			}
			@Override
			public String text(Player p) {
				return "<br>Slayer Task: <col=04BB3B>" + (p.getSlayerTask() == null ? "I don't have a task." : "<br><col=04BB3B>" + p.getSlayerTask());
			}
		},

		TASKLOCATION(19) {
			@Override
			public void usage(Player p) {
				if (p.getSlayerTaskTip() != null)
				p.getPackets().sendGameMessage("You can find your slayer monsters in:<br>" + p.getSlayerTaskTip());
			}
			@Override
			public String text(Player p) {
				return (p.getSlayerTaskTip() == null ? ""
						: "<u><br><br>Locations:<br><col=04BB3B>"
								+ p.getSlayerTaskTip().replace(" and ", "<br><col=04BB3B>")
										.replace(", ", "<br><col=04BB3B>").replace(".", ""));
			}
		},
		
		STAFF(22) {
			@Override
			public void usage(Player p) {
				//p.getPackets().sendGameMessage("You can find your slayer monsters in:<br>" + p.getSlayerTaskTip());
			}
			@Override
			public String text(Player p) {
				return "<br><br><u>Staff Online:<br><br>" + (p.hasStaffOnline() ? p.getStaffOnline() : "<br> <col=04BB3B>There is no staff online." + "") ;
			}
		},
		;
		

		private int compId;

		private JournalStore(int compId) {
			this.compId = compId;
		}

		public abstract String text(Player p);
		
		public abstract void usage(Player p);

	}

	public static void open(Player player) {
		sendComponents(player);
		for (int i = 3; i <= 22; i++)
			player.getPackets().sendHideIComponent(3002, i, true);
		for (int i = 28; i <= 56; i++)
			player.getPackets().sendHideIComponent(3002, i, true);
		player.getTemporaryAttributtes().put("CUSTOMTAB", 0);
		player.getPackets().sendHideIComponent(3002, BACK_BUTTON, true);
		player.getPackets().sendHideIComponent(3002, FORWARD_BUTTON, false);
		player.getPackets().sendSpriteOnIComponent(3002, BLUE_STAR_COMP, BLUE_HIGHLIGHTED);
		for (JournalStore store : JournalStore.values()) {
			if (store != null) {
				player.getPackets().sendHideIComponent(3002, store.compId, false);
				if (store.text(player) != null) {
					player.getPackets().sendIComponentText(3002, store.compId, store.text(player));
				}
			}
		}
	}

	public static void handleButtons(Player player, int compId) {
		for (JournalStore store : JournalStore.values()) {
			if (store != null) {
				if (compId != store.compId)
					continue;
				store.usage(player);
				open(player);
			}
		}
		switch (compId) {
		case FORWARD_BUTTON:
			TeleportTab.open(player);
			break;
		default:
			break;
		}
	}
}
