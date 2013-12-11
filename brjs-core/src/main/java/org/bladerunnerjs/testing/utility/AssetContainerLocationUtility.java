package org.bladerunnerjs.testing.utility;

import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;

public class AssetContainerLocationUtility {
	public static AssetLocation getAssetLocation(AssetContainer assetContainer, String locationPath) {
		AssetLocation assetLocation = null;
		
		for(AssetLocation nextAssetLocation : assetContainer.assetLocations()) {
			String nextLocationPath =  assetContainer.dir().toURI().relativize(nextAssetLocation.dir().toURI()).getPath();
			
			if(nextLocationPath.equals(locationPath)) {
				assetLocation = nextAssetLocation;
				break;
			}
		}
		
		return assetLocation;
	}
}
