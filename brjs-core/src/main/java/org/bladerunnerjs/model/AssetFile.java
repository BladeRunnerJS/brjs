package org.bladerunnerjs.model;

import java.io.Reader;

public interface AssetFile {
	Reader getReader();
	void addObserver(AssetFileObserver observer);
}
