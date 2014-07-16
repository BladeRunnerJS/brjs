package org.bladerunnerjs.plugin.plugins.brjsconformant;

import java.io.File;

import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.ConfigException;

public class BRJSConformantJsLibRootAssetLocation extends BRJSConformantRootAssetLocation {
	private BRLibConf libManifest;
	
	public BRJSConformantJsLibRootAssetLocation(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
		
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
	public void setNamespace(String namespace) throws ConfigException {
		libManifest.setRequirePrefix(namespace.replace('.', '/'));
	}
}
