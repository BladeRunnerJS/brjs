package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public final class ShallowAssetLocation extends AbstractShallowAssetLocation {
	public ShallowAssetLocation(RootNode rootNode, Node parent, File dir, AssetLocation assetLocation) {
		super(rootNode, parent, dir, assetLocation);
		
		// TODO: understand why removing this line doesn't break any tests
		registerInitializedNode();
	}
	
	public ShallowAssetLocation(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
		
		// TODO: understand why removing this line doesn't break any tests
		registerInitializedNode();
	}
}
