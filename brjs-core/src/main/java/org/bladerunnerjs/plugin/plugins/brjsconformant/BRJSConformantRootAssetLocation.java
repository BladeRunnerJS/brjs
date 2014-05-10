package org.bladerunnerjs.plugin.plugins.brjsconformant;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.RootAssetLocation;
import org.bladerunnerjs.model.TheAbstractAssetLocation;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.ConfigException;

public class BRJSConformantRootAssetLocation extends TheAbstractAssetLocation implements RootAssetLocation {
	private BRLibManifest libManifest;
	
	public BRJSConformantRootAssetLocation(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
		
		if(assetContainer() instanceof JsLib) {
			try {
				libManifest = new BRLibManifest((JsLib) assetContainer());
			}
			catch (ConfigException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	@Override
	protected List<File> getCandidateFiles() {
		return Collections.emptyList();
	}
	
	@Override
	public String requirePrefix() {
		if (!libManifest.manifestExists()) {
			return ((JsLib) assetContainer()).getName();
		}
		
		try {
			return libManifest.getRequirePrefix();
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
