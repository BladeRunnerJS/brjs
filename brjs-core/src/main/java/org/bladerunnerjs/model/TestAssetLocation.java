package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;


public class TestAssetLocation extends AbstractSourceAssetLocation
{

	public TestAssetLocation(RootNode rootNode, Node parent, File dir)
	{
		super(rootNode, parent, dir);
	}
	
	public TestAssetLocation(RootNode rootNode, Node parent, File dir, AssetLocation... dependentAssetLocations)
	{
		super(rootNode, parent, dir, dependentAssetLocations);
	}
	
	protected AssetLocation createNewAssetLocationForChildDir(File dir, AssetLocation parentAssetLocation)
	{
		return new ChildTestAssetLocation(assetContainer.root(), assetContainer, dir, parentAssetLocation);
	}
	
	@Override
	protected List<File> getCandidateFiles() {
		return dirInfo.files();
	}
}
