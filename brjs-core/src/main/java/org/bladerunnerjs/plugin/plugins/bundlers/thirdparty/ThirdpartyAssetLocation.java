package org.bladerunnerjs.plugin.plugins.bundlers.thirdparty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.AbstractAssetLocation;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.NonBladerunnerJsLibManifest;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.ConfigException;

public final class ThirdpartyAssetLocation extends AbstractAssetLocation {
	private final NonBladerunnerJsLibManifest manifest;
	
	public ThirdpartyAssetLocation(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
		
		try {
			manifest = new NonBladerunnerJsLibManifest(this);
		}
		catch(ConfigException e) {
			throw new RuntimeException(e);
		}
		
		registerInitializedNode();
	}
	
	protected List<File> getCandidateFiles() {
		try {
			List<File> assetFiles = new ArrayList<>(manifest.getCssFiles());
			assetFiles.add(file("library.manifest"));
			
			return assetFiles;
		}
		catch(ConfigException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String requirePrefix() {
		return ((JsLib) assetContainer()).getName();
	}
	
	@Override
	public String jsStyle() {
		return ThirdpartyAssetLocation.class.getSimpleName();
	}
}
