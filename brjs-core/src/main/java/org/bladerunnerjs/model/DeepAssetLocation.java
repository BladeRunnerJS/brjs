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
		List<LinkedAsset> assetFiles = new ArrayList<LinkedAsset>();
		
		for (AssetLocation assetLocation : getChildAssetLocations(dir()))
		{
			for(LinkedAsset linkedAsset : assetLocation.seedResources()) {
				assetFiles.add(new DeepLinkedAsset(linkedAsset, this));
			}
		}
		
		return assetFiles;
	}
	
	@Override
	public List<Asset> bundleResources(String fileExtension) {
		List<Asset> assetFiles = new ArrayList<>();
		
		for(AssetLocation assetLocation : getChildAssetLocations(dir())) {
			for(Asset asset : assetLocation.bundleResources(fileExtension)) {
				assetFiles.add(new DeepAsset(asset, this));
			}
		}
		
		return assetFiles;
	}
	
	private List<AssetLocation> getChildAssetLocations(File findInDir)
	{
		List<AssetLocation> assetLocations = new ArrayList<AssetLocation>();
		assetLocations.add( getCachedAssetLocationForDir(dir()) );
		getChildAssetLocations(assetLocations, findInDir);
		return assetLocations;
	}
	
	private void getChildAssetLocations(List<AssetLocation> assetLocations, File findInDir)
	{
		if (!findInDir.isDirectory())
		{
			return;
		}
		
		for (File childDir : root().getFileIterator(findInDir).files())
		{
			if (childDir.isDirectory() && childDir != dir())
			{
				AssetLocation assetLocationForDir = getCachedAssetLocationForDir(childDir);
    			assetLocations.add(assetLocationForDir);
    			
    			getChildAssetLocations(assetLocations, childDir);
			}
		}
		
		return;
	}

	private AssetLocation getCachedAssetLocationForDir(File childDir)
	{
		AssetLocation assetLocationForDir = resourcesMap.get(childDir);
		if (assetLocationForDir == null)
		{
			assetLocationForDir = new ShallowAssetLocation(getAssetContainer().root(), getAssetContainer(), childDir);
			resourcesMap.put(childDir, assetLocationForDir);
		}
		return assetLocationForDir;
	}
	
}