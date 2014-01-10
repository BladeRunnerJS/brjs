package org.bladerunnerjs.model;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
		return getChildAssets(new LinkedList<LinkedAsset>(), dir());
	}
	
	private List<LinkedAsset> getChildAssets(List<LinkedAsset> assetFiles, File findInDir)
	{
		if (!findInDir.isDirectory())
		{
			return assetFiles;
		}
		
		for (File childDir : root().getFileIterator(findInDir).files())
		{
			if (childDir.isDirectory() && childDir != dir())
			{
				AssetLocation dirResources = resourcesMap.get(childDir);
    			if (dirResources == null)
    			{
    				dirResources = new ShallowAssetLocation(getAssetContainer().root(), getAssetContainer(), childDir);
    				resourcesMap.put(childDir, dirResources);
    			}
    			assetFiles.addAll(dirResources.seedResources());
    			
    			getChildAssets(assetFiles, childDir);
			}
		}
		
		return assetFiles;
	}
	
}