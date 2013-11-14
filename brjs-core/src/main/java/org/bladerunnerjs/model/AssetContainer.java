package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;

public interface AssetContainer extends BRJSNode {
	App getApp();
	String getRequirePrefix();
	List<SourceFile> sourceFiles();
	SourceFile sourceFile(String requirePath);
	List<AssetLocation> getAssetLocations(File srcDir);
}
