package com.rs.game.player.content;

import com.rs.cores.CoresManager;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.map.MapBuilder;

public class EdgevillePvPInstance {
	
	
	
private static int[] boundChuncks;
	 
	public static void buildMap() {
		int[] map = new int[] { 384, 435 };
		MapBuilder.copyAllPlanesMap(map[0], map[1], boundChuncks[0], boundChuncks[1], 8);
	}
	
	public static WorldTile getWorldTile(int mapX, int mapY) {
		return new WorldTile(boundChuncks[0] * 8 + mapX, boundChuncks[1] * 8 + mapY, 0);
	}
	
	
	public static void buildInstance() {
		Runnable event = new Runnable() {
			@Override
			public void run() {
				CoresManager.slowExecutor.execute(new Runnable() {
					@Override
					public void run() {
						if (boundChuncks == null) {
							boundChuncks = MapBuilder.findEmptyChunkBound(8, 8);
							buildMap();
						} else {
							buildMap();
						}
						for (int i = 63; i <= 103; i++)
							World.spawnObject(new WorldObject(1, 0, 0, new WorldTile(i, 104, 0)));
						for (int i = 63; i <= 103; i++)
							World.spawnObject(new WorldObject(1, 0, 0, new WorldTile(104, i, 0)));
						System.out.println(getWorldTile(0, 0).getX() + " " + getWorldTile(0, 0).getY());
					}
				});

			}
		};
		event.run();
		
	}

}
