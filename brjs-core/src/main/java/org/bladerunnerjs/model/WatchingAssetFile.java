package org.bladerunnerjs.model;

import java.io.File;
import java.io.Reader;

public class WatchingAssetFile implements AssetFile {
	public WatchingAssetFile(File file) {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Reader getReader() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void addObserver(AssetFileObserver observer) {
		// TODO Auto-generated method stub
	}
}
