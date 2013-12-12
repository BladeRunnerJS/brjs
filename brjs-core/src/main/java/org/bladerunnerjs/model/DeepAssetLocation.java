package org.bladerunnerjs.model;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.utility.FileUtility;

public class DeepAssetLocation extends ShallowAssetLocation {
	
	Map<File,AssetLocation> resourcesMap = new LinkedHashMap<File,AssetLocation>();
	
	public DeepAssetLocation(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
	}

	@Override
	public List<LinkedAsset> seedResources()
	{
		List<LinkedAsset> assetFiles = new LinkedList<LinkedAsset>();
		
		for (File dir : FileUtility.recursiveListDirs(dir()))
		{
			if (!dir.equals(dir())) {
				AssetLocation dirResources = resourcesMap.get(dir);
    			if (dirResources == null)
    			{
    				dirResources = new ShallowAssetLocation(getAssetContainer().root(), getAssetContainer(), dir);
    				resourcesMap.put(dir, dirResources);
    			}
    			assetFiles.addAll(dirResources.seedResources());
			}
		}
		
		return assetFiles;
	}
	
}