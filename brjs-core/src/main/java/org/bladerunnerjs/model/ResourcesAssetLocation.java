package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.memoization.MemoizedValue;

public class ResourcesAssetLocation extends AbstractDeepAssetLocation implements ThemedAssetLocation{
	
	private String themeName = "";
	
	public ResourcesAssetLocation(BRJS root, AssetContainer assetContainer, File file) {
		this(root, assetContainer, file, "common");
	}
	
	public ResourcesAssetLocation(BRJS root, AssetContainer assetContainer, File file, String themeName) {
		super(root, assetContainer, file);
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
		if(this.getThemeName().equals("common")){
			result = populateThemeResources();
		}else{
			result = new ArrayList<AssetLocation>();
		}
		return result;
	}

	private List<AssetLocation>  populateThemeResources() {
		List<AssetLocation> result = new ArrayList<AssetLocation>();
		List<AssetLocation> assetLocations = this.assetContainer().assetLocations();
		for(AssetLocation location: assetLocations){
			if(location instanceof ThemedAssetLocation){
				if( !((ThemedAssetLocation)location).getThemeName().equals("common")){
					result.add(location);
				}
			}
		}
		return result;
	}
	
}
