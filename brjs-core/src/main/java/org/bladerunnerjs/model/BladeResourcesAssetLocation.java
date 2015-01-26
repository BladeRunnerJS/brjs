package org.bladerunnerjs.model;

import java.util.List;

import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.memoization.MemoizedFile;

public final class BladeResourcesAssetLocation extends ResourcesAssetLocation {
	
	
	private Bladeset parentBladeset;

	public BladeResourcesAssetLocation(BRJS root, AssetContainer assetContainer, MemoizedFile file, AssetLocation parentAssetLocation) {
		super(root, assetContainer, file, parentAssetLocation);
		parentBladeset = root.locateAncestorNodeOfClass(this, Bladeset.class);
	}
	
	@Override
	public List<AssetLocation> dependentAssetLocations()
	{
		List<AssetLocation> assetLocations = super.dependentAssetLocations();
		AssetLocation parentResourcesAssetLocation = parentBladeset.assetLocation("resources");
		if (parentResourcesAssetLocation != null) {
			assetLocations.add( parentResourcesAssetLocation );
		}
		return assetLocations;
	}
	
}
