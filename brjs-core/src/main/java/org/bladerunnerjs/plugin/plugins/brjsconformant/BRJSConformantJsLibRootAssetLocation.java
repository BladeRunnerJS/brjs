package org.bladerunnerjs.plugin.plugins.brjsconformant;

import java.io.File;

import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.ConfigException;

public class BRJSConformantJsLibRootAssetLocation extends BRJSConformantRootAssetLocation {
	private BRLibConf libManifest;
	
	public BRJSConformantJsLibRootAssetLocation(RootNode rootNode, AssetContainer assetContainer, File dir, AssetLocation parentAssetLocation) {
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
