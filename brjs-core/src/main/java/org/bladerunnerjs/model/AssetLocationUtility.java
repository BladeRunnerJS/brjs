package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.List;


public class AssetLocationUtility
{
	public static List<AssetLocation> getAllDependentAssetLocations(AssetLocation assetLocation) {
		List<AssetLocation> assetLocations = new ArrayList<>();
		addAssetLocation(assetLocation, assetLocations);
		
		return assetLocations;
	}
	
	private static void addAssetLocation(AssetLocation assetLocation, List<AssetLocation> assetLocations) {
		assetLocations.add(assetLocation);
		
		for(AssetLocation dependentAssetLocation : assetLocation.dependentAssetLocations()) {
			addAssetLocation(dependentAssetLocation, assetLocations);
		}
	}
}
