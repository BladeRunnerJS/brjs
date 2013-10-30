package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StandardFileSet<AF extends AssetFile> implements FileSet<AF> {
	private List<AF> files = new ArrayList<>();
	private List<FileSetObserver> observers = new ArrayList<>();
	
	public static String[] paths(String... paths) {
		return paths;
	}
	
	public StandardFileSet(File rootDir, String[] includePaths, String[] excludePaths, FileSetFactory<AF> fileSetFactory) {
		// TODO: this class should watch 'rootDir', and use the provided FileSetFactory to create new sub-types of AssetFile as matching files are discovered
	}
	
	public List<AF> getFiles() {
		return files;
	}
	
	public void addObserver(FileSetObserver fileSetObserver) {
		observers.add(fileSetObserver);
	}
}
