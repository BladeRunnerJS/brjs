package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public class SourceAssetLocation extends ShallowAssetLocation {
	private final Map<File, AssetLocation> assetLocations = new HashMap<>();
	private List<AssetLocation> dependentAssetLocations = new ArrayList<>();
	
	public SourceAssetLocation(RootNode rootNode, Node parent, File dir, AssetLocation assetLocation) {
		super(rootNode, parent, dir);
		dependentAssetLocations.add(assetLocation);
	}
	
	@Override
	public String requirePrefix() {
		return assetContainer.requirePrefix();
	}
	
	@Override
	public List<AssetLocation> getDependentAssetLocations() {
		return dependentAssetLocations;
	}
	
	public List<AssetLocation> getChildAssetLocations() {
		return getChildAssetLocations(new ArrayList<AssetLocation>(), dir());
	}
	
	private List<AssetLocation> getChildAssetLocations(List<AssetLocation> assetLocations, File findInDir)
	{
		if (!findInDir.isDirectory())
		{
			return assetLocations;
		}
		
		for (File childDir : root().getFileIterator(findInDir).files())
		{
			if (childDir.isDirectory() && childDir != dir())
			{
				assetLocations.add(createAssetLocationForChildDir(childDir));
				getChildAssetLocations(assetLocations, childDir);
			}
		}
		
		return assetLocations;
	}
	
	private AssetLocation createAssetLocationForChildDir(File dir) {
		AssetLocation assetLocation = assetLocations.get(dir);
		
		if (assetLocation == null) {
			AssetLocation parentAssetLocation = assetLocations.containsKey(dir.getParentFile()) ? assetLocations.get(dir.getParentFile()) : this;
			assetLocation = new ChildSourceAssetLocation(assetContainer.root(), assetContainer, dir, parentAssetLocation);
			assetLocations.put(dir, assetLocation);
		}
		
		return assetLocation;
	}
}
