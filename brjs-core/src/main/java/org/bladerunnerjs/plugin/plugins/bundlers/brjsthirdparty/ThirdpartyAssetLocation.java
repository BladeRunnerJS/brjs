package org.bladerunnerjs.plugin.plugins.bundlers.brjsthirdparty;

import java.io.File;

import org.bladerunnerjs.model.DeepAssetLocation;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public class ThirdpartyAssetLocation extends DeepAssetLocation {
	public ThirdpartyAssetLocation(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
	}
	
	@Override
	public String getJsStyle() {
		return ThirdpartyAssetLocation.class.getSimpleName();
	}
}
