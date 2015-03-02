package org.bladerunnerjs.plugin.brjsconformant;

import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.engine.RootNode;

public class BRJSConformantJsLibRootAssetLocation extends BRJSConformantRootAssetLocation {
	private BRLibConf libManifest;
	
	public BRJSConformantJsLibRootAssetLocation(RootNode rootNode, AssetContainer assetContainer, MemoizedFile dir, AssetLocation parentAssetLocation) {
		super(rootNode, assetContainer, dir, parentAssetLocation);
		
		try {
			libManifest = new BRLibConf((JsLib) assetContainer());
		}
		catch (ConfigException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String requirePrefix() {
		try {
			return (libManifest.manifestExists()) ? libManifest.getRequirePrefix() : ((JsLib) assetContainer()).getName();
		}
		catch (ConfigException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void setRequirePrefix(String requirePrefix) throws ConfigException {
		libManifest.setRequirePrefix(requirePrefix);
		libManifest.write();
	}
}
