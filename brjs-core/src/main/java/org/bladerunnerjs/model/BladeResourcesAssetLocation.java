package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;

public final class BladeResourcesAssetLocation extends ResourcesAssetLocation {
	
	
	private Bladeset parentBladeset;

	public BladeResourcesAssetLocation(BRJS root, AssetContainer assetContainer, File file, AssetLocation parentAssetLocation) {
		super(root, assetContainer, file, parentAssetLocation);
		parentBladeset = root.locateAncestorNodeOfClass(this, Bladeset.class);
	}
	
	@Override
	public List<AssetLocation> dependentAssetLocations()
	{
		List<AssetLocation> assetLocations = super.dependentAssetLocations();
		assetLocations.add( parentBladeset.assetLocation("resources") );
		return assetLocations;
	}
	
}
