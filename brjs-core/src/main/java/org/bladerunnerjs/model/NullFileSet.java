package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.List;

public class NullFileSet<AF extends AssetFile> implements FileSet<AF> {
	List<AF> files = new ArrayList<>();
	
	@Override
	public List<AF> getFiles() {
		return files;
	}
	
	@Override
	public void addObserver(FileSetObserver fileSetObserver) {
		// don't bother adding as they'll never get notified of anything
	}
}
