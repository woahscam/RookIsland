package com.rs.tools;

import java.io.File;
import java.io.IOException;

import com.rs.cache.Cache;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.Utils;

public class ItemRemoverC {

	public static void main(String[] args) {
		try {
			Cache.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Starting");
		File[] chars = new File("data/characters").listFiles();
		for (File acc : chars) {
			if (Utils.invalidAccountName(acc.getName().replace(".p", ""))) {
				acc.delete();
				continue;
			}
			try {
				Player player = (Player) SerializableFilesManager.loadSerializedFile(acc);
				for (int id = 0; id < Utils.getItemDefinitionsSize(); id++) {
					Item item = player.getBank().getItem(id);
					if (item == null)
						continue;
					@SuppressWarnings("unused")
					String name = item.getDefinitions().getName().toLowerCase();
				}
				for (Item item : player.getInventory().getItems().getItems()) {
					if (item == null)
						continue;
				}
				for (Item item : player.getEquipment().getItems().getItems()) {
					if (item == null)
						continue;
				}
				if (player.getFamiliar() != null) {
					player.setFamiliar(null);
				}
				SerializableFilesManager.storeSerializableClass(player, acc);
			} catch (Throwable e) {
				acc.delete();
				System.out.println("failed: " + acc.getName());
			}
		}
		System.out.println("Done.");
	}
}
