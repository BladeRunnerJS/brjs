package org.bladerunnerjs.model;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public class JsNonBladeRunnerAssetLocation extends SourceAssetLocation
{
	
	protected AssetContainer assetContainer;
	@SuppressWarnings("unused")
	private final Map<String, JsNonBladeRunnerAssetLocation> resources = new HashMap<>();
	
	public JsNonBladeRunnerAssetLocation(RootNode rootNode, Node parent, File dir)
	{
		super(rootNode, parent, dir);
		this.assetContainer = (AssetContainer) parent;
	}
	
}
