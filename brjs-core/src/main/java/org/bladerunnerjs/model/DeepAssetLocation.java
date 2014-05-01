package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public class DeepAssetLocation extends AbstractAssetLocation {
	public DeepAssetLocation(RootNode rootNode, Node parent, File dir, AssetLocation assetLocation) {
		super(rootNode, parent, dir, assetLocation);
		
		// TODO: understand why removing this line doesn't break any tests
		// TODO: we should never call registerInitializedNode() from a non-final class
		registerInitializedNode();
	}
	
	public DeepAssetLocation(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
		
		registerInitializedNode();
	}

	@Override
	protected List<File> getCandidateFiles() {
		return dirInfo.nestedFiles();
	}
}