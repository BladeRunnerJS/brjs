package org.bladerunnerjs.api;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.AssetContainer;

/**
 * Any file that lives within an App
 *
 */
public interface Asset {
	Reader getReader() throws IOException;
	MemoizedFile file();
	String getAssetName();
	String getAssetPath();
	List<String> getRequirePaths();
	String getPrimaryRequirePath();
	AssetContainer assetContainer();
	boolean isScopeEnforced();
	boolean isRequirable();
}
