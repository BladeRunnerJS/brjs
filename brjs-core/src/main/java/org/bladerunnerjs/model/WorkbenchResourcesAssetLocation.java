package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;

public class WorkbenchResourcesAssetLocation extends ResourcesAssetLocation
{

	private Aspect dependentAspect;

	public WorkbenchResourcesAssetLocation(BRJS root, AssetContainer assetContainer, File file)
	{
		super(root, assetContainer, file);
		dependentAspect = assetContainer.getApp().aspect("default");
	}

	@Override
	public List<AssetLocation> getDependentAssetLocations()
	{
		List<AssetLocation> assetLocations = super.getDependentAssetLocations();
		AssetLocation dependentAspectResources = dependentAspect.assetLocation("resources");
		if (dependentAspectResources != null)
		{
			assetLocations.add( dependentAspectResources );
			assetLocations.addAll( dependentAspectResources.getDependentAssetLocations() );			
		}
		
		return assetLocations;
	}
	
}
