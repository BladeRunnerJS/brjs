package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.memoization.MemoizedFile;

public class ResourcesAssetLocation extends AbstractDeepAssetLocation implements ThemedAssetLocation{
	
	private String themeName = "";
	
	public ResourcesAssetLocation(BRJS root, AssetContainer assetContainer, MemoizedFile file, AssetLocation parentAssetLocation) {
		this(root, assetContainer, file, parentAssetLocation, "common");
	}
	
	public ResourcesAssetLocation(BRJS root, AssetContainer assetContainer, MemoizedFile file, AssetLocation parentAssetLocation, String themeName) {
		super(root, assetContainer, file, parentAssetLocation);
		this.themeName = themeName;
	}
	
	@Override
	public String requirePrefix() {
		return assetContainer().requirePrefix();
	}

	@Override
	public String getThemeName() {
		return themeName;
	}
	
	@Override
	public List<AssetLocation> dependentAssetLocations()
	{
		List<AssetLocation> result =  null;
		if(dir().getName().endsWith("resources")){
			result = getThemeResources();
		}else{
			result = new ArrayList<AssetLocation>();
		}
		return result;
	}

	private List<AssetLocation>  getThemeResources() {
		List<AssetLocation> result = new ArrayList<AssetLocation>();
		List<AssetLocation> assetLocations = this.assetContainer().assetLocations();
		for(AssetLocation location: assetLocations){
			if(location instanceof ThemedAssetLocation){
				if(!location.dir().getName().endsWith("resources")){
					result.add(location);
				}
			}
		}
		return result;
	}
	
}
