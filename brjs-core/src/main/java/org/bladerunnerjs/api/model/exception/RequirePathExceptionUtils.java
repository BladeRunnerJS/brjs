package org.bladerunnerjs.api.model.exception;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.model.AssetContainer;


public class RequirePathExceptionUtils
{

	public static String getScopeLocationText(AssetContainer assetContainer) {
		BRJS brjs = assetContainer.root();
		StringBuilder scopedLocationsBuilder = new StringBuilder();
		for (AssetContainer scopeAssetContainer : assetContainer.scopeAssetContainers()) {
			if (scopedLocationsBuilder.length() > 0) {
				scopedLocationsBuilder.append(", ");
			}
			scopedLocationsBuilder.append( brjs.dir().getRelativePath(scopeAssetContainer.dir()) );
		}
		return scopedLocationsBuilder.toString();
	}
	
}
