package org.bladerunnerjs.model;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.bladerunnerjs.memoization.MemoizedFile;

/**
 * Any file that lives within an App
 *
 */
public interface Asset {
	Reader getReader() throws IOException;
	AssetLocation assetLocation();
	MemoizedFile dir();
	String getAssetName();
	String getAssetPath();
	List<String> getRequirePaths();
	String getPrimaryRequirePath();
}
