package org.bladerunnerjs.utility;

import java.util.List;

import org.bladerunnerjs.model.Asset;

public class PrimaryRequirePathUtility {
	public static String getPrimaryRequirePath(Asset asset) {
		List<String> requirePaths = asset.getRequirePaths();
		
		return (requirePaths.size() > 0) ? requirePaths.get(0) : null;
	}
}
