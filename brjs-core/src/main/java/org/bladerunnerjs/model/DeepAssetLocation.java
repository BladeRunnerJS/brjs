package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public class DeepAssetLocation extends ShallowAssetLocation {
	
	Map<File,AssetLocation> resourcesMap = new LinkedHashMap<File,AssetLocation>();
	
	public DeepAssetLocation(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
	}

	@Override
	public List<LinkedAsset> seedResources()
	{
		List<AssetLocation> assetLocations = new ArrayList<AssetLocation>();
		addChildAssetLocations(assetLocations, dir());
		return getAllAssetsFromAssetLocations(assetLocations);
	}
	
	private List<LinkedAsset> getAllAssetsFromAssetLocations(List<AssetLocation> assetLocations)
	{
		List<LinkedAsset> assetFiles = new ArrayList<LinkedAsset>();
		
		for (AssetLocation assetLocation : assetLocations)
		{
			assetFiles.addAll(assetLocation.seedResources());
		}
		
		return assetFiles;
	}
	
	private void addChildAssetLocations(List<AssetLocation> assetLocations, File findInDir)
	{
		if (!findInDir.isDirectory())
		{
			return;
		}
		
		for (File childDir : root().getFileIterator(findInDir).files())
		{
			if (childDir.isDirectory() && childDir != dir())
			{
				AssetLocation assetLocationForDir = resourcesMap.get(childDir);
    			if (assetLocationForDir == null)
    			{
    				assetLocationForDir = new ShallowAssetLocation(getAssetContainer().root(), getAssetContainer(), childDir);
    				resourcesMap.put(childDir, assetLocationForDir);
    			}
    			assetLocations.add(assetLocationForDir);
    			
    			addChildAssetLocations(assetLocations, childDir);
			}
		}
		
		return;
	}
	
}