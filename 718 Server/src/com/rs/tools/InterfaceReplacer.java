package com.rs.tools;

import java.io.IOException;

import org.displee.CacheLibrary;
import org.displee.cache.index.archive.Archive;
import org.displee.cache.index.archive.file.File;

public class InterfaceReplacer {

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		CacheLibrary library = new CacheLibrary("data/cache/");
		library.getIndex(3).update();
		System.out.println("Updated index 3");
		Archive fromArchive = library.getIndex(3).getArchive(3039);
		Archive toArchive = library.getIndex(3).getArchive(3041);
		for (File a : fromArchive.getFiles()) {
			System.out.println(a);
			toArchive.addFile(a);
		}
		library.getIndex(3).update();
		System.out.println("Finished packing all components from:" + fromArchive.getId() + " to:" + toArchive.getId());
	}
}
