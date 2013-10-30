package org.bladerunnerjs.model;

import java.util.List;

public interface FileSet<AF extends AssetFile> {
	List<AF> getFiles();
	void addObserver(FileSetObserver fileSetObserver);
}
