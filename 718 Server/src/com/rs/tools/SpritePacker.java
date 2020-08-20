package com.rs.tools;

import java.io.IOException;

import com.alex.store.Store;

public class SpritePacker {

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		Store store = new Store("data/cache2/");
		Store store2 = new Store("data/cache/");
		store2.getIndexes()[8/*interface index*/].packIndex(store);
		System.out.println("Done.");
	}
}
