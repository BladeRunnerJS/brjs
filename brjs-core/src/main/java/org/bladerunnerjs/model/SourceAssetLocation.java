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
	
	public SourceAssetLocation(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
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
		List<AssetLocation> assetLocations = new ArrayList<AssetLocation>();
		addChildAssetLocations(assetLocations, dir());
		return assetLocations;
	}
	
	private void addChildAssetLocations(List<AssetLocation> assetLocations, File findInDir)
	{
		if (findInDir.isDirectory())
		{
			for (File childDir : root().getFileIterator(findInDir).dirs())
			{
				if (childDir != dir())
				{
					assetLocations.add(createAssetLocationForChildDir(childDir));
					addChildAssetLocations(assetLocations, childDir);
				}
			}
		}
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
