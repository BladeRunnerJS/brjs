package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public final class SourceAssetLocation extends AbstractSourceAssetLocation {
	public SourceAssetLocation(RootNode rootNode, Node parent, File dir, AssetLocation... dependentAssetLocations) {
		super(rootNode, parent, dir, dependentAssetLocations);
		registerInitializedNode();
	}
	
	protected AssetLocation createNewAssetLocationForChildDir(File dir, AssetLocation parentAssetLocation) {
		return new ChildSourceAssetLocation(assetContainer.root(), assetContainer, dir, parentAssetLocation);
	}
}
