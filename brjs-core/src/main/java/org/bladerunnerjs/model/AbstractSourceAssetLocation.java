package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public abstract class AbstractSourceAssetLocation extends AbstractShallowAssetLocation {
	private final Map<File, AssetLocation> assetLocations = new HashMap<>();
	private final MemoizedValue<List<AssetLocation>> childAssetLocationList = new MemoizedValue<>("AssetLocation.childAssetLocations", root(), dir());
	
	public AbstractSourceAssetLocation(RootNode rootNode, Node parent, File dir, AssetLocation... dependentAssetLocations) {
		super(rootNode, parent, dir, dependentAssetLocations);
	}
	
	protected abstract AssetLocation createNewAssetLocationForChildDir(File dir, AssetLocation parentAssetLocation);
	
	public List<AssetLocation> getChildAssetLocations() {
		return childAssetLocationList.value(() -> {
			List<AssetLocation> assetLocations = new ArrayList<AssetLocation>();
			addChildAssetLocations(assetLocations, dir());
			return assetLocations;
		});
	}
	
	@Override
	public String requirePrefix() {
		return assetContainer.requirePrefix();
	}
	
	private void addChildAssetLocations(List<AssetLocation> assetLocations, File findInDir)
	{
		FileInfo dirInfo = root().getFileInfo(findInDir);
		
		if (dirInfo.isDirectory())
		{
			for (File childDir : dirInfo.dirs())
			{
				if (childDir != dir())
				{
					assetLocations.add(getAssetLocationForChildDir(childDir));
					addChildAssetLocations(assetLocations, childDir);
				}
			}
		}
	}
	
	private AssetLocation getAssetLocationForChildDir(File dir) {
		AssetLocation assetLocation = assetLocations.get(dir);
		
		if (assetLocation == null) {
			AssetLocation parentAssetLocation = assetLocations.containsKey(dir.getParentFile()) ? assetLocations.get(dir.getParentFile()) : this;
			assetLocation = createNewAssetLocationForChildDir(dir, parentAssetLocation);
			assetLocations.put(dir, assetLocation);
		}
		
		return assetLocation;
	}
}
