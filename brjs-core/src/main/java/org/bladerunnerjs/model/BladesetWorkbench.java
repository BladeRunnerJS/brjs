package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.Workbench;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public class BladesetWorkbench extends Workbench<Bladeset> {

	public BladesetWorkbench(RootNode rootNode, Node parent, MemoizedFile dir)
	{
		super(rootNode, parent, dir);
	}
	
	@Override
	public List<AssetContainer> scopeAssetContainers() {
		List<AssetContainer> assetContainers = new ArrayList<>();
		for (JsLib jsLib : app().jsLibs())
		{
			assetContainers.add( jsLib );			
		}
		Bladeset bladeset = root().locateAncestorNodeOfClass(this, Bladeset.class);
		assetContainers.add(bladeset);
		assetContainers.addAll(bladeset.blades());
		assetContainers.add(this);
		return assetContainers;	
	}
}
