package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.engine.RootNode;

//TODO: find a better name for this - its a JsLib that doesnt have a src or resources
public class ShallowJsLib extends JsLib
{
	
	private final NodeItem<SourceAssetLocation> src = new NodeItem<>(SourceAssetLocation.class, "");
	private final NodeItem<DeepAssetLocation> resources = new NodeItem<>(DeepAssetLocation.class, "");
	
	public ShallowJsLib(RootNode rootNode, Node parent, File dir)
	{
		super(rootNode, parent, dir);
	}
	
	@Override
	public List<AssetLocation> getAllAssetLocations() 
	{
		List<AssetLocation> assetLocations = new ArrayList<>();
		assetLocations.add( new ShallowAssetLocation(root(), this, dir()));
		return assetLocations;
	}
	
	@Override
	public SourceAssetLocation src()
	{
		return item(src);
	}
	
	@Override
	public DeepAssetLocation resources()
	{
		return item(resources);
	}
}
