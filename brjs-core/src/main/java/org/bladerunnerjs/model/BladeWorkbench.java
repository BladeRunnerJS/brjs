package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public final class BladeWorkbench extends Workbench
{
	
	public BladeWorkbench(RootNode rootNode, Node parent, File dir)
	{
		super(rootNode, parent, dir);
	}
		
	public Blade parent()
	{
		return (Blade) parentNode();
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
