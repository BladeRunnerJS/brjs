package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.model.engine.RootNode;

public abstract class AbstractSourceAssetLocation extends AbstractShallowAssetLocation {
	private final Map<File, AssetLocation> assetLocations = new TreeMap<>();
	private final MemoizedValue<List<AssetLocation>> childAssetLocationList = new MemoizedValue<>(dir()+" - childAssetLocations", root(), dir());
	
	public AbstractSourceAssetLocation(RootNode rootNode, AssetContainer assetContainer, MemoizedFile dir, AssetLocation parentAssetLocation, AssetLocation... dependentAssetLocations) {
		super(rootNode, assetContainer, dir, parentAssetLocation, dependentAssetLocations);
	}
	
	protected abstract AssetLocation createNewAssetLocationForChildDir(MemoizedFile dir, AssetLocation parentAssetLocation);
	
	public List<AssetLocation> getChildAssetLocations() {
		return childAssetLocationList.value(() -> {
			List<AssetLocation> assetLocations = new ArrayList<AssetLocation>();
			addChildAssetLocations(assetLocations, dir());
			return assetLocations;
		});
	}
	
	@Override
	public String requirePrefix() {
		return assetContainer().requirePrefix();
	}
	
	private void addChildAssetLocations(List<AssetLocation> assetLocations, MemoizedFile findInDir)
	{
		if (findInDir.isDirectory())
		{
			for (MemoizedFile childDir : findInDir.dirs())
			{
				if (childDir != dir())
				{
					assetLocations.add(getAssetLocationForChildDir(childDir));
					addChildAssetLocations(assetLocations, childDir);
				}
			}
		}
	}
	
	private AssetLocation getAssetLocationForChildDir(MemoizedFile dir) {
		AssetLocation assetLocation = assetLocations.get(dir);
		
		if (assetLocation == null) {
			AssetLocation parentAssetLocation = assetLocations.containsKey(dir.getParentFile()) ? assetLocations.get(dir.getParentFile()) : this;
			assetLocation = createNewAssetLocationForChildDir(dir, parentAssetLocation);
			assetLocations.put(dir, assetLocation);
		}
		
		return assetLocation;
	}
}
