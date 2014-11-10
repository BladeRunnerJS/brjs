package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public final class BladeWorkbench extends Workbench<Blade>
{
	
	public BladeWorkbench(RootNode rootNode, Node parent, MemoizedFile dir)
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
		assetContainers.add( root().locateAncestorNodeOfClass(this, Bladeset.class) );
		assetContainers.add( root().locateAncestorNodeOfClass(this, Blade.class) );
		assetContainers.add(this);
		return assetContainers;
	}

}
