package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.memoization.MemoizedValue;

public final class WorkbenchResourcesAssetLocation extends AbstractResourcesAssetLocation
{
	private final Aspect dependentAspect;
	private final MemoizedValue<List<AssetLocation>> assetLocationsList;
	
	public WorkbenchResourcesAssetLocation(BRJS root, AssetContainer assetContainer, File file)
	{
		super(root, assetContainer, file);
		dependentAspect = assetContainer.app().aspect("default");
		assetLocationsList = new MemoizedValue<>("WorkbenchResourcesAssetLocation.assetLocations", root(), root().dir());
		registerInitializedNode();
	}
	
	@Override
	public List<AssetLocation> dependentAssetLocations()
	{
		return assetLocationsList.value(() -> {
			List<AssetLocation> assetLocations = new ArrayList<>(super.dependentAssetLocations());
			assetLocations.addAll(dependentAspect.seedAssetLocations());
			
			return assetLocations;
		});
	}
}
