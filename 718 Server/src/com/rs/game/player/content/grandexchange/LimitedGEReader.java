package com.rs.game.player.content.grandexchange;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.ItemDefinitions.FileUtilities;

public class LimitedGEReader {

	private static ArrayList<Integer> items = new ArrayList<Integer>();
	private final static String TXT_PATH = "./data/GE/limitedItems.txt";
	private static FileReader fr;

	public static void init() {
		try {
			initReaders();
			readToStoreCollection();
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	private static void readToStoreCollection() throws Exception {
		StringBuffer names = new StringBuffer();
		for (String lines : FileUtilities.readFile(TXT_PATH)) {
			items.add(Integer.parseInt(lines));
			names.append("\n[Limited Items]"+ItemDefinitions.getItemDefinitions(Integer.parseInt(lines)).getName()).append(", ");
		}
		System.out.println("[Launcher] Initiated " + items.size() + " Limited items.");
		System.out.println("[Launcher] Initiated items: " + names.replace(names.length() - 2, names.length(), "").toString());
		items.add(getId("rocktail"));
		items.add(getId("overload (4)"));
		items.add(getId("super prayer (4)"));
		items.add(getId("super combat potion (4)"));
		items.add(getId("prayer renewal (4)"));
		items.add(getId("super antifire (4)"));
		items.add(10476);//purple sweets
		items.add(getId("potion flask"));
		items.add(getId("saradomin brew (4)"));
		items.add(getId("super restore (4)"));
		items.add(getId("sanfew serum (4)"));
		items.add(getId("super strength (4)"));
		items.add(getId("super attack (4)"));
		items.add(getId("super defence (4)"));
		items.add(getId("ranging potion (4)"));
		items.add(getId("magic potion (4)"));
		items.add(getId("shark"));
		items.add(getId("cavefish"));
		items.add(getId("tuna potato"));
		items.add(getId("cooked karambwan"));
		items.add(getId("morrigan's throwing axe"));
		items.add(getId("morrigan's javelin"));
		items.add(8848);
		items.add(8849);
		items.add(8850);
		// System.out.println("[Launcher] Initiated " + items.size() + " limited
		// items.");
	}
	
	private static int getId(String name) {
		return ItemDefinitions.getId(name);
	}

	private static void initReaders() throws Exception {
		fr = new FileReader(TXT_PATH);
		new BufferedReader(fr);
	}

	public static ArrayList<Integer> getLimitedItems() {
		return items;
	}

	public static void reloadLimiteditems() {
		try {
			items.clear();
			reloadReaders();
			readToStoreCollection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void reloadReaders() throws Exception {
		fr = null;
		initReaders();

	}

	public static boolean itemIsLimited(int itemId) {
		for (int i = 0; i < items.size(); i++) {
			if (itemId == items.get(i)) {
				return true;
			}
		}
		return false;
	}

}
