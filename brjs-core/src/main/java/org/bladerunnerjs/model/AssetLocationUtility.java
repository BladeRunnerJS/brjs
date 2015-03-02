package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.AssetLocation;


public class AssetLocationUtility
{
	public static List<AssetLocation> getAllDependentAssetLocations(AssetLocation assetLocation) {
		List<AssetLocation> assetLocations = new ArrayList<>();
		addAssetLocation(assetLocation, assetLocations);
		
		return assetLocations;
	}
	
	private static void addAssetLocation(AssetLocation parentAssetLocation, List<AssetLocation> assetLocations) {
		assetLocations.add(parentAssetLocation);
		
		for(AssetLocation dependentAssetLocation : parentAssetLocation.dependentAssetLocations()) {
			addAssetLocation(dependentAssetLocation, assetLocations);
		}
	}
}
