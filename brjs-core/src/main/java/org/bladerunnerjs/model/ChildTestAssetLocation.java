package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;


public final class ChildTestAssetLocation extends AbstractChildSourceAssetLocation
{

	public ChildTestAssetLocation(RootNode rootNode, Node parent, File dir, AssetLocation parentAssetLocation)
	{
		super(rootNode, parent, dir, parentAssetLocation);
		registerInitializedNode();
	}

}
